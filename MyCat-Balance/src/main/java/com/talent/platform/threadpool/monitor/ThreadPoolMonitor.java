/**
 * 
 */
package com.talent.platform.threadpool.monitor;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.talent.platform.threadpool.monitor.intf.MonitorableThreadPoolExecutorIntf;
import com.talent.platform.threadpool.monitor.intf.ThreadPoolMonitorIntf;

/**
 * 
 * @author 谭耀武
 * @date 2012-1-4
 *
 */
public class ThreadPoolMonitor implements ThreadPoolMonitorIntf
{

    private Set<MonitorableThreadPoolExecutorIntf> executorSet = Collections
            .synchronizedSet(new TreeSet<MonitorableThreadPoolExecutorIntf>());

    private static ThreadPoolMonitor instance = new ThreadPoolMonitor();

    /**
     * 暂时采用单态实例，以后有需要可以采用工厂模式
     * 
     * @return
     */
    public static ThreadPoolMonitor getInstance()
    {
        return instance;
    }

    /**
     * 
     */
    private ThreadPoolMonitor()
    {

    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    @Override
    public boolean register(MonitorableThreadPoolExecutorIntf executor)
    {
        return executorSet.add(executor);
    }

    @Override
    public Set<MonitorableThreadPoolExecutorIntf> getExecutors()
    {
        return executorSet;
    }
}
