package com.blocklynukkit.loader.script;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import com.blocklynukkit.loader.api.Comment;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.script.bases.BaseManager;
import com.xxmicloxx.NoteBlockAPI.*;

import javax.script.ScriptEngine;
import java.io.File;
import java.util.List;

public final class NotemusicManager extends BaseManager {
    public NotemusicManager(ScriptEngine scriptEngine) {
        super(scriptEngine);
    }

    @Override
    public String toString() {
        return "BlocklyNukkit Based Object";
    }
    @Comment(value = "从文件获取红石音乐歌曲对象")
    public Song getSongFromFile(@Comment(value = "文件名(在./plugins/BlocklyNukkit/notemusic文件夹下)") String name){
        return NBSDecoder.parse(new File(Loader.plugin.getDataFolder()+"/notemusic/"+name));
    }
    @Comment(value = "获取红石音乐歌曲对象的标题")
    public String getSongTitle(@Comment(value = "红石音乐歌曲对象") Song song){
        return song.getTitle();
    }
    @Comment(value = "获取红石音乐歌曲对象的描述")
    public String getSongDescription(@Comment(value = "红石音乐歌曲对象") Song song){
        return song.getDescription();
    }
    @Comment(value = "获取红石音乐歌曲对象的作者")
    public String getSongAuthor(@Comment(value = "红石音乐歌曲对象") Song song){
        return song.getAuthor();
    }
    @Comment(value = "构建红石音乐电台，电台中的所有玩家都将听到")
    public RadioSongPlayer buildRadio(@Comment(value = "红石音乐歌曲对象") Song song
            ,@Comment(value = "是否循环播放") boolean isloop
            ,@Comment(value = "是否播放完自动销毁电台") boolean isautodestroy){
        RadioSongPlayer radioSongPlayer = new RadioSongPlayer(song);
        radioSongPlayer.setLoop(isloop);
        radioSongPlayer.setAutoDestroy(isautodestroy);
        return radioSongPlayer;
    }
    @Comment(value = "将玩家添加到电台中")
    public void addPlayerToRadio(@Comment(value = "红石音乐电台对象") RadioSongPlayer radioSongPlayer
            ,@Comment(value = "玩家对象") Player player){
        radioSongPlayer.addPlayer(player);
        NoteBlockPlayerMain.getPlayerVolume(player);
    }
    @Comment(value = "将玩家从电台中移除")
    public void removePlayerToRadio(@Comment(value = "红石音乐电台对象") RadioSongPlayer radioSongPlayer
            ,@Comment(value = "玩家对象") Player player){
        radioSongPlayer.removePlayer(player);
    }
    @Comment(value = "获取电台中的所有玩家的玩家名")
    public List getPlayerInRadio(@Comment(value = "红石音乐电台对象") RadioSongPlayer radioSongPlayer){
        return radioSongPlayer.getPlayerList();
    }
    @Comment(value = "设置电台播放状态")
    public void setRadioStatus(@Comment(value = "红石音乐电台对象") RadioSongPlayer radioSongPlayer
            ,@Comment(value = "true:播放，false:暂停") boolean isplaying){
        radioSongPlayer.setPlaying(isplaying);
    }
    @Comment(value = "获取电台正在播放的红石音乐歌曲对象")
    public Song getSongInRadio(@Comment(value = "红石音乐电台对象") RadioSongPlayer radioSongPlayer){
        return radioSongPlayer.getSong();
    }
    @Comment(value = "获取红石音乐歌曲的长度")
    public int getSongLength(@Comment(value = "红石音乐歌曲对象") Song song){
        return song.getLength();
    }
    @Comment(value = "构建红石音乐喇叭，在喇叭中的玩家都能听到，而且音量随距离衰减")
    public HornSongPlayer buildHorn(@Comment(value = "红石音乐歌曲对象") Song song
            ,@Comment(value = "喇叭坐标") Position pos
            ,@Comment(value = "是否循环播放") boolean isloop
            ,@Comment(value = "播放完毕后是否自动销毁喇叭") boolean isautodestroy){
        HornSongPlayer hornSongPlayer = new HornSongPlayer(song);
        hornSongPlayer.setTargetLocation(Location.fromObject(pos,pos.level));
        hornSongPlayer.setLoop(isloop);
        hornSongPlayer.setAutoDestroy(isautodestroy);
        return hornSongPlayer;
    }
    @Comment(value = "将玩家添加到红石音乐喇叭中")
    public void addPlayerToHorn(@Comment(value = "红石音乐喇叭对象") HornSongPlayer SongPlayer
            ,@Comment(value = "玩家对象") Player player){
        SongPlayer.addPlayer(player);
        NoteBlockPlayerMain.getPlayerVolume(player);
    }
    @Comment(value = "将玩家从红石音乐喇叭移除")
    public void removePlayerToHorn(@Comment(value = "红石音乐喇叭对象") HornSongPlayer SongPlayer
            ,@Comment(value = "玩家对象") Player player){
        SongPlayer.removePlayer(player);
    }
    @Comment(value = "获取红石音乐喇叭中的所有玩家的玩家名")
    public List getPlayerInHorn(@Comment(value = "红石音乐喇叭对象") HornSongPlayer radioSongPlayer){
        return radioSongPlayer.getPlayerList();
    }
    @Comment(value = "设置红石音乐喇叭的播放状态")
    public void setHornStatus(@Comment(value = "红石音乐喇叭对象") HornSongPlayer radioSongPlayer
            ,@Comment(value = "true:播放，false:暂停") boolean isplaying){
        radioSongPlayer.setPlaying(isplaying);
    }
    @Comment(value = "获取红石音乐喇叭播放的红石音乐歌曲")
    public Song getSongInHorn(@Comment(value = "红石音乐喇叭对象") HornSongPlayer radioSongPlayer){
        return radioSongPlayer.getSong();
    }
}
