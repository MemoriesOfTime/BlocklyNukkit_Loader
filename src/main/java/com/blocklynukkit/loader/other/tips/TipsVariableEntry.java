package com.blocklynukkit.loader.other.tips;

import cn.nukkit.Player;
import com.blocklynukkit.loader.Loader;
import tip.utils.variables.BaseVariable;
import tip.utils.variables.defaults.ChangeMessage;

import java.util.Map;

@ChangeMessage
public class TipsVariableEntry extends BaseVariable {
    Player player = null;

    public TipsVariableEntry(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public void strReplace() {
        for(Map.Entry<String,String> entry: Loader.windowManager.tipsVar.entrySet()){
            if(entry.getValue().startsWith("function->")){
                String to = Loader.plugin.callbackString(entry.getValue().replaceFirst("function->",""),this.player);
                this.addStrReplaceString(entry.getKey(),to);
            }else {
                this.addStrReplaceString(entry.getKey(),entry.getValue());
            }
        }
    }
}
