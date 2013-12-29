package com.talent.nio.handler.error.server;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.util.NioUtils;
import com.talent.nio.handler.error.intf.ErrorPackageHandlerIntf;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public class DefaultErrorPackageHandler implements ErrorPackageHandlerIntf
{
    private static final int MAX_COUNT = 1;

    private static final Logger log = LoggerFactory.getLogger(DefaultErrorPackageHandler.class);

    private static DefaultErrorPackageHandler instance = new DefaultErrorPackageHandler();

    public static DefaultErrorPackageHandler getInstance()
    {
        return instance;
    }

    private DefaultErrorPackageHandler()
    {

    }

    @Override
    public int handle(SocketChannel socketChannel, ChannelContext channelContext, String errorReason)
    {
        channelContext.getStatVo().getCountOfErrorPackage().incrementAndGet();
        log.error("[" + "] received error package, reason is " + errorReason);

        if (channelContext.getStatVo().getCountOfErrorPackage().get() >= MAX_COUNT)
        {
            channelContext.getStatVo().getCountOfErrorPackage().set(0);
            NioUtils.remove(channelContext, "received an error package");
        }
        return (int) channelContext.getStatVo().getCountOfErrorPackage().get();
    }
}
