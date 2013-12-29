/**
 * 
 */
package com.talent.nio.communicate.monitor.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.talent.nio.api.Packet;

/**
 * 
 * @author 谭耀武
 * @date 2012-08-09
 * 
 */
public class PacketVo implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -7201421916136276481L;

    private Packet packet = null;

    private long sendOrRecvTime = 0;

    private String failReason = null;

    /**
     * 
     */
    public PacketVo()
    {

    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {

    }

    public void setSendOrRecvTime(long sendOrRecvTime)
    {
        this.sendOrRecvTime = sendOrRecvTime;
    }

    public long getSendOrRecvTime()
    {
        return sendOrRecvTime;
    }

    public void setPacket(Packet packet)
    {
        this.packet = packet;
    }

    public Packet getPacket()
    {
        return packet;
    }

    public void setFailReason(String failReason)
    {
        this.failReason = failReason;
    }

    public String getFailReason()
    {
        return failReason;
    }

    @Override
    public String toString()
    {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder builder = new StringBuilder();
        builder.append("PacketPojo [packet=").append(packet).append(", sendOrRecvTime=").append(sdf.format(new Date(sendOrRecvTime)))
                .append(", failReason=").append(failReason).append("]");
        return builder.toString();
    }

    public static PacketVo createPacketVo(Packet packet, long sendOrRecvTime, String failReason)
    {
        PacketVo ret = new PacketVo();
        ret.setPacket(packet);
        ret.setFailReason(failReason);
        ret.setSendOrRecvTime(sendOrRecvTime);
        return ret;
    }
}
