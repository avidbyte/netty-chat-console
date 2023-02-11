package com.inst.cloud.chat.server;


import com.inst.cloud.chat.common.protocol.IMDecoder;
import com.inst.cloud.chat.common.protocol.IMEncoder;
import com.inst.cloud.chat.server.handler.TerminalServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author aaron
 * @since 2023-01-01 09:18
 */

public class ChatServer {
    private int port = 80;

    public void start(int port){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            /**
                             * 解析自定义协议
                             */
                            pipeline.addLast(new IMDecoder()); //Inbound
                            pipeline.addLast(new IMEncoder());  //OutBound
                            pipeline.addLast(new TerminalServerHandler()); //Inbound


                        }
                    });
            ChannelFuture f = b.bind(this.port).sync();
            System.out.println("服务已启动,监听端口"+ this.port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
             e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void start(){
        start(this.port);
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            new ChatServer().start(Integer.valueOf(args[0]));
        }else {
            new ChatServer().start();
        }
    }

}
