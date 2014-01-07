/**
 * 
 */
package com.talent.mysql.packet.factory;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.mysql.packet.factory.MysqlHeaderFactory.MysqlHeader;

/**
 * 
 * @filename:	 com.talent.mysql.packet.factory.MysqlHeaderPoolFactory
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月27日 上午9:47:24
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年12月27日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class MysqlHeaderPoolFactory extends BasePooledObjectFactory<MysqlHeader>
{
	private static Logger log = LoggerFactory.getLogger(MysqlHeaderPoolFactory.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

	@Override
	public MysqlHeader create() throws Exception
	{
		return new MysqlHeader();
	}

	@Override
	public PooledObject<MysqlHeader> wrap(MysqlHeader mysqlHeader)
	{
		return new DefaultPooledObject<MysqlHeader>(mysqlHeader);
	}
}
