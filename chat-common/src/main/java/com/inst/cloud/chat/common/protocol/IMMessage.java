package com.inst.cloud.chat.common.protocol;

import lombok.Data;
import org.msgpack.annotation.Message;

/**
 * 自定义消息实体类
 *
 * @author aaron
 * @since 2022-12-30 11:20
 */
@Message
@Data
public class IMMessage {

    private String addr;
    /**
     * 命令类型[LOGIN]或者[SYSTEM]或者[LOGOUT]
     */
    private String cmd;
    /**
     * 命令发送时间
     */
    private Long time;
    /**
     * 当前在线人数
     */
    private Integer online;
    /**
     * 发送人
     */
    private String sender;
    /**
     * 接收人
     */
    private String receiver;
    /**
     * 消息内容
     */
    private String content;

    /**
     * 终端
     */
    private String terminal;

    /**
     * 是否是自己
     */
    private boolean self;

    /**
     * 头像地址
     */
    private String avatar;

    public IMMessage() {
    }


    public IMMessage(String cmd, String terminal, Long time, String sender, String content) {
        this.cmd = cmd;
        this.terminal = terminal;
        this.time = time;
        this.sender = sender;
        this.content = content;
    }

    public IMMessage(String cmd, String terminal, Long time, String sender, String content, Integer online) {
        this.cmd = cmd;
        this.terminal = terminal;
        this.time = time;
        this.sender = sender;
        this.content = content;
        this.online = online;
    }


    public IMMessage(String cmd, String terminal, Long time, String sender, String content, Integer online, Boolean self, String avatar) {
        this.cmd = cmd;
        this.terminal = terminal;
        this.time = time;
        this.sender = sender;
        this.content = content;
        this.online = online;
        this.self = self;
        this.avatar = avatar;
    }


    @Override
    public String toString() {
        return "IMMessage{" +
                "addr='" + addr + '\'' +
                ", cmd='" + cmd + '\'' +
                ", time=" + time +
                ", online=" + online +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
