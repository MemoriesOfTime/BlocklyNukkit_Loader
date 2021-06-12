package com.blocklynukkit.loader.script;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import com.blocklynukkit.loader.api.CallbackFunction;
import com.blocklynukkit.loader.api.Comment;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.script.bases.BaseManager;

import javax.script.ScriptEngine;

public final class AlgorithmManager extends BaseManager {
    public AlgorithmManager(ScriptEngine scriptEngine) {
        super(scriptEngine);
    }
    @Comment(value = "为a到b位置的所有方块执行回调函数")
    public void forEachBlockInArea(@Comment(value = "起点") Position a
            ,@Comment(value = "终点") Position b
            ,@Comment(value = "是否为空气方块也执行回调函数") boolean isair
            ,@Comment(value = "回调函数名，参数(cn.nukkit.Block)")
             @CallbackFunction(classes = "cn.nukkit.block.Block", parameters = "block", comments = "当前执行到的方块") String callback){
        Level level = a.level;
        for (int i=(int)a.x;i<(int)b.x;i++){
            for (int j=(int)a.y;i<(int)b.y;j++){
                for (int k=(int)a.z;i<(int)b.z;k++){
                    if(level.getBlockIdAt(i,j,k)==0){
                        if(isair){
                            Loader.plugin.call(callback,level.getBlock(i,j,k));
                        }
                    }else {
                        Loader.plugin.call(callback,level.getBlock(i,j,k));
                    }
                }
            }
        }
    }
    @Comment(value = "为指定坐标相邻的同种方块及相邻同种方块的相邻同种方块执行回调函数")
    public void forLinkedBlock(@Comment(value = "指定坐标") Position a
            ,@Comment(value = "回调函数，参数(cn.nukkit.level.Position)")
             @CallbackFunction(classes = "cn.nukkit.level.Position", parameters = "pos", comments = "当前执行到的位置") String callback){
        Loader.positionstmp="";
        forLinkedBlock((int)a.x,(int)a.y,(int)a.z,callback,0,a.level,a.getLevelBlock().getId());
    }

    @Comment(value = "xyz转字符串")
    public static String posinttostr(int x,int y,int z){
        return x+","+y+","+z+";";
    }

    @Comment(value = "forLinkedBlock(cn.nukkit.level.Position,java.lang.String)的递归函数")
    private void forLinkedBlock(int x,int y,int z,String callback,int step,Level level,int id){
        if(level.getBlockIdAt(x,y,z)==0)return;
        if(step>50)return;
        Loader.plugin.call(callback,Position.fromObject(new Vector3(x,y,z),level));
        Loader.positionstmp+=posinttostr(x,y,z);
        if(level.getBlockIdAt(x,y+1,z)==id&&(!Loader.positionstmp.contains(posinttostr(x,y+1,z))))forLinkedBlock(x,y+1,z,callback,step+1,level,id);
        if(level.getBlockIdAt(x,y-1,z)==id&&(!Loader.positionstmp.contains(posinttostr(x,y-1,z))))forLinkedBlock(x,y-1,z,callback,step+1,level,id);
        if(level.getBlockIdAt(x+1,y,z)==id&&(!Loader.positionstmp.contains(posinttostr(x+1,y,z))))forLinkedBlock(x+1,y,z,callback,step+1,level,id);
        if(level.getBlockIdAt(x-1,y,z)==id&&(!Loader.positionstmp.contains(posinttostr(x-1,y,z))))forLinkedBlock(x-1,y,z,callback,step+1,level,id);
        if(level.getBlockIdAt(x,y,z+1)==id&&(!Loader.positionstmp.contains(posinttostr(x,y,z+1))))forLinkedBlock(x,y,z+1,callback,step+1,level,id);
        if(level.getBlockIdAt(x,y,z-1)==id&&(!Loader.positionstmp.contains(posinttostr(x,y,z-1))))forLinkedBlock(x,y,z-1,callback,step+1,level,id);
    }
    @Comment(value = "从玩家构建坐标对象")
    public Position buildPositionfromPlayer(@Comment(value = "从哪个玩家") Player player){
        return player.getPosition().clone();
    }
    @Comment(value = "从方块构建坐标对象")
    public Position buildPositionfromBlock(@Comment(value = "从哪个方块") Block block){
        return (Position) block.clone();
    }
    @Comment(value = "从实体构建坐标对象")
    public Position buildPositionfromEntity(@Comment(value = "从哪个实体") Entity entity){return (Position) entity.clone();}
    @Comment(value = "通过xyz和世界构建坐标对象")
    public Position buildPosition(@Comment(value = "x") double x,@Comment(value = "y") double y,@Comment(value = "z") double z,@Comment(value = "世界") Level level){
        return new Position(x, y, z, level);
    }

    @Override
    public String toString() {
        return "BlocklyNukkit Based Object";
    }
}
