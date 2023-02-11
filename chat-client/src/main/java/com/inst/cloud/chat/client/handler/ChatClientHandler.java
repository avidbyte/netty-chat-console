package com.inst.cloud.chat.client.handler;


import com.inst.cloud.chat.common.protocol.IMMessage;
import com.inst.cloud.chat.common.protocol.IMP;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.util.Scanner;

/**
 * 聊天客户端逻辑实现
 *
 * @author aaron
 * @since 2023-01-01 13:47
 */
public class ChatClientHandler extends SimpleChannelInboundHandler<IMMessage> {

    private ChannelHandlerContext ctx;
    private String nickName;

    public ChatClientHandler(String nickName) {
        this.nickName = nickName;
    }

    public ChatClientHandler() {
    }

    /**
     * 启动客户端控制台
     *
     * @throws IOException
     */
    private void session() throws IOException {
        new Thread() {
            @Override
            public void run() {
                if (!checkNickName(nickName)) {
                    System.out.println(nickName + ",你好，请在控制台输入对话内容");

                }
                IMMessage message = null;
                Scanner scanner = new Scanner(System.in);
                do {
                    if (scanner.hasNext()) {
                        String input = scanner.nextLine();
                        if (checkNickName(nickName)) {
                            nickName = input;
                            message = new IMMessage(IMP.LOGIN.getName(), "Console", System.currentTimeMillis(), nickName, "login");
                        } else if ("exit".equals(input)) {
                            message = new IMMessage(IMP.LOGOUT.getName(), "Console", System.currentTimeMillis(), nickName, "exit");
                        } else {
                            message = new IMMessage(IMP.CHAT.getName(), "Console", System.currentTimeMillis(), nickName, input);
                        }
                    }
                }
                while (sendMsg(message));
                scanner.close();
            }
        }.start();
    }


    /**
     * TCP 链路建立成功后调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
//        IMMessage message = new IMMessage(IMP.LOGIN.getName(), "Console", System.currentTimeMillis(), this.nickName, "login");
//        sendMsg(message);
        System.out.println("成功连接服务器,你好请输入昵称：");
        session();
    }


    private boolean checkNickName(String nickName) {
        return nickName == null || "".equals(nickName);
    }

    /**
     * 发送消息
     *
     * @param msg
     * @return
     */
    private boolean sendMsg(IMMessage msg) {
        ctx.channel().writeAndFlush(msg);
        boolean status = msg.getCmd().equals(IMP.LOGOUT.getName());
        if(status){
            System.exit(0);
        }
        return true;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws IOException {
        if(msg.getCmd().equals(IMP.SYSTEM.getName())){
            System.out.println(msg.getContent());
        }
        if(msg.getCmd().equals(IMP.CHAT.getName())){
            System.out.println(msg.getSender() + "：" + msg.getContent());
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("与服务器断开连接:" + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
