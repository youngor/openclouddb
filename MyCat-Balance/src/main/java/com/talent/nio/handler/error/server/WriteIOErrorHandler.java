/**
 * 
 */
package com.talent.nio.handler.error.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.handler.error.intf.WriteIOErrorHandlerIntf;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public class WriteIOErrorHandler implements WriteIOErrorHandlerIntf

{
    /**
     * 
     */
    private static final long serialVersionUID = 5503700166173408457L;

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(WriteIOErrorHandler.class);

    private static WriteIOErrorHandler instance = null;

    public static WriteIOErrorHandler getInstance()
    {
        if (instance == null)
        {
            instance = new WriteIOErrorHandler();
        }
        return instance;
    }

    /**
     * 
     */
    protected WriteIOErrorHandler()
    {

    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    @Override
    public void handle(SocketChannel socketChannel, IOException e, ChannelContext channelContext, String customMsg)
    {
        String customMsg1 = customMsg;
        if (customMsg1 == null || "".equals(customMsg1))
        {
            customMsg1 = "IOException occured when writing";
        }
        if (channelContext != null)
        {
            channelContext.getStatVo().getCountOfWriteException().incrementAndGet();// .addWriteExceptionTimes();
        }
        DefaultIOErrorHandler.getInstance().handle(socketChannel, e, channelContext, customMsg1);

    }

}
