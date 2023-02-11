package com.inst.cloud.chat.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * 主要功能是发送消息，对自定义协议内容进行编码处理
 * 自定义 IMP 的编码器
 *
 * @author aaron
 * @since 2022-12-31 20:38
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, IMMessage msg, ByteBuf out)
            throws Exception {
        out.writeBytes(new MessagePack().write(msg));
    }

}
