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
    @Deprecated
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
    public Custom showAsSetting(Player p, String callback){
        synchronized (Loader.serverSettingCallback){
            Loader.serverSettingCallback.put(p.getName(),callback);
            p.addServerSettings(new FormWindowCustom(title,elements));
        }
        return this;
    }
    public void addNewElement(Element element){
        elements.add(element);
    }
    public Custom buildLabel(String text){
        addNewElement(new ElementLabel(text));
        //addNewElement(new ElementSlider("标题",0,100,20,55));
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
    public Custom buildDropdown(String title,String inner,int index){
        ElementDropdown dropdown=new ElementDropdown(title);
        for(String a:inner.split(";")){
            dropdown.addOption(a);
        }
        dropdown.setDefaultOptionIndex(index);
        addNewElement(dropdown);
        return this;
    }
    public Custom buildSlider(String title,double min,double max,int step,double defaultvalue){
        ElementSlider slider = new ElementSlider(title,(float) min,(float)max,step,(float)defaultvalue);
        addNewElement(slider);
        return this;
    }
    public Custom buildSlider(String title,double min,double max,int step){
        ElementSlider slider = new ElementSlider(title,(float) min,(float)max,step);
        addNewElement(slider);
        return this;
    }
    public Custom buildSlider(String title,double min,double max){
        ElementSlider slider = new ElementSlider(title,(float) min,(float)max);
        addNewElement(slider);
        return this;
    }
    public Custom buildStepSlider(String title,String options){
        ElementStepSlider  stepSlider = new ElementStepSlider(title);
        for(String each:options.split(";")){
            stepSlider.addStep(each);
        }
        addNewElement(stepSlider);
        return this;
    }
    public Custom buildStepSlider(String title,String options,int index){
        ElementStepSlider  stepSlider = new ElementStepSlider(title);
        for(String each:options.split(";")){
            stepSlider.addStep(each);
        }
        stepSlider.setDefaultOptionIndex(index);
        addNewElement(stepSlider);
        return this;
    }
}
