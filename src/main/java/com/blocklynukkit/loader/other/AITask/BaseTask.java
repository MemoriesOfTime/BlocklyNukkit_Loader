package com.blocklynukkit.loader.other.AITask;

import com.blocklynukkit.loader.other.Entities.BNNPC;

public abstract class BaseTask {
    public boolean canRun(BNNPC npc){
        return false;
    }
    public void doRun(){
        return;
    }
    public boolean isRunning(){
        return false;
    }
    public boolean isFinished(){
        return true;
    }
}
