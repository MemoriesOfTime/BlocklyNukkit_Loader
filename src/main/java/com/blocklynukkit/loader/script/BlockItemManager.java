package com.blocklynukkit.loader.script;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.inventory.FurnaceRecipe;
import cn.nukkit.inventory.ShapedRecipe;
import cn.nukkit.inventory.ShapelessRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItemMapping;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.randomitem.ConstantItemSelector;
import cn.nukkit.item.randomitem.Fishing;
import cn.nukkit.item.randomitem.RandomItem;
import cn.nukkit.item.randomitem.Selector;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.blocklynukkit.loader.Comment;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.script.bases.BaseManager;
import com.blocklynukkit.loader.utils.Utils;
import io.netty.util.collection.CharObjectHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import javassist.*;

import javax.script.ScriptEngine;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class BlockItemManager extends BaseManager {
    public BlockItemManager(ScriptEngine scriptEngine) {
        super(scriptEngine);
    }

    @Override
    public String toString() {
        return "BlocklyNukkit Based Object";
    }
    //-添加声音
    @Comment(value = "在指定位置播放声音")
    public void makeSound(@Comment(value = "播放声音的坐标") Position position
            ,@Comment(value = "声音名称，详见[声音列表](https://ci.opencollab.dev/job/NukkitX/job/Nukkit/job/master/javadoc/cn/nukkit/level/Sound.html)") String soundname){
        position.getLevel().addSound(position, Sound.valueOf(soundname));
    }
    //-生成经验求
    @Comment(value = "在指定位置生成经验球")
    public void makeExpBall(@Comment(value = "生成经验球的位置") Position position
            ,@Comment(value = "包含的经验点数") int point){
        position.getLevel().dropExpOrb(position,point);
    }
    //-生成掉落物
    @Comment(value = "在指定位置生成掉落物")
    public void makeDropItem(@Comment(value = "生成掉落物的位置") Position position
            ,@Comment(value = "生成的掉落物实体的内容") Item item){
        position.getLevel().dropItem(position,item);
    }
    @Comment(value = "在指定位置生成掉落物")
    public void makeDropItem(@Comment(value = "生成掉落物的位置") Position position
            ,@Comment(value = "生成的掉落物实体的内容") Item item
            ,@Comment(value = "生成的掉落物是否有初速度飞出，默认为false") boolean fly){
        if(fly){
            makeDropItem(position, item);
        }else {
            position.getLevel().dropItem(position,item,new Vector3(0,0,0));
        }
    }
    //-获取方块
    @Comment(value = "获取指定位置的方块对象")
    public Block getBlock(@Comment(value = "坐标") Position position) {
        return position.getLevelBlock();
    }
    //-获取生物数组
    @Comment(value = "获取指定坐标所在世界的所有实体")
    public List<Entity> getLevelEntities(@Comment(value = "坐标") Position position){
        return Arrays.asList(position.getLevel().getEntities());
    }
    //-获取世界中的玩家
    @Comment(value = "获取指定坐标所在世界的所有玩家")
    public List<Player> getLevelPlayers(@Comment(value = "坐标") Position position){
        return new ArrayList<Player>(position.getLevel().getPlayers().values());
    }
    //-获取是否晴天
    @Comment(value = "获取指定位置是否是晴天")
    public boolean getIsSunny(@Comment(value = "坐标") Position position){
        Level level=position.getLevel();
        return !(level.isRaining()||level.isThundering());
    }
    //-设置天气
    @Comment(value = "设置指定位置的天气")
    public void setLevelWeather(@Comment(value = "坐标") Position position
            ,@Comment(value = "天气，值为clear/rain/thunder") String mode){
        Level level=position.getLevel();
        if (!mode.equals("clear")) {
            if(mode.equals("rain")){
                level.setRaining(true);
                level.setThundering(false);
            }else if(mode.equals("thunder")){
                level.setThundering(true);
                level.setRaining(true);
            }
        } else {
            level.setRaining(false);
            level.setThundering(false);
        }
    }
    //-获取白天黑夜
    @Comment(value = "获取指定位置是白天还是黑夜")
    public boolean isDay(@Comment(value = "坐标") Position position){
        return position.getLevel().isDaytime();
    }
    //-设置方块
    @Comment(value = "设置指定位置的方块")
    public void setBlock(@Comment(value = "坐标") Position position
            ,@Comment(value = "要被设置的方块对象") Block block
            ,@Comment(value = "是否产生破坏先前方块的粒子") boolean particle){
        if(particle==true){
            position.getLevel().addParticle(new DestroyBlockParticle(new Vector3(position.x+0.5,position.y+0.5,position.z+0.5),position.getLevelBlock()));
        }
        Server.getInstance().getPluginManager().callEvent(new BlockUpdateEvent(position.getLevelBlock()));
        position.getLevel().setBlockAt(
                (int)position.x,(int)position.y,(int)position.z,block.getId(),block.getDamage()
        );
    }
    //-方块更新
    @Comment(value = "强制更新指定坐标上的方块")
    public void blockUpdate(@Comment(value = "坐标") Position position){
        int x = position.getFloorX();
        int y = position.getFloorY();
        int z = position.getFloorZ();
        Block block = position.getLevelBlock();
        BaseFullChunk chunk = position.level.getChunk(x >> 4, z >> 4, true);
        Block blockPrevious = chunk.getAndSetBlock(x & 15, y, z & 15, block);
        if (blockPrevious.isTransparent() != block.isTransparent() || blockPrevious.getLightLevel() != block.getLightLevel()) {
            position.level.addLightUpdate(x, y, z);
        }
        BlockUpdateEvent ev = new BlockUpdateEvent(block);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (!ev.isCancelled()) {
            Entity[] entities = position.level.getNearbyEntities(new SimpleAxisAlignedBB((x - 1), (y - 1), (z - 1), (x + 1), (y + 1), (z + 1)));
            for (Entity entity : entities) {
                entity.scheduleUpdate();
            }
            block = ev.getBlock();
            block.onUpdate(1);
            position.level.updateAround(x, y, z);
        }
    }
    //-获取玩家手中物品
    @Comment(value = "获取玩家手中物品")
    public Item getItemInHand(@Comment(value = "要获取物品的玩家") Player player){
        return player.getInventory().getItemInHand();
    }
    //-设置玩家手中物品
    @Comment(value = "设置玩家手中物品")
    public void setItemInHand(@Comment(value = "要设置物品的玩家") Player player
            ,@Comment(value = "要被设置到玩家手中的物品") Item item){
        player.getInventory().setItemInHand(item);
    }
    //-向玩家背包添加物品
    @Comment(value = "向玩家背包添加物品")
    public void addItemToPlayer(@Comment(value = "要添加物品的玩家") Player player
            ,@Comment(value = "要添加到玩家背包的物品") Item item){
        if(player.getInventory().canAddItem(item)){
            player.getInventory().addItem(item);
        }else {
            player.sendPopup("你有一些"+item.getName()+"装不下掉到了地上");
            player.getLevel().dropItem(player,item);
        }
    }
    //-玩家背包是否有物品
    @Comment(value = "检测玩家背包是否有指定物品")
    public boolean hasItemToPlayer(@Comment(value = "要检测物品的玩家") Player player
            ,@Comment(value = "要检测是否存在的物品") Item item){
        return player.getInventory().contains(item);
    }
    public boolean PlayercontainsItem(@Comment(value = "") Player player,int x,Item ...item){
        Item[] tmp = item;
        boolean have = true;
        if(x!=1)
        for (Item each:tmp){
            each.setCount(each.getCount()*x);
        }
        for(Item each:tmp){
            if(!have)return false;
            if (!player.getInventory().contains(each)){
                have=false;
            }
        }
        return true;
    }
    @Comment(value = "将玩家背包内的指定物品移除")
    public void removeItemToPlayer(@Comment(value = "要移除物品的玩家") Player player
            ,@Comment(value = "要被从玩家背包移除的物品") Item item){
        if(PlayercontainsItem(player,1,item)){
            player.getInventory().removeItem(item);
        }
    }
    //获取世界掉落物
    @Comment(value = "获取坐标所在世界的所有掉落物")
    public EntityItem[] getDropItems(@Comment(value = "坐标") Position position){
        ArrayList<EntityItem> list = new ArrayList();
        for (Entity entity:position.getLevel().getEntities()){
            if(entity.getNetworkId()==64){
                list.add((EntityItem)entity);
            }
        }
        return list.toArray(new EntityItem[list.size()]);
    }
    @Comment(value = "获取指定世界上所有的掉落物")
    public EntityItem[] getDropItems(@Comment(value = "世界对象") Level level){
        ArrayList<EntityItem> list = new ArrayList();
        for (Entity entity:level.getEntities()){
            if(entity.getNetworkId()==64){
                list.add((EntityItem)entity);
            }
        }
        return list.toArray(new EntityItem[list.size()]);
    }
    //获取世界生物
    @Comment(value = "获取坐标所在世界的所有生物")
    public Entity[] getEntities(@Comment(value = "坐标") Position position){
        ArrayList<Entity> list = new ArrayList();
        for (Entity entity:position.getLevel().getEntities()){
            list.add(entity);
        }
        return list.toArray(new Entity[list.size()]);
    }
    //获取世界名称
    @Comment(value = "获取世界对象的世界名")
    public String getLevelName(@Comment(value = "世界对象") Level level){
        return level.getName();
    }
    //here 4/23
    @Comment(value = "偏移一个坐标对象")
    public void PositionMove(@Comment(value = "被执行偏移操作的坐标对象") Position position
            ,@Comment(value = "x轴偏移量") double x
            ,@Comment(value = "y轴偏移量") double y
            ,@Comment(value = "z轴偏移量") double z){
        position.x += x;
        position.y += y;
        position.z += z;
    }
    /********************************* 纯方块物品方法 *************************************/
    //-构建方块
    @Comment(value = "构建方块")
    public Block buildBlock(@Comment(value = "方块id") int id,@Comment(value = "方块特殊值") int data){
        return Block.get(id, data);
    }
    //-获取方块id*
    //-构建物品
    @Comment(value = "构建物品")
    public Item buildItem(@Comment(value = "物品id") int id,@Comment(value = "物品特殊值") int data,@Comment(value = "物品对象数量") int count){
        return Item.get(id,data,count);
    }
    //-从方块构建物品
    @Comment(value = "从方块构建物品")
    public Item buildItemFromBlock(@Comment(value = "源方块") Block block){
        return block.toItem();
    }
    //-设置物品数量*
    //-设置物品数据值*
    //-设置物品名称*
    //-获取物品id*
    //-获取物品数量*
    //-获取物品数据值*
    //-获取物品名称*
    //-物品不可破坏*
    @Deprecated
    @Comment(value = "此函数已弃用")
    public void setItemProperty(Item item, Integer data, Integer count, Boolean unbreakable, String name, String lore, String nbt) {

    }
    //未完成-获取工具种类*
    @Comment(value = "获取物品的Lore标签内容，多行用;分割")
    public String getItemLore(@Comment(value = "物品对象") Item item){
        String string="";
        for(String a:item.getLore()){
            string+=a+";";
        }
        return string;
    }
    //添加创造物品栏
    @Comment(value = "将指定物品对象添加到创造模式物品栏中")
    public void addToCreativeBar(@Comment(value = "物品对象") Item item){
        if(!Item.isCreativeItem(item)){
            Item.addCreativeItem(item);
        }
    }
    @Comment(value = "设置物品的lore标签内容，多行;分割")
    public void setItemLore(@Comment(value = "物品对象") Item item
            ,@Comment(value = "要设置的lore内容") String string){
        item.setLore(string.split(";"));
    }
    //-无序合成
    @Comment(value = "添加无序合成")
    public void addShapelessCraft(@Comment(value = "原料物品数组，需使用`Java.to`函数进行转换") Item[] inputs
            ,@Comment(value = "产物物品对象") Item output){
        Server.getInstance().addRecipe(new ShapelessRecipe(
                UUID.randomUUID().toString(),99,output,Arrays.asList(inputs)
        ));
        Server.getInstance().getCraftingManager().rebuildPacket();
    }
    //-冶炼合成
    @Comment(value = "添加熔炉冶炼合成")
    public void addFurnaceCraft(@Comment(value = "原料物品对象") Item input
            ,@Comment(value = "产物物品对象") Item output){
        Server.getInstance().addRecipe(new FurnaceRecipe(output,input));
        Server.getInstance().getCraftingManager().rebuildPacket();
        Loader.furnaceMap.put(input,output);
    }
    //-有序合成
    @Comment(value = "添加有序合成，不推荐使用")
    public void addShapedCraft(@Comment(value = "合成形状字符串，需使用`Java.to`函数进行转换") String[] shape
            ,@Comment(value = "产物") Item output
            ,@Comment(value = "追加返回产物") Item[] append){
        if(shape[2].equals("   ")){
            String[] tmp=shape;
            shape=new String[]{tmp[0],tmp[1]};
            if(shape[1].equals("   ")){
                tmp=shape;
                shape=new String[]{tmp[0]};
            }
        }
        for (int i=0;i<shape.length;i++){
            if(shape[i].endsWith(' '+"")){
                if(shape[i].length()==3){
                    String tt=shape[i];
                    shape[i]=tt.charAt(0)+""+tt.charAt(1);
                }else if(shape[i].length()==2){
                    String tt=shape[i];
                    shape[i]=tt.charAt(0)+"";
                }
            }
            if(shape[i].endsWith(' '+"")){
                if(shape[i].length()==3){
                    String tt=shape[i];
                    shape[i]=tt.charAt(0)+""+tt.charAt(1);
                }else if(shape[i].length()==2){
                    String tt=shape[i];
                    shape[i]=tt.charAt(0)+"";
                }
            }
        }
        int max=0;
        for (String a:shape){
            if(a.length()>=max)max=a.length();
        }
        for(int i=0;i<shape.length;i++){
            if(shape[i].length()<max){
                String tt=shape[i];
                if(max-tt.length()==1){
                    tt+=" ";
                }else if(max-tt.length()==2){
                    tt+="  ";
                }
                shape[i]=tt;
            }
        }
        if(!Item.isCreativeItem(output)){
            Item.addCreativeItem(output);
        }
        Map<Character,Item> map=new CharObjectHashMap<>();
        for (String a:shape){
            for (char b:a.toCharArray()){
                if(b!=' ')
                map.put(b,(Item)Loader.easytmpmap.get(b+""));
            }
        }
        LinkedList<Item> linkedList = new LinkedList<>();
        for(Item a:append){
            linkedList.add(a);
        }
        ShapedRecipe recipe = new ShapedRecipe(
                UUID.randomUUID().toString(),100,
                output,shape,map,linkedList
        );
        Server.getInstance().addRecipe(recipe);
        Server.getInstance().getCraftingManager().rebuildPacket();
    }
    @Comment(value = "添加有序合成")
    public void addShapedCraft(@Comment(value = "用一个字母或数字代表合成原材料的摆放方式，用\"|\"来连接三行，用空格来占格，如果最后的一行或列没有东西，请不要保留\n例如石镐:`addShapedCraft(\"SSS| I | I \",石镐物品对象,\"S\",圆石物品对象,\"I\",木棍物品对象)`，工作台:`addShapedCraft(\"MM|MM\",工作台物品,\"M\",橡木木板物品)`") String shape
            ,@Comment(value = "合成产物") Item output
            ,@Comment(value = "shape中的第1个字母") String s1,@Comment(value = "第个字母对应的物品对象") Item i1){
        this.addShapedCraft(shape, output, s1, i1, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
    @Comment(value = "添加有序合成")
    public void addShapedCraft(@Comment(value = "用一个字母或数字代表合成原材料的摆放方式，用\"|\"来连接三行，用空格来占格，如果最后的一行或列没有东西，请不要保留\n例如石镐:`addShapedCraft(\"SSS| I | I \",石镐物品对象,\"S\",圆石物品对象,\"I\",木棍物品对象)`，工作台:`addShapedCraft(\"MM|MM\",工作台物品,\"M\",橡木木板物品)`") String shape
            ,@Comment(value = "合成产物") Item output
            ,@Comment(value = "shape中的第1个字母") String s1,@Comment(value = "第1个字母对应的物品对象") Item i1
            ,@Comment(value = "shape中的第2个字母") String s2,@Comment(value = "第2个字母对应的物品对象") Item i2){
        this.addShapedCraft(shape, output, s1, i1, s2, i2, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
    @Comment(value = "添加有序合成")
    public void addShapedCraft(@Comment(value = "用一个字母或数字代表合成原材料的摆放方式，用\"|\"来连接三行，用空格来占格，如果最后的一行或列没有东西，请不要保留\n例如石镐:`addShapedCraft(\"SSS| I | I \",石镐物品对象,\"S\",圆石物品对象,\"I\",木棍物品对象)`，工作台:`addShapedCraft(\"MM|MM\",工作台物品,\"M\",橡木木板物品)`") String shape
            ,@Comment(value = "合成产物") Item output
            ,@Comment(value = "shape中的第1个字母") String s1,@Comment(value = "第1个字母对应的物品对象") Item i1
            ,@Comment(value = "shape中的第2个字母") String s2,@Comment(value = "第2个字母对应的物品对象") Item i2
            ,@Comment(value = "shape中的第3个字母") String s3,@Comment(value = "第3个字母对应的物品对象") Item i3){
        this.addShapedCraft(shape, output, s1, i1, s2, i2, s3, i3, null, null, null, null, null, null, null, null, null, null, null, null);
    }
    @Comment(value = "添加有序合成")
    public void addShapedCraft(@Comment(value = "用一个字母或数字代表合成原材料的摆放方式，用\"|\"来连接三行，用空格来占格，如果最后的一行或列没有东西，请不要保留\n例如石镐:`addShapedCraft(\"SSS| I | I \",石镐物品对象,\"S\",圆石物品对象,\"I\",木棍物品对象)`，工作台:`addShapedCraft(\"MM|MM\",工作台物品,\"M\",橡木木板物品)`") String shape
            ,@Comment(value = "合成产物") Item output
            ,@Comment(value = "shape中的第1个字母") String s1,@Comment(value = "第1个字母对应的物品对象") Item i1
            ,@Comment(value = "shape中的第2个字母") String s2,@Comment(value = "第2个字母对应的物品对象") Item i2
            ,@Comment(value = "shape中的第3个字母") String s3,@Comment(value = "第3个字母对应的物品对象") Item i3
            ,@Comment(value = "shape中的第4个字母") String s4,@Comment(value = "第4个字母对应的物品对象") Item i4){
        this.addShapedCraft(shape, output, s1, i1, s2, i2, s3, i3, s4, i4, null, null, null, null, null, null, null, null, null, null);
    }
    @Comment(value = "添加有序合成")
    public void addShapedCraft(@Comment(value = "用一个字母或数字代表合成原材料的摆放方式，用\"|\"来连接三行，用空格来占格，如果最后的一行或列没有东西，请不要保留\n例如石镐:`addShapedCraft(\"SSS| I | I \",石镐物品对象,\"S\",圆石物品对象,\"I\",木棍物品对象)`，工作台:`addShapedCraft(\"MM|MM\",工作台物品,\"M\",橡木木板物品)`") String shape
            ,@Comment(value = "合成产物") Item output
            ,@Comment(value = "shape中的第1个字母") String s1,@Comment(value = "第1个字母对应的物品对象") Item i1
            ,@Comment(value = "shape中的第2个字母") String s2,@Comment(value = "第2个字母对应的物品对象") Item i2
            ,@Comment(value = "shape中的第3个字母") String s3,@Comment(value = "第3个字母对应的物品对象") Item i3
            ,@Comment(value = "shape中的第4个字母") String s4,@Comment(value = "第4个字母对应的物品对象") Item i4
            ,@Comment(value = "shape中的第5个字母") String s5,@Comment(value = "第5个字母对应的物品对象") Item i5){
        this.addShapedCraft(shape, output, s1, i1, s2, i2, s3, i3, s4, i4, s5, i5, null, null, null, null, null, null, null, null);
    }
    @Comment(value = "添加有序合成")
    public void addShapedCraft(@Comment(value = "用一个字母或数字代表合成原材料的摆放方式，用\"|\"来连接三行，用空格来占格，如果最后的一行或列没有东西，请不要保留\n例如石镐:`addShapedCraft(\"SSS| I | I \",石镐物品对象,\"S\",圆石物品对象,\"I\",木棍物品对象)`，工作台:`addShapedCraft(\"MM|MM\",工作台物品,\"M\",橡木木板物品)`") String shape
            ,@Comment(value = "合成产物") Item output
            ,@Comment(value = "shape中的第1个字母") String s1,@Comment(value = "第1个字母对应的物品对象") Item i1
            ,@Comment(value = "shape中的第2个字母") String s2,@Comment(value = "第2个字母对应的物品对象") Item i2
            ,@Comment(value = "shape中的第3个字母") String s3,@Comment(value = "第3个字母对应的物品对象") Item i3
            ,@Comment(value = "shape中的第4个字母") String s4,@Comment(value = "第4个字母对应的物品对象") Item i4
            ,@Comment(value = "shape中的第5个字母") String s5,@Comment(value = "第5个字母对应的物品对象") Item i5
            ,@Comment(value = "shape中的第6个字母") String s6,@Comment(value = "第6个字母对应的物品对象") Item i6){
        this.addShapedCraft(shape, output, s1, i1, s2, i2, s3, i3, s4, i4, s5, i5, s6, i6, null, null, null, null, null, null);
    }
    @Comment(value = "添加有序合成")
    public void addShapedCraft(@Comment(value = "用一个字母或数字代表合成原材料的摆放方式，用\"|\"来连接三行，用空格来占格，如果最后的一行或列没有东西，请不要保留\n例如石镐:`addShapedCraft(\"SSS| I | I \",石镐物品对象,\"S\",圆石物品对象,\"I\",木棍物品对象)`，工作台:`addShapedCraft(\"MM|MM\",工作台物品,\"M\",橡木木板物品)`") String shape
            ,@Comment(value = "合成产物") Item output
            ,@Comment(value = "shape中的第1个字母") String s1,@Comment(value = "第1个字母对应的物品对象") Item i1
            ,@Comment(value = "shape中的第2个字母") String s2,@Comment(value = "第2个字母对应的物品对象") Item i2
            ,@Comment(value = "shape中的第3个字母") String s3,@Comment(value = "第3个字母对应的物品对象") Item i3
            ,@Comment(value = "shape中的第4个字母") String s4,@Comment(value = "第4个字母对应的物品对象") Item i4
            ,@Comment(value = "shape中的第5个字母") String s5,@Comment(value = "第5个字母对应的物品对象") Item i5
            ,@Comment(value = "shape中的第6个字母") String s6,@Comment(value = "第6个字母对应的物品对象") Item i6
            ,@Comment(value = "shape中的第7个字母") String s7,@Comment(value = "第7个字母对应的物品对象") Item i7){
        this.addShapedCraft(shape, output, s1, i1, s2, i2, s3, i3, s4, i4, s5, i5, s6, i6, s7, i7, null, null, null, null);
    }
    @Comment(value = "添加有序合成")
    public void addShapedCraft(@Comment(value = "用一个字母或数字代表合成原材料的摆放方式，用\"|\"来连接三行，用空格来占格，如果最后的一行或列没有东西，请不要保留\n例如石镐:`addShapedCraft(\"SSS| I | I \",石镐物品对象,\"S\",圆石物品对象,\"I\",木棍物品对象)`，工作台:`addShapedCraft(\"MM|MM\",工作台物品,\"M\",橡木木板物品)`") String shape
            ,@Comment(value = "合成产物") Item output
            ,@Comment(value = "shape中的第1个字母") String s1,@Comment(value = "第1个字母对应的物品对象") Item i1
            ,@Comment(value = "shape中的第2个字母") String s2,@Comment(value = "第2个字母对应的物品对象") Item i2
            ,@Comment(value = "shape中的第3个字母") String s3,@Comment(value = "第3个字母对应的物品对象") Item i3
            ,@Comment(value = "shape中的第4个字母") String s4,@Comment(value = "第4个字母对应的物品对象") Item i4
            ,@Comment(value = "shape中的第5个字母") String s5,@Comment(value = "第5个字母对应的物品对象") Item i5
            ,@Comment(value = "shape中的第6个字母") String s6,@Comment(value = "第6个字母对应的物品对象") Item i6
            ,@Comment(value = "shape中的第7个字母") String s7,@Comment(value = "第7个字母对应的物品对象") Item i7
            ,@Comment(value = "shape中的第8个字母") String s8,@Comment(value = "第8个字母对应的物品对象") Item i8){
        this.addShapedCraft(shape, output, s1, i1, s2, i2, s3, i3, s4, i4, s5, i5, s6, i6, s7, i7, s8, i8,null,null);
    }
    @Comment(value = "添加有序合成")
    public void addShapedCraft(@Comment(value = "用一个字母或数字代表合成原材料的摆放方式，用\"|\"来连接三行，用空格来占格，如果最后的一行或列没有东西，请不要保留\n例如石镐:`addShapedCraft(\"SSS| I | I \",石镐物品对象,\"S\",圆石物品对象,\"I\",木棍物品对象)`，工作台:`addShapedCraft(\"MM|MM\",工作台物品,\"M\",橡木木板物品)`") String shape
            ,@Comment(value = "合成产物") Item output
            ,@Comment(value = "shape中的第1个字母") String s1,@Comment(value = "第1个字母对应的物品对象") Item i1
            ,@Comment(value = "shape中的第2个字母") String s2,@Comment(value = "第2个字母对应的物品对象") Item i2
            ,@Comment(value = "shape中的第3个字母") String s3,@Comment(value = "第3个字母对应的物品对象") Item i3
            ,@Comment(value = "shape中的第4个字母") String s4,@Comment(value = "第4个字母对应的物品对象") Item i4
            ,@Comment(value = "shape中的第5个字母") String s5,@Comment(value = "第5个字母对应的物品对象") Item i5
            ,@Comment(value = "shape中的第6个字母") String s6,@Comment(value = "第6个字母对应的物品对象") Item i6
            ,@Comment(value = "shape中的第7个字母") String s7,@Comment(value = "第7个字母对应的物品对象") Item i7
            ,@Comment(value = "shape中的第8个字母") String s8,@Comment(value = "第8个字母对应的物品对象") Item i8
            ,@Comment(value = "shape中的第9个字母") String s9,@Comment(value = "第9个字母对应的物品对象") Item i9){
        CharObjectHashMap<Item> ingredients = new CharObjectHashMap<>();
        if(s1!=null)ingredients.put(s1.charAt(0),i1);
        if(s2!=null)ingredients.put(s2.charAt(0),i2);
        if(s3!=null)ingredients.put(s3.charAt(0),i3);
        if(s4!=null)ingredients.put(s4.charAt(0),i4);
        if(s5!=null)ingredients.put(s5.charAt(0),i5);
        if(s6!=null)ingredients.put(s6.charAt(0),i6);
        if(s7!=null)ingredients.put(s7.charAt(0),i7);
        if(s8!=null)ingredients.put(s8.charAt(0),i8);
        if(s9!=null)ingredients.put(s9.charAt(0),i9);
        ShapedRecipe recipe = new ShapedRecipe(
                UUID.randomUUID().toString(),100,
                output,shape.split("\\|"),ingredients,new ArrayList<>()
        );
        Server.getInstance().addRecipe(recipe);
        Server.getInstance().getCraftingManager().rebuildPacket();
    }
    //添加附魔
    @Comment(value = "为物品对象添加附魔")
    public void addItemEnchant(@Comment(value = "物品对象") Item item
            ,@Comment(value = "附魔id") int id
            ,@Comment(value = "附魔等级") int level){
        Enchantment enchantment = Enchantment.getEnchantment(id);
        enchantment.setLevel(level);
        addEnchantment(item,level,enchantment);
    }
    //获取附魔
    @Comment(value = "获取物品对象的所有附魔属性对象")
    public Enchantment[] getItemEnchant(@Comment(value = "物品对象") Item item){
        return item.getEnchantments();
    }
    @Comment(value = "获取附魔对象的id")
    public int getEnchantID(@Comment(value = "附魔对象") Enchantment enchantment){
        return enchantment.getId();
    }
    @Comment(value = "获取附魔对象的等级")
    public int getEnchantLevel(@Comment(value = "附魔等级") Enchantment enchantment){
        return enchantment.getLevel();
    }
    //比较物品
    @Comment(value = "比较两个物品对象内容是否相同")
    public boolean isSame(@Comment(value = "一个物品对象") Item item1
            ,@Comment(value = "另一个物品对象") Item item2
            ,@Comment(value = "是否比较特殊值") boolean damage
            ,@Comment(value = "是否比较nbt") boolean nbt){
        return item1.equals(item2,damage,nbt);
    }
    //makeenchant
    private void addEnchantment(Item item,int level,Enchantment... enchantments) {
        CompoundTag tag;
        if (!item.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = item.getNamedTag();
        }

        ListTag<CompoundTag> ench;
        if (!tag.contains("ench")) {
            ench = new ListTag<>("ench");
            tag.putList(ench);
        } else {
            ench = tag.getList("ench", CompoundTag.class);
        }

        for (Enchantment enchantment : enchantments) {
            boolean found = false;

            for (int k = 0; k < ench.size(); k++) {
                CompoundTag entry = ench.get(k);
                if (entry.getShort("id") == enchantment.getId()) {
                    ench.add(k, new CompoundTag()
                            .putShort("id", enchantment.getId())
                            .putShort("lvl", level)
                    );
                    found = true;
                    break;
                }
            }

            if (!found) {
                ench.add(new CompoundTag()
                        .putShort("id", enchantment.getId())
                        .putShort("lvl", level)
                );
            }
        }

        item.setNamedTag(tag);
    }
    //添加BN合成
    @Comment(value = "添加bn高级合成")
    public void addBNCraft(@Comment(value = "合成类别，可自定义") String type
            ,@Comment(value = "对合成的描述") String description
            ,@Comment(value = "原料物品对象数组，需要使用`Java.to`函数转换") Item[] input
            ,@Comment(value = "合成的产物数组，需要使用`Java.to`函数转换") Item[] output
            ,@Comment(value = "合成耗时") int delay
            ,@Comment(value = "合成成功率") double percent){
        Loader.bnCrafting.addCraft(type, description, input, output, delay, percent);
    }
    //打开BN合成
    @Comment(value = "为指定玩家打开指定类别的bn合成")
    public void openBNCraftForPlayer(@Comment(value = "合成类别") String type
            ,@Comment(value = "为哪个玩家打开") Player player){
        Loader.bnCrafting.showTypeToPlayer(type, player);
    }
    //获取nbt字符串
    @Comment(value = "获取物品对象的nbt字符串")
    public String getNBTString(@Comment(value = "物品对象") Item item){
        return Utils.bytesToHexString(item.getCompoundTag());
    }
    //给物品注入nbt字符串
    @Comment(value = "把nbt字符串包含的物品信息注入到物品对象上")
    public void putinNBTString(@Comment(value = "物品对象") Item item,@Comment(value = "nbt字符串") String str){
        item.setCompoundTag(Utils.hexStringToBytes(str));
    }
    //设置物品颜色
    @Comment(value = "设置物品的颜色")
    public void setItemColor(@Comment(value = "物品对象") Item item,int r,int g,int b){
        CompoundTag compoundTag = item.hasCompoundTag()? item.getNamedTag() : new CompoundTag();
        compoundTag.putInt("customColor", r*65536+g*256+b);
        item.setCompoundTag(compoundTag);
    }
    //设置不可破坏
    @Comment(value = "设置物品是否无限耐久")
    public void setItemUnbreakable(@Comment(value = "物品对象") Item item
            ,@Comment(value = "是否无限耐久") boolean unbreakable){
        CompoundTag compoundTag = item.hasCompoundTag()? item.getNamedTag() : new CompoundTag();
        compoundTag.putBoolean("Unbreakable",unbreakable);
        item.setCompoundTag(compoundTag);
    }
    //添加钓鱼产物
    @Comment(value = "添加钓鱼可能产出的产物")
    public void addFishingResult(@Comment(value = "种类，可以为TREASURES/JUNKS/FISH") String type
            ,@Comment(value = "新增的产物物品") Item item
            ,@Comment(value = "概率权重") double chance){
        Selector parent;
        switch (type){
            case "TREASURES":
            case "宝藏":
                parent = Fishing.TREASURES;
                break;
            case "JUNKS":
            case "垃圾":
                parent = Fishing.JUNKS;
                break;
            default:
                parent = Fishing.FISHES;
        }
        RandomItem.putSelector(new ConstantItemSelector(item, parent),(float) chance);
    }
    //不对外暴露: 注册新方块
    public void registerBlock(int id, Class<? extends Block> clazz) {
        Item.list[id] = clazz;
        RuntimeItemMapping runtimeItemMapping = RuntimeItems.getRuntimeMapping();
        Field legacyNetworkMap = null;
        try {
            legacyNetworkMap = runtimeItemMapping.getClass().getDeclaredField("legacyNetworkMap");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        legacyNetworkMap.setAccessible(true);
        Field networkLegacyMap = null;
        try {
            networkLegacyMap = runtimeItemMapping.getClass().getDeclaredField("networkLegacyMap");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        networkLegacyMap.setAccessible(true);
        try {
            ((Int2IntMap)legacyNetworkMap.get(runtimeItemMapping)).put(RuntimeItems.getFullId(id,0), id << 1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            ((Int2IntMap)networkLegacyMap.get(runtimeItemMapping)).put(id,RuntimeItems.getFullId(id,0));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Loader.registerItemIds.add(id);
        Block.list[id] = clazz;
        Block block;
        try {
            block = clazz.newInstance();
            try {
                Constructor constructor = clazz.getDeclaredConstructor(int.class);
                constructor.setAccessible(true);
                for (int data = 0; data < 16; ++data) {
                    Block.fullList[(id << 4) | data] = (Block) constructor.newInstance(data);
                }
                Block.hasMeta[id] = true;
            } catch (NoSuchMethodException ignore) {
                for (int data = 0; data < 16; ++data) {
                    Block.fullList[(id << 4) | data] = block;
                }
            }
        } catch (Exception e) {
            Loader.getlogger().alert("Error while registering " + clazz.getName(), e);
            for (int data = 0; data < 16; ++data) {
                Block.fullList[(id << 4) | data] = new BlockUnknown(id, data);
            }
            return;
        }
        Block.solid[id] = block.isSolid();
        Block.transparent[id] = block.isTransparent();
        Block.hardness[id] = block.getHardness();
        Block.light[id] = block.getLightLevel();
        if (block.isSolid()) {
            if (block.isTransparent()) {
                Block.lightFilter[id] = 1;
            } else {
                Block.lightFilter[id] = 15;
            }
        } else {
            Block.lightFilter[id] = 1;
        }
    }
    //动态生成类并注册新的固体方块
    @Comment(value = "注册新的自定义方块")
    public void registerSolidBlock(
            @Comment(value = "新方块的id") int id
            ,@Comment(value = "新方块的名称") String name
            ,@Comment(value = "新方块的硬度") double hardness
            ,@Comment(value = "新方块的抗爆炸度") double resistance
            ,@Comment(value = "用于开采的工具种类，0-无,1-剑,2-铲,3-镐,4-斧,5-剪刀") int toolType
            ,@Comment(value = "是否允许被精准采集") boolean isSilkTouchable
            ,@Comment(value = "挖掘后掉落的最小经验") int dropMinExp
            ,@Comment(value = "挖掘后掉落的最大经验") int dropMaxExp
            ,@Comment(value = "新方块的挖掘等级，0-空手,1-木,2-金,3-石,4-铁,5-钻石") int mineTier){
        try {
            //获取类加载器并导入类路径
            ClassPool classPool = ClassPool.getDefault();
            CtClass blockClass = null;
            classPool.insertClassPath(Loader.pluginFile.getAbsolutePath());
            classPool.insertClassPath(new ClassClassPath(Loader.class));
            classPool.insertClassPath(new ClassClassPath(Nukkit.class));
            classPool.importPackage("com.blocklynukkit.loader");
            //创建继承固体方块的类
            blockClass = classPool.makeClass("Block_ID_"+id+"_"+Loader.registerBlocks++,classPool.getCtClass("com.blocklynukkit.loader.other.Blocks.BaseSolidBlock"));
            //添加构造函数
            CtConstructor constructor = new CtConstructor(new CtClass[]{},blockClass);
            constructor.setBody("{}");
            blockClass.addConstructor(constructor);
            //添加public int getId()方法
            blockClass.addMethod(CtMethod.make("public int getId(){return "+id+";}",blockClass));
            //添加public String getName()
            blockClass.addMethod(CtMethod.make("public String getName(){return \""+name+"\";}",blockClass));
            //添加public double getHardness()
            blockClass.addMethod(CtMethod.make("public double getHardness(){return "+hardness+";}",blockClass));
            //添加public double getResistance()
            blockClass.addMethod(CtMethod.make("public double getResistance(){return "+resistance+";}",blockClass));
            //添加public int getToolType()
            blockClass.addMethod(CtMethod.make("public int getToolType(){return "+toolType+";}",blockClass));
            //添加public int getDropExp()
            blockClass.addMethod(CtMethod.make("public int getDropExp(){return new cn.nukkit.math.NukkitRandom().nextRange("+dropMinExp+","+dropMaxExp+");}",blockClass));
            //添加public boolean canHarvestWithHand()
            blockClass.addMethod(CtMethod.make("public boolean canHarvestWithHand(){return false;}",blockClass));
            //添加public boolean canSilkTouch()
            blockClass.addMethod(CtMethod.make("public boolean canSilkTouch(){return "+isSilkTouchable+";}",blockClass));
            //添加public int getTier()
            blockClass.addMethod(CtMethod.make("public int getTier(){return "+mineTier+";}",blockClass));
            //编译到jvm中
            registerBlock(id, (Class<? extends Block>) blockClass.toClass());
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }
    //动态生成类并注册新的简单物品
    @Comment(value = "注册新的物品")
    public void registerSimpleItem(@Comment(value = "新物品的id") int id
            ,@Comment(value = "新物品的名称") String name){
        this.registerSimpleItem(id, name, 64);
    }
    @Comment(value = "注册新的物品")
    public void registerSimpleItem(@Comment(value = "新物品的id") int id
            ,@Comment(value = "新物品的名称") String name
            ,@Comment(value = "新物品的最大堆叠上限") int stackSize){
        try{
            ClassPool classPool = ClassPool.getDefault();
            CtClass itemClass = null;
            classPool.insertClassPath(Loader.pluginFile.getAbsolutePath());
            classPool.insertClassPath(new ClassClassPath(Loader.class));
            classPool.insertClassPath(new ClassClassPath(Nukkit.class));
            classPool.importPackage("com.blocklynukkit.loader");
            //构建物品类
            itemClass = classPool.makeClass("Item_ID_"+id+"_"+Loader.registerItems++,classPool.getCtClass("cn.nukkit.item.Item"));
            //添加构造函数
            CtConstructor twoConstructor = new CtConstructor(new CtClass[]{classPool.getCtClass("java.lang.Integer"),CtClass.intType},itemClass);
            twoConstructor.setBody("{super("+id+",$1,$2,\""+name+"\");}");
            itemClass.addConstructor(twoConstructor);
            CtConstructor oneConstructor = new CtConstructor(new CtClass[]{classPool.getCtClass("java.lang.Integer")},itemClass);
            oneConstructor.setBody("{super("+id+",$1,1,\""+name+"\");}");
            itemClass.addConstructor(oneConstructor);
            CtConstructor voidConstructor = new CtConstructor(new CtClass[]{},itemClass);
            voidConstructor.setBody("{super("+id+",new Integer(0),1,\""+name+"\");}");
            itemClass.addConstructor(voidConstructor);
            //最大堆叠数量
            itemClass.addMethod(CtMethod.make("public int getMaxStackSize(){return "+stackSize+";}",itemClass));
            Item.list[id] = itemClass.toClass();
            if(id == 326 || id == 327 || id == 343 || id == 435 || id == 436 || id == 439
                    || id == 440 ||(id>477&&id<498)|| id == 512 ||(id>513&&id<720)
                    ||(id>720&&id<734)|| id == 735 ||(id>760&&id<801)|| id>801){
                RuntimeItemMapping runtimeItemMapping = RuntimeItems.getRuntimeMapping();
                Field legacyNetworkMap = runtimeItemMapping.getClass().getDeclaredField("legacyNetworkMap");legacyNetworkMap.setAccessible(true);
                Field networkLegacyMap = runtimeItemMapping.getClass().getDeclaredField("networkLegacyMap");networkLegacyMap.setAccessible(true);
                int fullId = RuntimeItems.getFullId(id, 0);
                ((Int2IntMap)legacyNetworkMap.get(runtimeItemMapping)).put(fullId, id << 1);
                ((Int2IntMap)networkLegacyMap.get(runtimeItemMapping)).put(id,fullId);
                Loader.registerItemIds.add(id);
            }
        } catch (NotFoundException | NoSuchFieldException | CannotCompileException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
