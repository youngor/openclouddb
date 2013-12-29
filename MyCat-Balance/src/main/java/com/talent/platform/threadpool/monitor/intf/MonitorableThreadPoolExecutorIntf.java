package com.talent.platform.threadpool.monitor.intf;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * 可监控的线程池调度器
 * 
 * @author 谭耀武
 * @date 2011-12-26
 * 
 */
public interface MonitorableThreadPoolExecutorIntf extends Executor, Comparable<Object>
{
    /**
     * 获取常活线程数
     * 
     * @return
     */
    public int getCorePoolSize();

    /**
     * 获取允许最大活动线程数
     * 
     * @return
     */
    public int getMaximumPoolSize();

    /**
     * 获取当前存活的线程数
     * 
     * @return
     */
    public int getPoolSize();

    /**
     * 当前正在执行任务的线程数
     * 
     * @return
     */
    public int getActiveCount();

    /**
     * 程序运行过程中，池中存活线程数的最大值
     * 
     * @return
     */
    public int getLargestPoolSize();

    /**
     * 已经完成的任务数
     * 
     * @return
     */
    public long getCompletedTaskCount();

    /**
     * 获取存放任务对象的队列
     * 
     * @return
     */
    public BlockingQueue<Runnable> getQueue();

    /**
     * 线程池的名字
     * 
     * @return
     */
    public String getName();

}
