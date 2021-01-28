package com.blocklynukkit.loader.other.lizi.json;

import java.io.IOException;
import java.util.Base64;

import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.script.event.QQFriendMessageEvent;
import com.blocklynukkit.loader.script.event.QQGroupMessageEvent;
import com.blocklynukkit.loader.script.event.QQOtherEvent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {

    public static ChatClient clientTest = null;
    public static String ip = "127.0.0.1";
    public static int port = 8404;

    public Main(){

    }
    public static void start(){
        if(clientTest!=null) {
            try {
                clientTest.socket.close();
            } catch (IOException e) {
                Loader.getlogger().warning("与小栗子机器人框架通讯错误！");
            }
            clientTest.interrupt();
        }
        clientTest = new ChatClient(ip, port);
        clientTest.start();
    }

    public static void stop(){
        if(clientTest!=null){
            try {
                clientTest.socket.close();
                clientTest.interrupt();
                clientTest = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 收到好友消息
     * @param data
     */
    public static void receivePrivateMessages(String data){
        JsonObject json = new JsonParser().parse(data).getAsJsonObject();
        long selfQQ = json.getAsJsonPrimitive("selfQQ").getAsLong();//框架QQ
        if(selfQQ<0)selfQQ=4294967296l+selfQQ;
        long fromQQ = json.getAsJsonPrimitive("fromQQ").getAsLong();//对方QQ
        if(fromQQ<0)fromQQ=4294967296l+fromQQ;
        long random = json.getAsJsonPrimitive("random").getAsLong();//撤回消息用
        long req = json.getAsJsonPrimitive("req").getAsLong();//撤回消息用
        String msg = json.getAsJsonPrimitive("msg").getAsString();//消息内容
        Loader.callEventHandler(new QQFriendMessageEvent(selfQQ,fromQQ,random,req,msg),"QQFriendMessageEvent","QQFriendMessageEvent");
//        if(msg.equals("点赞")){
//            Core.callpPraise(selfQQ,fromQQ,10);
//        }else if(msg.equals("红包")){
//            Core.pushRedPacket(selfQQ,fromQQ,1,1,"祝福语","支付密码");
//        }else if(msg.equals("图文")){
//            byte[] bts = StringUtils.readFile("D:\\1.png");//读取文件
//            String base64Str = Base64.getEncoder().encodeToString(bts);//字节数组转Base64
//            base64Str = "[pic:"+ base64Str + "]";//组装图片的格式
//            Core.sendPrivateMessagesPicText(selfQQ, fromQQ, base64Str + "111" + base64Str, random, req);
//        }else {
//            Core.sendPrivateMessages(selfQQ, fromQQ, msg, random, req);
//        }
        //红包发送成功  "msgType":141,"msgType2":134,"msgTempType":129
    }
    /**
     * 收到群聊消息
     * @param data
     */
    public static void receiveGroupMessages(String data){
        JsonObject json = new JsonParser().parse(data).getAsJsonObject();
        long selfQQ = json.getAsJsonPrimitive("selfQQ").getAsLong();//框架QQ
        if(selfQQ<0)selfQQ=4294967296l+selfQQ;
        long fromGroup = json.getAsJsonPrimitive("fromGroup").getAsLong();//群号
        if(fromGroup<0)fromGroup=4294967296l+fromGroup;
        long fromQQ = json.getAsJsonPrimitive("fromQQ").getAsLong();//对方QQ
        if(fromQQ<0)fromQQ=4294967296l+fromQQ;
        String msg = json.getAsJsonPrimitive("msg").getAsString();//消息内容
        Loader.callEventHandler(new QQGroupMessageEvent(selfQQ,fromGroup,fromQQ,msg),"QQGroupMessageEvent","QQGroupMessageEvent");
        //这里我写了3个指令用于测试  改名片、踢出群员、禁言群员
//        if(msg.contains("改名片")){//默认改自己的 如  改名片404
//            String cardName = msg.substring(msg.indexOf("改名片") + 3);//取出右边的名片
//            Core.setGroupCardName(selfQQ, fromGroup, fromQQ, cardName);
//        }else if(msg.contains("踢")){//右边需要加上要踢的QQ 如 踢123456
//            String otherQQ = msg.substring(msg.indexOf("踢") + 1);//取出右边要踢的QQ
//            Core.delGroupMember(selfQQ, fromGroup, Integer.valueOf(otherQQ), 0);
//        }else if(msg.contains("禁言")){//右边需要加上要禁言的QQ 如 禁言123456
//            String otherQQ = msg.substring(msg.indexOf("禁言") + 2);//取出右边要禁言的QQ
//            Core.prohibitSpeak(selfQQ, fromGroup, Integer.valueOf(otherQQ), 60);
//        }else if(msg.equals("图文")){
//            byte[] bts = StringUtils.readFile("D:\\1.png");//读取文件
//            String base64Str = Base64.getEncoder().encodeToString(bts);//字节数组转Base64
//            base64Str = "[pic:"+ base64Str + "]";//组装图片的格式
//            Core.sendGroupMessagesPicText(selfQQ, fromGroup, base64Str + "111" + base64Str,0);
//        }else if(msg.equals("红包")){
//            Core.pushRedPacketGroup(selfQQ,fromGroup,1,1,"祝福语","支付密码");
//        }else{//除了以上三个指令 其他的都原样返回
//            Core.sendGroupMessages(selfQQ, fromGroup, msg, 0);
//        }

    }

    public static void receiveEventMessages(String data){
        JsonObject json = new JsonParser().parse(data).getAsJsonObject();
        long selfQQ = json.getAsJsonPrimitive("selfQQ").getAsLong();//框架QQ
        if(selfQQ<0)selfQQ=4294967296l+selfQQ;
        long fromGroup = json.getAsJsonPrimitive("fromGroup").getAsLong();//群号
        if(fromGroup<0)fromGroup=4294967296l+fromGroup;
        int msgType = json.getAsJsonPrimitive("msgType").getAsInt();//类型
        long triggerQQ = json.getAsJsonPrimitive("triggerQQ").getAsLong();//对方QQ
        if(triggerQQ<0)triggerQQ=triggerQQ+4294967296l;
        //String triggerQQName = json.getString("triggerQQName");//对方昵称
        long seq = json.getAsJsonPrimitive("seq").getAsLong();//操作用
        Loader.callEventHandler(new QQOtherEvent(selfQQ,fromGroup,msgType,triggerQQ,seq),"QQOtherEvent","QQOtherEvent");
        //32表示QQ上线
        //17表示好友更改昵称
        //25表示邀请加入了群聊
//        if(msgType == 3){//群验证事件 申请入群
//            Core.handleGroupEvent(selfQQ, fromGroup, triggerQQ, seq, 11, 3);
//        }else if(msgType == 23){
//            Core.callpPraise(selfQQ,triggerQQ,10);
//        }
    }
}
