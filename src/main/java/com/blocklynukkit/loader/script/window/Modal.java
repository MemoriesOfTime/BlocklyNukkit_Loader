package com.blocklynukkit.loader.script.window;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.api.CallbackFunction;
import com.blocklynukkit.loader.api.Comment;
import com.blocklynukkit.loader.script.window.windowCallbacks.ModalCallback;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.Map;

public final class Modal {
    public int id = (int) Math.floor(Math.random()*10000000);
    public String title="";
    public String context="";
    public String btn1="";
    public String btn2="";
    public String btn1Callback=null;
    public String btn2Callback=null;
    public short previous = 0;
    @Comment(value = "设置窗口标题")
    public Modal setTitle(@Comment("窗口标题") String title){
        this.title=title;
        return this;
    }
    @Comment(value = "设置窗口标题")
    public Modal title(@Comment("窗口标题") String title){
        return setTitle(title);
    }
    @Comment(value = "设置窗口提示文本")
    public Modal setContext(@Comment(value = "窗口提示文本") String context){
        this.context=context;
        return this;
    }
    @Comment(value = "设置窗口提示文本")
    public Modal context(@Comment(value = "窗口提示文本") String context){
        return setContext(context);
    }
    @Comment(value = "设置左侧按钮文本")
    public Modal setButton1(@Comment(value = "文本") String text){
        btn1=text;previous=1;
        return this;
    }
    @Comment(value = "设置左侧按钮文本")
    public Modal setButton2(@Comment(value = "文本") String text){
        btn2=text;previous=2;
        return this;
    }
    @Comment(value = "设置左侧按钮文本")
    public Modal button1(@Comment(value = "文本") String text){
        return setButton1(text);
    }
    @Comment(value = "设置左侧按钮文本")
    public Modal button2(@Comment(value = "文本") String text){
        return setButton2(text);
    }
    @Comment(value = "为上一个操作的按钮绑定点击动作回调")
    public Modal setAction(@Comment(value = "回调函数") @CallbackFunction(classes = {"cn.nukkit.event.player.PlayerFormRespondedEvent"}, parameters = {"action"}, comments = {"玩家提交窗口事件"}) String callback){
        if(previous==0)return this;
        if(previous==1)btn1Callback=callback;
        if(previous==2)btn2Callback=callback;
        return this;
    }
    @Comment(value = "为上一个添加的按钮绑定点击动作回调")
    public Modal action(@Comment(value = "回调函数") @CallbackFunction(classes = {"cn.nukkit.event.player.PlayerFormRespondedEvent"}, parameters = {"action"}, comments = {"玩家提交窗口事件"}) String callback){
        return setAction(callback);
    }
    @Comment(value = "向玩家发送此窗口")
    public Modal showToPlayer(@Comment(value = "发送给的玩家") Player p){
        return this.showToPlayer(p,null,false);
    }
    @Comment(value = "向玩家发送此窗口")
    public Modal showToPlayer(@Comment(value = "发送给的玩家") Player p,@Comment(value = "玩家关闭窗口是否触发回调") boolean acceptClose){
        return this.showToPlayer(p,null,acceptClose);
    }
    @Comment(value = "向玩家发送此窗口")
    public Modal showToPlayer(@Comment(value = "发送给的玩家") Player p,@Comment(value = "回调函数") @CallbackFunction(classes = {"cn.nukkit.event.player.PlayerFormRespondedEvent"}, parameters = {"action"}, comments = {"玩家提交窗口事件"}) String callback){
        return this.showToPlayer(p,callback,false);
    }
    @Comment(value = "向玩家发送此窗口")
    public Modal showToPlayer(@Comment(value = "发送给的玩家") Player p,@Comment(value = "回调函数") @CallbackFunction(classes = {"cn.nukkit.event.player.PlayerFormRespondedEvent"}, parameters = {"action"}, comments = {"玩家提交窗口事件"}) String callback,@Comment(value = "玩家关闭窗口是否触发回调") boolean acceptClose){
        synchronized (Loader.windowCallbackMap){
            ModalCallback windowCallback = new ModalCallback(acceptClose);
            FormWindowModal window=new FormWindowModal(title,context,btn1,btn2);
            if(callback!=null){
                windowCallback.setDefaultCallback(callback);
            }
            windowCallback.setYesCallback(btn1Callback);
            windowCallback.setNoCallback(btn2Callback);
            Loader.windowCallbackMap.put(id,windowCallback);
            p.showFormWindow(window,id);
        }
        return this;
    }
    @Comment(value = "向玩家发送此窗口")
    public Modal show(@Comment(value = "发送给的玩家") Player p){
        return this.showToPlayer(p);
    }
    @Comment(value = "向玩家发送此窗口")
    public Modal show(@Comment(value = "发送给的玩家") Player p,@Comment(value = "玩家关闭窗口是否触发回调") boolean accpetClose){
        return this.showToPlayer(p, accpetClose);
    }
    @Comment(value = "向玩家发送此窗口")
    public Modal show(@Comment(value = "发送给的玩家") Player p,@Comment(value = "回调函数") @CallbackFunction(classes = {"cn.nukkit.event.player.PlayerFormRespondedEvent"}, parameters = {"action"}, comments = {"玩家提交窗口事件"}) String callback){
        return this.showToPlayer(p, callback);
    }
    @Comment(value = "向玩家发送此窗口")
    public Modal show(@Comment(value = "发送给的玩家") Player p,@Comment(value = "回调函数") @CallbackFunction(classes = {"cn.nukkit.event.player.PlayerFormRespondedEvent"}, parameters = {"action"}, comments = {"玩家提交窗口事件"}) String callback,@Comment(value = "玩家关闭窗口是否触发回调") boolean acceptClose){
        return this.showToPlayer(p, callback, acceptClose);
    }
//    public Modal showToPlayer(Player p, String callback){
//        synchronized (Loader.functioncallback){
//            Loader.functioncallback.put(id,callback);
//            FormWindowModal modal=new FormWindowModal(title,context,btn1,btn2);
//            p.showFormWindow(modal,id);
//        }
//        return this;
//    }
//    public Modal showToPlayer(Player p,String callback,boolean acceptClose){
//        if(acceptClose){
//            synchronized (Loader.acceptCloseCallback){
//                Loader.acceptCloseCallback.put(callback,true);
//            }
//        }
//        return this.showToPlayer(p, callback);
//    }
//    @Deprecated
//    public Modal showToPlayerCallLambda(Player p,ScriptObjectMirror mirror){
//        synchronized (Loader.scriptObjectMirrorCallback){
//            if(mirror!=null){
//                Loader.scriptObjectMirrorCallback.put(id,mirror);
//            }
//            FormWindowModal modal=new FormWindowModal(title,context,btn1,btn2);
//            p.showFormWindow(modal,id);
//        }
//        return this;
//    }
//    @Deprecated
//    public Modal showToPlayerCallLambda(Player p,ScriptObjectMirror mirror,boolean acceptClose){
//        if(acceptClose){
//            synchronized (Loader.acceptCloseCallback){
//                Loader.acceptCloseCallback.put(mirror.toString(),true);
//            }
//        }
//        return this.showToPlayerCallLambda(p, mirror);
//    }
    @Override
    public String toString() {
        FormWindowModal modal=new FormWindowModal(title,context,btn1,btn2);
        return "ModalWindowForm: "+modal.getJSONData()+"";
    }
}
