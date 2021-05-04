package com.blocklynukkit.loader.script;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.math.Vector3;
import com.blocklynukkit.loader.Comment;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.McFunction;
import com.blocklynukkit.loader.other.particle.CircleFlat;
import com.blocklynukkit.loader.other.particle.FireworkRocket;
import com.blocklynukkit.loader.other.particle.LineFlat;
import com.blocklynukkit.loader.script.bases.BaseManager;

import javax.script.ScriptEngine;
import java.awt.*;

public class ParticleManager extends BaseManager {
    public ParticleManager(ScriptEngine scriptEngine) {
        super(scriptEngine);
    }

    @Override
    public String toString() {
        return "BlocklyNukkit Based Object";
    }
    @Comment(value = "绘制粒子发射器，详见[编程开发文档](https://wiki.blocklynukkit.com/%E7%BC%96%E7%A8%8B%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3/#particle%E5%9F%BA%E5%AF%B9%E8%B1%A1)")
    public void drawEmitter(@Comment(value = "坐标") Position pos,@Comment(value = "发射器id") String id,@Comment(value = "可以看见粒子发射器的玩家") Player toPlayer){
        pos.level.addParticleEffect(pos.asVector3f(),id, -1L,pos.level.getGenerator().getDimension(),toPlayer);
    }
    @Comment(value = "绘制粒子发射器，详见[编程开发文档](https://wiki.blocklynukkit.com/%E7%BC%96%E7%A8%8B%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3/#particle%E5%9F%BA%E5%AF%B9%E8%B1%A1)")
    public void drawEmitter(@Comment(value = "坐标") Position pos,@Comment(value = "发射器id") String id){
        pos.level.addParticleEffect(pos.asVector3f(),id, -1L,pos.level.getGenerator().getDimension(),(Player[])null);
    }
    @Comment(value = "绘制粒子发射器，详见[编程开发文档](https://wiki.blocklynukkit.com/%E7%BC%96%E7%A8%8B%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3/#particle%E5%9F%BA%E5%AF%B9%E8%B1%A1)")
    public void drawEmitter(@Comment(value = "坐标") Position pos){
        ParticleEffect[] random = ParticleEffect.values();
        pos.level.addParticleEffect(pos,random[Loader.mainRandom.nextInt(random.length)-1]);
    }
    @Comment(value = "绘制粒子点")
    public void drawDot(@Comment(value = "坐标") Position pos,@Comment(value = "粒子id") int pid
            ,@Comment(value = "能看到的玩家") Player toPlayer){
        pos.level.addParticle(new GenericParticle(pos,pid, Loader.mainRandom.nextInt()),toPlayer);
    }
    @Comment(value = "绘制粒子点")
    public void drawDot(@Comment(value = "坐标") Position pos,@Comment(value = "粒子id") int pid
            ,@Comment(value = "粒子数据值") int data,@Comment(value = "能看到的玩家") Player toPlayer){
        pos.level.addParticle(new GenericParticle(pos,pid, data),toPlayer);
    }
    @Comment(value = "绘制粒子点")
    public void drawDot(@Comment(value = "坐标") Position pos,@Comment(value = "粒子id") int pid
            ,@Comment(value = "粒子颜色") int r,@Comment(value = "粒子颜色") int g,@Comment(value = "粒子颜色") int b,@Comment(value = "能看到的玩家") Player toPlayer){
        this.drawDot(pos, pid, (0xFF << 24)|((r & 0xFF) << 16)|((g & 0xFF) << 8)|((b & 0xFF)),toPlayer);
    }
    @Comment(value = "绘制粒子点")
    public void drawDot(@Comment(value = "坐标") Position pos,@Comment(value = "粒子id") int pid){
        pos.level.addParticle(new GenericParticle(pos,pid, Loader.mainRandom.nextInt()));
    }
    @Comment(value = "绘制粒子点")
    public void drawDot(@Comment(value = "坐标") Position pos,@Comment(value = "粒子id") int pid
            ,@Comment(value = "粒子数据值") int data){
        pos.level.addParticle(new GenericParticle(pos,pid, data));
    }
    @Comment(value = "绘制粒子点")
    public void drawDot(@Comment(value = "坐标") Position pos,@Comment(value = "粒子id") int pid
            ,@Comment(value = "粒子颜色") int r,@Comment(value = "粒子颜色") int g,@Comment(value = "粒子颜色") int b){
        this.drawDot(pos, pid, (0xFF << 24)|((r & 0xFF) << 16)|((g & 0xFF) << 8)|((b & 0xFF)));
    }
    @Comment(value = "绘制用粒子组成的圆")
    public void drawCircle(@Comment(value = "圆心坐标") Position pos
            ,@Comment(value = "半径") double radius
            ,@Comment(value = "粒子id") int pid
            ,@Comment(value = "每个粒子与其他粒子相隔的距离") double sep){
        new Thread(new CircleFlat(pos, radius, pid, sep)).start();
    }
    @Comment(value = "绘制粒子组成的直线")
    public void drawLine(@Comment(value = "起点坐标") Position pos1
            ,@Comment(value = "终点坐标") Position pos2
            ,@Comment(value = "每个粒子与其他粒子相隔的距离") double sep
            ,@Comment(value = "粒子id") int pid){
        new Thread(new LineFlat(pos1, pos2, sep, pid)).start();
    }
    @Comment(value = "绘制烟花，详见[编程开发文档](https://wiki.blocklynukkit.com/%E7%BC%96%E7%A8%8B%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3/#particle%E5%9F%BA%E5%AF%B9%E8%B1%A1)")
    public void drawFireWork(@Comment(value = "位置") Position pos
            ,@Comment(value = "颜色码") int colornum
            ,@Comment(value = "是否闪烁") boolean flick
            ,@Comment(value = "是否有曳尾") boolean trail
            ,@Comment(value = "形状码") int shape
            ,@Comment(value = "飞行时间") int second){
        FireworkRocket.make(pos.level,pos,colornum,flick,trail,shape,second);
    }
    @Comment(value = "绘制方块被破坏的粒子")
    public void drawBlockBreak(@Comment(value = "坐标") Position pos,@Comment(value = "方块类型，该方块可以不是指定坐标上的方块，也可以根本不存在于世界上") Block block){
        pos.level.addParticle(new DestroyBlockParticle(pos,block));
    }
    @Comment(value = "绘制粒子工厂导出粒子")
    public void drawParticleFactoryMcFunction(String fun,Position pos,double turn){
        new McFunction(fun,pos,pos.level).setturn(turn).run();
    }
}
