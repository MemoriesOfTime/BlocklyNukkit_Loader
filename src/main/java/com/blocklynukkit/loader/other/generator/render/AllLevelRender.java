package com.blocklynukkit.loader.other.generator.render;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import com.blocklynukkit.loader.Loader;

public final class AllLevelRender extends BaseRender{
    private String callback = null;
    public AllLevelRender(String callback){
        super(0);
        this.callback = callback;
    }
    public AllLevelRender(String callback,int priority){
        super(priority);
        this.callback = callback;
    }

    @Override
    public boolean canRend(Level level) {
        return true;
    }

    @Override
    public void rend(Level level,FullChunk fullChunk) {
        if(!canRend(level))return;
        Loader.plugin.call(callback,level,fullChunk);
    }
}
