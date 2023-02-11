package com.inst.cloud.chat.common.protocol;

/**
 *  自定义 IMP  Instant Messaging Protocol，即时通信协议
 * @author aaron
 * @since 2022-12-30 11:14
 */
public enum IMP {

    SYSTEM("SYSTEM"), // 系统消息
    LOGIN("LOGIN"),   //登录指令
    LOGOUT("LOGOUT"), //退出指令
    CHAT("CHAT");     //聊天消息

    private String name;

    public static boolean isIMP(String content){
        return content.matches("^\\[(SYSTEM|LOGIN|LOGOUT|CHAT|FLOWER)\\]");
    }

    IMP(String name){
        this.name=name;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public String toString(){
        return this.name;
    }

}
