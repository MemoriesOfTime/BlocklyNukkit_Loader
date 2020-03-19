package dls.icesight.blocklynukkit.script;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class BlockItemManager {
    //未完成-添加声音
    public void makeSound(Position position,String soundname){
        position.getLevel().addSound(new Vector3(position.x,position.y,position.z), Sound.valueOf(soundname));
    }
    //未完成-生成经验求
    public void makeExpBall(Position position,int point){
        position.getLevel().dropExpOrb(new Vector3(position.x,position.y,position.z),point);
    }
    //未完成-生成掉落物
    public void makeDropItem(Position position, Item item){
        position.getLevel().dropItem(new Vector3(position.x,position.y,position.z),item);
    }
    //未完成-获取方块
    public Block getBlock(Position position) {
        return position.getLevelBlock();
    }
    //未完成-获取生物数组
    public Entity[] getLevelEntities(Position position){
        return position.getLevel().getEntities();
    }
    //未完成-获取世界中的玩家
    public List getLevelPlayers(Position position){
        return new ArrayList(position.getLevel().getPlayers().values());
    }
    //未完成-获取是否晴天
    public boolean getIsSunny(Position position){
        Level level=position.getLevel();
        return !(level.isRaining()||level.isThundering());
    }
    //未完成-设置天气
    public void setLevelWeather(Position position,String mode){
        Level level=position.getLevel();
        if (!mode.equals("clear")) {
            if(mode.equals("rain")){
                level.setRaining(true);
                level.setThundering(false);
            }else if(mode.equals("thunder")){
                level.setThundering(true);
                level.setRaining(false);
            }
        } else {
            level.setRaining(false);
            level.setThundering(false);
        }
    }
    //未完成-获取白天黑夜
    public boolean isDay(Position position){
        return position.getLevel().isDaytime();
    }
    //未完成-设置方块
    public void setBlock(Position position,Block block,boolean particle){
        if(particle){
            position.getLevel().addParticle(new DestroyBlockParticle(new Vector3(position.x+0.5,position.y+0.5,position.z+0.5),getBlock(position)));
        }
        position.getLevel().setBlock(new Vector3(position.x,position.y,position.z),block);
    }
    /********************************* 纯方块物品方法 *************************************/
    //未完成-构建方块
    public Block buildBlock(int id,int data){
        return Block.get(id, data);
    }
    //未完成-获取方块id*
    //未完成-构建物品
    public Item buildItem(int id,int data,int count){
        return Item.get(id,data,count);
    }
    //未完成-从方块构建物品
    public Item buildItemFromBlock(Block block){
        return block.toItem();
    }
    //未完成-设置物品数量*
    //未完成-设置物品数据值*
    //未完成-设置物品名称*
    //未完成-获取物品id*
    //未完成-获取物品数量*
    //未完成-获取物品数据值*
    //未完成-获取物品名称*
}
