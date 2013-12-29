/**
 * 
 */
package com.talent.balance.common;

import io.netty.buffer.ByteBuf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @filename:	 com.talent.balance.common.ParseUtils
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月20日 上午9:50:46
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年12月20日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class ParseUtils
{
	private static Logger log = LoggerFactory.getLogger(ParseUtils.class);

	/**
	 * 
	 */
	public ParseUtils()
	{
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		
	}

	public static int processReadIndex(ByteBuf buffer)
	{
		int newReaderIndex = buffer.readerIndex();
		if (newReaderIndex < (buffer.capacity()))
		{
			buffer.readerIndex(newReaderIndex + 1);
			return 1;
		}
		return 0;
	}
}
