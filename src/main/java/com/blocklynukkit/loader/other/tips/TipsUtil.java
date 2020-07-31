package com.blocklynukkit.loader.other.tips;

import tip.utils.Api;

public class TipsUtil {
    public static void registerTips(){
        Api.registerVariables("BlocklyNukkit",TipsVariableEntry.class);
    }
}
