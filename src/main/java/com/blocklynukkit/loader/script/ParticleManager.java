package com.blocklynukkit.loader.script;

import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import com.blocklynukkit.loader.other.McFunction;
import com.blocklynukkit.loader.other.particle.CircleFlat;
import com.blocklynukkit.loader.other.particle.FireworkRocket;
import com.blocklynukkit.loader.other.particle.LineFlat;

public class ParticleManager {
    public void drawCircle(Position pos,double radius,int pid,double sep){
        new Thread(new CircleFlat(pos, radius, pid, sep)).start();
    }
    public void drawLine(Position pos1,Position pos2,double sep,int pid){
        new Thread(new LineFlat(pos1, pos2, sep, pid)).start();
    }
    public void drawFireWork(Position pos, int colornum, boolean flick, boolean trail, int shape, int second){
        FireworkRocket.make(pos.level,pos,colornum,flick,trail,shape,second);
    }
    public void drawParticleFactoryMcFunction(String fun,Position pos,double turn){
        new McFunction(fun,pos,pos.level).setturn(turn).run();
    }
}
