package com.blocklynukkit.loader.script.window;

import java.util.LinkedList;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.*;
import cn.nukkit.form.window.FormWindowCustom;
import com.blocklynukkit.loader.Loader;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class Custom {
    public LinkedList<Element> elements = new LinkedList<>();
    public int id = (int) Math.floor(Math.random()*1000000);
    public String title="";
    public Custom setTitle(String title){
        this.title=title;
        return this;
    }
    public Custom showToPlayer(Player p, String callback){
        synchronized (Loader.functioncallback){
            Loader.functioncallback.put(id,callback);
            FormWindowCustom window=new FormWindowCustom(title,elements);
            p.showFormWindow(window,id);
        }
        return this;
    }
    public Custom showToPlayerCallLambda(Player p,ScriptObjectMirror mirror){
        synchronized (Loader.scriptObjectMirrorCallback){
            if(mirror!=null){
                Loader.scriptObjectMirrorCallback.put(id,mirror);
            }
            FormWindowCustom window=new FormWindowCustom(title,elements);
            p.showFormWindow(window,id);
        }
        return this;
    }
    public void addNewElement(Element element){
        elements.add(element);
    }
    public Custom buildLabel(String text){
        addNewElement(new ElementLabel(text));
        return this;
    }
    public Custom buildInput(String title,String placeholder){
        addNewElement(new ElementInput(title,placeholder));
        return this;
    }
    public Custom buildInput(String title,String placeholder,String defaulttext){
        addNewElement(new ElementInput(title,placeholder,defaulttext));
        return this;
    }
    public Custom buildToggle(String title){
        addNewElement(new ElementToggle(title));
        return this;
    }
    public Custom buildToggle(String title,boolean open){
        addNewElement(new ElementToggle(title,open));
        return this;
    }
    public Custom buildDropdown(String title,String inner){
        ElementDropdown dropdown=new ElementDropdown(title);
        for(String a:inner.split(";")){
            dropdown.addOption(a);
        }
        addNewElement(dropdown);
        return this;
    }
}
