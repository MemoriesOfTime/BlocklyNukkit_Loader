package dls.icesight.blocklynukkit.script;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;

public class EntityManager {
    //获取掉落物物品
    public Item getDropItemStack(EntityItem entityItem){
        return entityItem.getItem();
    }
    //移除生物
    public void removeEntity(Entity entity){
        entity.close();
    }
    //设置生物的名称
    public void setEntityName(Entity entity,String name){
        entity.setNameTag(name);
    }
    //设置生物名称高亮
    public void setEntityNameTagAlwaysVisable(Entity entity,boolean vis){
        entity.setNameTagAlwaysVisible(vis);
    }
}
