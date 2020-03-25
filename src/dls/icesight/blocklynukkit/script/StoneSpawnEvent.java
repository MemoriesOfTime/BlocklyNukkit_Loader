package dls.icesight.blocklynukkit.script;

import cn.nukkit.block.Block;
import cn.nukkit.level.Position;

public class StoneSpawnEvent extends cn.nukkit.event.Event{
    public Position pos;
    public Block block;
    public StoneSpawnEvent(Position position,Block block1){
        pos=position;block=block1;
    }
    public Position getPosition(){return pos;}
    public Block getBlock(){return block;}
}
