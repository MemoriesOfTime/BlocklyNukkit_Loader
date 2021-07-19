package com.blocklynukkit.loader.script.window;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.api.CallbackFunction;
import com.blocklynukkit.loader.api.Comment;
import com.blocklynukkit.loader.script.window.windowCallbacks.SimpleCallback;
import com.blocklynukkit.loader.utils.Utils;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.*;

public final class Simple {
    public LinkedHashMap<ElementButton,String> buttonsMap=new LinkedHashMap<>();
    public int id = (int) Math.floor(Math.random()*10000000);
    public String title="";
    public String context="";
    private ElementButton previousButton = null;
    @Comment(value = "向窗口添加一个按钮")
    public Simple buildButton(@Comment(value = "按钮文本") String text
            ,@Comment(value = "按钮图片链接，可为材质包内部链接或网络url") String img){
        ElementButton buttontmp=new ElementButton(text);
        if(img.startsWith("http")){
            buttontmp.addImage(new ElementButtonImageData("url",img));
        }else if(img.length()>4){
            buttontmp.addImage(new ElementButtonImageData("path",img));
        }
        buttonsMap.put(buttontmp,null);
        previousButton = buttontmp;
        return this;
    }
    @Comment(value = "向窗口添加一个按钮")
    public Simple button(@Comment(value = "按钮文本") String text,@Comment(value = "按钮图片链接，可为材质包内部链接或网络url") String img){
        return buildButton(text, img);
    }
    @Comment(value = "向窗口添加一个按钮")
    public Simple button(@Comment(value = "按钮文本") String text){
        return buildButton(text,"");
    }
    @Comment(value = "为上一个添加的按钮绑定点击动作回调")
    public Simple setAction(
            @Comment(value = "回调函数") @CallbackFunction(classes = {"cn.nukkit.event.player.PlayerFormRespondedEvent"}, parameters = {"action"}, comments = {"玩家提交窗口事件"}) String action){
        if(previousButton != null){
            buttonsMap.replace(previousButton,action);
        }
        return this;
    }
    @Comment(value = "为上一个添加的按钮绑定点击动作回调")
    public Simple action(
            @Comment(value = "回调函数") @CallbackFunction(classes = {"cn.nukkit.event.player.PlayerFormRespondedEvent"}, parameters = {"action"}, comments = {"玩家提交窗口事件"}) String action){
        return setAction(action);
    }
    @Comment(value = "设置窗口标题")
    public Simple setTitle(@Comment("窗口标题") String title){
        this.title=title;
        return this;
    }
    @Comment(value = "设置窗口标题")
    public Simple title(@Comment("窗口标题") String title){
        return this.setTitle(title);
    }
    @Comment(value = "设置窗口提示文本")
    public Simple setContext(@Comment(value = "窗口提示文本") String context){
        this.context=context;
        return this;
    }
    @Comment(value = "设置窗口提示文本")
    public Simple context(@Comment(value = "窗口提示文本") String context){
        return this.setContext(context);
    }
    @Comment(value = "向玩家发送此窗口")
    public Simple showToPlayer(@Comment(value = "发送给的玩家") Player p){
        return this.showToPlayer(p,null,false);
    }
    @Comment(value = "向玩家发送此窗口")
    public Simple showToPlayer(@Comment(value = "发送给的玩家") Player p
            ,@Comment(value = "玩家关闭窗口是否触发回调") boolean acceptClose){
        return this.showToPlayer(p,null,acceptClose);
    }
    @Comment(value = "向玩家发送此窗口")
    public Simple showToPlayer(@Comment(value = "发送给的玩家") Player p
            ,@Comment(value = "回调函数") @CallbackFunction(classes = {"cn.nukkit.event.player.PlayerFormRespondedEvent"}, parameters = {"action"}, comments = {"玩家提交窗口事件"}) String callback){
        return this.showToPlayer(p,callback,false);
    }
    @Comment(value = "向玩家发送此窗口")
    public Simple showToPlayer(@Comment(value = "发送给的玩家") Player p
            ,@Comment(value = "回调函数") @CallbackFunction(classes = {"cn.nukkit.event.player.PlayerFormRespondedEvent"}, parameters = {"action"}, comments = {"玩家提交窗口事件"}) String callback
            ,@Comment(value = "玩家关闭窗口是否触发回调") boolean acceptClose){
        synchronized (Loader.windowCallbackMap){
            SimpleCallback windowCallback = new SimpleCallback(acceptClose);
            FormWindowSimple window=new FormWindowSimple(title,context);
            int index = 0;
            for(Map.Entry<ElementButton,String> each:buttonsMap.entrySet()){
                if(each.getValue()!=null){
                    windowCallback.addActionCallback(index,each.getValue());
                }
                index++;
                window.addButton(each.getKey());
            }
            if(callback!=null){
                windowCallback.setDefaultCallback(callback);
            }
            Loader.windowCallbackMap.put(id,windowCallback);
            p.showFormWindow(window,id);
        }
        return this;
    }
    @Comment(value = "向玩家发送此窗口")
    public Simple show(@Comment(value = "发送给的玩家") Player p){
        return this.showToPlayer(p);
    }
    @Comment(value = "向玩家发送此窗口")
    public Simple show(@Comment(value = "发送给的玩家") Player p
            ,@Comment(value = "玩家关闭窗口是否触发回调") boolean accpetClose){
        return this.showToPlayer(p, accpetClose);
    }
    @Comment(value = "向玩家发送此窗口")
    public Simple show(@Comment(value = "发送给的玩家") Player p
            ,@Comment(value = "回调函数") @CallbackFunction(classes = {"cn.nukkit.event.player.PlayerFormRespondedEvent"}, parameters = {"action"}, comments = {"玩家提交窗口事件"}) String callback){
        return this.showToPlayer(p, callback);
    }
    @Comment(value = "向玩家发送此窗口")
    public Simple show(@Comment(value = "发送给的玩家") Player p
            ,@Comment(value = "回调函数") @CallbackFunction(classes = {"cn.nukkit.event.player.PlayerFormRespondedEvent"}, parameters = {"action"}, comments = {"玩家提交窗口事件"}) String callback
            ,@Comment(value = "玩家关闭窗口是否触发回调") boolean acceptClose){
        return this.showToPlayer(p, callback, acceptClose);
    }
    @Override
    public String toString() {
        FormWindowSimple window=new FormWindowSimple(title,context);
        for(ElementButton button: buttonsMap.keySet()){
            window.addButton(button);
        }
        return "SimpleWindowForm: "+window.getJSONData()+"";
    }
}
