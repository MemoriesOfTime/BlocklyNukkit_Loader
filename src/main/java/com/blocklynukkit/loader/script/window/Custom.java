package com.blocklynukkit.loader.script.window;

import java.util.LinkedList;

import cn.nukkit.Player;
import cn.nukkit.form.element.*;
import cn.nukkit.form.window.FormWindowCustom;
import com.blocklynukkit.loader.Loader;

public class Custom {
    public LinkedList<Element> elements = new LinkedList<>();
    public int id = (int) Math.floor(Math.random()*1000000);
    public String title="";
    public void setTitle(String title){
        this.title=title;
    }
    public void showToPlayer(Player p, String callback){
        Loader.functioncallback.put(id,callback);
        FormWindowCustom window=new FormWindowCustom(title,elements);
        p.showFormWindow(window,id);
    }
    public void addNewElement(Element element){
        elements.add(element);
    }
    public void buildLabel(String text){
        addNewElement(new ElementLabel(text));
    }
    public void buildInput(String title,String placeholder){
        addNewElement(new ElementInput(title,placeholder));
    }
    public void buildToggle(String title){
        addNewElement(new ElementToggle(title));
    }
    public void buildDropdown(String title,String inner){
        ElementDropdown dropdown=new ElementDropdown(title);
        for(String a:inner.split(";")){
            dropdown.addOption(a);
        }
        addNewElement(dropdown);
    }
}
