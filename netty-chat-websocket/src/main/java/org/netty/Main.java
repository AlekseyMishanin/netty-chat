package org.netty;

import org.netty.websocket.WebSocketChatServer;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        new WebSocketChatServer(8090).run();
    }
}