package com.blocklynukkit.loader.script.window;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowModal;
import com.blocklynukkit.loader.Loader;

public class Modal {
    public int id = (int) Math.floor(Math.random()*1000000);
    public String title="";
    public String context="";
    public String btn1="";
    public String btn2="";
    public void setTitle(String title){
        this.title=title;
    }
    public void setContext(String context){
        this.context=context;
    }
    public void setButton1(String text){
        btn1=text;
    }
    public void setButton2(String text){
        btn2=text;
    }
    public void showToPlayer(Player p, String callback){
        Loader.functioncallback.put(id,callback);
        FormWindowModal modal=new FormWindowModal(title,context,btn1,btn2);
        p.showFormWindow(modal,id);
    }
}
