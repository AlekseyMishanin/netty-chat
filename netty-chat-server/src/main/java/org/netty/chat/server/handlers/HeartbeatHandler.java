package org.netty.chat.server.handlers;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import org.netty.chat.server.events.LiveActivityEvent;

public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    private static final int HEARTBEATS_BEFORE_DISCONNECT = 2;

    private int currentHeartbeats = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent idleStateEvent) {
            switch (idleStateEvent.state()) {
                case READER_IDLE -> ++currentHeartbeats;
                case WRITER_IDLE, ALL_IDLE -> {
                    // ignore
                }
            }

            processHeartbeat(ctx);
        }

        if (evt instanceof LiveActivityEvent) {
            if (currentHeartbeats != 0) {
                currentHeartbeats = 0;
                ctx.channel().writeAndFlush("[system] welcome back");
            }
        }
    }

    private void processHeartbeat(ChannelHandlerContext ctx) {

        Channel channel = ctx.channel();

        switch (currentHeartbeats) {
            case 1 -> channel.writeAndFlush("[system] you are not active");
            case HEARTBEATS_BEFORE_DISCONNECT -> {
                channel.writeAndFlush("[system] you are not active too long and you are disconnected");
                ctx.close();
            }
        }
    }
}
