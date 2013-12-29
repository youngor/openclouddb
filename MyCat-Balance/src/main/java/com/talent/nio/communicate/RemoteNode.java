package com.talent.nio.communicate;

/**
 * 远程节点的信息
 * 
 * @author 谭耀武
 * @date 2011-12-23
 * 
 */
public class RemoteNode
{
    private String ip;
    private int port;

    public RemoteNode(String ip, int port)
    {
        super();

        this.setIp(ip);
        this.setPort(port);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(ip).append(":").append(port);
        return builder.toString();
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException
    {

        java.lang.Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                // 在退出JVM时要做的事
                System.out.println("在退出JVM时要做的事");

            }
        });
        System.exit(0);
        Thread.sleep(1000000);

    }

}
