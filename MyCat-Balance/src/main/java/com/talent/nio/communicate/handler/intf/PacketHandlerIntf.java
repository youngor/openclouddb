/**
 * 
 */
package com.talent.nio.communicate.handler.intf;

import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;

/**
 * 
 * @author 谭耀武
 * @date 2011-12-27
 * 
 */
public interface PacketHandlerIntf
{
    /**
     * 
     * 在发送消息前，将消息序列化（如果没有特殊需求，一般不需要实现此方法，直接return null即可。）
     * 
     * @param packet
     * @return
     * @throws Exception
     */
    byte[] onSend(Packet packet, ChannelContext channelContext) throws Exception;

    /**
     * 
     * 处理接收到的消息
     * 
     * @param packet
     * @throws Exception
     */
    void onReceived(Packet packet, ChannelContext channelContext) throws Exception;
}
