package com.blocklynukkit.loader;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import me.onebone.economyapi.event.money.AddMoneyEvent;
import me.onebone.economyapi.event.money.ReduceMoneyEvent;
import me.onebone.economyapi.event.money.SetMoneyEvent;

public class CompatibleEventLoader implements Listener {
    private Loader plugin;

    public CompatibleEventLoader(Loader plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAddMoneyEvent(AddMoneyEvent event){
        Loader.callEventHandler(event, "AddMoneyEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerReduceMoneyEvent(ReduceMoneyEvent event){
        Loader.callEventHandler(event, "ReduceMoneyEvent");
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSetMoneyEvent(SetMoneyEvent event){
        Loader.callEventHandler(event, "SetMoneyEvent");
    }
}
