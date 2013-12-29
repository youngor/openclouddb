/**
 * 
 */
package com.talent.platform.threadpool.demo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.platform.threadpool.AbstractQueueRunnable;

/**
 * 
 * @filename: com.talent.platform.threadpool.demo.DemoRunnable
 * @copyright: Copyright (c)2012
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2012-4-27 上午9:39:46
 * @record <table cellPadding="3" cellSpacing="0" style="width:600px">
 *         <thead style="font-weight:bold;background-color:#e3e197">
 *         <tr>
 *         <td>date</td>
 *         <td>author</td>
 *         <td>version</td>
 *         <td>description</td>
 *         </tr>
 *         </thead> <tbody style="background-color:#ffffeb">
 *         <tr>
 *         <td>2012-4-27</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class DemoRunnable<T> extends AbstractQueueRunnable<T>
{

    private long total = 0;
    AtomicLong count = new AtomicLong();
    private static AtomicLong counts = new AtomicLong();
    CyclicBarrier barrier;
    private static Logger log = LoggerFactory.getLogger(DemoRunnable.class);

    public DemoRunnable(long total, CyclicBarrier barrier)
    {
        this.total = total;
        this.barrier = barrier;
    }

    @Override
    public String getCurrentProcessor()
    {
        return this.getClass().getName();
    }

    @Override
    public void run()
    {
        T t = null;
        while ((t = getMsgQueue().poll()) != null)
        {
            long ac = counts.incrementAndGet();

            log.debug(t.toString() + "-----" + String.valueOf(ac));
            long c = count.incrementAndGet();
            if (c == total)
            {
                try
                {
                    barrier.await();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                } catch (BrokenBarrierException e)
                {
                    e.printStackTrace();
                }
            }

        }

    }

}
