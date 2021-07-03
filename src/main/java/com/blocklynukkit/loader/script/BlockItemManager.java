package com.blocklynukkit.loader.script;

import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.inventory.FurnaceRecipe;
import cn.nukkit.inventory.ShapedRecipe;
import cn.nukkit.inventory.ShapelessRecipe;
import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.food.Food;
import cn.nukkit.item.food.FoodNormal;
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
import cn.nukkit.utils.BinaryStream;
import com.blocklynukkit.loader.api.Comment;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.AddonsAPI.CustomItemInfo;
import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourceNode;
import com.blocklynukkit.loader.other.AddonsAPI.resource.ResourcePack;
import com.blocklynukkit.loader.other.AddonsAPI.resource.TranslationNode;
import com.blocklynukkit.loader.other.AddonsAPI.resource.data.ResourceArmorManifest;
import com.blocklynukkit.loader.other.AddonsAPI.resource.data.ResourceItemManifest;
import com.blocklynukkit.loader.other.AddonsAPI.resource.data.ResourceJSON;
import com.blocklynukkit.loader.other.AddonsAPI.resource.data.ResourcePicture;
import com.blocklynukkit.loader.other.Items.ItemData;
import com.blocklynukkit.loader.script.bases.BaseManager;
import com.blocklynukkit.loader.utils.Utils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.netty.util.collection.CharObjectHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import javassist.*;

import javax.script.ScriptEngine;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class BlockItemManager extends BaseManager {
    public static ResourcePack blocklyNukkitMcpack = new ResourcePack("./resource_packs/blocklynukkit.mcpack");
    public static TranslationNode mcpackTranslation = new TranslationNode();

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
    //检测是否为含水方块
    static Method getLevelBlockAtLayerMethod = null;
    @Comment(value = "检测方块是否为含水方块")
    public boolean isBlockWaterLogged(@Comment(value = "要检测的方块") Block block){
        if(Loader.getFunctionManager().isPowerNukkit()){
            try {
                if(getLevelBlockAtLayerMethod == null){
                    getLevelBlockAtLayerMethod = Block.class.getMethod("getLevelBlockAtLayer", int.class);
                }
                return getLevelBlockAtLayerMethod.invoke(block, 1) instanceof BlockWater;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return false;
            }
        }else{
            return false;
        }
    }
    //不对外暴露: 获取全称ID
    public int getFullId(int id, int data) {
        return (((short) id) << 16) | ((data & 0x7fff) << 1);
    }
    //不对外暴露: 向NK写入物品数据
    Method getRuntimeItemMappingMethod = null;
    Field runtime2Legacy = null;
    Field legacy2Runtime = null;
    Field identifier2Legacy = null;
    Field legacyNetworkMap = null;
    Field networkLegacyMap = null;
    Field namespaceNetworkMap = null;
    Field networkNamespaceMap = null;
    public void injectItem2Nukkit(String name, int id) throws NoSuchFieldException, IllegalAccessException {
        RuntimeItemMapping runtimeItemMapping = null;
        try {
            runtimeItemMapping = RuntimeItems.getMapping();
        }catch (NoSuchMethodError error){
            try {
                if(getRuntimeItemMappingMethod == null) getRuntimeItemMappingMethod = RuntimeItems.class.getMethod("getRuntimeMapping");
                runtimeItemMapping = (RuntimeItemMapping) getRuntimeItemMappingMethod.invoke(RuntimeItemMapping.class);
            } catch (NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        try {
            if(runtime2Legacy == null) runtime2Legacy = runtimeItemMapping.getClass().getDeclaredField("runtime2Legacy");runtime2Legacy.setAccessible(true);
            if(legacy2Runtime == null) legacy2Runtime = runtimeItemMapping.getClass().getDeclaredField("legacy2Runtime");legacy2Runtime.setAccessible(true);
            if(identifier2Legacy == null) identifier2Legacy = runtimeItemMapping.getClass().getDeclaredField("identifier2Legacy");identifier2Legacy.setAccessible(true);
            int fullId = (short)id << 16 | ((0) & 32767) << 1;
            RuntimeItemMapping.LegacyEntry legacyEntry = new RuntimeItemMapping.LegacyEntry(id, false, 0);
            ((Int2ObjectMap<RuntimeItemMapping.RuntimeEntry>)legacy2Runtime.get(runtimeItemMapping)).put(fullId, new RuntimeItemMapping.RuntimeEntry("blocklynukkit:"+name, id, false));
            ((Int2ObjectMap<RuntimeItemMapping.LegacyEntry>)runtime2Legacy.get(runtimeItemMapping)).put(id, legacyEntry);
            ((HashMap<String, RuntimeItemMapping.LegacyEntry>)identifier2Legacy.get(runtimeItemMapping)).put("blocklynukkit:"+name, legacyEntry);
        }catch (NoSuchFieldException | IllegalAccessException e){
            if(legacyNetworkMap == null) legacyNetworkMap = runtimeItemMapping.getClass().getDeclaredField("legacyNetworkMap");legacyNetworkMap.setAccessible(true);
            if(networkLegacyMap == null) networkLegacyMap = runtimeItemMapping.getClass().getDeclaredField("networkLegacyMap");networkLegacyMap.setAccessible(true);
            if(namespaceNetworkMap == null) namespaceNetworkMap = runtimeItemMapping.getClass().getDeclaredField("namespaceNetworkMap");namespaceNetworkMap.setAccessible(true);
            if(networkNamespaceMap == null) networkNamespaceMap = runtimeItemMapping.getClass().getDeclaredField("networkNamespaceMap");networkNamespaceMap.setAccessible(true);
            int fullId = (short)id << 16 | ((0) & 32767) << 1;
            ((Int2IntMap)legacyNetworkMap.get(runtimeItemMapping)).put(fullId, id << 1);
            ((Int2IntMap)networkLegacyMap.get(runtimeItemMapping)).put(id, fullId);
            ((Map<String, OptionalInt>)namespaceNetworkMap.get(runtimeItemMapping)).put("blocklynukkit:"+name, OptionalInt.of(id));
            ((Int2ObjectMap<String>)networkNamespaceMap.get(runtimeItemMapping)).put(id, "blocklynukkit:"+name);
        }
    }
    //不对外暴露: 注册新方块
    public void registerBlock(int id, String name, Class<? extends Block> clazz) {
        if(Block.list.length < 800){
            Class[] list = new Class[2048];
            System.arraycopy(Block.list, 0, list, 0, Block.list.length);
            Block.list = list;
            Block[] fullList = new Block[2048 * (1 << 4)];
            System.arraycopy(Block.fullList, 0, fullList, 0, Block.fullList.length);
            Block.fullList = fullList;
            int[] light = new int[2048];
            System.arraycopy(Block.light, 0, light, 0, Block.light.length);
            Block.light = light;
            int[] lightFilter = new int[2048];
            System.arraycopy(Block.lightFilter, 0, lightFilter, 0, Block.lightFilter.length);
            Block.lightFilter = lightFilter;
            boolean[] solid = new boolean[2048];
            System.arraycopy(Block.solid, 0, solid, 0, Block.solid.length);
            Block.solid = solid;
            double[] hardness = new double[2048];
            System.arraycopy(Block.hardness, 0, hardness, 0, Block.hardness.length);
            Block.hardness = hardness;
            boolean[] transparent = new boolean[2048];
            System.arraycopy(Block.transparent, 0, transparent, 0, Block.transparent.length);
            Block.transparent = transparent;
            boolean[] diffusesSkyLight = new boolean[2048];
            try {
                Field diffField = Block.class.getDeclaredField("diffusesSkyLight");
                boolean[] source = (boolean[]) diffField.get(Block.class);
                System.arraycopy(source, 0, diffusesSkyLight, 0, source.length);
                diffField.set(Block.class, diffusesSkyLight);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            boolean[] hasMeta = new boolean[2048];
            System.arraycopy(Block.hasMeta, 0, hasMeta, 0, Block.hasMeta.length);
            Block.hasMeta = hasMeta;
        }
        Block.list[id] = clazz;
        try {
            Method registerBlockImplementationMethod = Block.class.getMethod("registerBlockImplementation", int.class, Class.class, String.class, boolean.class);
            registerBlockImplementationMethod.invoke(Block.class, id, clazz, "blocklynukkit:"+name, true);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

//        /////
//        Class<? extends Block> c = clazz;
//        if (c != null) {
//            Block block;
//            try {
//                block = c.getDeclaredConstructor().newInstance();
//                Class BlockStateRegistry = Class.forName("cn.nukkit.blockstate.BlockStateRegistry");
//                Method registerPersistenceNameMethod = BlockStateRegistry.getMethod("registerPersistenceName", int.class, String.class);
//                Method getPersistenceName = block.getClass().getMethod("getPersistenceName");
//                String persistenceName = (String) getPersistenceName.invoke(block);
//                registerPersistenceNameMethod.invoke(BlockStateRegistry, id, persistenceName);
//                Field runtimeIdRegistrationField = BlockStateRegistry.getDeclaredField("runtimeIdRegistration");
//                registerPersistenceNameMethod.setAccessible(true);
//                Int2ObjectMap runtimeIdRegistration = (Int2ObjectMap) runtimeIdRegistrationField.get(BlockStateRegistry);
//                Method registerStateIdMethod = BlockStateRegistry.getDeclaredMethod("registerStateId", CompoundTag.class, int.class);
//                registerStateIdMethod.setAccessible(true);
//                registerStateIdMethod.invoke(BlockStateRegistry, new CompoundTag().putCompound(""))
//                try {
//                    Constructor<? extends Block> constructor = c.getDeclaredConstructor(int.class);
//                    constructor.setAccessible(true);
//                    for (int data = 0; data < (1 << 4); ++data) {
//                        int fullId = (id << 4) | data;
//                        Block b;
//                        try {
//                            b = constructor.newInstance(data);
//                            if (b.getDamage() != data) {
//                                b = new BlockUnknown(id, data);
//                            }
//                        } catch (InvocationTargetException wrapper) {
//                            Throwable uncaught = wrapper.getTargetException();
//                            if (!(uncaught.getClass().getCanonicalName().equals("InvalidBlockDamageException"))) {
//                                Loader.getlogger().error("Error while registering "+c.getName()+" with meta "+data, uncaught);
//                            }
//                            b = new BlockUnknown(id, data);
//                        }
//                        Block.fullList[fullId] = b;
//                    }
//                    Block.hasMeta[id] = true;
//                } catch (NoSuchMethodException ignore) {
//                    for (int data = 0; data < (1 << 4); ++data) {
//                        int fullId = (id << 4) | data;
//                        Block.fullList[fullId] = block;
//                    }
//                }
//            } catch (Exception e) {
//                Loader.getlogger().error("Error while registering "+c.getName(), e);
//                for (int data = 0; data < (1 << 4); ++data) {
//                    Block.fullList[(id << 4) | data] = new BlockUnknown(id, data);
//                }
//                block = Block.fullList[id << 4];
//            }
//
//            Block.solid[id] = block.isSolid();
//            Block.transparent[id] = block.isTransparent();
//            try {
//                Field diffField = Block.class.getDeclaredField("diffusesSkyLight");
//                boolean[] source = (boolean[]) diffField.get(Block.class);
//                source[id] = false;
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            Block.hardness[id] = block.getHardness();
//            Block.light[id] = block.getLightLevel();
//            Block.lightFilter[id] = 1;
//        } else {
//            Block.lightFilter[id] = 1;
//            for (int data = 0; data < (1 << 4); ++data) {
//                Block.fullList[(id << 4) | data] = new BlockUnknown(id, data);
//            }
//        }
        /////

//        Block block;
//        try {
//            block = clazz.newInstance();
//            try {
//                Constructor constructor = clazz.getDeclaredConstructor(int.class);
//                constructor.setAccessible(true);
//                for (int data = 0; data < 16; ++data) {
//                    Block.fullList[(id << 4) | data] = (Block) constructor.newInstance(data);
//                }
//                Block.hasMeta[id] = true;
//            } catch (NoSuchMethodException ignore) {
//                for (int data = 0; data < 16; ++data) {
//                    Block.fullList[(id << 4) | data] = block;
//                }
//            }
//        } catch (Exception e) {
//            Loader.getlogger().alert("Error while registering " + clazz.getName(), e);
//            for (int data = 0; data < 16; ++data) {
//                Block.fullList[(id << 4) | data] = new BlockUnknown(id, data);
//            }
//            return;
//        }
//        Block.solid[id] = block.isSolid();
//        Block.transparent[id] = block.isTransparent();
//        Block.hardness[id] = block.getHardness();
//        Block.light[id] = block.getLightLevel();
//        if (block.isSolid()) {
//            if (block.isTransparent()) {
//                Block.lightFilter[id] = 1;
//            } else {
//                Block.lightFilter[id] = 15;
//            }
//        } else {
//            Block.lightFilter[id] = 1;
//        }
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
            //仅在PowerNukkit上可用
            if(!Loader.getFunctionManager().isPowerNukkit()){
                Loader.getlogger().alert("Custom block can only work on powernukkit!");
                return;
            }
            //记录方块id
            Loader.registerBlockIds.add(id);
            Loader.registerCustomBlocks++;
            //注册该方块的物品形式
            injectItem2Nukkit(name, id);
            //获取类加载器并导入类路径
            ClassPool classPool = ClassPool.getDefault();
            CtClass blockClass = null;
            classPool.insertClassPath(Loader.pluginFile.getAbsolutePath());
            classPool.insertClassPath(new ClassClassPath(Loader.class));
            classPool.insertClassPath(new ClassClassPath(Nukkit.class));
            classPool.importPackage("com.blocklynukkit.loader");
            //创建继承固体方块的类
            blockClass = classPool.makeClass("Block_ID_"+id+"_"+Loader.registerBlocks++,classPool.getCtClass("com.blocklynukkit.loader.other.AddonsAPI.BaseSolidBlock"));
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
            registerBlock(id, name, (Class<? extends Block>) blockClass.toClass());
        } catch (NotFoundException | CannotCompileException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    //动态生成类并注册新的简单物品
    @Comment(value = "注册新的简易物品")
    public void registerSimpleItem(@Comment(value = "新物品的id") int id
            ,@Comment(value = "新物品的名称") String name){
        this.registerSimpleItem(id, name, 64, "items");
    }
    @Comment(value = "注册新的简易物品")
    public void registerSimpleItem(@Comment(value = "新物品的id") int id
            ,@Comment(value = "新物品的名称") String name
            ,@Comment(value = "新物品的类别，可选construction nature equipment items") String type){
        this.registerSimpleItem(id, name, 64, type);
    }
    @Comment(value = "注册新的简易物品")
    public void registerSimpleItem(@Comment(value = "新物品的id") int id
            ,@Comment(value = "新物品的名称") String name
            ,@Comment(value = "新物品的最大堆叠上限") int stackSize
            ,@Comment(value = "新物品的类别，可选construction nature equipment items") String type){
        this.registerSimpleItem(id, name, stackSize, type, false, false);
    }
    @Comment(value = "注册新的简易物品")
    public void registerSimpleItem(@Comment(value = "新物品的id") int id
            ,@Comment(value = "新物品的名称") String name
            ,@Comment(value = "新物品的最大堆叠上限") int stackSize
            ,@Comment(value = "新物品的类别，可选construction nature equipment items") String type
            ,@Comment(value = "是否展示为工具(竖着拿在手里)") boolean isDisplayAsTool
            ,@Comment(value = "是否可装备在副手") boolean canOnOffhand){
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
            //修改运行时物品数据
            if(id == 326 || id == 327 || id == 343 || id == 435 || id == 436 || id == 439
                    || id == 440 ||(id>477&&id<498)|| id == 512 ||(id>513&&id<720)
                    ||(id>720&&id<734)|| id == 735 ||(id>760&&id<801)|| id>801){
                injectItem2Nukkit(name, id);
                Loader.registerItemIds.add(id);
                //记录物品注册信息
                CustomItemInfo customItemInfo = new CustomItemInfo(id, 4, isDisplayAsTool, canOnOffhand);
                //记录物品分类种类
                switch (type){
                    case "construction": customItemInfo.setType(1);break;
                    case "nature": customItemInfo.setType(2);break;
                    case "equipment": customItemInfo.setType(3);break;
                    default: customItemInfo.setType(4); //item
                }
                Loader.registerItemInfos.put(id, customItemInfo);
                //更新物品注册表
                this.refreshItemPalette();
            }
        } catch (NotFoundException | NoSuchFieldException | CannotCompileException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    @Comment(value = "注册新的工具物品")
    public void registerToolItem(@Comment(value = "新物品的id") int id
            ,@Comment(value = "新物品的名称") String name
            ,@Comment(value = "工具种类,可为sword shovel pickaxe axe hoe") String toolType
            ,@Comment(value = "工具挖掘等级 0-空手,1-木,2-金,3-石,4-铁,5-钻石,6-下界合金") int toolTier
            ,@Comment(value = "工具耐久值") int durability
            ,@Comment(value = "攻击伤害") int attackDamage
            ,@Comment(value = "是否可装备在副手") boolean canOnOffhand){
        try{
            ClassPool classPool = ClassPool.getDefault();
            CtClass itemClass = null;
            classPool.insertClassPath(Loader.pluginFile.getAbsolutePath());
            classPool.insertClassPath(new ClassClassPath(Loader.class));
            classPool.insertClassPath(new ClassClassPath(Nukkit.class));
            classPool.importPackage("com.blocklynukkit.loader");
            //构建物品类
            itemClass = classPool.makeClass("Item_ID_"+id+"_"+Loader.registerItems++,classPool.getCtClass("cn.nukkit.item.ItemTool"));
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
            itemClass.addMethod(CtMethod.make("public int getMaxStackSize(){return 1;}",itemClass));
            //工具种类
            int toolTypeNum = 0;
            switch (toolType){
                case "sword":
                    toolTypeNum = 1;
                    itemClass.addMethod(CtMethod.make("public boolean isSword(){return true;}",itemClass));
                    break;
                case "shovel":
                    toolTypeNum = 2;
                    itemClass.addMethod(CtMethod.make("public boolean isShovel(){return true;}",itemClass));
                    break;
                case "pickaxe":
                    toolTypeNum = 3;
                    itemClass.addMethod(CtMethod.make("public boolean isPickaxe(){return true;}",itemClass));
                    break;
                case "axe":
                    toolTypeNum = 4;
                    itemClass.addMethod(CtMethod.make("public boolean isAxe(){return true;}",itemClass));
                    break;
                case "hoe":
                    toolTypeNum = 6;
                    itemClass.addMethod(CtMethod.make("public boolean isHoe(){return true;}",itemClass));
                    break;
            }
            //工具挖掘等级
            itemClass.addMethod(CtMethod.make("public int getTier(){return "+toolTier+";}",itemClass));
            //工具耐久
            itemClass.addMethod(CtMethod.make("public int getMaxDurability(){return "+durability+";}",itemClass));
            //工具伤害
            itemClass.addMethod(CtMethod.make("public int getAttackDamage() { return "+attackDamage+"; }",itemClass));
            //完成类生成
            Item.list[id] = itemClass.toClass();
            //修改运行时物品数据
            if(id == 326 || id == 327 || id == 343 || id == 435 || id == 436 || id == 439
                    || id == 440 ||(id>477&&id<498)|| id == 512 ||(id>513&&id<720)
                    ||(id>720&&id<734)|| id == 735 ||(id>760&&id<801)|| id>801){
                injectItem2Nukkit(name, id);
                Loader.registerItemIds.add(id);
                //记录工具信息
                CustomItemInfo customItemInfo = new CustomItemInfo(id, canOnOffhand, toolTypeNum, toolTier, durability, attackDamage);
                Loader.registerItemInfos.put(id, customItemInfo);
                //更新物品注册表
                this.refreshItemPalette();
            }
        } catch (NotFoundException | NoSuchFieldException | CannotCompileException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Comment(value = "注册新的食物物品")
    public void registerFoodItem(@Comment(value = "新物品的id") int id
            ,@Comment(value = "新物品的名称") String name
            ,@Comment(value = "新物品的最大堆叠上限") int stackSize
            ,@Comment(value = "提供的饥饿度") int nutrition
            ,@Comment(value = "食用持续时间(刻)") int eatTime
            ,@Comment(value = "是否可装备在副手") boolean canOnOffhand){
        try{
            ClassPool classPool = ClassPool.getDefault();
            CtClass itemClass = null;
            classPool.insertClassPath(Loader.pluginFile.getAbsolutePath());
            classPool.insertClassPath(new ClassClassPath(Loader.class));
            classPool.insertClassPath(new ClassClassPath(Nukkit.class));
            classPool.importPackage("com.blocklynukkit.loader");
            //构建物品类
            itemClass = classPool.makeClass("Item_ID_"+id+"_"+Loader.registerItems++,classPool.getCtClass("cn.nukkit.item.ItemEdible"));
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
            //覆写使用函数
            itemClass.addMethod(CtMethod.make("public boolean onUse(cn.nukkit.Player player, int ticksUsed){" +
                    "   return super.onUse(player, ticksUsed);" +
                    "}",itemClass));
            //最大堆叠数量
            itemClass.addMethod(CtMethod.make("public int getMaxStackSize(){return "+stackSize+";}",itemClass));
            Item.list[id] = itemClass.toClass();
            //构建食物类
            Food.registerFood((new FoodNormal(nutrition, nutrition*0.6f)).addRelative(id), Loader.plugin);
            //修改运行时物品数据
            if(id == 326 || id == 327 || id == 343 || id == 435 || id == 436 || id == 439
                    || id == 440 ||(id>477&&id<498)|| id == 512 ||(id>513&&id<720)
                    ||(id>720&&id<734)|| id == 735 ||(id>760&&id<801)|| id>801){
                injectItem2Nukkit(name, id);
                Loader.registerItemIds.add(id);
                //记录物品注册信息
                CustomItemInfo customItemInfo = new CustomItemInfo(id, true, canOnOffhand, eatTime, nutrition);
                Loader.registerItemInfos.put(id, customItemInfo);
                //更新物品注册表
                this.refreshItemPalette();
            }
        } catch (NotFoundException | NoSuchFieldException | CannotCompileException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Comment(value = "注册新的饮品物品")
    public void registerDrinkItem(@Comment(value = "新物品的id") int id
            ,@Comment(value = "新物品的名称") String name
            ,@Comment(value = "新物品的最大堆叠上限") int stackSize
            ,@Comment(value = "提供的饥饿度") int nutrition
            ,@Comment(value = "饮用持续时间(刻)") int drinkTime
            ,@Comment(value = "是否可装备在副手") boolean canOnOffhand){
        try{
            ClassPool classPool = ClassPool.getDefault();
            CtClass itemClass = null;
            classPool.insertClassPath(Loader.pluginFile.getAbsolutePath());
            classPool.insertClassPath(new ClassClassPath(Loader.class));
            classPool.insertClassPath(new ClassClassPath(Nukkit.class));
            classPool.importPackage("com.blocklynukkit.loader");
            //构建物品类
            itemClass = classPool.makeClass("Item_ID_"+id+"_"+Loader.registerItems++,classPool.getCtClass("cn.nukkit.item.ItemEdible"));
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
            //覆写使用函数
            itemClass.addMethod(CtMethod.make("public boolean onUse(cn.nukkit.Player player, int ticksUsed){" +
                    "   return super.onUse(player, ticksUsed);" +
                    "}",itemClass));
            //最大堆叠数量
            itemClass.addMethod(CtMethod.make("public int getMaxStackSize(){return "+stackSize+";}",itemClass));
            Item.list[id] = itemClass.toClass();
            //构建食物类
            Food.registerFood((new FoodNormal(nutrition, nutrition*0.6f)).addRelative(id), Loader.plugin);
            //修改运行时物品数据
            if(id == 326 || id == 327 || id == 343 || id == 435 || id == 436 || id == 439
                    || id == 440 ||(id>477&&id<498)|| id == 512 ||(id>513&&id<720)
                    ||(id>720&&id<734)|| id == 735 ||(id>760&&id<801)|| id>801){
                injectItem2Nukkit(name, id);
                Loader.registerItemIds.add(id);
                //记录物品注册信息
                CustomItemInfo customItemInfo = new CustomItemInfo(id, false, canOnOffhand, drinkTime, nutrition);
                Loader.registerItemInfos.put(id, customItemInfo);
                //更新物品注册表
                this.refreshItemPalette();
            }
        } catch (NotFoundException | NoSuchFieldException | CannotCompileException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Comment(value = "注册新的盔甲物品")
    public void registerArmorItem(@Comment(value = "新物品的id") int id
            ,@Comment(value = "新物品的名称") String name
            ,@Comment(value = "盔甲种类,可为helmet chest leggings boots") String armorType
            ,@Comment(value = "盔甲等级 0-无,1-皮革,2-铁,3-锁链,4-金,5-钻石,6-下界合金") int armorTier
            ,@Comment(value = "工具耐久值") int durability
            ,@Comment(value = "提供的盔甲值") int armorPoint
            ,@Comment(value = "是否可装备在副手") boolean canOnOffhand){
        try{
            ClassPool classPool = ClassPool.getDefault();
            CtClass itemClass = null;
            classPool.insertClassPath(Loader.pluginFile.getAbsolutePath());
            classPool.insertClassPath(new ClassClassPath(Loader.class));
            classPool.insertClassPath(new ClassClassPath(Nukkit.class));
            classPool.importPackage("com.blocklynukkit.loader");
            //构建物品类
            itemClass = classPool.makeClass("Item_ID_"+id+"_"+Loader.registerItems++,classPool.getCtClass("cn.nukkit.item.ItemArmor"));
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
            itemClass.addMethod(CtMethod.make("public int getMaxStackSize(){return 1;}",itemClass));
            //表明是盔甲
            itemClass.addMethod(CtMethod.make("public boolean isArmor(){return true;}",itemClass));
            //工具种类
            int armorTypeNum = 0;
            switch (armorType){
                case "helmet":
                    armorTypeNum = 0;
                    itemClass.addMethod(CtMethod.make("public boolean isHelmet(){return true;}",itemClass));
                    break;
                case "chest":
                    armorTypeNum = 1;
                    itemClass.addMethod(CtMethod.make("public boolean isChestplate(){return true;}",itemClass));
                    break;
                case "leggings":
                    armorTypeNum = 2;
                    itemClass.addMethod(CtMethod.make("public boolean isLeggings(){return true;}",itemClass));
                    break;
                case "boots":
                    armorTypeNum = 3;
                    itemClass.addMethod(CtMethod.make("public boolean isBoots(){return true;}",itemClass));
                    break;
            }
            //盔甲品阶
            itemClass.addMethod(CtMethod.make("public int getTier(){return "+armorTier+";}",itemClass));
            //盔甲耐久
            itemClass.addMethod(CtMethod.make("public int getMaxDurability(){return "+durability+";}",itemClass));
            //盔甲护甲值
            itemClass.addMethod(CtMethod.make("public int getArmorPoints(){return "+armorPoint+";}",itemClass));
            //完成类生成
            Item.list[id] = itemClass.toClass();
            //修改运行时物品数据
            if(id == 326 || id == 327 || id == 343 || id == 435 || id == 436 || id == 439
                    || id == 440 ||(id>477&&id<498)|| id == 512 ||(id>513&&id<720)
                    ||(id>720&&id<734)|| id == 735 ||(id>760&&id<801)|| id>801){
                injectItem2Nukkit(name, id);
                Loader.registerItemIds.add(id);
                //记录盔甲信息
                CustomItemInfo customItemInfo = new CustomItemInfo(id, armorTypeNum, canOnOffhand, durability);
                Loader.registerItemInfos.put(id, customItemInfo);
                //更新物品注册表
                this.refreshItemPalette();
            }
        } catch (NotFoundException | NoSuchFieldException | CannotCompileException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Comment(value = "刷新客户端物品注册表")
    public void refreshItemPalette(){
        if(Loader.isEnabling){
            return;
        }
        InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_item_states.json");
        if(stream == null){
            stream = Server.class.getClassLoader().getResourceAsStream("runtime_item_ids.json");
        }
        if (stream == null) {
            throw new AssertionError("Unable to load runtime_item_states.json or runtime_item_ids.json");
        }
        final Gson GSON = new Gson();
        final Type ENTRY_TYPE = new TypeToken<ArrayList<ItemData>>(){}.getType();
        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        Collection<ItemData> entries = GSON.fromJson(reader, ENTRY_TYPE);
        BinaryStream paletteBuffer = new BinaryStream();
        paletteBuffer.putUnsignedVarInt(entries.size() + Loader.registerItems);
        for (ItemData entry : entries) {
            paletteBuffer.putString(entry.name);
            paletteBuffer.putLShort(entry.id);
            paletteBuffer.putBoolean(false);
        }
        for(Integer i : Loader.registerItemIds){
            Item item = Item.get(i);
            paletteBuffer.putString("blocklynukkit:"+item.getName());
            paletteBuffer.putLShort(i);
            paletteBuffer.putBoolean(true);
        }
        Loader.ItemPalette = paletteBuffer.getBuffer();
    }

    @Comment(value = "向材质包中指定位置添加json文件")
    public void addResourcePackJsonEntry(@Comment(value = "材质包内相对位置，包含路径和文件全名") String entryPath
            ,@Comment(value = "json文件内容") String json){
        blocklyNukkitMcpack.addNode(new ResourceNode().putData(entryPath,new ResourceJSON(json)));
    }

    @Comment(value = "向材质包中指定位置添加图片文件")
    public void addResourcePackPictureEntry(@Comment(value = "材质包内相对位置，包含路径和文件全名") String entryPath
            ,@Comment(value = "要复制到材质包中的硬盘上的图片路径") String path){
        blocklyNukkitMcpack.addNode(new ResourceNode().putData(entryPath,new ResourcePicture(path)));
    }

    @Comment(value = "添加新的物品材质")
    public void addItemTexture(@Comment(value = "物品id") int id
            ,@Comment(value = "物品材质图片路径") String path){
        Item tmp = Item.get(id);
        ResourceItemManifest.addItem(tmp.getName());
        blocklyNukkitMcpack.addNode(
                new ResourceNode().putData("textures/items/"+tmp.getName()+".png",new ResourcePicture(path))
        );
    }

    @Comment(value = "添加新的盔甲材质")
    public void addArmorTexture(@Comment(value = "物品id") int id
            ,@Comment(value = "盔甲物品栏材质图片路径") String inventoryPath
            ,@Comment(value = "盔甲穿着时材质图片路径") String modelPath){
        Item tmp = Item.get(id);
        ResourceItemManifest.addItem(tmp.getName());
        String type = "helmet";
        if(tmp.isChestplate()){
            type = "chest";
        }else if(tmp.isLeggings()){
            type = "leggings";
        }else if(tmp.isBoots()){
            type = "boots";
        }
        blocklyNukkitMcpack.addNode(
                new ResourceNode()
                        .putData("textures/items/"+tmp.getName()+".png",new ResourcePicture(inventoryPath))
                        .putData("textures/models/armor/"+tmp.getName()+".png", new ResourcePicture(modelPath))
                        .putData("attachables/"+tmp.getName()+".json", new ResourceArmorManifest(tmp.getName(), type))
        );
    }

    @Comment(value = "为指定id物品添加中文翻译名")
    public void addItemChineseTranslation(@Comment(value = "物品id") int id
            ,@Comment(value = "中文名") String name){
        Item tmp = Item.get(id);
        mcpackTranslation.addChineseTranslation("item.blocklynukkit:"+tmp.getName(), name);
    }

    @Comment(value = "为指定id物品添加英文翻译名")
    public void addItemEnglishTranslation(@Comment(value = "物品id") int id
            ,@Comment(value = "英文名") String name){
        Item tmp = Item.get(id);
        mcpackTranslation.addEnglishTranslation("item.blocklynukkit:"+tmp.getName(), name);
    }
}
