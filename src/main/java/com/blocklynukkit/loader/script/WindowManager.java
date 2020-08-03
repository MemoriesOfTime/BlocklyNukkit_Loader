package com.blocklynukkit.loader.script;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerSettingsRespondedEvent;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.utils.DummyBossBar;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;
import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;
import com.blocklynukkit.loader.script.window.Custom;
import com.blocklynukkit.loader.script.window.Modal;
import com.blocklynukkit.loader.script.window.Simple;

import java.util.HashMap;
import java.util.Map;


public class WindowManager {
    public Map<String,Scoreboard> boards = new HashMap<>();
    public Map<String,String> tipsVar = new HashMap<>();

    public void updateAllScoreBoard(String title,String text){
        for (Player p: Server.getInstance().getOnlinePlayers().values()){
            if(title.contains("%"))
                title = PlaceholderAPI.getInstance().translateString(title,p);
            if(text.contains("%"))
                text = PlaceholderAPI.getInstance().translateString(text,p);
            Scoreboard sb = ScoreboardAPI.createScoreboard();
            ScoreboardDisplay sbd = sb.addDisplay(DisplaySlot.SIDEBAR, "dumy", title);
            String[] l = text.split(";");
            for(int i = 0; i < l.length; ++i) {
                sbd.addLine(l[i], i);
            }
            if(boards.containsKey(p.getName())){
                boards.get(p.getName()).hideFor(p);
                sb.showFor(p);
                boards.put(p.getName(),sb);
            }else {
                sb.showFor(p);
                boards.put(p.getName(),sb);
            }
        }
    }
    public void updateOneScoreBoard(String title,String text,Player p){
        if(title.contains("%"))
            title = PlaceholderAPI.getInstance().translateString(title,p);
        if(text.contains("%"))
            text = PlaceholderAPI.getInstance().translateString(text,p);
        Scoreboard sb = ScoreboardAPI.createScoreboard();
        ScoreboardDisplay sbd = sb.addDisplay(DisplaySlot.SIDEBAR, "dumy", title);
        String[] l = text.split(";");

        for(int i = 0; i < l.length; ++i) {
            sbd.addLine(l[i], i);
        }
        if(boards.containsKey(p.getName())){
            boards.get(p.getName()).hideFor(p);
            sb.showFor(p);
            boards.put(p.getName(),sb);
        }else {
            sb.showFor(p);
            boards.put(p.getName(),sb);
        }
    }

    public Simple getSimpleWindowBuilder(String title,String context){
        Simple simple=new Simple();
        simple.setTitle(title);
        simple.setContext(context);
        return simple;
    }
    public Modal getModalWindowBuilder(String title,String context){
        Modal modal=new Modal();
        modal.setContext(context);
        modal.setTitle(title);
        return modal;
    }
    public Custom getCustomWindowBuilder(String title){
        Custom custom=new Custom();
        custom.setTitle(title);
        return custom;
    }
    public String getEventResponseText(PlayerFormRespondedEvent event){
        FormResponseSimple simple=(FormResponseSimple)event.getResponse();
        if(simple!=null){
            return simple.getClickedButton().getText();
        }else {
            return "NULL";
        }
    }
    public String getEventResponseModal(PlayerFormRespondedEvent event){
        FormResponseModal modal=(FormResponseModal)event.getResponse();
        if(modal!=null){
            return modal.getClickedButtonText();
        }else {
            return "NULL";
        }
    }
    public String getEventCustomVar(PlayerFormRespondedEvent event,int id,String mode){
        FormResponseCustom custom=(FormResponseCustom)event.getResponse();
        if(mode.equals("input")){
            return custom.getInputResponse(id);
        }else if(mode.equals("toggle")){
            return custom.getToggleResponse(id)?"TRUE":"FALSE";
        }else if(mode.equals("dropdown")){
            return custom.getDropdownResponse(id).getElementContent();
        }else if(mode.equals("slider")){
            return custom.getSliderResponse(id)+"";
        }else if(mode.equals("stepslider")||mode.equals("step")){
            return custom.getStepSliderResponse(id).getElementContent();
        }else {
            return "NULL";
        }
    }
    public String getEventCustomVar(PlayerSettingsRespondedEvent event, int id, String mode){
        FormResponseCustom custom=(FormResponseCustom)event.getResponse();
        if(mode.equals("input")){
            return custom.getInputResponse(id);
        }else if(mode.equals("toggle")){
            return custom.getToggleResponse(id)?"TRUE":"FALSE";
        }else if(mode.equals("dropdown")){
            return custom.getDropdownResponse(id).getElementContent();
        }else if(mode.equals("slider")){
            return custom.getSliderResponse(id)+"";
        }else if(mode.equals("stepslider")||mode.equals("step")){
            return custom.getStepSliderResponse(id).getElementContent();
        }else {
            return "NULL";
        }
    }
    public void setPlayerBossBar(Player player,String text,float len){
        for(long bar:player.getDummyBossBars().keySet()){
            player.removeBossBar(bar);
        }
        player.createBossBar(new DummyBossBar.Builder(player).text(text).length(len).build());
    }
    public void removePlayerBossBar(Player player){
        for(long bar:player.getDummyBossBars().keySet()){
            player.removeBossBar(bar);
        }
    }
    public double getLengthOfPlayerBossBar(Player player){
        for(long bar:player.getDummyBossBars().keySet()){
            return player.getDummyBossBar(bar).getLength();
        }
        return 0.0d;
    }
    public String getTextOfPlayerBossBar(Player player){
        for(long bar:player.getDummyBossBars().keySet()){
            return player.getDummyBossBar(bar).getText();
        }
        return "NULL";
    }
    //here 6/26
    public void setBelowName(Player player,String str){
        player.setScoreTag(str);
    }

    public void makeTipsVar(String varname,String provider){
        tipsVar.put(varname,"function->"+provider);
    }

    public void makeTipsStatic(String varname,String toReplace){
        tipsVar.put(varname, toReplace);
    }
}
