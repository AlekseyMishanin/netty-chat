package org.netty.chat.server.handlers;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.netty.chat.server.events.LiveActivityEvent;

/**
 *
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        CHANNEL_GROUP.writeAndFlush("[system] %s joined to the chat room\n".formatted(channel.remoteAddress()));
        CHANNEL_GROUP.add(channel);
        System.out.println("The size of chat group is " + CHANNEL_GROUP.size());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        CHANNEL_GROUP.writeAndFlush("[system] %s left the chat room\n".formatted(channel.remoteAddress()));
        System.out.println("The size of chat group is " + CHANNEL_GROUP.size());
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

        CHANNEL_GROUP.forEach(channel -> {
            if (currentChannel != channel) {
                channel.writeAndFlush("[member] %s: %s".formatted(currentChannel.remoteAddress(), message));
            } else {
                channel.writeAndFlush("[I] send: %s".formatted(message));
            }
        });

        ctx.fireUserEventTriggered(new LiveActivityEvent());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
