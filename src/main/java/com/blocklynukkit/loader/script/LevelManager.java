package com.blocklynukkit.loader.script;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.generator.Flat;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.Nether;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.generator.SkyLand;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelManager {
    public Map<String,Object> skylandoptions = new HashMap<>();
    public LevelManager(){
        Generator.addGenerator(Void.class,"void_bn",Generator.TYPE_INFINITE);
        Generator.addGenerator(SkyLand.class,"skyland_bn3",Generator.TYPE_INFINITE);
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
                Server.getInstance().generateLevel(name,seed, Generator.getGenerator("void_bn"));
                break;
            case "SKYLAND":
                Server.getInstance().generateLevel(name,seed, Generator.getGenerator("skyland_bn3"));
                Level l = Server.getInstance().getLevelByName(name);
                l.setBlock(l.getSafeSpawn().add(0,-2,0), Block.get(BlockID.BEDROCK));
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
    public void loadLevel(String string){
        Server.getInstance().loadLevel(string);
    }
    //here 4/23
    public List<Level> getServerLevels(){
        return new ArrayList<>(Server.getInstance().getLevels().values());
    }
    public class Void extends cn.nukkit.level.generator.Generator {
        private final String NAME = "void_bn";
        private ChunkManager chunkManager;
        private NukkitRandom random;
        @Override
        public int getId() {
            return 3324;
        }

        @Override
        public void init(ChunkManager chunkManager, NukkitRandom nukkitRandom) {
            this.chunkManager = chunkManager;
        }

        @Override
        public void generateChunk(int i, int i1) {

        }

        @Override
        public void populateChunk(int chunkX, int chunkZ) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    this.chunkManager.getChunk(chunkX, chunkZ).setBiomeId(x, z, Biome.AIR);
                }
            }
        }

        @Override
        public Map<String, Object> getSettings() {
            return null;
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public Vector3 getSpawn() {
            return new Vector3(128.0, 65.0, 128.0);
        }

        @Override
        public ChunkManager getChunkManager() {
            return chunkManager;
        }
    }
}
