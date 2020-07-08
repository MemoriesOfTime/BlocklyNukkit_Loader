package com.blocklynukkit.loader.script.window;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import com.blocklynukkit.loader.Loader;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.LinkedHashSet;

public class Simple {
    public LinkedHashSet<ElementButton> buttonsmap=new LinkedHashSet<>();
    public int id = (int) Math.floor(Math.random()*1000000);
    public String title="";
    public String context="";
    public Simple buildButton(String text,String img){
        ElementButton buttontmp=new ElementButton(text);
        if(img.startsWith("http")){
            buttontmp.addImage(new ElementButtonImageData("url",img));
        }else if(img.length()>4){
            buttontmp.addImage(new ElementButtonImageData("path",img));
        }
        buttonsmap.add(buttontmp);
        return this;
    }
    public Simple setTitle(String title){
        this.title=title;
        return this;
    }
    public Simple setContext(String context){
        this.context=context;
        return this;
    }
    public Simple showToPlayer(Player p,String callback){
        synchronized (Loader.functioncallback){
            Loader.functioncallback.put(id,callback);
            FormWindowSimple window=new FormWindowSimple(title,context);
            for(ElementButton button:buttonsmap){
                window.addButton(button);
            }
            p.showFormWindow(window,id);
        }
        return this;
    }
    public Simple showToPlayerCallLambda(Player p,ScriptObjectMirror mirror){
        synchronized (Loader.scriptObjectMirrorCallback){
            if(mirror!=null){
                Loader.scriptObjectMirrorCallback.put(id,mirror);
            }
            FormWindowSimple window=new FormWindowSimple(title,context);
            for(ElementButton button:buttonsmap){
                window.addButton(button);
            }
            p.showFormWindow(window,id);
        }
        return this;
    }
}
