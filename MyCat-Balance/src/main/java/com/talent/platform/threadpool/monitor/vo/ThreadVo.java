/**
 * 
 */
package com.talent.platform.threadpool.monitor.vo;

/**
 * 
 * @author 谭耀武
 * @date 2012-1-4
 *
 */
public class ThreadVo implements java.io.Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 84686040098906523L;
    /**
     * 线程名字
     */
    private String name = null;
    /**
     * 线程标识
     */
    private long id = 0;
    /**
     * 线程所耗CPU时间
     */
    private long cpuTime = 0;

    /**
     * @return the cpuTime
     */
    public long getCpuTime()
    {
        return cpuTime;
    }

    /**
     * @param cpuTime
     *            the cpuTime to set
     */
    public void setCpuTime(long cpuTime)
    {
        this.cpuTime = cpuTime;
    }

    /**
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
}
