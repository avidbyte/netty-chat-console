package com.inst.cloud.chat.server.processor;


import com.inst.cloud.chat.common.protocol.IMMessage;
import com.inst.cloud.chat.common.protocol.IMP;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.json.simple.JSONObject;

/**
 * 主要用于自定义协议内容的逻辑处理
 *
 * @author aaron
 * @since 2022-12-31 21:14
 */
public class MsgProcessor {

    //记录在线用户
    private static final ChannelGroup onlineUsers = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //定义一些扩展属性
    public static final AttributeKey<String> NICK_NAME = AttributeKey.valueOf("nickName");
    public static final AttributeKey<String> IP_ADDR = AttributeKey.valueOf("ipAddr");
    public static final AttributeKey<JSONObject> ATTRS = AttributeKey.valueOf("attrs");
    public static final AttributeKey<String> FROM = AttributeKey.valueOf("from");


    /**
     * 获取用户昵称
     *
     * @param client client
     * @return String
     */
    public String getNickName(Channel client) {
        return client.attr(NICK_NAME).get();
    }

    /**
     * 获取用户远程IP地址
     *
     * @param client client
     * @return String
     */
    public String getAddress(Channel client) {
        return client.remoteAddress().toString().replaceFirst("/", "");
    }

    /**
     * 获取扩展属性
     *
     * @param client client
     * @return JSONObject
     */
    public JSONObject getAttrs(Channel client) {
        try {
            return client.attr(ATTRS).get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 设置扩展属性
     *
     * @param client client
     * @param key    key
     * @param value  value
     */
    private void setAttrs(Channel client, String key, Object value) {
        try {
            JSONObject json = client.attr(ATTRS).get();
            json.put(key, value);
            client.attr(ATTRS).set(json);
        } catch (Exception e) {
            JSONObject json = new JSONObject();
            json.put(key, value);
            client.attr(ATTRS).set(json);
        }
    }

    /**
     * 退出通知
     *
     * @param client client
     */
    public void logout(Channel client) {
        //如果 nickName 为 null，没有遵从聊天协议的连接，表示为非法登录
        if (getNickName(client) == null) {
            return;
        }
        for (Channel channel : onlineUsers) {
            IMMessage request = new IMMessage(IMP.SYSTEM.getName(), "", System.currentTimeMillis(), getNickName(client), getNickName(client) + "离开", onlineUsers.size(), false, null);
            channel.writeAndFlush(request);
        }
        onlineUsers.remove(client);
    }


    public void mainProcess(Channel client, IMMessage request) {
        String addr = getAddress(client);
        if (request.getCmd().equals(IMP.LOGIN.getName())) {
            System.out.println(request.getSender() + "加入聊天");
            client.attr(NICK_NAME).getAndSet(request.getSender());
            client.attr(IP_ADDR).getAndSet(addr);
            client.attr(FROM).getAndSet(request.getTerminal());
            onlineUsers.add(client);
            for (Channel channel : onlineUsers) {
                boolean isSelf = (channel == client);
                if (isSelf) {
                    request = new IMMessage(IMP.SYSTEM.getName(), "Console", System.currentTimeMillis(), request.getSender(), "已与服务器建立连接！", onlineUsers.size());
                } else {
                    request = new IMMessage(IMP.SYSTEM.getName(), "Console", System.currentTimeMillis(), request.getSender(), "系统提示：---"+request.getSender() + " 加入聊天---", onlineUsers.size());
                }
                if ("Console".equals(channel.attr(FROM).get())) {
                    channel.writeAndFlush(request);
                }
            }
        } else if (request.getCmd().equals(IMP.CHAT.getName())) {
            for (Channel channel : onlineUsers) {
                boolean isSelf = (channel == client);
                request.setTime(System.currentTimeMillis());
                if ("Console".equals(channel.attr(FROM).get()) && !isSelf) {
                    channel.writeAndFlush(request);
                }
            }
        } else if (request.getCmd().equals(IMP.SYSTEM.getName())) {
            for (Channel channel : onlineUsers) {
                boolean isSelf = (channel == client);
                if (!isSelf) {
                    request = new IMMessage(IMP.SYSTEM.getName(), "Console", System.currentTimeMillis(), request.getSender(), request.getSender() + "退出", onlineUsers.size());
                }
                if ("Console".equals(channel.attr(FROM).get()) & !isSelf) {
                    channel.writeAndFlush(request);
                }
            }

        } else if (request.getCmd().equals(IMP.LOGOUT.getName())) {
            System.out.println(request.getSender() + "退出");
            for (Channel channel : onlineUsers) {
                request = new IMMessage(IMP.SYSTEM.getName(), "Console", System.currentTimeMillis(), request.getSender(), request.getSender() + "退出", onlineUsers.size());
                if ("Console".equals(channel.attr(FROM).get())) {
                    channel.writeAndFlush(request);
                }
            }
        }
    }


}
