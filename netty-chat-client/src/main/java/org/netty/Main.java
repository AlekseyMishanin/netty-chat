package org.netty;

import org.netty.chat.client.GroupChatClient;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        new GroupChatClient("127.0.0.1", 8090).run();
    }
}