package com.blocklynukkit.loader.other.lizi;

import com.blocklynukkit.loader.Utils;
import com.blocklynukkit.loader.other.lizi.json.Core;
import com.blocklynukkit.loader.other.lizi.json.Main;

import java.util.Random;


public class bnqqbot {
    public int qqid;
    public Random random = new Random(System.currentTimeMillis());
    public bnqqbot(){

    }
    public void startBot(){
        Main.start();
    }
    public void sendFriendMessage(String fromQQ,String toQQ,String message){
        Core.sendPrivateMessages(Long.parseLong(fromQQ),Long.parseLong(toQQ),message,random.nextLong(),random.nextLong());
    }
    public void sendGroupMessage(String fromQQ,String toGroup,String message){
        Core.sendGroupMessages(Long.parseLong(fromQQ),Long.parseLong(toGroup),message,0);
    }
    public void praise(String fromQQ,String toQQ,int count){
        Core.callPraise(Long.parseLong(fromQQ),Long.parseLong(toQQ),count);
    }
}
