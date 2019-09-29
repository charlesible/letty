package com.bfbm.netty.protocol.json.client;

import com.bfbm.netty.protocol.json.protocol.RpcRequest;
import io.netty.channel.Channel;

import java.util.UUID;


public class ClientStartup {
    public static void main(String[] args) throws Exception {
        NettyClient client = new NettyClient("127.0.0.1", 8080);
        //启动client服务
        client.start();

        Channel channel = client.getChannel();

        //channel对象可保存在map中，供其它地方发送消息
        for (int i = 0; i < 1; i++) {
            //System.out.println("i=" + i);
            RpcRequest request = new RpcRequest();
            request.setId(UUID.randomUUID().toString());
            request.setData("client.message");
            request.setId(UUID.randomUUID().toString() + "_" + i);
            channel.writeAndFlush(request);
        }
    }
}
