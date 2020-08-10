package com.blocklynukkit.loader.script;

import cn.nukkit.Player;
import com.blocklynukkit.gameAPI.API.BNGame;
import com.blocklynukkit.gameAPI.GameBase;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class GameManager {
    public void createGame(String name,boolean useTeam,String startGameCallBack,String endGameCallBack,String mainLoopCallBack,String deathCallBack){
        try {
            BNGame.newGame(name, useTeam, startGameCallBack, endGameCallBack, mainLoopCallBack, deathCallBack);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public void joinGame(Player player, String gameName){
        BNGame.joinGame(player, gameName);
    }
    public void leaveGame(Player player){
        BNGame.leaveGame(player);
    }
    public boolean isPlayerInGame(Player player){
        return BNGame.isPlayerInGame(player);
    }
    public GameBase getPlayerRoom(Player player){
        return BNGame.getPlayerRoom(player);
    }
    public List<GameBase> getAllRoomByName(String gameName){
        return BNGame.getAllRoomByName(gameName);
    }
    public List<String> getAllGameNames(){
        return BNGame.getAllGameNames();
    }
}
