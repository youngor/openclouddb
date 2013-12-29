package com.talent.nio.handler.error.intf;

import java.nio.channels.SocketChannel;

import com.talent.nio.communicate.ChannelContext;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public interface ErrorPackageHandlerIntf
{
    /**
     * 返回参数channelContext对应链路的错误包的个数
     * 
     * @author tanyaowu
     * @param socketChannel
     * @param channelContext
     * @param errorReason
     * @return
     */
    int handle(SocketChannel socketChannel, ChannelContext channelContext, String errorReason);
}
