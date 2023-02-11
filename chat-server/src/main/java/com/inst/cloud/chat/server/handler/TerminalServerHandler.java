package com.inst.cloud.chat.server.handler;



import com.inst.cloud.chat.common.protocol.IMMessage;
import com.inst.cloud.chat.server.processor.MsgProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 *  用于处理Java控制台发过来的 Java Object 消息体
 * @author aaron
 * @since 2022-12-31 21:11
 */

public class TerminalServerHandler extends SimpleChannelInboundHandler<IMMessage> {

    private MsgProcessor processor = new MsgProcessor();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws Exception {
        processor.mainProcess(ctx.channel(),msg);
    }

    /**
     * 异常处理
     * @param ctx ctx
     * @param cause cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        System.out.println("Socket Client: 与客户端断开连接:" + cause.getMessage());
        cause.printStackTrace();
        processor.logout(ctx.channel());
        ctx.close();
    }
}
