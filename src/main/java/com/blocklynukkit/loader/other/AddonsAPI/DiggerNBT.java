package com.blocklynukkit.loader.other.AddonsAPI;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;

public class DiggerNBT {
    public static String[] pickaxeBlocks = {
            "minecraft:ice","minecraft:anvil","minecraft:bone_block","minecraft:iron_trapdoor",
            "minecraft:undyed_shulker_box","minecraft:shulker_box","minecraft:prismarine",
            "minecraft:double_stone_slab2","minecraft:stone_slab4","minecraft:prismarine_bricks_stairs",
            "minecraft:prismarine_stairs","minecraft:dark_prismarine_stairs","minecraft:nether_brick_fence",
            "minecraft:crying_obsidian","minecraft:magma","minecraft:smoker","minecraft:lit_smoker",
            "minecraft:hopper","minecraft:redstone_block","minecraft:mob_spawner",
            "minecraft:netherite_block","minecraft:sandstone","minecraft:grindstone",
            "minecraft:enchanting_table","minecraft:cracked_polished_blackstone_bricks",
            "minecraft:nether_brick","minecraft:cracked_nether_bricks",
            "minecraft:lapis_block","minecraft:emerald_block","minecraft:end_bricks",
            "minecraft:purpur_block","minecraft:purpur_stairs","minecraft:end_brick_stairs",
            "minecraft:stone_slab2","minecraft:stone_slab3","minecraft:stone_brick_stairs",
            "minecraft:mossy_stone_brick_stairs","minecraft:polished_blackstone_bricks",
            "minecraft:polished_blackstone_stairs","minecraft:blackstone_wall","minecraft:blackstone_wall",
            "minecraft:polished_blackstone_wall","minecraft:smooth_stone","minecraft:smooth_stone",
            "minecraft:stonebrick","minecraft:brewing_stand","minecraft:chain","minecraft:lantern",
            "minecraft:soul_lantern","minecraft:ancient_debris","minecraft:quartz_ore",
            "minecraft:netherrack","minecraft:basalt","minecraft:polished_basalt","minecraft:warped_nylium",
            "minecraft:crimson_nylium","minecraft:end_stone","minecraft:ender_chest",
            "minecraft:quartz_block","minecraft:quartz_stairs","minecraft:quartz_bricks",
            "minecraft:quartz_stairs","minecraft:nether_gold_ore","minecraft:furnace",
            "minecraft:blast_furnace","minecraft:lit_furnace","minecraft:blast_furnace", "minecraft:blackstone"
    };
    public static CompoundTag getPickaxeDiggerNBT(int tier){
        int speed = 1;
        if(tier == 0){
            return new CompoundTag().putBoolean("use_efficiency",true);
        }else if(tier == 5){
            speed = 6;
        }else if(tier == 4){
            speed = 5;
        }else if(tier == 3){
            speed = 4;
        }else if(tier == 2){
            speed = 3;
        }else if(tier == 1){
            speed = 2;
        }
        CompoundTag diggerRoot = new CompoundTag().putBoolean("use_efficiency",true);
        ListTag<Tag> destroy_speeds = new ListTag<>("destroy_speeds");
        destroy_speeds.add(new CompoundTag()
                .putCompound("block",new CompoundTag()
                        .putString("tags","q.any_tag('stone', 'metal', 'diamond_pick_diggable', 'mob_spawner', 'rail')"))
                .putInt("speed",speed));
        //too many tags will make client crash
//        for(String each:pickaxeBlocks){
//            destroy_speeds.add(new CompoundTag()
//                .putString("block",each)
//                .putInt("speed",speed));
//        }
        return diggerRoot.putList(destroy_speeds);
    }
    public static CompoundTag getAxeDiggerNBT(int tier){
        int speed = 1;
        if(tier == 0){
            return new CompoundTag().putBoolean("use_efficiency",true);
        }else if(tier == 5){
            speed = 6;
        }else if(tier == 4){
            speed = 5;
        }else if(tier == 3){
            speed = 4;
        }else if(tier == 2){
            speed = 3;
        }else if(tier == 1){
            speed = 2;
        }
        CompoundTag diggerRoot = new CompoundTag().putBoolean("use_efficiency",true);
        ListTag<Tag> destroy_speeds = new ListTag<>("destroy_speeds");
        destroy_speeds.add(new CompoundTag()
                .putCompound("block",new CompoundTag()
                        .putString("tags","q.any_tag('wood', 'pumpkin', 'plant')"))
                .putInt("speed",speed));
        return diggerRoot.putList(destroy_speeds);
    }
    public static CompoundTag getShovelDiggerNBT(int tier){
        int speed = 1;
        if(tier == 0){
            return new CompoundTag().putBoolean("use_efficiency",true);
        }else if(tier == 5){
            speed = 6;
        }else if(tier == 4){
            speed = 5;
        }else if(tier == 3){
            speed = 4;
        }else if(tier == 2){
            speed = 3;
        }else if(tier == 1){
            speed = 2;
        }
        CompoundTag diggerRoot = new CompoundTag().putBoolean("use_efficiency",true);
        ListTag<Tag> destroy_speeds = new ListTag<>("destroy_speeds");
        destroy_speeds.add(new CompoundTag()
                .putCompound("block",new CompoundTag()
                        .putString("tags","q.any_tag('sand', 'dirt', 'gravel', 'snow')"))
                .putInt("speed",speed));
        return diggerRoot.putList(destroy_speeds);
    }
}
