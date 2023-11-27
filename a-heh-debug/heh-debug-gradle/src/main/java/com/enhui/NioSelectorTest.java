package com.enhui;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * kafka client 与 broker通信，使用了java原生的nio + 多路复用器技术 <br>
 * 所以需要回顾下nio的api
 */
public class NioSelectorTest {

    public static void main(String[] args) throws IOException {
        java.nio.channels.Selector nioSelector = java.nio.channels.Selector.open();

        SocketChannel socketChannel = SocketChannel.open();

        SelectionKey connectKey = socketChannel.register(nioSelector, SelectionKey.OP_CONNECT);
        SelectionKey readKey = socketChannel.register(nioSelector, SelectionKey.OP_READ);

        int selectType = 1;
        if (selectType == 1) {
            // 非阻塞立即返回
            nioSelector.selectNow();
        } else if (selectType == 2) {
            // 阻塞操作，在如下情景会返回：1、至少有一个channel事件；2、selector的wakeup方法被调用；3、线程interrupted；4、到达等待时间；
            nioSelector.select(3000);
        } else if (selectType == 3) {
            // 阻塞操作，在如下情景会返回：1、至少有一个channel事件；2、selector的wakeup方法被调用；3、线程interrupted；
            nioSelector.select();
        }


        Set<SelectionKey> readyKeys = nioSelector.selectedKeys();
    }
}
