/*
 * Copyright
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bfbm.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

    private Selector selector;

    private ServerSocketChannel servChannel;

    private volatile boolean stop;

    /**
     * 初始化多路复用器、绑定监听端口
     *
     * @param port
     */
    public MultiplexerTimeServer(int port) {
        try {
            selector = Selector.open();
            servChannel = ServerSocketChannel.open();
            servChannel.configureBlocking(false);
            servChannel.socket().bind(new InetSocketAddress(port), 1024);

            //注册OP_ACCEPT
            servChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("The time server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null)
                                key.channel().close();
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        // 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
        if (selector != null)
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void handleInput(SelectionKey key) throws IOException {

        if (key.isValid()) {
            // 处理新接入的请求消息
            if (key.isAcceptable()) {
                // Accept the new connection
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                // Add the new connection to the selector
                sc.register(selector, SelectionKey.OP_READ);
                //System.out.println(sc.getLocalAddress() + "accept");
            }
            if (key.isReadable()) {
                // Read the data
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);

                int readBytes = sc.read(readBuffer);//把socketChannel中的数据写入 readFBuffer（写入模式）中
                if (readBytes > 0) {
                    //从readBuffer中 分析client传了什么信息
                    StringBuffer ss = new StringBuffer();

                    while (readBytes > 0) {
                        readBuffer.flip();//把readBuffer 从写的模式 转到 读的模式

                        byte[] bytes = new byte[readBuffer.remaining()];
                        readBuffer.get(bytes);

                        String body = new String(bytes, "UTF-8");
                        ss.append(body);

                        readBuffer.rewind();
                        readBytes = sc.read(readBuffer);//把socketChannel中的数据写入 readFBuffer（写入模式）中
                    }

                    System.out.println("The time server receive order : " + ss.toString());

                    int count = subStrCount(ss.toString(), "QUERY TIME ORDER");
                    System.out.println("the count of QUERY TIME ORDER is: " + count);


                    String currentTime = ss != null && ss.toString().contains("QUERY TIME ORDER") ? new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";

                    doWrite(sc, currentTime);

                } else if (readBytes < 0) {
                    // 对端链路关闭
                    key.cancel();
                    sc.close();
                } else
                    ; // 读到0字节，忽略
            }
        }
    }

    private void doWrite(SocketChannel channel, String response) throws IOException {
        if (response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);

            writeBuffer.flip();//writeBuffer 从写的模式 转到 读的模式

            channel.write(writeBuffer); //把writeBuffer中数据读到socketChannel
        }
    }

    private int subStrCount(String str, String subStr) {
        int i = str.length() - str.replace(subStr, "").length();
        int result = i / subStr.length();
        return result;
    }

}
