/**
 * 
 */
package com.talent.platform.threadpool;

import com.talent.platform.threadpool.intf.SynRunnableIntf;

/**
 * 
 * 
 * @filename:	 com.talent.platform.threadpool.AbstractSynRunnable
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2012-5-18 下午3:30:30
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2012-5-18</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public abstract class AbstractSynRunnable implements SynRunnableIntf
{
    protected AbstractSynRunnable()
    {

    }

    /**
     * 是否正在执行，true:正在执行，false：不是正在执行
     */
    private boolean isRunning = false;

    /**
     * 是否在执行列表中，true:是的，false:没有纳入计划执行列表
     */
    private boolean isInSchedule = false;

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

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }
}
