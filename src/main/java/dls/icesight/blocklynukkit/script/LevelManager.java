package dls.icesight.blocklynukkit.script;

import cn.nukkit.Server;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.Flat;
import cn.nukkit.level.generator.Nether;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.Map;

public class LevelManager {
    public void genLevel(String name, long seed, String generator){
        switch(generator){
            case "FLAT":
                Server.getInstance().generateLevel(name,seed, Flat.class);
                break;
            case "NETHER":
                Server.getInstance().generateLevel(name,seed, Nether.class);
                break;
            case "VOID":
                Server.getInstance().generateLevel(name,seed, Void.class);
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
    private class Void extends cn.nukkit.level.generator.Generator {
        private final String NAME = "Void";
        private ChunkManager chunkManager;
        @Override
        public int getId() {
            return 0;
        }

        @Override
        public void init(ChunkManager chunkManager, NukkitRandom nukkitRandom) {
            this.chunkManager = chunkManager;
        }

        @Override
        public void generateChunk(int i, int i1) {

        }

        @Override
        public void populateChunk(int i, int i1) {

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
