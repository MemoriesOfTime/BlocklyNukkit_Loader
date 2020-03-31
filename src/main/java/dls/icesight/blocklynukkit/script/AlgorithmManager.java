package dls.icesight.blocklynukkit.script;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.Flat;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.Nether;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import dls.icesight.blocklynukkit.Loader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Map;

public class AlgorithmManager {
    public void forEachBlockInArea(Position a, Position b,boolean isair,String callback){
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
}
