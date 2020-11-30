package com.blocklynukkit.loader.other.tips;

import cn.nukkit.Player;
import com.blocklynukkit.loader.Loader;
import tip.utils.variables.BaseVariable;

import java.util.Map;

public class TipsVariableEntry extends BaseVariable {
    public TipsVariableEntry(Player player) {
        super(player);
    }

    @Override
    public void strReplace() {
        for(Map.Entry<String,String> entry: Loader.tipsVar.entrySet()){
            if(entry.getValue().startsWith("function->")){
                if(this.player!=null){
                    String to = Loader.plugin.callbackString(entry.getValue().replaceFirst("function->",""),this.player);
                    this.addStrReplaceString(entry.getKey(),to);
                }
            }else {
                this.addStrReplaceString(entry.getKey(),entry.getValue());
            }
        }
    }
}
