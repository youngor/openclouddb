/**
 * 
 */
package com.talent.nio.handler.error.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.util.NioUtils;
import com.talent.nio.handler.error.intf.IOErrorHandlerIntf;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public class DefaultIOErrorHandler implements IOErrorHandlerIntf
{
    /**
     * 
     */
    private static final long serialVersionUID = 3266926133526484161L;

    private static final Logger log = LoggerFactory.getLogger(DefaultIOErrorHandler.class);

    private static DefaultIOErrorHandler instance = null;

    public static DefaultIOErrorHandler getInstance()
    {
        if (instance == null)
        {
            instance = new DefaultIOErrorHandler();
        }
        return instance;
    }

    /**
     * 
     */
    protected DefaultIOErrorHandler()
    {

    }

    @Override
    public void handle(SocketChannel socketChannel, IOException e, ChannelContext channelContext, String customMsg)
    {
        String reasonString = "";
        if (channelContext != null)
        {
            StringBuilder buffer = new StringBuilder();
            String _customMsg = customMsg == null ? "IOException" : customMsg;
            buffer.append(channelContext.getId() + " " + _customMsg);

            if (e != null)
            {
                log.error(buffer.toString(), e);
                reasonString = e.getMessage();
            } else
            {
                log.error(buffer.toString());
            }
        }
        NioUtils.remove(channelContext, reasonString);
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }
}
