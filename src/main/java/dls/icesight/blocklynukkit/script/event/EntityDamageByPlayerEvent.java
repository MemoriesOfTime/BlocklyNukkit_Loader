package dls.icesight.blocklynukkit.script.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByPlayerEvent extends EntityDamageByEntityEvent {
    public EntityDamageByPlayerEvent(EntityDamageByEntityEvent event){
        super(event.getDamager(),event.getEntity(),event.getCause(),event.getDamage(),event.getKnockBack());
    }
    public Player getPlayer(){
        Player player = null;
        if(getDamager() instanceof Player && (getDamager().getNetworkId()==63 || getDamager().getNetworkId()==319)){
            player = Server.getInstance().getPlayer(getDamager().getNameTag());
            if(player.equals(null))player = Server.getInstance().getPlayer(getDamager().getName());
        }
        return player;
    }
}
