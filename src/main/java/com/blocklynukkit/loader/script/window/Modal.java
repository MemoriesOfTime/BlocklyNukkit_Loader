package com.blocklynukkit.loader.script.window;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import com.blocklynukkit.loader.Loader;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class Modal {
    public int id = (int) Math.floor(Math.random()*1000000);
    public String title="";
    public String context="";
    public String btn1="";
    public String btn2="";
    public Modal setTitle(String title){
        this.title=title;
        return this;
    }
    public Modal setContext(String context){
        this.context=context;
        return this;
    }
    public Modal setButton1(String text){
        btn1=text;
        return this;
    }
    public Modal setButton2(String text){
        btn2=text;
        return this;
    }
    public Modal showToPlayer(Player p, String callback){
        synchronized (Loader.functioncallback){
            Loader.functioncallback.put(id,callback);
            FormWindowModal modal=new FormWindowModal(title,context,btn1,btn2);
            p.showFormWindow(modal,id);
        }
        return this;
    }
    @Deprecated
    public Modal showToPlayerCallLambda(Player p,ScriptObjectMirror mirror){
        synchronized (Loader.scriptObjectMirrorCallback){
            if(mirror!=null){
                Loader.scriptObjectMirrorCallback.put(id,mirror);
            }
            FormWindowModal modal=new FormWindowModal(title,context,btn1,btn2);
            p.showFormWindow(modal,id);
        }
        return this;
    }
    @Override
    public String toString() {
        FormWindowModal modal=new FormWindowModal(title,context,btn1,btn2);
        return "ModalWindowForm: "+modal.getJSONData()+"";
    }
}
