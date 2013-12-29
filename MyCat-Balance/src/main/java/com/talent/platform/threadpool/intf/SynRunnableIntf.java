/**
 * 
 */
package com.talent.platform.threadpool.intf;

/**
 * 
 * @filename:	 com.talent.platform.threadpool.intf.SynRunnableIntf
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2012-5-18 下午2:55:50
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
public interface SynRunnableIntf extends Runnable
{
    /**
     * 设置是否正在运行
     * 
     * @param isRunning
     */
    public void setRunning(boolean isRunning);

    /**
     * 任务是否正在运行
     * 
     * @return true:正在运行；false:未运行
     */
    public boolean isRunning();

    /**
     * 设置任务是否被添加到了执行日程。
     * 
     * @param isInSchedule
     *            true:已经添加到了执行日程，会在将来的某个时候执行；false:反之
     */
    public void setInSchedule(boolean isInSchedule);

    /**
     * 任务是否被添加到了执行日程
     * 
     * @return isInSchedule true:已经添加到了执行日程，会在将来的某个时候执行；false:反之
     */
    public boolean isInSchedule();
}
