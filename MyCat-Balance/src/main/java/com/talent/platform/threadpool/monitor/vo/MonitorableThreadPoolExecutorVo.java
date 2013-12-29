/**
 * 
 */
package com.talent.platform.threadpool.monitor.vo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.talent.platform.threadpool.monitor.intf.MonitorableThreadPoolExecutorIntf;

/**
 * 
 * @author 谭耀武
 * @date 2012-1-4
 *
 */
public class MonitorableThreadPoolExecutorVo implements MonitorableThreadPoolExecutorIntf
{

    private String name = null;
    private int corePoolSize = 0;
    private int maximumPoolSize = 0;
    private int poolSize = 0;
    private int activeCount = 0;
    private int largestPoolSize = 0;
    private long completedTaskCount = 0;
    private BlockingQueue<Runnable> queue = null;

    private static Map<MonitorableThreadPoolExecutorIntf, MonitorableThreadPoolExecutorVo> mapOfClassNameAndExecutor = new HashMap<MonitorableThreadPoolExecutorIntf, MonitorableThreadPoolExecutorVo>();

    /**
     * 
     */
    private MonitorableThreadPoolExecutorVo()
    {

    }

    public static MonitorableThreadPoolExecutorVo getInstance(MonitorableThreadPoolExecutorIntf monitorableThreadPoolExecutor)
    {
        MonitorableThreadPoolExecutorVo executor = mapOfClassNameAndExecutor.get(monitorableThreadPoolExecutor);
        if (executor == null)
        {
            executor = new MonitorableThreadPoolExecutorVo();
            mapOfClassNameAndExecutor.put(monitorableThreadPoolExecutor, executor);
        }
        return executor;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    public MonitorableThreadPoolExecutorVo setValueFromOther(MonitorableThreadPoolExecutorIntf monitorableThreadPoolExecutor)
    {
        this.setActiveCount(monitorableThreadPoolExecutor.getActiveCount());
        this.setCompletedTaskCount(monitorableThreadPoolExecutor.getCompletedTaskCount());
        this.setCorePoolSize(monitorableThreadPoolExecutor.getCorePoolSize());
        this.setLargestPoolSize(monitorableThreadPoolExecutor.getLargestPoolSize());
        this.setMaximumPoolSize(monitorableThreadPoolExecutor.getMaximumPoolSize());
        this.setName(monitorableThreadPoolExecutor.getName());
        this.setPoolSize(monitorableThreadPoolExecutor.getPoolSize());
        // this.setQueue(monitorableThreadPoolExecutor.getQueue());
        return this;
    }

    @Override
    public void execute(Runnable command)
    {
        throw new RuntimeException(MonitorableThreadPoolExecutorVo.class.getName() + " is not implement this method");
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getCorePoolSize()
    {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize)
    {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize()
    {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize)
    {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getPoolSize()
    {
        return poolSize;
    }

    public void setPoolSize(int poolSize)
    {
        this.poolSize = poolSize;
    }

    public int getActiveCount()
    {
        return activeCount;
    }

    public void setActiveCount(int activeCount)
    {
        this.activeCount = activeCount;
    }

    public int getLargestPoolSize()
    {
        return largestPoolSize;
    }

    public void setLargestPoolSize(int largestPoolSize)
    {
        this.largestPoolSize = largestPoolSize;
    }

    public long getCompletedTaskCount()
    {
        return completedTaskCount;
    }

    public void setCompletedTaskCount(long completedTaskCount)
    {
        this.completedTaskCount = completedTaskCount;
    }

    public BlockingQueue<Runnable> getQueue()
    {
        return queue;
    }

    public void setQueue(BlockingQueue<Runnable> queue)
    {
        this.queue = queue;
    }

    @Override
    public int compareTo(Object o)
    {
        MonitorableThreadPoolExecutorVo other = (MonitorableThreadPoolExecutorVo) o;
        return this.getName().compareTo(other.getName());
    }
}
