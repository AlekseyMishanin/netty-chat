package org.netty;

import org.netty.chat.server.GroupChatServer;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        new GroupChatServer(8090).run();
    }
}