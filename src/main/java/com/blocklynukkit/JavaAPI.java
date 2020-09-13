package com.blocklynukkit;

import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.script.*;

public class JavaAPI {
    public AlgorithmManager algorithm = Loader.algorithmManager;
    public BlockItemManager blockitem = Loader.blockItemManager;
    public DatabaseManager database = Loader.databaseManager;
    public EntityManager entity = Loader.entityManager;
    public FunctionManager function = Loader.functionManager;
    public GameManager gameapi = Loader.gameManager;
    public InventoryManager inventory = Loader.inventoryManager;
    public LevelManager world = Loader.levelManager;
    public NotemusicManager notemusic = Loader.notemusicManager;
    public ParticleManager particle = Loader.particleManager;
    public WindowManager window = Loader.windowManager;
}
