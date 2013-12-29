/**
 * 
 */
package com.talent.nio.communicate.server;

import com.talent.nio.communicate.ChannelContext;

/**
 * 
 * @filename:	 com.talent.nio.communicate.server.ChannelContextCompleter
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月21日 下午2:59:51
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年12月21日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public interface ChannelContextCompleter
{
	
	/**
	 * 建链后，再完善一下ChannelContext
	 * @param channelContext
	 */
	void complete(ChannelContext channelContext);
}
