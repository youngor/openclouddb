/**
 * 
 */
package com.talent.nio.communicate.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.handler.intf.PacketHandlerIntf;

/**
 * 支持同步发送的消息处理者
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public class PacketHandlerProxy implements PacketHandlerIntf
{
    private static final Logger log = LoggerFactory.getLogger(PacketHandlerProxy.class);

    private PacketHandlerIntf packetHandler = null;

    /**
     * key:序列号；value: Packet对象
     */
    private Map<String, Packet> mapOfSeqnoAndPacket = new ConcurrentHashMap<String, Packet>();

    /**
     * 是否包含指定的序列号
     * 
     * @param seqNo
     * @return
     */
    public boolean isContainSeqNo(String seqNo)
    {
        return mapOfSeqnoAndPacket.containsKey(seqNo);
    }

    /**
     * 删除序列号及其对应的消息
     * 
     * @param seqNo
     * @return
     */
    public Packet removeSynSeqNo(String seqNo)
    {
        return mapOfSeqnoAndPacket.remove(seqNo);
    }

    public PacketHandlerProxy(PacketHandlerIntf packetHandler)
    {
        super();
        this.packetHandler = packetHandler;
    }

    /**
     * 通知消息等待者
     * 
     * @param receivedPacket
     */
    public void notifyPacketWaiter(Packet receivedPacket)
    {
        if (receivedPacket == null)
        {
            log.error("receivedPacket is null");
            return;
        }

        String seqNo = receivedPacket.getSeqNo();
        Packet initPacket = mapOfSeqnoAndPacket.remove(seqNo);

        if (initPacket != null)
        {
            try
            {
                mapOfSeqnoAndPacket.put(seqNo, receivedPacket);
                synchronized (initPacket)
                {
                    initPacket.notify();
                }
            } catch (Exception e)
            {
                log.error("", e);
            } finally
            {
                // synMessageMap.remove(serialNo);
            }
        } else
        {
            log.error("initPacket is null! seqNo is {}", seqNo);
        }
    }

    @Override
    public void onReceived(Packet packet, ChannelContext channelContext) throws Exception
    {
        if (StringUtils.isNotBlank(packet.getSeqNo()) && mapOfSeqnoAndPacket.containsKey(packet.getSeqNo()))
        {
            try
            {
                packetHandler.onReceived(packet, channelContext);
            } catch (Exception e)
            {
                throw new RuntimeException(e.getMessage(), e);
            } finally
            {
                if (mapOfSeqnoAndPacket.containsKey(packet.getSeqNo()))
                {
                    notifyPacketWaiter(packet);
                }
            }
        } else
        {
            packetHandler.onReceived(packet, channelContext);
        }

    }

    @Override
    public byte[] onSend(Packet packet, ChannelContext channelContext) throws Exception
    {
        if (packet.isSyn())
        {
            Packet sp = (Packet) packet;
            mapOfSeqnoAndPacket.put(sp.getSeqNo(), sp);
        }
        return packetHandler.onSend(packet, channelContext);

    }
}
