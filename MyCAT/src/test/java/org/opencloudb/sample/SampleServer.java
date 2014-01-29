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
package org.opencloudb.sample;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.opencloudb.net.NIOAcceptor;
import org.opencloudb.net.NIOProcessor;
import org.opencloudb.util.TimeUtil;

/**
 * 服务器组装示例
 * 
 * @author mycat
 */
public class SampleServer {
    private static final int SERVER_PORT = 8066;
    private static final long TIME_UPDATE_PERIOD = 100L;
    private static final SampleServer INSTANCE = new SampleServer();
    private static final Logger LOGGER = Logger.getLogger(SampleServer.class);

    public static final SampleServer getInstance() {
        return INSTANCE;
    }

    private SampleConfig config;
    private Timer timer;
    private NIOProcessor[] processors;
    private NIOAcceptor server;

    private SampleServer() {
        this.config = new SampleConfig();
    }

    public SampleConfig getConfig() {
        return config;
    }

    public void startup() throws IOException {
        String name = config.getServerName();
        LOGGER.info("===============================================");
        LOGGER.info(name + " is ready to startup ...");

        // schedule timer task
        timer = new Timer(name + "Timer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimeUtil.update();
            }
        }, 0L, TIME_UPDATE_PERIOD);
        LOGGER.info("Task Timer is started ...");

        // startup processors
        processors = new NIOProcessor[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < processors.length; i++) {
            processors[i] = new NIOProcessor(name + "Processor" + i,1024*1024,4096,1);
            processors[i].startup();
        }

        // startup server
        SampleConnectionFactory factory = new SampleConnectionFactory();
        server = new NIOAcceptor(name + "Server", SERVER_PORT, factory);
        server.setProcessors(processors);
        server.start();
        LOGGER.info(server.getName() + " is started and listening on " + server.getPort());

        // end
        LOGGER.info("===============================================");
    }

}