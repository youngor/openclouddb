/**
 * 
 */
package com.talent.balance.backend.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.conf.BackendServerConf;
import com.talent.nio.communicate.ChannelContext;

/**
 * 
 * @filename:	 com.talent.balance.backend.handler.BackendChannelContextExt
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月21日 下午3:35:09
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
public class BackendExt
{
	public static final String PROTOCOL_MYSQL = "mysql";
	
	
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(BackendExt.class);

	private static final String FRONTEND_CHANNEL_CONTEXT_KEY = "FRONTEND_CHANNEL_CONTEXT_KEY";
	
	private static final String BACKEND_SERVER_KEY = "BACKEND_SERVER_KEY";
	
	
	public static void setBackendServer(ChannelContext backendChannelContext, BackendServerConf backendServer)
	{
		backendChannelContext.addProperty(BACKEND_SERVER_KEY, backendServer);
	}

	public static BackendServerConf getBackendServer(ChannelContext backendChannelContext)
	{
		return (BackendServerConf) backendChannelContext.getProperty(BACKEND_SERVER_KEY);
	}
	
	public static void removeBackendServer(ChannelContext backendChannelContext)
	{
		backendChannelContext.removeProperty(BACKEND_SERVER_KEY);
	}
	

	public static void setFrontend(ChannelContext backendChannelContext, ChannelContext frontendChannelContext)
	{
		backendChannelContext.addProperty(FRONTEND_CHANNEL_CONTEXT_KEY, frontendChannelContext);
	}

	public static ChannelContext getFrontend(ChannelContext backendChannelContext)
	{
		return (ChannelContext) backendChannelContext.getProperty(FRONTEND_CHANNEL_CONTEXT_KEY);
	}
	
	public static void removeFrontend(ChannelContext backendChannelContext)
	{
		backendChannelContext.removeProperty(FRONTEND_CHANNEL_CONTEXT_KEY);
	}

	/**
	 * 
	 */
	public BackendExt()
	{
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		
	}
}
