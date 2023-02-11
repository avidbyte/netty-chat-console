package com.inst.cloud.chat.common.protocol;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;

import java.util.List;

/**
 * 自定义 IMP 的 编码器
 * 主要功能是接收消息后对自定义协议内容进行解码处理
 *
 * @author aaron
 * @since 2022-12-30 11:32
 */

public class IMDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            //先获取可读字节数
            final int length = in.readableBytes();
            final byte[] array = new byte[length];
            String content = new String(array, in.readerIndex(), length);
            //空消息不解析
            if (!"".equals(content.trim())) {
                ctx.channel().pipeline().remove(this);
                return;
            }
            in.getBytes(in.readerIndex(), array, 0, length);
            out.add(new MessagePack().read(array, IMMessage.class));
            in.clear();
        } catch (MessageTypeException e) {
            ctx.channel().pipeline().remove(this);
        }
    }

}
