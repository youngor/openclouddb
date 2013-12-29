/**
 * 
 */
package com.talent.nio.communicate.handler.intf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public abstract class BlockSupportPacketHandler implements PacketHandlerIntf
{
    private static final Logger log = LoggerFactory.getLogger(BlockSupportPacketHandler.class);

    private MessageChangeListener msgChangeListener;

    /**
     * key:序列号；value: Packet对象
     */
    private static Map<BlockSupportPacketHandler, Packet> mapOfProcessorAndPacket = new ConcurrentHashMap<BlockSupportPacketHandler, Packet>();

    private List<Packet> receivedPackets = new ArrayList<Packet>();

    private boolean isRunning = false;

    /**
     * 
     */
    public BlockSupportPacketHandler()
    {

    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    /**
     * 删除key
     * 
     * @param serialNo
     * @return
     */
    public static Packet removeKey(BlockSupportPacketHandler blockSupportpacketHandler)
    {
        return mapOfProcessorAndPacket.remove(blockSupportpacketHandler);
    }

    /**
     * 获取key
     * 
     * @param serialNo
     * @return
     */
    public static Packet getKey(BlockSupportPacketHandler blockSupportpacketHandler)
    {
        return mapOfProcessorAndPacket.get(blockSupportpacketHandler);
    }

    @Override
    /**
     * 
     */
    public byte[] onSend(Packet packet, ChannelContext channelContext)
    {
        this.setBlockCompleted(false);
        mapOfProcessorAndPacket.put(this, packet);
        receivedPackets.clear();
        return serialMessage(packet);
    }

    @Override
    public void onReceived(Packet packet, ChannelContext channelContext) throws Exception
    {
        if (packet == null)
        {
            log.info("received data is null, Refuse to distribute it");
        }
        MessageChangeListener msgChangeListener = this.getMsgChangeListener();
        Object obj = msgChangeListener.onMessage(packet);
        receivedPackets.add(packet);

        Packet afterProcessedPacket = null;
        try
        {
            afterProcessedPacket = this.processMessage(packet, obj);
        } catch (Exception e)
        {
            throw new RuntimeException(e.getMessage(), e);
        } finally
        {
            if (this.isBlockCompleted(packet))
            {
                notifyMessageWaiter(afterProcessedPacket, channelContext);
            }
        }
    }

    /**
     * 通知消息等待者
     * 
     * @param afterProcessedPacket
     */
    public void notifyMessageWaiter(Packet afterProcessedPacket, ChannelContext channelContext)
    {
        if (afterProcessedPacket == null)
        {
            log.error("afterProcessedPacket is null");
            return;
        }

        Packet initPacket = mapOfProcessorAndPacket.remove(this);

        if (initPacket != null)
        {
            try
            {
                mapOfProcessorAndPacket.put(this, afterProcessedPacket);
                synchronized (initPacket)
                {
                    initPacket.notify();
                }
            } catch (Exception e)
            {
                log.error(e.getMessage(), e);
            } finally
            {
                // synMessageMap.remove(serialNo);
            }
        } else
        {
            log.error("initPacket is null!{}", channelContext);
        }
    }

    /**
     * 收到消息后，进行业务处理
     * 
     * @param packet
     *            收到的packet对象
     * @param obj
     *            MsgChangeListener.onMessage()返回的对象
     * @return
     * @throws Exception
     */
    public abstract Packet processMessage(Packet packet, Object obj) throws Exception;

    /**
     * 在发送消息前，应用需要先将消息序列化成字节数组
     * 
     * @param packet
     * @return
     */
    public abstract byte[] serialMessage(Packet packet);

    /**
     * 阻塞发送是否已经完成
     * 
     * @param packet
     * @return true:已经完成
     */
    public abstract boolean isBlockCompleted(Packet packet);

    public abstract void setBlockCompleted(boolean isBlockCompleted);

    public void setRunning(boolean isRunning)
    {
        this.isRunning = isRunning;
    }

    public boolean isRunning()
    {
        return isRunning;
    }

    public void setMsgChangeListener(MessageChangeListener msgChangeListener)
    {
        this.msgChangeListener = msgChangeListener;
    }

    public MessageChangeListener getMsgChangeListener()
    {
        return msgChangeListener;
    }

    public void setReceivedPackets(List<Packet> receivedPackets)
    {
        this.receivedPackets = receivedPackets;
    }

    public List<Packet> getReceivedPackets()
    {
        return receivedPackets;
    }
}
