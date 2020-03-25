package dls.icesight.blocklynukkit.script.window;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;
import dls.icesight.blocklynukkit.Loader;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Simple {
    public LinkedHashSet<ElementButton> buttonsmap=new LinkedHashSet<>();
    public int id = (int) Math.floor(Math.random()*1000000);
    public String title="";
    public String context="";
    public void buildButton(String text,String img){
        ElementButton buttontmp=new ElementButton(text);
        if(img.startsWith("http")){
            buttontmp.addImage(new ElementButtonImageData("url",img));
        }else if(img.length()>4){
            buttontmp.addImage(new ElementButtonImageData("path",img));
        }
        buttonsmap.add(buttontmp);
    }
    public void setTitle(String title){
        this.title=title;
    }
    public void setContext(String context){
        this.context=context;
    }
    public void showToPlayer(Player p,String callback){
        Loader.functioncallback.put(id,callback);
        FormWindowSimple window=new FormWindowSimple(title,context);
        for(ElementButton button:buttonsmap){
            window.addButton(button);
        }
        p.showFormWindow(window,id);
    }
}
