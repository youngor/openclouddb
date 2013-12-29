package com.talent.platform.threadpool.intf;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.talent.platform.threadpool.monitor.intf.MonitorableRunnableIntf;

/**
 * 队列数据处理任务接口
 * @author 谭耀武
 * @date 2012-1-4
 *
 * @param <T> 队列中存的数据类型
 */
public interface QueueRunnableIntf<T> extends MonitorableRunnableIntf
{
    /**
     * 获取数据队列
     * 
     * @return 保存着要处理的数据的队列
     */
    ConcurrentLinkedQueue<T> getMsgQueue();
}
