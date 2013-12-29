package com.talent.nio.communicate.monitor.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.talent.platform.threadpool.monitor.intf.MonitorableThreadIntf;
import com.talent.platform.threadpool.monitor.intf.MonitorableThreadPoolExecutorIntf;
import com.talent.platform.threadpool.monitor.vo.ThreadVo;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public class JvmVo implements Serializable
{
    // 节点号,最大堆内存,已用堆内存,已用非堆内存,可用内存,已用内存,总线程数,死锁线程数,链路检测时间,内存清除时间

    // 线程池1/线程池2/线程池3 --getName //tab,用两个列表

    // 列表1
    // 正在执行的线程数,当前存活的线程数,常活线程数,最多允许存活的线程数,已完成的任务数

    // 列表2
    // 链路唯一标识名字,线程名字,任务名字,业务处理对象（指packetHandler等）,线程CPU时间,任务处理的消息数,任务是否正在运行?

    /**
     * 
     */
    private static final long serialVersionUID = -1254431192464335955L;

    /**
     * 内存信息
     */
    private MemoryVo memoryData = null;

    /**
     * 线程信息
     */
    private ThreadVo[] threadDatas = null;

    /**
     * 处于死锁下的线程信息
     */
    private ThreadVo[] deadLockThreads = null;

    /**
     * 线程调度器和线程Map
     */
    private Map<MonitorableThreadPoolExecutorIntf, List<MonitorableThreadIntf>> mapOfExecutorAndThread;

    /**
     * @return the memoryData
     */
    public MemoryVo getMemoryData()
    {
        return memoryData;
    }

    /**
     * @param memoryData
     *            the memoryData to set
     */
    public void setMemoryData(MemoryVo memoryData)
    {
        this.memoryData = memoryData;
    }

    /**
     * @return the threadDatas
     */
    public ThreadVo[] getThreadDatas()
    {
        return threadDatas;
    }

    /**
     * @param threadDatas
     *            the threadDatas to set
     */
    public void setThreadDatas(ThreadVo[] threadDatas)
    {
        this.threadDatas = threadDatas;
    }

    /**
     * @return the deadLockThreads
     */
    public ThreadVo[] getDeadLockThreads()
    {
        return deadLockThreads;
    }

    /**
     * @param deadLockThreads
     *            the deadLockThreads to set
     */
    public void setDeadLockThreads(ThreadVo[] deadLockThreads)
    {
        this.deadLockThreads = deadLockThreads;
    }

    public void setMapOfExecutorAndThread(Map<MonitorableThreadPoolExecutorIntf, List<MonitorableThreadIntf>> mapOfExecutorAndThread)
    {
        this.mapOfExecutorAndThread = mapOfExecutorAndThread;
    }

    public Map<MonitorableThreadPoolExecutorIntf, List<MonitorableThreadIntf>> getMapOfExecutorAndThread()
    {
        return mapOfExecutorAndThread;
    }

    public static void main(String[] args)
    {

    }
}
