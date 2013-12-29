/**
 * 
 */
package com.talent.platform.threadpool.monitor.intf;

/**
 * 可监控的线程。如果一个线程想被监控，一定要实现此接口
 * @author 谭耀武
 * @date 2012-1-4
 *
 */
public interface MonitorableThreadIntf
{
    /**
     * 获取线程调度器
     * 
     * @return
     */
    MonitorableThreadPoolExecutorIntf getExecutor();

    /**
     * 设置线程调度器
     * 
     * @param executor
     */
    void setExecutor(MonitorableThreadPoolExecutorIntf executor);

    /**
     * 获取当前的任务对象
     * 
     * @return
     */
    Runnable getRunnable();

    /**
     * 设置当前线程运行的任务对象
     * 
     * @param task
     */
    void setRunnable(Runnable task);

    /**
     * 获取线程所用的cpu时间,单位毫秒
     * 
     * @return
     */
    long getCpuTime();

    /**
     * 设置线程所用的cpu时间，单位毫秒
     * 
     * @param cpuTime
     */
    void setCpuTime(long cpuTime);

    /**
     * 线程名字
     * 
     * @return
     */
    String getName();

}
