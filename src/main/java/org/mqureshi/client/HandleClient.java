package org.mqureshi.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class HandleClient {

    private Channel udpChannel;
    private EventLoopGroup group;

    public Channel startClient(InetSocketAddress serverAddress) {
        group = new NioEventLoopGroup();
        udpChannel = null;
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
                            String message = packet.content().toString(CharsetUtil.UTF_8);
                            System.out.println("Received from server: " + message);
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                            cause.printStackTrace();
                        }
                    });

            udpChannel = bootstrap.bind(0).sync().channel();

            // Send a connection message to the server
            udpChannel.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("Client connected", CharsetUtil.UTF_8),
                    serverAddress
            )).sync();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return udpChannel;
    }

    // This method cleans up both the UDP channel and EventLoopGroup
    public void stopClient() {
        if (udpChannel != null) {
            udpChannel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }
}
