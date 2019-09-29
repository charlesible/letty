package com.bfbm.netty.protocol.json.protocol;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.List;

public class RpcDecoderByLengthField extends LengthFieldBasedFrameDecoder {

    //目标对象类型进行解码
    private Class<?> target;

    public RpcDecoderByLengthField(Class target) {
        super(1024 * 1024 * 1024, 0, 4, 0, 4);
        this.target = target;
    }


    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        System.out.println("RpcDecoderByLengthField......" + target.getName() + "...");
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        byte[] data = new byte[frame.readableBytes()];
        frame.readBytes(data);
        //将byte数据转化为我们需要的对象
        Object obj = JSON.parseObject(data, target);
        return obj;
    }
}
