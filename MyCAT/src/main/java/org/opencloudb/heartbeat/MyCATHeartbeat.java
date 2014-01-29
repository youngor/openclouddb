/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package org.opencloudb.heartbeat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.opencloudb.MycatNode;
import org.opencloudb.config.Alarms;
import org.opencloudb.config.model.MycatNodeConfig;
import org.opencloudb.net.mysql.OkPacket;
import org.opencloudb.statistic.HeartbeatRecorder;
import org.opencloudb.util.TimeUtil;

/**
 * @author mycat
 */
public class MyCATHeartbeat {
    public static final int OK_STATUS = 1;
    public static final int OFF_STATUS = 2;
    public static final int SEND = 3;
    public static final int ERROR_STATUS = -1;
    private static final int TIMEOUT_STATUS = -2;
    private static final int INIT_STATUS = 0;
    private static final int MAX_RETRY_COUNT = 5;
    private static final Logger ALARM = Logger.getLogger("alarm");
    private static final Logger LOGGER = Logger.getLogger(MyCATHeartbeat.class);
    private static final Logger HEARTBEAT = Logger.getLogger("heartbeat");

    private final MycatNode node;
    private final AtomicBoolean isStop;
    private final AtomicBoolean isChecking;
    private final MyCATDetectorFactory factory;
    private final HeartbeatRecorder recorder;
    private final ReentrantLock lock;
    private final int maxRetryCount;
    private int errorCount;
    private volatile int status;
    private MyCATDetector detector;
    public final AtomicLong detectCount;

    public MyCATHeartbeat(MycatNode node) {
        this.node = node;
        this.isStop = new AtomicBoolean(false);
        this.isChecking = new AtomicBoolean(false);
        this.factory = new MyCATDetectorFactory();
        this.recorder = new HeartbeatRecorder();
        this.lock = new ReentrantLock(false);
        this.maxRetryCount = MAX_RETRY_COUNT;
        this.status = OK_STATUS;
        this.detectCount = new AtomicLong(0);
    }

    public MycatNode getNode() {
        return node;
    }

    public MyCATDetector getDetector() {
        return detector;
    }

    public int getStatus() {
        return status;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public long getTimeout() {
        MyCATDetector detector = this.detector;
        if (detector == null) {
            return -1L;
        }
        return detector.getHeartbeatTimeout();
    }

    public HeartbeatRecorder getRecorder() {
        return recorder;
    }

    public String lastActiveTime() {
        MyCATDetector detector = this.detector;
        if (detector == null) {
            return null;
        }
        long t = Math.max(detector.lastReadTime(), detector.lastWriteTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(t));
    }

    public boolean isStop() {
        return isStop.get();
    }

    public boolean isChecking() {
        return isChecking.get();
    }

    public void start() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            isStop.compareAndSet(true, false);
        } finally {
            lock.unlock();
        }
    }

    public void stop() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (isStop.compareAndSet(false, true)) {
                if (isChecking.get()) {
                    // nothing
                } else {
                    MyCATDetector detector = this.detector;
                    if (detector != null) {
                        detector.quit();
                        isChecking.set(false);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 执行心跳
     */
    public void heartbeat() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (isChecking.compareAndSet(false, true)) {
                MyCATDetector detector = this.detector;
                if (detector == null || detector.isQuit() || detector.isClosed()) {
                    try {
                        detector = factory.make(this);
                    } catch (Throwable e) {
                        LOGGER.warn(node.toString(), e);
                        setError(null);
                        return;
                    }
                    this.detector = detector;
                } else {
                    detector.heartbeat();
                }
            } else {
                MyCATDetector detector = this.detector;
                if (detector != null) {
                    if (detector.isQuit() || detector.isClosed()) {
                        isChecking.compareAndSet(true, false);
                    } else if (detector.isHeartbeatTimeout()) {
                        setTimeout(detector);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设定结果
     */
    public void setResult(int result, MyCATDetector detector, boolean isTransferError, byte[] message) {
        switch (result) {
        case OK_STATUS:
            setOk(detector);
            if (HEARTBEAT.isInfoEnabled()) {
                HEARTBEAT.info(requestMessage(OK_STATUS, message));
            }
            break;
        case OFF_STATUS:
            setOff(detector);
            if (HEARTBEAT.isInfoEnabled()) {
                HEARTBEAT.info(requestMessage(OFF_STATUS, message));
            }
            break;
        case ERROR_STATUS:
            if (detector.isQuit()) {
                isChecking.set(false);
            } else {
                if (isTransferError) {
                    detector.close("heatbeat transferError");
                }
                setError(detector);
            }
            if (HEARTBEAT.isInfoEnabled()) {
                HEARTBEAT.info(requestMessage(ERROR_STATUS, message));
            }
            break;
        }
    }

    private void setOk(MyCATDetector detector) {
        recorder.set(detector.lastReadTime() - detector.lastWriteTime());
        switch (status) {
        case TIMEOUT_STATUS:
            this.status = INIT_STATUS;
            this.errorCount = 0;
            this.isChecking.set(false);
            if (isStop.get()) {
                detector.quit();
            } else {
                heartbeat();// 超时状态，再次执行心跳。
            }
            break;
        default:
            this.status = OK_STATUS;
            this.errorCount = 0;
            this.isChecking.set(false);
            if (isStop.get()) {
                detector.quit();
            }
        }
    }

    private void setOff(MyCATDetector detector) {
        this.status = OFF_STATUS;
        this.errorCount = 0;
        this.isChecking.set(false);
        if (isStop.get()) {
            detector.quit();
        }
    }

    private void setError(MyCATDetector detector) {
        if (++errorCount < maxRetryCount) {
            this.isChecking.set(false);
            if (detector != null && isStop.get()) {
                detector.quit();
            } else {
                heartbeat();// 未到达错误次数，再次执行心跳。
            }
        } else {
            this.status = ERROR_STATUS;
            this.errorCount = 0;
            this.isChecking.set(false);
            try {
                ALARM.error(alarmMessage("ERROR"));
            } finally {
                if (detector != null && isStop.get()) {
                    detector.quit();
                }
            }
        }
    }

    private void setTimeout(MyCATDetector detector) {
        status = TIMEOUT_STATUS;
        try {
            ALARM.error(alarmMessage("TIMEOUT"));
            if (HEARTBEAT.isInfoEnabled()) {
                HEARTBEAT.info(requestMessage(TIMEOUT_STATUS, null));
            }
        } finally {
            detector.quit();
            isChecking.set(false);
        }
    }

    /**
     * 报警信息
     */
    private String alarmMessage(String reason) {
        MycatNodeConfig cnc = node.getConfig();
        return new StringBuilder().append(Alarms.DEFAULT).append("[name=").append(cnc.getName()).append(",host=")
                .append(cnc.getHost()).append(",port=").append(cnc.getPort()).append(",reason=").append(reason)
                .append(']').toString();
    }

    /**
     * 心跳日志信息
     */
    public String requestMessage(int type, byte[] message) {
        String action = null;
        String id = null;
        switch (type) {
        case OK_STATUS:
            action = "OK";
            OkPacket ok = new OkPacket();
            ok.read(message);
            id = String.valueOf(ok.affectedRows);
            break;
        case OFF_STATUS:
            action = "OFFLINE";
            if (message != null) {
                id = new String(message);
            }
            break;
        case ERROR_STATUS:
            action = "ERROR";
            if (message != null) {
                id = new String(message);
            }
            break;
        case TIMEOUT_STATUS:
            action = "TIMEOUT";
            if (message != null) {
                id = new String(message);
            }
            break;
        case SEND:
            action = "SEND";
            if (message != null) {
                id = new String(message);
            }
            break;
        default:
            action = "UNKNOWN";
            if (message != null) {
                id = new String(message);
            }
        }

        // 如果取不到从服务端返回的id，则从本地取得。
        if (id == null) {
            id = "$" + detectCount.get();
        }

        return new StringBuilder().append("REQUEST:").append(action).append(", id=").append(id).append(", host=")
                .append(node.getConfig().getHost()).append(", port=").append(node.getConfig().getPort())
                .append(", time=").append(TimeUtil.currentTimeMillis()).toString();
    }

}