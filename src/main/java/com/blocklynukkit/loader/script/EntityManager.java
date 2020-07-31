package com.blocklynukkit.loader.script;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Effect;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.Clothes;
import com.blocklynukkit.loader.other.Entities.BNNPC;
import com.blocklynukkit.loader.other.Entities.FloatingText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
        entity.setNameTagVisible(vis);
        entity.setNameTagAlwaysVisible(vis);
    }
    //设置生物血量
    public void setEntityHealth(Entity entity,double health){
        entity.setHealth((float)health);
    }
    //设置生物最大血量
    public void setEntityMaxHealth(Entity entity,double health){
        entity.setMaxHealth((int) health);
    }
    //获取生物血量
    public float getEntityHealth(Entity entity){
        return entity.getHealth();
    }
    //获取生物最大血量
    public float getEntityMaxHealth(Entity entity){
        return entity.getMaxHealth();
    }
    //here 4/23
    //清除生物的药水状态
    public void clearEntityEffect(Entity entity){
        entity.removeAllEffects();
    }
    //为生物添加药水状态
    public void addEntityEffect(Entity entity,int id,int level,int tick,int r,int g,int b){
        Effect effect =Effect.getEffect(id).setAmplifier(level).setVisible(true).setDuration(tick);
        effect.setColor(r, g, b);
        entity.addEffect(effect);
    }
    public void addEntityEffect(Entity entity,int id,int level,int tick){
        Effect effect =Effect.getEffect(id).setAmplifier(level).setDuration(tick).setVisible(false);
        entity.addEffect(effect);
    }
    //玩家经验操作
    public void setPlayerExp(Player player,int exp){
        player.sendExperience(exp);
    }
    public int getPlayerExp(Player player){
        return player.getExperience();
    }
    public void setPlayerExpLevel(Player player,int lel){
        player.sendExperienceLevel(lel);
    }
    public int getPlayerExpLevel(Player player){
        return player.getExperienceLevel();
    }
    //玩家饥饿度操作
    public void setPlayerHunger(Player player,int hunger){
        player.getFoodData().setLevel(hunger);
    }
    public int getPlayerHunger(Player player){
        return player.getFoodData().getLevel();
    }
    //获取实体id
    public String getEntityID(Entity entity){
        return entity.getId()+"";
    }
    //从id获取实体
    public Entity getEntityByLevelAndID(Level level,String id){
        Entity entity = null;
        entity = level.getEntity(Long.parseLong(id));
        return entity;
    }
    //获取实体世界
    public Level getEntityLevel(Entity entity){
        return entity.getLevel();
    }
    //获取实体名称
    public String getEntityName(Entity entity){
        return entity.getNameTag();
    }
    //获取实体位置
    public Position getEntityPosition(Entity entity){
        return entity.getPosition();
    }
    //设置实体位置
    public void setEntityPosition(Entity entity,Position position){
        entity.teleport(position);
    }
    //在指定位置构造浮空字实体
    public Entity buildFloatingText(String text,Position pos,int calltick,String callback){
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        Level level = pos.level;
        FloatingText tmp = new FloatingText(level.getChunk(((int)x)>>4,((int)z)>>4),Entity.getDefaultNBT(new Vector3(x,y,z)),calltick,callback);
        tmp.setNameTagVisible(false);
        tmp.setNameTag(text.replaceAll(";;","ahsfioabvb").replaceAll(";","\n").replaceAll("ahsfioabvb",";"));
        tmp.setNameTagAlwaysVisible(false);
        tmp.setMaxHealth(29999);
        tmp.setHealth(29999);
        tmp.setPosition(new Vector3(x,y,z));
        tmp.setScale(0.0001f);
        tmp.setLevel(level);
        return tmp;
    }
    //启动浮空字实体
    public void startDisplayFloatingText(Entity entity){
        if(entity.getNetworkId()!=61){return;}
        entity.setNameTagVisible(true);
        entity.setNameTagAlwaysVisible(true);
        entity.getLevel().addEntity(entity);
        entity.spawnToAll();
    }
    //获取指定世界的所有浮空字
    public List<FloatingText> getLevelFloatingText(Level level){
        List<FloatingText> list=new ArrayList<>();
        for(Entity e:level.getEntities()){
            if(e.getNetworkId()==61&&e.getMaxHealth()>=29998&&e.getScale()<=0.001f&&(!e.isClosed())){
                list.add((FloatingText)e);
            }
        }
        return list;
    }
    //回收所有浮空字(不对外暴露)
    public void recycleAllFloatingText(){
        for(Level l:Server.getInstance().getLevels().values()){
            for(Entity entity:getLevelFloatingText(l)){
                entity.close();

            }
        }
    }
    //回收所有BNNPC(不对外暴露)
    public void recycleAllBNNPC(){
        for (Level l:Server.getInstance().getLevels().values()){
            for(Entity entity:l.getEntities()){
                if(entity.getName().equals("BNNPC")){
                    entity.close();
                }
            }
        }
    }
    //here 5/2
    //获取实体所有的药水效果
    public List<Effect> getEntityEffect(Entity entity){
        List<Effect> list = new ArrayList<>(entity.getEffects().values());
        return list;
    }
    //获取药水效果等级
    public int getEffectLevel(Effect effect){
        return effect.getAmplifier();
    }
    //同上id
    public int getEffectID(Effect effect){
        return effect.getId();
    }
    //同上剩余时间(s)
    public int getEffectTime(Effect effect){
        return effect.getDuration();
    }
    //获取生物networkid
    public int getNetworkID(Entity entity){
        return entity.getNetworkId();
    }
    //获取生物标识名
    public String getIDName(Entity entity){
        if((entity.getNetworkId()==63||entity.getNetworkId()==-1)&&entity instanceof Player){
            return "Player";
        }else {
            return entity.getName();
        }
    }
    //生成生物
    public void spawnEntity(String name,Position pos){
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        Level level = pos.level;
        Entity entity = Entity.createEntity(name,level.getChunk(((int)x)>>4,((int)z)>>4),Entity.getDefaultNBT(new Vector3(x,y,z)));
        level.addEntity(entity);
        entity.spawnToAll();
    }
    //构建bnNPC
    public BNNPC buildNPC(Position pos,String name,String skinID){
        return new BNNPC(pos.level.getChunk(((int)pos.x)>>4,((int)pos.z)>>4),Entity.getDefaultNBT(pos),name,new Clothes(skinID));
    }
    public BNNPC buildNPC(Position pos,String name,String skinID,int calltick,String callfunction){
        return new BNNPC(pos.level.getChunk(((int)pos.x)>>4,((int)pos.z)>>4),Entity.getDefaultNBT(pos),name,new Clothes(skinID),calltick,callfunction);
    }
    public BNNPC buildNPC(Position pos,String name,String skinID,int calltick,String callfunction,String attackfunction){
        return new BNNPC(pos.level.getChunk(((int)pos.x)>>4,((int)pos.z)>>4),Entity.getDefaultNBT(pos),name,new Clothes(skinID),calltick,callfunction,attackfunction);
    }
    //展示浮空物品
    public void showFloatingItem(Position pos,Item item){
        Loader.floatingItemManager.addFloatingItem(pos,item);
    }
    public void removeFloatingItem(Position pos,Item item){
        Loader.floatingItemManager.removeFloatingItem(pos, item);
    }
}
