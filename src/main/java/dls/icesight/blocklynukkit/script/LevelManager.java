package dls.icesight.blocklynukkit.script;

import cn.nukkit.Server;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.generator.Flat;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.Nether;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import dls.icesight.blocklynukkit.other.generator.SkyLand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelManager {
    public LevelManager(){
        Generator.addGenerator(Void.class,"void_bn",Generator.TYPE_INFINITE);
        Generator.addGenerator(SkyLand.class,"skyland_bn",Generator.TYPE_INFINITE);
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
                Server.getInstance().generateLevel(name,seed, Generator.getGenerator("skyland_bn"));
                break;
            case "NORMAL":
            default:
                Server.getInstance().generateLevel(name,seed, Normal.class);
                break;
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
