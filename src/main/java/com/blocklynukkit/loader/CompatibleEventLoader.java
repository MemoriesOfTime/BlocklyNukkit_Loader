package com.blocklynukkit.loader;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJumpEvent;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;

public class CompatibleEventLoader implements Listener {
    private Loader plugin;

    public CompatibleEventLoader(Loader plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJump(PlayerJumpEvent event){
        plugin.callEventHandler(event,event.getClass().getSimpleName());
    }
}
