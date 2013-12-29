/**
 * 
 */
package com.talent.nio.page;

/**
 * 
 * 
 * @filename: com.talent.platform.core.vo.AjaxRespPojo
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-5-18 下午2:12:26
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
 *         <td>2013-5-18</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class AjaxRespPojo
{

    public AjaxRespPojo(int result, String title, String msg, Object data)
    {
        super();
        this.result = result;
        this.title = title;
        this.msg = msg;
        this.data = data;
    }

    public AjaxRespPojo(int result, String title, String msg)
    {
        super();
        this.result = result;
        this.title = title;
        this.msg = msg;
    }

    /**
     * 0表示成功, 1表示异常
     */
    private int result;

    /**
	 * 
	 */
    private String title;

    /**
	 * 
	 */
    private String msg;

    /**
     * 
     */
    private Object data;

    /**
	 * 
	 */
    public AjaxRespPojo()
    {
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    public int getResult()
    {
        return result;
    }

    public void setResult(int result)
    {
        this.result = result;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Object getData()
    {
        return data;
    }

    public void setData(Object data)
    {
        this.data = data;
    }

}
