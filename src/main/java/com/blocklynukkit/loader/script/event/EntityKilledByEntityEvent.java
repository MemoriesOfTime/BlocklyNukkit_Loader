package com.blocklynukkit.loader.script.event;

import cn.nukkit.Server;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;

public class EntityKilledByEntityEvent extends EntityDamageByEntityEvent {
    public EntityKilledByEntityEvent(EntityDamageByEntityEvent event){
        super(event.getDamager(),event.getEntity(),event.getCause(),event.getDamage(),event.getKnockBack());
    }
}
