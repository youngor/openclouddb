package com.talent.nio.handler.error.intf;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.talent.nio.communicate.ChannelContext;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public interface IOErrorHandlerIntf extends java.io.Serializable
{
    /**
     * 处理IO异常
     * 
     * @param ioe
     *            　异常对象
     * @param channelContext
     *            　
     * @param customMsg
     *            自定义的消息
     */
    void handle(SocketChannel socketChannel, IOException ioe, ChannelContext channelContext, String customMsg);
}
