package com.blocklynukkit.loader.script;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.anvil.Chunk;
import cn.nukkit.level.generator.Flat;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.Nether;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ChangeDimensionPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.generator.OceanGenerator;
import com.blocklynukkit.loader.other.generator.SkyLand;
import com.blocklynukkit.loader.other.generator.VoidGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelManager {
    public Map<String,Object> skylandoptions = new HashMap<>();
    public int OceanSeaLevel = 64;
    public LevelManager(){
        Generator.addGenerator(VoidGenerator.class,"void_bn2",Generator.TYPE_INFINITE);
        Generator.addGenerator(SkyLand.class,"skyland_bn3",Generator.TYPE_INFINITE);
        Generator.addGenerator(OceanGenerator.class,"ocean_bn4",Generator.TYPE_INFINITE);
    }
    public void genLevel(String name, long seed, String generator){
        switch(generator){
            case "FLAT":
                Server.getInstance().generateLevel(name,seed, Flat.class);
                break;
            case "NETHER":
                Server.getInstance().generateLevel(name,seed, Nether.class);
                break;
            case "VOID":
                Server.getInstance().generateLevel(name,seed, Generator.getGenerator("void_bn2"));
                Level l2 = Server.getInstance().getLevelByName(name);
                Position pos = l2.getSafeSpawn();
                l2.setBlock(pos.add(0,-2,0), Block.get(BlockID.BEDROCK));
                break;
            case "SKYLAND":
                if(skylandoptions.keySet().size()<2){
                    setSkyLandGenerator(64,0,true,
                            20,17,0,128,20,9,0,64,
                            8,8,0,16,1,7,0,10,
                            2,9,0,32,1,8,0,16,
                            10,33,0,128,8,33,0,128,
                            10,33,0,80,10,33,0,80,
                            10,33,0,80,true,true,true);
                }
                Server.getInstance().generateLevel(name,seed, Generator.getGenerator("skyland_bn3"));
                Level l = Server.getInstance().getLevelByName(name);
                Position pos2 = l.getSafeSpawn();
                l.setBlock(pos2.add(0,-2,0), Block.get(BlockID.BEDROCK));
                break;
            case "OCEAN":
                Server.getInstance().generateLevel(name,seed, Generator.getGenerator("ocean_bn4"));
                Level l3 = Server.getInstance().getLevelByName(name);
                Position pos3 = l3.getSafeSpawn();
                pos3.y=OceanSeaLevel;
                l3.setBlock(pos3.add(0,1,0), Block.get(BlockID.BEDROCK));
                break;
            case "NORMAL":
            default:
                Server.getInstance().generateLevel(name,seed, Normal.class);
                break;
        }

    }
    //here 4/29
    public void setSkyLandGenerator(
        int seaHeight,int movey,boolean enableOre,
        int coalcount,int coalsize,int coalmin,int coalmax,
        int ironcount,int ironsize,int ironmin,int ironmax,
        int redstonecount,int redstonesize,int redstonemin,int redstonemax,
        int lapiscount,int lapissize,int lapismin,int lapismax,
        int goldcount,int goldsize,int goldmin,int goldmax,
        int diamondcount,int diamondsize,int diamondmin,int diamondmax,
        int dirtcount,int dirtsize,int dirtmin,int dirtmax,
        int gravelcount,int gravelsize,int gravelmin,int gravelmax,
        int granitecount,int granitesize,int granitemin,int granitemax,
        int dioritecount,int dioritesize,int dioritemin,int dioritemax,
        int andesitecount,int andesitesize,int andesitemin,int andesitemax,
        boolean enableCave,boolean enableBiome,boolean enableOcean
    ){
        Map<String,Object> map = new HashMap<>();
        map.put("movey",movey);
        map.put("seaHeight",seaHeight);
        map.put("enableOre",enableOre);
        map.put("enableCave",enableCave);
        map.put("enableBiome",enableBiome);
        map.put("enableOcean",enableOcean);
        map.put("coal_option_count",coalcount);
        map.put("coal_option_size",coalsize);
        map.put("coal_option_min",coalmin);
        map.put("coal_option_max",coalmax);
        map.put("iron_option_count",ironcount);
        map.put("iron_option_size",ironsize);
        map.put("iron_option_min",ironmin);
        map.put("iron_option_max",ironmax);
        map.put("redstone_option_count",redstonecount);
        map.put("redstone_option_size",redstonesize);
        map.put("redstone_option_min",redstonemin);
        map.put("redstone_option_max",redstonemax);
        map.put("lapis_option_count",lapiscount);
        map.put("lapis_option_size",lapissize);
        map.put("lapis_option_min",lapismin);
        map.put("lapis_option_max",lapismax);
        map.put("gold_option_count",goldcount);
        map.put("gold_option_size",goldsize);
        map.put("gold_option_min",goldmin);
        map.put("gold_option_max",goldmax);
        map.put("diamond_option_count",diamondcount);
        map.put("diamond_option_size",diamondsize);
        map.put("diamond_option_min",diamondmin);
        map.put("diamond_option_max",diamondmax);
        map.put("dirt_option_count",dirtcount);
        map.put("dirt_option_size",dirtsize);
        map.put("dirt_option_min",dirtmin);
        map.put("dirt_option_max",dirtmax);
        map.put("gravel_option_count",gravelcount);
        map.put("gravel_option_size",gravelsize);
        map.put("gravel_option_min",gravelmin);
        map.put("gravel_option_max",gravelmax);
        map.put("granite_option_count",granitecount);
        map.put("granite_option_size",granitesize);
        map.put("granite_option_min",granitemin);
        map.put("granite_option_max",granitemax);
        map.put("diorite_option_count",dioritecount);
        map.put("diorite_option_size",dioritesize);
        map.put("diorite_option_min",dioritemin);
        map.put("diorite_option_max",dioritemax);
        map.put("andesite_option_count",andesitecount);
        map.put("andesite_option_size",andesitesize);
        map.put("andesite_option_min",andesitemin);
        map.put("andesite_option_max",andesitemax);
        skylandoptions=map;
    }
    public void setOceanGenerator(int seaLevel){
        this.OceanSeaLevel=seaLevel;
    }
    public void dosaveSkyLandGeneratorSettings(){
        File folder=new File(Loader.plugin.getDataFolder()+"/GeneratorSettings");
        folder.mkdirs();
        Config config = new Config(Loader.plugin.getDataFolder()+"/GeneratorSettings/SkyLandGeneratorSettings.yml");
        for(Map.Entry<String,Object> each:skylandoptions.entrySet()){
            config.set(each.getKey(),each.getValue());
        }
        config.save();
    }
    public void doreloadSkyLandGeneratorSettings(){
        if(new File(Loader.plugin.getDataFolder()+"/GeneratorSettings/SkyLandGeneratorSettings.yml").exists()){
            Config config = new Config(Loader.plugin.getDataFolder()+"/GeneratorSettings/SkyLandGeneratorSettings.yml");
            for(String each:config.getKeys()){
                skylandoptions.put(each,config.get(each));
            }
        }
    }
    public void dosaveOceanGeneratorSettings(){
        File folder=new File(Loader.plugin.getDataFolder()+"/GeneratorSettings");
        folder.mkdirs();
        Config config = new Config(Loader.plugin.getDataFolder()+"/GeneratorSettings/OceanGeneratorSettings.yml");
        config.set("OceanSeaLevel",OceanSeaLevel);
        config.save();
    }
    public void doreloadOceanGeneratorSettings(){
        if(new File(Loader.plugin.getDataFolder()+"/GeneratorSettings/OceanGeneratorSettings.yml").exists()){
            Config config = new Config(Loader.plugin.getDataFolder()+"/GeneratorSettings/OceanGeneratorSettings.yml");
            OceanSeaLevel = config.getInt("OceanSeaLevel");
        }
    }
    public void loadLevel(String string){
        Server.getInstance().loadLevel(string);
    }
    //here 4/23
    public List<Level> getServerLevels(){
        return new ArrayList<>(Server.getInstance().getLevels().values());
    }

    public void loadScreenTP(Player player,Position pos){
        this.loadScreenTP(player, pos, 60,false);
    }
    public void loadScreenTP(Player player,Position pos,int loadScreenTick){
        this.loadScreenTP(player, pos, loadScreenTick,false);
    }
    public void loadScreenTP(Player player,Position pos,int loadScreenTick,boolean finish){
        boolean has = false;
        for (File file:new File(Server.getInstance().getDataPath()+"/worlds/").listFiles()){
            if(file.getName().equals("loadScreenWorld")&&file.isDirectory()){
                Server.getInstance().loadLevel("loadScreenWorld");
                has = true;
                break;
            }
        }
        if(!has){
            genLevel("loadScreenWorld",999,"VOID");
        }

        Level level = Server.getInstance().getLevelByName("loadScreenWorld");

        ChangeDimensionPacket changeDimensionPacket = new ChangeDimensionPacket();
        changeDimensionPacket.dimension = 2;
        player.dataPacket(changeDimensionPacket);

        Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
            public void onRun(int i) {
                PlayStatusPacket playStatusPacket = new PlayStatusPacket();
                playStatusPacket.status = 3;
                player.dataPacket(playStatusPacket);
            }
        }, 1);

        Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
            public void onRun(int i) {
                ChangeDimensionPacket changeDimensionPacket = new ChangeDimensionPacket();
                changeDimensionPacket.dimension = 0;
                player.dataPacket(changeDimensionPacket);
                player.teleport(level.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.UNKNOWN);
            }
        }, 2);

        Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
            public void onRun(int i) {
                PlayStatusPacket playStatusPacket = new PlayStatusPacket();
                playStatusPacket.status = 3;
                player.dataPacket(playStatusPacket);
            }
        }, 3+loadScreenTick);
        Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
            public void onRun(int i) {
                player.teleport(pos, PlayerTeleportEvent.TeleportCause.UNKNOWN);
                if(!finish){
                    loadScreenTP(player, pos, loadScreenTick,true);
                }
            }
        }, 4+loadScreenTick);

    }
    public void clearChunk(Position pos){
        pos.level.setChunk((int)pos.x>>4,(int)pos.z>>4, Chunk.getEmptyChunk((int)pos.x>>4,(int)pos.z>>4));
    }
}
