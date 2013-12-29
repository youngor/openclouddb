/**
 * 
 */
package com.talent.platform.threadpool;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.platform.threadpool.intf.SynRunnableIntf;

/**
 * 默认的RejectedExecutionHandler实现<br>
 * 如果Runnable提交被拒绝，本拒绝处理器会将Runnable放到一个队列中，并延时将该Runnable提交给ThreadPool执行。
 * 
 * @filename:	 com.talent.platform.threadpool.DefaultRejectedExecutionHandler
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年10月18日 上午10:05:16
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年10月18日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class DefaultRejectedExecutionHandler implements RejectedExecutionHandler
{
	private static Logger log = LoggerFactory.getLogger(DefaultRejectedExecutionHandler.class);

	private static Timer timer = new Timer(DefaultRejectedExecutionHandler.class.getSimpleName() + "-Timer", true);
	private MyTimerTask myTimerTask = new MyTimerTask();

	public MyTimerTask getMyTimerTask()
	{
		return myTimerTask;
	}

	public void setMyTimerTask(MyTimerTask myTimerTask)
	{
		this.myTimerTask = myTimerTask;
	}

	/**
	 * 
	 */
	public DefaultRejectedExecutionHandler(SynThreadPoolExecutor<?> synThreadPoolExecutor)
	{
		myTimerTask.executor = synThreadPoolExecutor;
		timer.schedule(myTimerTask, 1000);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
	{
		LinkedBlockingQueue<Runnable> queue = myTimerTask.getQueue();
		if (!queue.contains(r))
		{
			if (r instanceof SynRunnableIntf)
			{
				((SynRunnableIntf) r).setInSchedule(false);
			}
			queue.add(r);
		} else
		{
			log.info("{} has contained in queue, queue size is {}", r, queue.size());
		}

		log.warn("{} is rejected, {} tasks is waiting!", r.getClass().getSimpleName(), queue.size());
	}

	public static class MyTimerTask extends TimerTask
	{
		LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor executor = null;

		public LinkedBlockingQueue<Runnable> getQueue()
		{
			return queue;
		}

		public void setQueue(LinkedBlockingQueue<Runnable> queue)
		{
			this.queue = queue;
		}

		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					Runnable r = queue.poll(20, TimeUnit.SECONDS);
					if (r != null)
					{
						executor.execute(r);
						log.warn("submit a runnable, {} runnables waiting for submit", queue.size());
						if (queue.size() < 20)
						{
							Thread.sleep(50);
						} else
						{
							Thread.sleep(1);
						}
					}
				} catch (java.lang.Throwable e)
				{
					log.error(e.getMessage(), e);
				}
			}
		}
	}
}
