package com.blocklynukkit.loader.script;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.script.bases.BaseManager;

import javax.script.ScriptEngine;

public class AlgorithmManager extends BaseManager {
    public AlgorithmManager(ScriptEngine scriptEngine) {
        super(scriptEngine);
    }

    public void forEachBlockInArea(Position a, Position b, boolean isair, String callback){
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

    public void forLinkedBlock(Position a,String callback){
        Loader.positionstmp="";
        forLinkedBlock((int)a.x,(int)a.y,(int)a.z,callback,0,a.level,a.getLevelBlock().getId());
    }


    public static String posinttostr(int x,int y,int z){
        return x+","+y+","+z+";";
    }
    public void forLinkedBlock(int x,int y,int z,String callback,int step,Level level,int id){
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
    public Position buildPositionfromPlayer(Player player){
        return player.getPosition();
    }
    public Position buildPositionfromBlock(Block block){
        return (Position)block;
    }
    public Position buildPositionfromEntity(Entity entity){return (Position) entity;}
    public Position buildPosition(double x,double y,double z,Level level){
        return new Position(x, y, z, level);
    }

    @Override
    public String toString() {
        return "BlocklyNukkit Based Object";
    }
}
