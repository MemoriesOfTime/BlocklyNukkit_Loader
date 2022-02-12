package com.blocklynukkit.loader.other.Entities;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.EmotePacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.Clothes;
import com.blocklynukkit.loader.other.ai.entity.MovingEntity;
import com.blocklynukkit.loader.other.ai.entity.MovingVanillaEntity;
import com.blocklynukkit.loader.other.ai.route.AdvancedRouteFinder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class VanillaNPC extends MovingVanillaEntity {
    public VanillaNPC vanillaNPC;

    public Vector3 dvec = new Vector3(0, 0, 0);

    public List<Item> extraDropItems = new ArrayList<>();
    public boolean dropHand = false;
    public boolean dropOffhand = false;
    public List<Integer> dropSlot = new ArrayList<>();

    public boolean enableAttack = false;
    public boolean enableHurt = false;
    public boolean enableGravity = false;
    public double g = 9.8d;
    public boolean enableKnockBack = false;
    public double knockBase = 1.2d;

    public String callbackfunction = "VanillaNPCUpdate";
    public String attackfunction = "VanillaNPCAttack";
    public int calltimetick = 10;

    public boolean isjumping = false;
    public double jumphigh = 1;
    public boolean isonRoute = false;
    public Vector3 nowtarget = null;
    public double speed = 3;
    public int actions = 0;
    public Vector3 actioinVec = new Vector3();
    public int routeMax = 50;
    public Vector3 previousTo = null;
    public boolean justDamaged = false;

    public float width = 0.6f;
    public float length = 0.6f;
    public float height = 1.8f;
    public float eyeHeight = 1.62f;

    public final int networkId;

    public VanillaNPC(FullChunk chunk, CompoundTag nbt, int networkId) {
        super(chunk, nbt);
        super.close();
        this.networkId = networkId;
    }

    public VanillaNPC(FullChunk chunk, CompoundTag nbt, String name, int networkId) {
        super(chunk, nbt.putString("NameTag", name).putString("name", "VanillaNPC")
                .putFloat("scale", 1));
        this.setNameTag(name);
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setScale(1.0f);
        vanillaNPC = this;
        this.networkId = networkId;
    }

    public VanillaNPC(FullChunk chunk, CompoundTag nbt, String name, int networkId, int calltick, String callback) {
        this(chunk, nbt, name, networkId);
        calltimetick = calltick;
        callbackfunction = callback;
    }

    public VanillaNPC(FullChunk chunk, CompoundTag nbt, String name, int networkId, int calltick, String callback, String attackcall) {
        this(chunk, nbt, name, networkId, calltick, callback);
        attackfunction = attackcall;
    }

    @Override
    public String getName() {
        return "VanillaNPC";
    }

    @Override
    public int getNetworkId() {
        return networkId;
    }

    @Override
    public float getGravity() {
        return (float) g / 20;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getLength() {
        return length;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getEyeHeight() {
        return eyeHeight;
        //return 0.0F;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setEyeHeight(float eyeHeight) {
        this.eyeHeight = eyeHeight;
    }

    @Override
    public float getMovementSpeed() {
        return (float) this.speed / 30;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        //调用玩家自定义函数
        if (calltimetick != 0 && currentTick % calltimetick == 0 && this.isAlive()) {
            Loader.plugin.call(callbackfunction, this, currentTick);
        }
        //更新乘客
        try {
            for (Entity entity : this.getPassengers()) {
                if (entity.distance(this) > 3) {
                    this.setEntityRideOff(entity);
                }
            }
        } catch (java.util.ConcurrentModificationException e) {
            //ignore
        }
        //处理骑乘
        this.updatePassengers();
        //调用nk预设函数
        return super.onUpdate(currentTick);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        this.updateMovement();
        Loader.plugin.call(attackfunction, this, source);
        if (enableHurt) {
            this.displayHurt();
        }
        if (enableAttack) {
            if (enableKnockBack) {
                justDamaged = true;
                if (source instanceof EntityDamageByEntityEvent) {
                    Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
                    this.knockBack(damager, source.getFinalDamage(), -(damager.x - this.x), -(damager.z - this.z), knockBase);
                }
            }
            return super.attack(source);
        } else {
            return true;
        }
    }

    @Override
    public void close() {
        List<Item> tmp = new ArrayList<>(extraDropItems);
        tmp.forEach(each -> vanillaNPC.getLevel().dropItem(vanillaNPC, each));
        super.close();
    }

    public void addExtraDropItem(Item item) {
        this.extraDropItems.add(item);
    }

    public boolean hasDropItem(Item item) {
        if (dropHand) {
            return true;
        } else if (dropOffhand) {
            return true;
        } else {
            for (Item i : this.extraDropItems) {
                if (item.equals(i, true, true)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeExtraDropItem(Item item) {
        this.extraDropItems.remove(item);
    }

    public Item[] getExtraDropItems() {
        return this.extraDropItems.toArray(new Item[0]);
    }

    public Item[] getDropItems() {
        List<Item> tmp = new ArrayList<>(extraDropItems);
        return tmp.toArray(new Item[0]);
    }

    public void setDropHand(boolean drop) {
        this.dropHand = drop;
    }

    public void setDropHand() {
        this.setDropHand(true);
    }

    public void setDropOffhand(boolean drop) {
        this.dropOffhand = drop;
    }

    public void setDropOffhand() {
        this.setDropOffhand(true);
    }

    public void addDropSlot(int slot) {
        this.dropSlot.add(slot);
    }

    public int[] getDropSlots() {
        int[] tmp = new int[dropSlot.size()];
        int pos = 0;
        for (int x : dropSlot) {
            tmp[pos] = x;
            pos++;
        }
        return tmp;
    }

    public void removeDropSlot(int slot) {
        this.dropSlot.remove(slot);
    }

    public void turnRound(double yaw) {
        this.yaw += yaw;
    }

    public void headUp(double pitch) {
        this.pitch += pitch;
    }

    public void setEnableAttack(boolean attack) {
        this.enableAttack = attack;
    }

    public void setEnableAttack() {
        this.setEnableAttack(true);
    }

    public void setEnableHurt(boolean hurt) {
        this.enableHurt = hurt;
    }

    public void setEnableHurt() {
        this.setEnableHurt(true);
    }

    public void setEnableGravity(boolean gravity) {
        this.enableGravity = gravity;
    }

    public void setEnableGravity() {
        this.setEnableGravity(true);
    }

    public void setG(double newg) {
        this.g = newg;
    }

    public void setSneak(boolean sneak) {
        this.setSneaking(sneak);
    }

    public void setSneak() {
        this.setSneaking(!this.isSneaking());
    }

    public void setJumpHigh(double j) {
        this.jumphigh = j;
    }

    public void setEnableKnockBack(boolean knock) {
        this.enableKnockBack = knock;
    }

    public void setEnableKnockBack() {
        this.setEnableKnockBack(true);
    }

    public void setKnockBase(double base) {
        this.knockBase = base;
    }

    public void setSpeed(double s) {
        this.speed = s;
    }

    public void setRouteMax(int m) {
        this.routeMax = m;
        this.route.setSearchLimit(m);
    }

    public void setSwim(boolean swim) {
        this.setSwimming(swim);
    }

    public void setSwim() {
        this.setSwim(!this.isSwimming());
    }

    public void setTickCallback(String callback) {
        callbackfunction = callback;
    }

    public void setAttackCallback(String callback) {
        attackfunction = callback;
    }

    public void displayHurt() {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.id;
        pk.event = EntityEventPacket.HURT_ANIMATION;
        this.getLevel().getPlayers().values().forEach((player -> player.dataPacket(pk)));
    }

    public void displaySwing() {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.id;
        pk.event = EntityEventPacket.ARM_SWING;
        this.getLevel().getPlayers().values().forEach((player -> player.dataPacket(pk)));
    }

    public void jump() {
        if (this.onGround) {
            this.motionY = 0.42 * jumphigh;
        }
    }

    public void lookAt(Position pos) {
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

    public Player getNearestPlayer(double far) {
        Player nearest = null;
        double distance = 999999999;
        for (Player p : Server.getInstance().getOnlinePlayers().values()) {
            if (!p.level.getName().equals(this.level.getName()) || p.distance(this) > far) {
                continue;
            } else {
                double d = this.distance(p);
                if (distance > d) {
                    nearest = p;
                    distance = d;
                }
            }
        }
        return nearest;
    }

    public Player getNearestPlayer() {
        return getNearestPlayer(999999998);
    }

    public List<Player> getPlayersIn(double distance) {
        ArrayList<Player> players = new ArrayList<>();
        for (Player p : Server.getInstance().getOnlinePlayers().values()) {
            if (!p.level.getName().equals(this.level.getName())) {
                continue;
            } else {
                double d = this.distance(p);
                if (d < distance) {
                    players.add(p);
                }
            }
        }
        return players;
    }

    public List<Entity> getEntitiesIn(double distance) {
        ArrayList<Entity> entities = new ArrayList<>();
        for (Entity e : this.level.getEntities()) {
            if (e.distance(this) <= distance) {
                entities.add(e);
            }
        }
        return entities;
    }

    public boolean isSneak() {
        return this.isSneaking();
    }

    public boolean canMoveTo(Position to) {
        AdvancedRouteFinder finder = new AdvancedRouteFinder(this);
        finder.setStart(this);
        finder.setDestination(to);
        finder.setSearchLimit(routeMax);
        finder.setLevel(to.level);
        finder.search();
        return finder.isSuccess();
    }

    public boolean findAndMove(Position to) {
        this.route = new AdvancedRouteFinder(this);
        this.route.setStart(this);
        this.route.setDestination(to);
        this.route.setSearchLimit(routeMax);
        this.route.setLevel(to.level);
        this.route.search();
        this.setTarget(to, true);
        return this.route.isSuccess();
    }

    public boolean directMove(Position to) {
        this.setTarget(to, true);
        return true;
    }

    public void stopMove() {
        this.route.forceStop();

    }

    public void hit(Entity entity) {
        this.displaySwing();
        entity.attack(new EntityDamageByEntityEvent(this, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1f, 0.5f));
    }

    public void start() {
        this.spawnToAll();
    }

    public void start(Player player) {
        this.spawnTo(player);
    }

    public void setEntityRideOn(Entity entity) {
        this.mountEntity(entity);
    }

    public void isEntityRideOn(Entity entity) {
        this.isPassenger(entity);
    }

    public void setEntityRideOff(Entity entity) {
        entity.riding = null;
        this.dismountEntity(entity);
        entity.setPosition(this);
        this.getPassengers().clear();
        this.updatePassengers();
    }

    public Player getRidingPlayer() {
        for (Entity entity : this.getPassengers()) {
            if (entity instanceof Player) {
                return (Player) entity;
            }
        }
        return null;
    }
}
