package com.blocklynukkit.loader.script.event;

import cn.nukkit.Player;
import cn.nukkit.entity.item.EntityFishingHook;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.event.player.PlayerEvent;

public class StartFishingEvent extends PlayerEvent {
    ProjectileLaunchEvent parent;

    public StartFishingEvent(ProjectileLaunchEvent ev){
        this.parent = ev;
        this.player = (Player) ev.getEntity().shootingEntity;
    }

    @Override
    public boolean isCancelled() {
        return parent.isCancelled();
    }

    @Override
    public void setCancelled(boolean value) {
        parent.setCancelled(value);
    }

    public EntityFishingHook getFishingHook(){
        return (EntityFishingHook) parent.getEntity();
    }
}
