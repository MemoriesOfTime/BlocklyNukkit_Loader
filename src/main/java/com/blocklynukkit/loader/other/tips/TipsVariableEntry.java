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
            //entry的key是被替换的字符串，value是要替换成的字符串
            if(entry.getValue().startsWith("function->")){
                //function->是bn内部的标识方式，表示这是一个要执行回调函数获取的替换值的回调，->后面是回调函数名
                if(player!=null){//player指的是要显示给的玩家
                    String to = Loader.plugin.callbackString(
                            entry.getValue().replaceFirst("function->","")
                            ,player
                    );//callbackString将使得bn从所有bn堆栈中查找并调用函数
                    this.addStrReplaceString(entry.getKey(),to);
                }
            }else {
                this.addStrReplaceString(entry.getKey(),entry.getValue());
            }
        }
    }
}
