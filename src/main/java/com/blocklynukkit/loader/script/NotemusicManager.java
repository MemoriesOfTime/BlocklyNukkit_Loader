package com.blocklynukkit.loader.script;

import cn.nukkit.Player;
import com.blocklynukkit.loader.Loader;
import com.xxmicloxx.NoteBlockAPI.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.NoteBlockPlayerMain;
import com.xxmicloxx.NoteBlockAPI.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.Song;

import java.io.File;
import java.util.List;

public class NotemusicManager {
    public Song getSongFromFile(String name){
        return NBSDecoder.parse(new File(Loader.plugin.getDataFolder()+"/notemusic/"+name));
    }
    public String getSongTitle(Song song){
        return song.getTitle();
    }
    public String getSongDescription(Song song){
        return song.getDescription();
    }
    public String getSongAuthor(Song song){
        return song.getAuthor();
    }
    public RadioSongPlayer buildRadio(Song song,boolean isloop,boolean isautodestroy){
        RadioSongPlayer radioSongPlayer = new RadioSongPlayer(song);
        radioSongPlayer.setLoop(isloop);
        radioSongPlayer.setAutoDestroy(isautodestroy);
        return radioSongPlayer;
    }
    public void addPlayerToRadio(RadioSongPlayer radioSongPlayer, Player player){
        radioSongPlayer.addPlayer(player);
        NoteBlockPlayerMain.getPlayerVolume(player);
    }
    public void removePlayerToRadio(RadioSongPlayer radioSongPlayer, Player player){
        radioSongPlayer.removePlayer(player);
    }
    public List getPlayerInRadio(RadioSongPlayer radioSongPlayer){
        return radioSongPlayer.getPlayerList();
    }
    public void setRadioStatus(RadioSongPlayer radioSongPlayer,boolean isplaying){
        radioSongPlayer.setPlaying(isplaying);
    }
    public Song getSongInRadio(RadioSongPlayer radioSongPlayer){
        return radioSongPlayer.getSong();
    }
    public int getSongLength(Song song){
        return song.getLength();
    }
}
