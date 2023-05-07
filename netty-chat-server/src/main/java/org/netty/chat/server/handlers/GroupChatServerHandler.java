package org.netty.chat.server.handlers;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 *
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        channelGroup.writeAndFlush("[system] %s joined to the chat room\n".formatted(channel.remoteAddress()));
        channelGroup.add(channel);
        System.out.println("The size of chat group is " + channelGroup.size());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        channelGroup.writeAndFlush("[system] %s left the chat room\n".formatted(channel.remoteAddress()));
        System.out.println("The size of chat group is " + channelGroup.size());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " is active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " is inactive");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        Channel currentChannel = ctx.channel();

        channelGroup.forEach(channel -> {
            if (currentChannel != channel) {
                channel.writeAndFlush("[member] %s: %s\n".formatted(currentChannel.remoteAddress(), message));
            } else {
                channel.writeAndFlush("[I] send %s\n".formatted(message));
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
