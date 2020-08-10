package com.blocklynukkit.loader.other.Entities;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.Utils;
import com.blocklynukkit.loader.other.Clothes;
import com.mobplugin.route.WalkerRouteFinder;


import java.nio.charset.StandardCharsets;

public class BNNPC extends EntityHuman {
    public Vector3 dvec = new Vector3(0,0,0);
    public WalkerRouteFinder routeFinder = null;

    public boolean enableAttack = false;
    public boolean enableHurt = false;
    public boolean enableGravity = false;public double g = 9.8d;
    public boolean enableKnockBack = false;public double knockBase = 1.2d;

    public String callbackfunction = "BNNPCUpdate";
    public String attackfunction = "BNNPCAttack";
    public int calltimetick = 10;

    public boolean isjumping = false;public int jumpingtick = 0;public double jumphigh = 1;
    public int fallingtick = 0;
    public boolean isonRoute = false;public Vector3 nowtarget = null;public double speed = 3;public int actions=0;public Vector3 actioinVec = new Vector3();public int routeMax = 50;

    public BNNPC(FullChunk chunk, CompoundTag nbt, String name, Clothes clothes) {
        super(chunk, nbt.putString("NameTag", name).putString("name", "BNNPC")
                .putCompound("Skin", new CompoundTag()).putBoolean("ishuman", true).putBoolean("npc", true)
                .putFloat("scale", 1));
        Skin sk = clothes.build();
        nbt.putByteArray("Data",sk.getSkinData().data);
        nbt.putString("ModelID", this.skin.getSkinId()).putString("GeometryName", clothes.gen).putByteArray("GeometryData", sk.getGeometryData().getBytes(StandardCharsets.UTF_8));
        nbt.putString("SkinResourcePatch","{\"geometry\" : {\"default\" : \""+clothes.gen+"\"}}\n");
        this.setSkin(clothes.build());
        this.setNameTag(name);
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
    }
    public BNNPC(FullChunk chunk, CompoundTag nbt, String name, Clothes clothes,int calltick, String callback){
        this(chunk, nbt, name, clothes);
        calltimetick = calltick;
        callbackfunction=callback;
    }
    public BNNPC(FullChunk chunk, CompoundTag nbt, String name, Clothes clothes,int calltick, String callback, String attackcall){
        this(chunk, nbt, name, clothes,calltick, callback);
        attackfunction = attackcall;
    }
    @Override
    public String getName(){
        return "BNNPC";
    }
    @Override
    public int getNetworkId(){
        return 63;
    }
    @Override
    public float getGravity() {
        return (float) g/20;
    }
    @Override
    public void knockBack(Entity attacker, double damage, double x, double z, double base) {
        double f = Math.sqrt(x * x + z * z);
        if (f > 0.0D) {
            f = 1.0D / f;
            Vector3 motion = new Vector3(0, 0, 0);
            motion.x /= 2.0D;
            motion.y /= 2.0D;
            motion.z /= 2.0D;
            motion.x += x * f * base;
            motion.y += base;
            motion.z += z * f * base;
            if (motion.y > base) {
                motion.y = base;
            }
            this.dvec.x+=motion.x;this.dvec.y+=motion.y;this.dvec.z+=motion.z;
        }
    }
    @Override
    public boolean onUpdate(int currentTick){
        //调用玩家自定义函数
        if(currentTick%calltimetick==0){
            Loader.plugin.call(callbackfunction,this,currentTick);
        }
        //计算并执行线性重力
        if(enableGravity && !isjumping ){
            if (Position.fromObject(this,this.level).add(0,-0.05,0).getLevelBlock().isSolid()) {
                if(!(this.y-((int)this.y)<=0.01)){
                    this.y = Math.round(this.y);
                    if(fallingtick>=10){
                        this.level.addParticle(new DestroyBlockParticle(this,Position.fromObject(this,this.level).add(0,-0.05,0).getLevelBlock()));
                    }
                    fallingtick=0;
                }
            } else if (this.y > -this.getGravity() * 4) {
                if (!(this.level.getBlock(new Vector3(NukkitMath.floorDouble(this.x), (int) (this.y + 0.8), NukkitMath.floorDouble(this.z))) instanceof BlockLiquid)) {
                    this.dvec.y -= this.getGravity();
                    fallingtick++;
                }
            } else {
                this.dvec.y -= this.getGravity();
                fallingtick++;
            }
        }
        //处理跳跃
        if(isjumping){
            if(jumpingtick>10){
                jumpingtick=0;isjumping=false;
            }else {
                jumpingtick++;
                this.dvec.y+=0.5*(10-jumpingtick)*jumphigh*0.05;
            }
        }
        //处理路径
        if(isonRoute && routeFinder!=null && routeFinder.hasNext()){
            if(Position.fromObject(this,this.level).add(0,-0.5,0).getLevelBlock().getId()== BlockID.WATER){
                this.setSwim(true);
            }else {
                this.setSwim(false);
            }
            if(nowtarget==null){
                nowtarget = routeFinder.next();
                double dis = nowtarget.distance(this);
                this.actions=(int)(dis/(speed*0.05));
                if(nowtarget.y-this.y>0.1){
                    this.actioinVec.x = (nowtarget.x-this.x)/(actions);
                    this.actioinVec.z = (nowtarget.z-this.z)/actions;
                    this.actioinVec.y = (nowtarget.y-this.y)/(actions);
                    actions+=8;
                }
                this.actioinVec.x = (nowtarget.x-this.x)/actions;
                this.actioinVec.z = (nowtarget.z-this.z)/actions;
                this.actioinVec.y = (nowtarget.y-this.y)/actions;
            }else {
                if(actions>0){
                    if(actioinVec.y>0.0001 && actions> 8){
                        this.dvec.x+=actioinVec.x;
                        this.dvec.z+=actioinVec.z;
                    }else if(actioinVec.y<0.0001){
                        this.dvec.x+=actioinVec.x;
                        this.dvec.z+=actioinVec.z;
                    }

                    if(actioinVec.y>0.0001){
                        this.isjumping=true;
                    }else {
                        this.dvec.y+=actioinVec.y;
                    }
                    this.actions--;
                }else {
                    this.actions=0;
                    this.dvec.x+=(nowtarget.x-this.x);
                    this.dvec.y+=(nowtarget.y-this.y);
                    this.dvec.z+=(nowtarget.z-this.z);
                    if(routeFinder.hasNext()){
                        nowtarget = routeFinder.next();
                        double dis = nowtarget.distance(this);
                        this.actions=(int)(dis/(speed*0.05));
                        this.actioinVec.x = (nowtarget.x-this.x)/actions;
                        this.actioinVec.z = (nowtarget.z-this.z)/actions;
                        this.actioinVec.y = (nowtarget.y-this.y)/actions;
                    }else {
                        isonRoute = false;
                    }
                }
            }

        }
        if(routeFinder!=null && !routeFinder.hasNext()){
            this.isonRoute=false;
            this.nowtarget=null;
        }
        //处理当前刻运动
        this.x+=dvec.x;this.y+=dvec.y;this.z+=dvec.z;
        dvec = new Vector3(0,0,0);

        //调用nk预设函数
        return super.onUpdate(currentTick);
    }
    @Override
    public boolean attack(EntityDamageEvent source) {
        this.updateMovement();
        Loader.plugin.call(attackfunction,this,source);
        if(enableHurt){
            this.displayHurt();
        }
        if(enableAttack){
            if(enableKnockBack){
                if(source instanceof EntityDamageByEntityEvent){
                    Entity damager = ((EntityDamageByEntityEvent)source).getDamager();
                    this.knockBack(damager,source.getFinalDamage(),-(damager.x-this.x),-(damager.z-this.z),knockBase);
                }
            }
            return super.attack(source);
        }else {
            return true;
        }
    }
    public void turnRound(double yaw){
        this.yaw+=yaw;
    }
    public void headUp(double pitch){
        this.pitch+=pitch;
    }
    public void setEnableAttack(boolean attack){
        this.enableAttack=attack;
    }
    public void setEnableAttack(){
        this.setEnableAttack(true);
    }
    public void setEnableHurt(boolean hurt){
        this.enableHurt=hurt;
    }
    public void setEnableHurt(){
        this.setEnableHurt(true);
    }
    public void setEnableGravity(boolean gravity){
        this.enableGravity=gravity;
    }
    public void setEnableGravity(){
        this.setEnableGravity(true);
    }
    public void setG(double newg){
        this.g=newg;
    }
    public void setSneak(boolean sneak){
        this.setSneaking(sneak);
    }
    public void setSneak(){
        this.setSneaking(!this.isSneaking());
    }
    public void setJumpHigh(double j){
        this.jumphigh = j;
    }
    public void setEnableKnockBack(boolean knock){
        this.enableKnockBack = knock;
    }
    public void setEnableKnockBack(){
        this.setEnableKnockBack(true);
    }
    public void setKnockBase(double base){
        this.knockBase = base;
    }
    public void setSpeed(double s){
        this.speed=s;
    }
    public void setRouteMax(int m){
        this.routeMax = m;
    }
    public void setSwim(boolean swim){
        this.setSwimming(swim);
    }
    public void setSwim(){
        this.setSwim(!this.isSwimming());
    }
    public void setTickCallback(String callback){
        callbackfunction = callback;
    }
    public void setAttackCallback(String callback){
        attackfunction = callback;
    }
    public void displayHurt(){
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.id;
        pk.event = EntityEventPacket.HURT_ANIMATION;
        this.getLevel().getPlayers().values().forEach((player -> player.dataPacket(pk)));
    }
    public void displaySwing(){
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.id;
        pk.event = EntityEventPacket.ARM_SWING;
        this.getLevel().getPlayers().values().forEach((player -> player.dataPacket(pk)));
    }
    public void jump(){
        if(isonRoute)return;
        this.isjumping=true;
    }
    public void lookAt(Position pos){
        double xdiff = pos.x - this.x;
        double zdiff = pos.z - this.z;
        double angle = Math.atan2(zdiff, xdiff);
        double yaw = ((angle * 180) / Math.PI) - 90;
        double ydiff = pos.y - this.y;
        Vector2 v = new Vector2(this.x, this.z);
        double dist = v.distance(pos.x, pos.z);
        angle = Math.atan2(dist, ydiff);
        double pitch = ((angle * 180) / Math.PI) - 90;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    public Player getNearestPlayer(){
        Player nearest = null;double distance = 999999999;
        for(Player p:Server.getInstance().getOnlinePlayers().values()){
            if(!p.level.getName().equals(this.level.getName())){
                continue;
            }else {
                double d = this.distance(p);
                if(distance>d){
                    nearest = p;
                    distance = d;
                }
            }
        }
        return nearest;
    }
    public boolean isSneak(){
        return this.isSneaking();
    }
    public boolean canMoveTo(Position to){
        WalkerRouteFinder finder = new WalkerRouteFinder(this,this,to);
        finder.setSearchLimit(routeMax);
        boolean out = finder.search();
        return out;
    }
    public boolean findAndMove(Position to){
        if(isonRoute){
            return false;
        }
        routeFinder = new WalkerRouteFinder(this,this,to);
        routeFinder.setSearchLimit(routeMax);
        boolean out = routeFinder.search();
        if(out){
            isonRoute = true;
            return true;
        }else {
            return false;
        }
    }
    public void stopMove(){
        this.isonRoute = false;
        this.actioinVec = new Vector3();
        this.actions = 0;
    }
    public void hit(Entity entity){
        double d;
        if(this.getInventory().getItemInHand()!=null){
            d = this.getInventory().getItemInHand().getAttackDamage();
        }else {
            d = 1;
        }
        entity.attack(new EntityDamageByEntityEvent(this,entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK,(float) d,0.5f));
        this.displaySwing();
    }
    public void start(){
        this.spawnToAll();
    }
}
