/**
 * 
 */
package com.talent.nio.handler.error.client;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.handler.error.intf.ReadIOErrorHandlerIntf;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public class ReadIOErrorHandler implements ReadIOErrorHandlerIntf
{

    /**
     * 
     */
    private static final long serialVersionUID = -8228728044325607887L;

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ReadIOErrorHandler.class);

    private static ReadIOErrorHandler instance = null;

    public static ReadIOErrorHandler getInstance()
    {
        if (instance == null)
        {
            instance = new ReadIOErrorHandler();
        }
        return instance;
    }

    /**
     * 
     */
    protected ReadIOErrorHandler()
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
            customMsg1 = "ioexception occured when reading";
        }
        if (channelContext != null)
        {
            channelContext.getStatVo().getCountOfReadException().incrementAndGet();// .addReadExceptionTimes();
        }
        DefaultIOErrorHandler.getInstance().handle(socketChannel, e, channelContext, customMsg1);

    }
}
