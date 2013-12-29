/**
 * 
 */
package com.talent.platform.threadpool.monitor.vo;

import java.util.concurrent.atomic.AtomicLong;

import com.talent.platform.threadpool.monitor.intf.MonitorableRunnableIntf;

/**
 * 
 * @author 谭耀武
 * @date 2012-1-4
 *
 */
public class MonitorableRunnableVo implements MonitorableRunnableIntf, java.io.Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -928730596551325312L;

    private String currentProcessor = null;

    /**
     * 
     */
    public MonitorableRunnableVo()
    {

    }

    /**
     * 本任务已经被提交的执行次数
     */
    private AtomicLong submitCount = new AtomicLong();

    /**
     * 本任务被执行的有效次数
     */
    private AtomicLong executeCount = new AtomicLong();

    /**
     * 本任务名字
     */
    private String runnableName = null;

    /**
     * 是否正在执行，true:正在执行，false：不是正在执行
     */
    private boolean isRunning = false;

    /**
     * 是否在执行列表中，true:是的，false:没有纳入计划执行列表
     */
    private boolean isInSchedule = false;

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    @Override
    public AtomicLong getSubmitCount()
    {
        return submitCount;
    }

    public void setRunnableName(String runnableName)
    {
        this.runnableName = runnableName;
    }

    @Override
    public String getRunnableName()
    {
        return runnableName;
    }

    @Override
    public void setRunning(boolean isRunning)
    {
        this.isRunning = isRunning;
    }

    @Override
    public boolean isRunning()
    {
        return isRunning;
    }

    @Override
    public void setInSchedule(boolean isInSchedule)
    {
        this.isInSchedule = isInSchedule;
    }

    @Override
    public boolean isInSchedule()
    {
        return isInSchedule;
    }

    @Override
    public AtomicLong getProcessedMsgCount()
    {
        return executeCount;
    }

    public void setSubmitCount(AtomicLong submitCount)
    {
        this.submitCount = submitCount;
    }

    public void setExecuteCount(AtomicLong executeCount)
    {
        this.executeCount = executeCount;
    }

    @Override
    public String getCurrentProcessor()
    {
        return currentProcessor;
    }

    @Override
    public void run()
    {
        throw new RuntimeException("this object is not implement this method");
    }

    public MonitorableRunnableVo setValueFromOther(MonitorableRunnableIntf monitorableTask)
    {
        this.currentProcessor = monitorableTask.getCurrentProcessor();
        this.setInSchedule(monitorableTask.isInSchedule());
        this.setRunning(monitorableTask.isRunning());
        this.setRunnableName(monitorableTask.getRunnableName());
        this.setExecuteCount(monitorableTask.getProcessedMsgCount());
        this.setSubmitCount(monitorableTask.getSubmitCount());
        return this;
    }

}
