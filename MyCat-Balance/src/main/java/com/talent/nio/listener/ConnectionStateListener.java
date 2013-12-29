/**
 * 
 */
package com.talent.nio.listener;

import com.talent.nio.communicate.ChannelContext;

/**
 * 
 * @filename:	 com.talent.nio.listener.ConnectionStateListener
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月18日 下午4:55:19
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年12月18日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public interface ConnectionStateListener
{
	/**
	 * 未连接
	 * @param channelContext
	 */
	void onTcpOff(ChannelContext channelContext) throws Exception;

	/**
	 * TCP正在建链
	 * @param channelContext
	 */
	void onTcpBuilding(ChannelContext channelContext) throws Exception;

	/**
	 * TCP层已经连上链路
	 * @param channelContext
	 * @throws Exception 
	 */
	void onTcpOn(ChannelContext channelContext) throws Exception;

	/**
	 * TCP建链失败
	 * @param channelContext
	 */
	void onTcpLinkFailed(ChannelContext channelContext) throws Exception;

	/**
	 * 应用层链路断开
	 * @param channelContext
	 */
	void onAppOff(ChannelContext channelContext) throws Exception;

	/**
	 * 应用层正在建链
	 * @param channelContext
	 */
	void onAppBuilding(ChannelContext channelContext) throws Exception;

	/**
	 * 应用层已经连上链路
	 * @param channelContext
	 */
	void onAppOn(ChannelContext channelContext) throws Exception;

	/**
	 * 应用层建链失败
	 * @param channelContext
	 */
	void onAppLinkFailed(ChannelContext channelContext) throws Exception;

	/**
	 * 正在注销链路
	 * @param channelContext
	 */
	void onLogouting(ChannelContext channelContext) throws Exception;
	
	void onRemoved(ChannelContext channelContext) throws Exception;
}
