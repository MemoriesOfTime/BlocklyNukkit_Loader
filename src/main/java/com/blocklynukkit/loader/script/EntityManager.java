package com.blocklynukkit.loader.script;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.SetSpawnPositionPacket;
import cn.nukkit.potion.Effect;
import com.blocklynukkit.loader.api.CallbackFunction;
import com.blocklynukkit.loader.api.Comment;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.Clothes;
import com.blocklynukkit.loader.other.Entities.*;
import com.blocklynukkit.loader.other.ai.route.AdvancedRouteFinder;
import com.blocklynukkit.loader.script.bases.BaseManager;
import com.blocklynukkit.loader.utils.MathUtils;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public final class EntityManager extends BaseManager {
    public EntityManager(ScriptEngine scriptEngine) {
        super(scriptEngine);
    }

    @Override
    public String toString() {
        return "BlocklyNukkit Based Object";
    }
    //获取掉落物物品
    @Comment(value = "获取掉落物的物品")
    public Item getDropItemStack(@Comment(value = "掉落物实体对象") EntityItem entityItem){
        return entityItem.getItem();
    }
    //移除生物
    @Comment(value = "移除生物")
    public void removeEntity(@Comment(value = "生物实体对象") Entity entity){
        entity.close();
    }
    //设置生物的名称
    @Comment(value = "设置生物的名称")
    public void setEntityName(@Comment(value = "生物实体对象") Entity entity
            ,@Comment(value = "名称") String name){
        entity.setNameTag(name);
    }
    //设置生物名称高亮
    @Comment(value = "设置生物名称是否高亮（永远能看到）")
    public void setEntityNameTagAlwaysVisable(@Comment(value = "生物对象") Entity entity
            ,@Comment(value = "是否高亮") boolean vis){
        entity.setNameTagVisible(vis);
        entity.setNameTagAlwaysVisible(vis);
    }
    //
    @Comment(value = "设置生物血量")
    public void setEntityHealth( Entity entity
            ,@Comment(value = "血量") double health){
        entity.setHealth((float)health);
    }
    //设置生物最大血量
    @Comment(value = "设置生物最大血量")
    public void setEntityMaxHealth(@Comment(value = "生物对象") Entity entity
            ,@Comment(value = "最大血量") double health){
        entity.setMaxHealth((int) health);
    }
    //获取生物血量
    @Comment(value = "获取生物血量")
    public float getEntityHealth(@Comment(value = "生物对象") Entity entity){
        return entity.getHealth();
    }
    //获取生物最大血量
    @Comment(value = "获取生物最大血量")
    public float getEntityMaxHealth(@Comment(value = "生物对象") Entity entity){
        return entity.getMaxHealth();
    }
    //here 4/23
    //清除生物的药水状态
    @Comment(value = "清除生物的药水状态")
    public void clearEntityEffect(@Comment(value = "生物对象") Entity entity){
        entity.removeAllEffects();
    }
    //为生物添加药水状态
    @Comment(value = "为生物添加药水状态")
    public void addEntityEffect(@Comment(value = "生物对象") Entity entity
            ,@Comment(value = "药水id") int id
            ,@Comment(value = "药效等级") int level
            ,@Comment(value = "持续时间(刻)") int tick
            ,@Comment(value = "药效粒子颜色 r") int r
            ,@Comment(value = "药效粒子颜色 g") int g
            ,@Comment(value = "药效粒子颜色 b") int b){
        Effect effect =Effect.getEffect(id).setAmplifier(level).setVisible(true).setDuration(tick);
        effect.setColor(r, g, b);
        entity.addEffect(effect);
    }
    @Comment(value = "为生物添加药水状态")
    public void addEntityEffect(@Comment(value = "生物对象") Entity entity
            ,@Comment(value = "药水id") int id
            ,@Comment(value = "药效等级") int level
            ,@Comment(value = "持续时间(刻)") int tick){
        Effect effect =Effect.getEffect(id).setAmplifier(level).setDuration(tick).setVisible(false);
        entity.addEffect(effect);
    }
    //玩家经验操作
    @Comment(value = "给玩家添加经验")
    public void setPlayerExp(@Comment(value = "玩家对象") Player player
            ,@Comment(value = "经验值量") int exp){
        player.setExperience(exp);
        player.sendExperience(exp);
    }
    @Comment(value = "获取玩家的经验值")
    public int getPlayerExp(@Comment(value = "玩家对象") Player player){
        return player.getExperience();
    }
    @Comment(value = "设置玩家的经验值等级")
    public void setPlayerExpLevel(@Comment(value = "玩家对象") Player player
            ,@Comment(value = "经验值等级") int lel){
        player.setExperience(player.getExperience(),lel);
        player.sendExperienceLevel(lel);
    }
    @Comment(value = "设置玩家指南针指向")
    public void setPlayerCompassTarget(@Comment(value = "玩家") Player player
            ,@Comment(value = "指向的位置") Position target){
        if(target.getLevel() != null && target.getLevel().getGenerator().getDimension() != 0){
            throw new RuntimeException("Operate compass in nether is not allowed");
        }
        SetSpawnPositionPacket packet = new SetSpawnPositionPacket();
        packet.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
        packet.x = target.getFloorX();
        packet.y = target.getFloorY();
        packet.z = target.getFloorZ();
        player.dataPacket(packet);
    }
    @Comment(value = "设置玩家的披风图像")
    public void setPlayerCape(@Comment(value = "玩家") Player player
            ,@Comment(value = "披风图片路径") String capeImagePath){
        try {
            player.getSkin().setCapeData(ImageIO.read(new File(capeImagePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Comment(value = "获取玩家的经验值等级")
    public int getPlayerExpLevel(@Comment(value = "玩家对象") Player player){
        return player.getExperienceLevel();
    }
    //玩家饥饿度操作
    @Comment(value = "设置玩家的饥饿度")
    public void setPlayerHunger(@Comment(value = "玩家对象") Player player
            ,@Comment(value = "饥饿度") int hunger){
        player.getFoodData().setLevel(hunger);
    }
    @Comment(value = "获取玩家的饥饿度")
    public int getPlayerHunger(@Comment(value = "玩家对象") Player player){
        return player.getFoodData().getLevel();
    }
    //获取实体id
    @Comment(value = "获取实体的唯一标识符(ID)")
    public String getEntityID(@Comment(value = "实体对象") Entity entity){
        return entity.getId()+"";
    }
    //从id获取实体
    @Comment(value = "根据实体唯一标识符(ID)获取实体对象")
    public Entity getEntityByLevelAndID(@Comment(value = "实体所在的世界") Level level
            ,@Comment(value = "实体唯一标识符(ID)") String id){
        Entity entity = null;
        entity = level.getEntity(Long.parseLong(id));
        return entity;
    }
    //获取实体世界
    @Comment(value = "获取实体所在的世界")
    public Level getEntityLevel(@Comment(value = "实体对象") Entity entity){
        return entity.getLevel();
    }
    //获取实体名称
    @Comment(value = "获取实体名称")
    public String getEntityName(@Comment(value = "实体对象") Entity entity){
        return entity.getNameTag();
    }
    //获取实体位置
    @Comment(value = "获取实体位置")
    public Position getEntityPosition(@Comment(value = "实体对象") Entity entity){
        return entity.getPosition();
    }
    //设置实体位置
    @Comment(value = "设置实体位置")
    public void setEntityPosition(@Comment(value = "实体对象") Entity entity
            ,@Comment(value = "坐标") Position position){
        entity.teleport(position);
    }
    //在指定位置构造浮空字实体
    @Comment(value = "在指定位置构造浮空字实体")
    public Entity buildFloatingText(@Comment(value = "浮空字内容，其实就是实体名") String text
            ,@Comment(value = "浮空字实体的位置") Position pos
            ,@Comment(value = "回调函数回调间隔(tick)") int calltick
            ,@Comment(value = "回调函数名,参数(cn.nukkit.entity.Entity 浮空字实体自身)")
             @CallbackFunction(classes = "com.blocklynukkit.loader.other.Entities.FloatingText", parameters = "ent", comments = "浮空字实体") String callback){
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
    @Comment(value = "启动浮空字实体显示")
    public void startDisplayFloatingText(@Comment(value = "浮空字实体") Entity entity){
        if(entity.getNetworkId()!=61){return;}
        entity.setNameTagVisible(true);
        entity.setNameTagAlwaysVisible(true);
        entity.getLevel().addEntity(entity);
        entity.spawnToAll();
    }
    //获取指定世界的所有浮空字
    @Comment(value = "获取指定世界的所有浮空字")
    public FloatingText[] getLevelFloatingText(@Comment(value = "世界对象") Level level){
        List<FloatingText> list=new ArrayList<>();
        for(Entity e:level.getEntities()){
            if(e.getNetworkId()==61&&e.getMaxHealth()>=29998&&e.getScale()<=0.001f&&(!e.isClosed())){
                list.add((FloatingText)e);
            }
        }
        return list.toArray(new FloatingText[list.size()]);
    }
    //回收所有浮空字(不对外暴露)
    public static void recycleAllFloatingText(){
        for(Level l:Server.getInstance().getLevels().values()){
            List<FloatingText> list=new ArrayList<>();
            for(Entity e:l.getEntities()){
                if(e.getNetworkId()==61&&e.getMaxHealth()>=29998&&e.getScale()<=0.001f&&(!e.isClosed())){
                    list.add((FloatingText)e);
                }
            }
            for(Entity entity:list){
                entity.close();
            }
        }
    }
    //回收所有BNNPC(不对外暴露)
    public static void recycleAllBNNPC(){
        for (Level l:Server.getInstance().getLevels().values()){
            for(Entity entity:l.getEntities()){
                if(entity!=null)
                if(entity.getName()!=null)
                if(entity.getName().equals("BNNPC") || entity instanceof BNNPC){
                    entity.close();
                }
            }
        }
    }
    //here 5/2
    //获取实体所有的药水效果
    @Comment(value = "获取实体或玩家的所有的药水效果对象")
    public Effect[] getEntityEffect(@Comment(value = "实体对象") Entity entity){
        List<Effect> list = new ArrayList<>(entity.getEffects().values());
        return list.toArray(new Effect[list.size()]);
    }
    //获取药水效果等级
    @Comment(value = "获取药水效果等级")
    public int getEffectLevel(@Comment(value = "药水效果对象") Effect effect){
        return effect.getAmplifier();
    }
    //同上id
    @Comment(value = "获取药水效果ID")
    public int getEffectID(@Comment(value = "药水效果对象") Effect effect){
        return effect.getId();
    }
    //同上剩余时间(s)
    @Comment(value = "获取药水效果剩余的对象")
    public int getEffectTime(@Comment(value = "药水效果对象") Effect effect){
        return effect.getDuration();
    }
    //获取生物networkid
    @Comment(value = "获取生物networkid")
    public int getNetworkID(@Comment(value = "药水效果对象") Entity entity){
        return entity.getNetworkId();
    }
    //获取生物标识名
    @Comment(value = "获取生物标识名")
    public String getIDName(@Comment(value = "实体对象") Entity entity){
        if((entity.getNetworkId()==63||entity.getNetworkId()==-1)&&entity instanceof Player){
            return "Player";
        }else {
            return entity.getName();
        }
    }
    //生成生物
    @Comment(value = "生成生物")
    public Entity spawnEntity(@Comment(value = "生物标识名") String name
            ,@Comment(value = "生成的位置") Position pos){
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        Level level = pos.level;
        Entity entity = Entity.createEntity(name,level.getChunk(((int)x)>>4,((int)z)>>4),Entity.getDefaultNBT(new Vector3(x,y,z)));
        level.addEntity(entity);
        entity.spawnToAll();
        return entity;
    }
    //构建bnNPC
    @Comment(value = "构建bnNPC")
    public BNNPC buildNPC(@Comment(value = "生成bnnpc的位置") Position pos, @Comment(value = "bnnpc的名字") String name){
        return new BNNPC(pos.level.getChunk(((int)pos.x)>>4,((int)pos.z)>>4),Entity.getDefaultNBT(pos),name,new Clothes("Steve"));
    }
    @Comment(value = "构建bnNPC")
    public BNNPC buildNPC(@Comment(value = "生成bnnpc的位置") Position pos,@Comment(value = "bnnpc的名字") String name,@Comment(value = "皮肤名称") String skinID){
        return new BNNPC(pos.level.getChunk(((int)pos.x)>>4,((int)pos.z)>>4),Entity.getDefaultNBT(pos),name,new Clothes(skinID));
    }
    @Comment(value = "构建bnNPC")
    public BNNPC buildNPC(@Comment(value = "生成bnnpc的位置") Position pos,@Comment(value = "bnnpc的名字") String name,@Comment(value = "皮肤名称") String skinID,@Comment(value = "定时回调函数回调间隔") int calltick
            ,@Comment(value = "定时回调函数名，参数(cn.nukkit.Entity bnnpc自身)") @CallbackFunction(classes = {"com.blocklynukkit.loader.other.Entities.BNNPC","int"}, parameters = {"npc", "tick"}, comments = {"执行此函数的npc实体", "当前实体运行刻"}) String callfunction){
        return new BNNPC(pos.level.getChunk(((int)pos.x)>>4,((int)pos.z)>>4),Entity.getDefaultNBT(pos),name,new Clothes(skinID),calltick,callfunction);
    }
    @Comment(value = "构建bnNPC")
    public BNNPC buildNPC(@Comment(value = "生成bnnpc的位置") Position pos,@Comment(value = "bnnpc的名字") String name,@Comment(value = "皮肤名称") String skinID,@Comment(value = "定时回调函数回调间隔") int calltick
            ,@Comment(value = "定时回调函数名，参数(cn.nukkit.Entity bnnpc自身)") @CallbackFunction(classes = {"com.blocklynukkit.loader.other.Entities.BNNPC","int"}, parameters = {"npc", "tick"}, comments = {"执行此函数的npc实体", "当前实体运行刻"})  String callfunction
            ,@Comment(value = "被打回调函数名，参数(cn.nukkit.Entity bnnpc自身, cn.nukkit.event.Event 实体收到伤害事件)") @CallbackFunction(classes = {"com.blocklynukkit.loader.other.Entities.BNNPC","cn.nukkit.event.entity.EntityDamageEvent"}, parameters = {"npc", "damageEvent"}, comments = {"执行此函数的npc实体", "实体受到的伤害事件"})  String attackfunction){
        return new BNNPC(pos.level.getChunk(((int)pos.x)>>4,((int)pos.z)>>4),Entity.getDefaultNBT(pos),name,new Clothes(skinID),calltick,callfunction,attackfunction);
    }
    @Comment(value = "构建旧版bnNPC")
    public BNNPC_Fix buildNPC_Old(@Comment(value = "生成bnnpc的位置") Position pos, @Comment(value = "bnnpc的名字") String name){
        return new BNNPC_Fix(pos.level.getChunk(((int)pos.x)>>4,((int)pos.z)>>4),Entity.getDefaultNBT(pos),name,new Clothes("Steve"));
    }
    @Comment(value = "构建旧版bnNPC")
    public BNNPC_Fix buildNPC_Old(@Comment(value = "生成bnnpc的位置") Position pos,@Comment(value = "bnnpc的名字") String name,@Comment(value = "皮肤名称") String skinID){
        return new BNNPC_Fix(pos.level.getChunk(((int)pos.x)>>4,((int)pos.z)>>4),Entity.getDefaultNBT(pos),name,new Clothes(skinID));
    }
    @Comment(value = "构建旧版bnNPC")
    public BNNPC_Fix buildNPC_Old(@Comment(value = "生成bnnpc的位置") Position pos,@Comment(value = "bnnpc的名字") String name,@Comment(value = "皮肤名称") String skinID,@Comment(value = "定时回调函数回调间隔") int calltick
            ,@Comment(value = "定时回调函数名，参数(cn.nukkit.Entity bnnpc自身)") @CallbackFunction(classes = {"com.blocklynukkit.loader.other.Entities.BNNPC_Fix","int"}, parameters = {"npc", "tick"}, comments = {"执行此函数的npc实体", "当前实体运行刻"})  String callfunction){
        return new BNNPC_Fix(pos.level.getChunk(((int)pos.x)>>4,((int)pos.z)>>4),Entity.getDefaultNBT(pos),name,new Clothes(skinID),calltick,callfunction);
    }
    @Comment(value = "构建旧版bnNPC")
    public BNNPC_Fix buildNPC_Old(@Comment(value = "生成bnnpc的位置") Position pos,@Comment(value = "bnnpc的名字") String name,@Comment(value = "皮肤名称") String skinID,@Comment(value = "定时回调函数回调间隔") int calltick
            ,@Comment(value = "定时回调函数名，参数(cn.nukkit.Entity bnnpc自身)") @CallbackFunction(classes = {"com.blocklynukkit.loader.other.Entities.BNNPC","int"}, parameters = {"npc", "tick"}, comments = {"执行此函数的npc实体", "当前实体运行刻"})  String callfunction
            ,@Comment(value = "被打回调函数名，参数(cn.nukkit.Entity bnnpc自身, cn.nukkit.event.Event 实体收到伤害事件)")@CallbackFunction(classes = {"com.blocklynukkit.loader.other.Entities.BNNPC_Fix","cn.nukkit.event.entity.EntityDamageEvent"}, parameters = {"npc", "damageEvent"}, comments = {"执行此函数的npc实体", "实体受到的伤害事件"})  String attackfunction){
        return new BNNPC_Fix(pos.level.getChunk(((int)pos.x)>>4,((int)pos.z)>>4),Entity.getDefaultNBT(pos),name,new Clothes(skinID),calltick,callfunction,attackfunction);
    }
    //构建展示4d模型
    @Comment(value = "构建4d展示模型")
    public BNModel buildModel(@Comment(value = "生成模型的位置") Position pos
            ,@Comment(value = "模型4d皮肤id") String modelSkinID
            ,@Comment(value = "模型长") double length
            ,@Comment(value = "模型宽") double width
            ,@Comment(value = "模型高") double height
            ,@Comment(value = "模型缩放比例") double scale
            ,@Comment(value = "定时回调函数") @CallbackFunction(classes = {"com.blocklynukkit.loader.other.Entities.BNModel","int"}, parameters = {"model", "tick"}, comments = {"执行此函数的模型实体", "当前实体运行刻"}) String tickCallback
            ,@Comment(value = "定时回调函数回调间隔(刻)") int callTick
            ,@Comment(value = "被攻击回调函数") @CallbackFunction(classes = {"com.blocklynukkit.loader.other.Entities.BNModel","cn.nukkit.event.entity.EntityDamageEvent"}, parameters = {"model", "damageEvent"}, comments = {"执行此函数的模型实体", "实体受到的伤害事件"}) String attackCallback
            ,@Comment(value = "实体交互回调函数") @CallbackFunction(classes = {"com.blocklynukkit.loader.other.Entities.BNModel","cn.nukkit.Player","cn.nukkit.item.Item","cn.nukkit.math.Vector3"}, parameters = {"model","player","item","clickPos"}, comments = {"执行此函数的模型实体", "发起交互的玩家", "交互使用的物品", "交互点击的位置"})  String interactCallback){
        return new BNModel(pos.getChunk(), Entity.getDefaultNBT(pos),new Clothes(modelSkinID),length,width,height,scale,tickCallback,callTick,attackCallback,interactCallback);
    }
    //展示浮空物品
    @Comment(value = "展示浮空物品")
    public void showFloatingItem(@Comment(value = "展示浮空物品的坐标") Position pos
            ,@Comment(value = "展示的内容") Item item){
        Loader.floatingItemManager.addFloatingItem(pos,item);
    }
    @Comment(value = "取消浮空物品展示")
    public void removeFloatingItem(@Comment(value = "浮空物品的坐标") Position pos,@Comment(value = "浮空物品的内容") Item item){
        Loader.floatingItemManager.removeFloatingItem(pos, item);
    }
    //是否是玩家
    @Comment(value = "检测实体是否是玩家")
    public boolean isPlayer(@Comment(value = "实体对象") Entity e){
        if(e.getName().equals("BNNPC")|| e.getName().equalsIgnoreCase("NPC")){
            return false;//优先排除各种npc
        }else{
            return e instanceof Player;//因为要遍历堆栈，性能消耗最高，能不做就不做
        }
    }
    //生成方块实体
    @Comment(value = "生成实体方块")
    public Entity spawnFallingBlock(@Comment(value = "生成的位置") Position pos
            ,@Comment(value = "方块") Block block
            ,@Comment(value = "是否启用重力") boolean enableGravity
            ,@Comment(value = "掉到地上是否成为固体方块") boolean canBePlaced){
        CompoundTag tag = new CompoundTag().putList(new ListTag("Pos").add(new DoubleTag("", pos.x)).add(new DoubleTag("", pos.y)).add(new DoubleTag("", pos.z)))
                .putList(new ListTag("Motion").add(new DoubleTag("", 0)).add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
                .putList(new ListTag("Rotation").add(new FloatTag("", 0)).add(new FloatTag("", 0)))
                .putInt("TileID", block.getId()).putInt("Tile", block.getId())
                .putByte("Data", block.getDamage());
        if(enableGravity){
            EntityFallingBlock fallingBlock = new EntityFallingBlock(pos.getChunk(),tag);
            fallingBlock.spawnToAll();
            return fallingBlock;
        }else {
            NoFallBlock nofallBlock = new NoFallBlock(pos.getChunk(),tag,canBePlaced);
            nofallBlock.spawnToAll();
            return nofallBlock;
        }
    }
    //单独播放声音
    @Comment(value = "向指定玩家播放声音")
    public void makeSoundToPlayer(@Comment(value = "要指定播放声音的玩家") Player player
            ,@Comment(value = "声音名称，详见[声音列表](https://ci.opencollab.dev/job/NukkitX/job/Nukkit/job/master/javadoc/cn/nukkit/level/Sound.html)") String sound){
        player.getLevel().addSound(player, Sound.valueOf(sound));
    }
    //发射箭矢
    @Comment(value = "发射箭矢")
    public EntityArrow shootArrow(@Comment(value = "发射的起点") Position from
            ,@Comment(value = "目标终点方向坐标") Position to){
        return this.shootArrow(from, to, null, true, 1.0d);
    }
    @Comment(value = "发射箭矢")
    public EntityArrow shootArrow(@Comment(value = "发射的起点") Position from
            ,@Comment(value = "目标终点方向坐标") Position to
            ,@Comment(value = "速度扩倍倍率") double multiply){
        return this.shootArrow(from, to, null, true, multiply);
    }
    @Comment(value = "发射箭矢")
    public EntityArrow shootArrow(@Comment(value = "发射的起点") Position from
            ,@Comment(value = "目标终点方向坐标") Position to
            ,@Comment(value = "是否可以被捡起") boolean canPickUp){
        return this.shootArrow(from, to, null, canPickUp, 1.0d);
    }
    @Comment(value = "发射箭矢")
    public EntityArrow shootArrow(@Comment(value = "发射的起点") Position from
            ,@Comment(value = "目标终点方向坐标") Position to
            ,@Comment(value = "是否可以被捡起") boolean canPickUp
            ,@Comment(value = "速度扩倍倍率") double multiply){
        return this.shootArrow(from, to, null, canPickUp, multiply);
    }
    @Comment(value = "发射箭矢")
    public EntityArrow shootArrow(@Comment(value = "发射的起点") Position from
            ,@Comment(value = "目标终点方向坐标") Position to
            ,@Comment(value = "发射箭矢的实体") Entity shooter
            ,@Comment(value = "是否可以被捡起") boolean canPickUp
            ,@Comment(value = "速度扩倍倍率") double multiply){
        Entity k;
        if(shooter != null){
            k = Entity.createEntity("Arrow", from, shooter);
        }else {
            k = Entity.createEntity("Arrow", from);
        }
        if (!(k instanceof EntityArrow)) {
            return null;
        }
        EntityArrow arrow = (EntityArrow) k;
        double xdiff = to.x - from.x;
        double zdiff = to.z - from.z;
        double angle = Math.atan2(zdiff, xdiff);
        double yaw = ((angle * 180) / Math.PI) - 90;
        double ydiff = to.y - from.y;
        Vector2 v = new Vector2(from.x, from.z);
        double dist = v.distance(to.x, to.z);
        angle = Math.atan2(dist, ydiff);
        double pitch = ((angle * 180) / Math.PI) - 90;
        double yawR = MathUtils.toRadians(yaw);
        double pitchR = MathUtils.toRadians(pitch);

        double verticalMultiplier = Math.cos(pitchR);
        double x = verticalMultiplier * Math.sin(-yawR);
        double z = verticalMultiplier * Math.cos(yawR);
        double y = Math.sin(-MathUtils.toRadians(pitch));
        double magnitude = Math.sqrt(x * x + y * y + z * z);
        if (magnitude > 0.0D) {
            x += x * (multiply - magnitude) / magnitude;
            y += y * (multiply - magnitude) / magnitude;
            z += z * (multiply - magnitude) / magnitude;
        }

        ThreadLocalRandom rand = ThreadLocalRandom.current();
        x += rand.nextGaussian() * 0.007499999832361937D * 6.0D;
        y += rand.nextGaussian() * 0.007499999832361937D * 6.0D;
        z += rand.nextGaussian() * 0.007499999832361937D * 6.0D;
        arrow.setMotion(new Vector3(x, y, z));

        EntityShootBowEvent ev = new EntityShootBowEvent((EntityLiving) shooter, Item.get(Item.ARROW, 0, 1), arrow, multiply);
        Server.getInstance().getPluginManager().callEvent(ev);
        EntityProjectile projectile = ev.getProjectile();
        if (ev.isCancelled()) {
            projectile.close();
        } else {
            ProjectileLaunchEvent launch = new ProjectileLaunchEvent(projectile);
            Server.getInstance().getPluginManager().callEvent(launch);
            if (launch.isCancelled()) {
                projectile.close();
            } else {
                projectile.spawnToAll();
                ((EntityArrow) projectile).setPickupMode(canPickUp?EntityArrow.PICKUP_ANY:EntityArrow.PICKUP_NONE);
                from.level.addSound(from, Sound.RANDOM_BOW);
            }
        }
        return arrow;
    }
    //发射雪球
    @Comment(value = "发射雪球")
    public EntitySnowball shootSnowball(@Comment(value = "发射的起点") Position from
            ,@Comment(value = "目标终点方向坐标") Position to){
        return this.shootSnowball(from, to, null, true, 1.0d);
    }
    @Comment(value = "发射雪球")
    public EntitySnowball shootSnowball(@Comment(value = "发射的起点") Position from
            ,@Comment(value = "目标终点方向坐标") Position to
            ,@Comment(value = "速度扩倍倍率") double multiply){
        return this.shootSnowball(from, to, null, true, multiply);
    }
    @Comment(value = "发射雪球")
    public EntitySnowball shootSnowball(@Comment(value = "发射的起点") Position from
            ,@Comment(value = "目标终点方向坐标") Position to
            ,@Comment(value = "是否可以被捡起") boolean canPickUp){
        return this.shootSnowball(from, to, null, canPickUp, 1.0d);
    }
    @Comment(value = "发射雪球")
    public EntitySnowball shootSnowball(@Comment(value = "发射的起点") Position from
            ,@Comment(value = "目标终点方向坐标") Position to
            ,@Comment(value = "是否可以被捡起") boolean canPickUp
            ,@Comment(value = "速度扩倍倍率") double multiply){
        return this.shootSnowball(from, to, null, canPickUp, multiply);
    }
    @Comment(value = "发射雪球")
    public EntitySnowball shootSnowball(@Comment(value = "发射的起点") Position from
            , @Comment(value = "目标终点方向坐标") Position to
            , @Comment(value = "发射雪球的实体") Entity shooter
            , @Comment(value = "是否可以被捡起") boolean canPickUp
            , @Comment(value = "速度扩倍倍率") double multiply){
        EntitySnowball arrow = new EntitySnowball(from.getChunk(),Entity.getDefaultNBT(from), shooter);
        double xdiff = to.x - from.x;
        double zdiff = to.z - from.z;
        double angle = Math.atan2(zdiff, xdiff);
        double yaw = ((angle * 180) / Math.PI) - 90;
        double ydiff = to.y - from.y;
        Vector2 v = new Vector2(from.x, from.z);
        double dist = v.distance(to.x, to.z);
        angle = Math.atan2(dist, ydiff);
        double pitch = ((angle * 180) / Math.PI) - 90;
        double yawR = MathUtils.toRadians(yaw);
        double pitchR = MathUtils.toRadians(pitch);

        double verticalMultiplier = Math.cos(pitchR);
        double x = verticalMultiplier * Math.sin(-yawR);
        double z = verticalMultiplier * Math.cos(yawR);
        double y = Math.sin(-MathUtils.toRadians(pitch));
        double magnitude = Math.sqrt(x * x + y * y + z * z);
        if (magnitude > 0.0D) {
            x += x * (multiply - magnitude) / magnitude;
            y += y * (multiply - magnitude) / magnitude;
            z += z * (multiply - magnitude) / magnitude;
        }

        ThreadLocalRandom rand = ThreadLocalRandom.current();
        x += rand.nextGaussian() * 0.007499999832361937D * 6.0D;
        y += rand.nextGaussian() * 0.007499999832361937D * 6.0D;
        z += rand.nextGaussian() * 0.007499999832361937D * 6.0D;
        arrow.setMotion(new Vector3(x, y, z));

        EntityShootBowEvent ev = new EntityShootBowEvent((EntityLiving) shooter, Item.get(Item.SNOWBALL, 0, 1), arrow, multiply);
        Server.getInstance().getPluginManager().callEvent(ev);
        EntityProjectile projectile = ev.getProjectile();
        if (ev.isCancelled()) {
            projectile.close();
        } else {
            ProjectileLaunchEvent launch = new ProjectileLaunchEvent(projectile);
            Server.getInstance().getPluginManager().callEvent(launch);
            if (launch.isCancelled()) {
                projectile.close();
            } else {
                projectile.spawnToAll();
                from.level.addSound(from, Sound.RANDOM_POP);
            }
        }
        return arrow;
    }
    //转视角
    @Comment(value = "让生物转动视角看向指定坐标")
    public void lookAt(@Comment(value = "实体对象") Entity e,@Comment(value = "坐标") Position pos){
        double xdiff = pos.x - e.x;
        double zdiff = pos.z - e.z;
        double angle = Math.atan2(zdiff, xdiff);
        double yaw = ((angle * 180) / Math.PI) - 90;
        double ydiff = pos.y - e.y;
        Vector2 v = new Vector2(e.x, e.z);
        double dist = v.distance(pos.x, pos.z);
        angle = Math.atan2(dist, ydiff);
        double pitch = ((angle * 180) / Math.PI) - 90;
        e.yaw = yaw;
        e.pitch = pitch;
    }
    //显示受伤动画
    @Comment(value = "让指定生物显示受伤动画")
    public void displayHurt(@Comment(value = "实体对象") Entity e){
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = e.getId();
        pk.event = EntityEventPacket.HURT_ANIMATION;
        e.getViewers().values().forEach((player -> player.dataPacket(pk)));
    }
    //显示死亡动画
    @Comment(value = "让指定生物显示死亡动画")
    public void displayDie(@Comment(value = "实体对象") Entity e){
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = e.getId();
        pk.event = EntityEventPacket.DEATH_ANIMATION;
        e.getViewers().values().forEach((player -> player.dataPacket(pk)));
    }

    //创建寻路器
    @Comment(value = "为实体创建寻路器")
    public AdvancedRouteFinder buildRouteFinder(Entity entity){
        return new AdvancedRouteFinder(entity);
    }
}
