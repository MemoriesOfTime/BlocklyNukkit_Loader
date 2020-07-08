package com.mobplugin.route;

import cn.nukkit.entity.Entity;

/**
 * @author zzz1999 @ MobPlugin
 */
public class SimpleRouteFinder extends RouteFinder{

    public SimpleRouteFinder(Entity entity) {
        super(entity);
    }

    @Override
    public boolean search() {
        this.resetNodes();
        this.addNode(new Node(this.destination));
        return true;
    }
}