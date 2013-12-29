/**
 * 
 */
package com.talent.platform.threadpool.monitor.intf;

import java.util.concurrent.atomic.AtomicLong;

import com.talent.platform.threadpool.intf.SynRunnableIntf;

/**
 * 可监控的任务(Runnable or Callable)
 * @author 谭耀武
 * @date 2012-1-4
 *
 */
public interface MonitorableRunnableIntf extends SynRunnableIntf
{

    /**
     * 计数器,用于获取被提交到线程池的次数
     * 
     * @return
     */
    AtomicLong getSubmitCount();

    /**
     * 执行的次数，与getSubmitCount()不同，此方法返回的数是该Runnable的有效执行次数，譬如消息处理类，该方法返回的是处理的消息总数
     * 
     * @return
     */
    AtomicLong getProcessedMsgCount();

    /**
     * 获取本任务的名字
     * 
     * @return
     */
    String getRunnableName();

    /**
     * 获取当前正在执行任务的Object
     * 
     * @return
     */
    String getCurrentProcessor();
}
