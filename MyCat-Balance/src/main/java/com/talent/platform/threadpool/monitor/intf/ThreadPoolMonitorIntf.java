/**
 * 
 */
package com.talent.platform.threadpool.monitor.intf;

import java.util.Set;

/**
 * 
 * @author 谭耀武
 * @date 2012-1-4
 *
 */
public interface ThreadPoolMonitorIntf
{
    /**
     * 注册IMonitorableThreadPoolExecutor
     * 
     * @param monitorableThreadPoolExecutor
     * @return
     */
    boolean register(MonitorableThreadPoolExecutorIntf monitorableThreadPoolExecutor);

    /**
     * 获取本对象所管理的线程池调度器
     * 
     * @return
     */
    Set<MonitorableThreadPoolExecutorIntf> getExecutors();
}
