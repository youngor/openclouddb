/**
 * 
 */
package com.talent.nio.communicate.handler.intf;

import com.talent.nio.api.Packet;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public interface MessageChangeListener
{
    Object onMessage(Packet packet) throws Exception;
}
