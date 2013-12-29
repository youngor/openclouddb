/**
 * 
 */
package com.talent.platform.threadpool.quickstart;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import com.talent.platform.threadpool.AbstractQueueRunnable;

/**
 * @filename: com.talent.platform.threadpool.demo.QuickStartRunnable
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
 * @param <T> 队列中存的数据类型
 */
public class QuickStartRunnable<T> extends AbstractQueueRunnable<T>
{
    private static java.util.concurrent.atomic.AtomicLong atomicLong = new AtomicLong();

    @Override
    public void run()
    {
        checkSyn();

        T t = null;
        while ((t = this.getMsgQueue().poll()) != null)
        {
            System.out.println(t.toString() + "--" + atomicLong.incrementAndGet());
        }
    }

    /**
     * 检查线程池是不是同步调用runnable的(在同一时刻，只有同一个runnable对象被调用)。
     */
    private static void checkSyn()
    {
        String threadName = Thread.currentThread().getName();
        File dir = new File("d:/log/dfd/");
        dir.mkdirs();
        File f = new File(dir, threadName + ".txt");
        if (!f.exists())
        {
            try
            {
                f.createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
