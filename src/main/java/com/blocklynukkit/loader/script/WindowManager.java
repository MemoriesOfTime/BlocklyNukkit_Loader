package com.blocklynukkit.loader.script;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerSettingsRespondedEvent;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.network.protocol.ShowCreditsPacket;
import cn.nukkit.network.protocol.ShowProfilePacket;
import cn.nukkit.utils.DummyBossBar;
import com.blocklynukkit.loader.api.CallbackFunction;
import com.blocklynukkit.loader.api.Comment;
import com.blocklynukkit.loader.script.bases.BaseManager;
import com.blocklynukkit.loader.utils.Utils;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;
import com.blocklynukkit.loader.script.window.Custom;
import com.blocklynukkit.loader.script.window.Modal;
import com.blocklynukkit.loader.script.window.Simple;

import javax.script.ScriptEngine;
import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.multi.MultiLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.UUID;

import static com.blocklynukkit.loader.Loader.boards;
import static com.blocklynukkit.loader.Loader.tipsVar;

public final class WindowManager extends BaseManager {
    public WindowManager(ScriptEngine scriptEngine) {
        super(scriptEngine);
    }

    @Override
    public String toString() {
        return "BlocklyNukkit Based Object";
    }
    @Comment(value = "更新所有玩家的计分板内容")
    public void updateAllScoreBoard(@Comment(value = "计分板标题") String title
            ,@Comment(value = "计分板内容，以;分割多行") String text){
        for (Player p: Server.getInstance().getOnlinePlayers().values()){
            if(title.contains("%"))
                title = PlaceholderAPI.getInstance().translateString(title,p);
            if(text.contains("%"))
                text = PlaceholderAPI.getInstance().translateString(text,p);
            Scoreboard sb = ScoreboardAPI.createScoreboard();
            ScoreboardDisplay sbd = sb.addDisplay(DisplaySlot.SIDEBAR, "dumy", title);
            String[] l = text.split("(?<!\\\\);");
            for(int i = 0; i < l.length; ++i) {
                sbd.addLine(l[i].replaceAll("\\\\;",";"), i);
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
    @Comment(value = "更新单个玩家的计分板内容")
    public void updateOneScoreBoard(@Comment(value = "计分板标题") String title,@Comment(value = "计分板内容，以;分割多行") String text,@Comment(value = "要更新计分板内容的玩家") Player p){
        if(title.contains("%"))
            title = PlaceholderAPI.getInstance().translateString(title,p);
        if(text.contains("%"))
            text = PlaceholderAPI.getInstance().translateString(text,p);
        Scoreboard sb = ScoreboardAPI.createScoreboard();
        ScoreboardDisplay sbd = sb.addDisplay(DisplaySlot.SIDEBAR, "dumy", title);
        String[] l = text.split("(?<!\\\\);");

        for(int i = 0; i < l.length; ++i) {
            sbd.addLine(l[i].replaceAll("\\\\;",";"), i);
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
    @Comment(value = "获取新的简单窗口构建器")
    public Simple getSimpleWindowBuilder(@Comment(value = "标题") String title,@Comment(value = "文字") String context){
        Simple simple=new Simple();
        simple.setTitle(title);
        simple.setContext(context);
        return simple;
    }
    @Comment(value = "获取新的对话框构建器")
    public Modal getModalWindowBuilder(@Comment(value = "标题") String title,@Comment(value = "文字") String context){
        Modal modal=new Modal();
        modal.setContext(context);
        modal.setTitle(title);
        return modal;
    }
    @Comment(value = "获取新的自定义窗口构建器")
    public Custom getCustomWindowBuilder(@Comment(value = "标题") String title){
        Custom custom=new Custom();
        custom.setTitle(title);
        return custom;
    }
    @Comment(value = "从玩家操作窗口事件获取简单窗口被点击按钮的文字")
    public String getEventResponseText(@Comment(value = "玩家操作窗口事件") PlayerFormRespondedEvent event){
        if(event==null){
            return "NULL";
        }else if(event.wasClosed()){
            return "NULL";
        }
        FormResponseSimple simple=(FormResponseSimple)event.getResponse();
        if(simple!=null){
            return simple.getClickedButton().getText();
        }else {
            return "NULL";
        }
    }
    @Comment(value = "从玩家操作窗口事件获取对话框被点击按钮的文字")
    public String getEventResponseModal(@Comment(value = "玩家操作窗口事件") PlayerFormRespondedEvent event){
        if(event==null){
            return "NULL";
        }else if(event.wasClosed()){
            return "NULL";
        }
        FormResponseModal modal=(FormResponseModal)event.getResponse();
        if(modal!=null){
            return modal.getClickedButtonText();
        }else {
            return "NULL";
        }
    }
    @Comment(value = "从玩家操作窗口事件获取对话框被点击按钮的索引")
    public int getEventResponseIndex(@Comment(value = "玩家操作窗口事件") PlayerFormRespondedEvent event){
        if(event==null||event.wasClosed()){
            return -2147483648;
        }
        if(event.getWindow() instanceof FormWindowSimple){
            return ((FormResponseSimple)event.getResponse()).getClickedButtonId();
        }else if(event.getWindow() instanceof FormWindowModal){
            return ((FormResponseModal)event.getResponse()).getClickedButtonId();
        }else {
            return -2147483648;
        }
    }
    @Comment(value = "从玩家操作窗口事件获取高级窗口被操作元素的内容")
    public String getEventCustomVar(@Comment(value = "玩家操作窗口事件") PlayerFormRespondedEvent event
            ,@Comment(value = "元素索引") int id
            ,@Comment(value = "元素种类，可为input/toggle/dropdown/slider/stepslider") String mode){
        if(event==null){
            return "NULL";
        }else if(event.wasClosed()){
            return "NULL";
        }
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
    @Comment(value = "从玩家操作窗口事件获取高级窗口被操作元素的内容")
    public String getEventCustomVar(@Comment(value = "玩家操作服务器设置事件") PlayerSettingsRespondedEvent event
            ,@Comment(value = "元素索引") int id
            ,@Comment(value = "元素种类，可为input/toggle/dropdown/slider/stepslider") String mode){
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
    @Comment(value = "设置玩家的boss血条，返回此次设置的所有血条的id")
    public long[] setPlayerBossBar(@Comment(value = "要设置玩家血条的id") Player player
            ,@Comment(value = "血量内容，以;分割多个血条") String text
            ,@Comment(value = "血条长度0~1") float len){
        for(long bar:player.getDummyBossBars().keySet()){
            player.removeBossBar(bar);
        }
        String[] bossbars = text.split("(?<!\\\\);");
        long[] ids = new long[bossbars.length];
        for(int i=0;i<bossbars.length;i++){
            String each = bossbars[i].replaceAll("\\\\;",";");
            if(each.startsWith("#")){
                String hex = each.substring(0,7);
                Color color = Utils.hex2rgb(hex);
                ids[i]=player.createBossBar(new DummyBossBar.Builder(player).text(each.replaceFirst(hex,"")).color(color.getRed(),color.getGreen(),color.getBlue()).length(len).build());
            }else if (text.startsWith("rgb(")){
                String[] rgb = each.split("\\)",2)[0].replaceFirst("rgb\\(","").split(",");
                ids[i]=player.createBossBar(new DummyBossBar.Builder(player).text(each.split("\\)",2)[1]).length(len)
                        .color(Integer.parseInt(rgb[0]),Integer.parseInt(rgb[1]),Integer.parseInt(rgb[2])).build());
            }else {
                ids[i]=player.createBossBar(new DummyBossBar.Builder(player).text(each).length(len).build());
            }
        }
        return ids;
    }
    @Comment(value = "清除玩家的所有boss血条")
    public void removePlayerBossBar(@Comment(value = "玩家对象") Player player){
        for(long bar:player.getDummyBossBars().keySet()){
            player.removeBossBar(bar);
        }
    }
    @Comment(value = "根据id清除玩家的指定boss血条")
    public void removePlayerBossBar(@Comment(value = "玩家对象") Player player,@Comment(value = "血条id") long id){
        player.removeBossBar(id);
    }
    @Comment(value = "获取玩家屏幕最上方boss血条的长度")
    public double getLengthOfPlayerBossBar(@Comment(value = "玩家对象") Player player){
        for(long bar:player.getDummyBossBars().keySet()){
            return player.getDummyBossBar(bar).getLength();
        }
        return -1.0d;
    }
    @Comment(value = "获取玩家指定id的boss血条的长度")
    public double getLengthOfPlayerBossBar(@Comment(value = "玩家对象") Player player,@Comment(value = "血条id") long id){
        DummyBossBar bar = player.getDummyBossBar(id);
        if(bar==null)return -1.0d;else return bar.getLength();
    }
    @Comment(value = "获取玩家屏幕最上方boss血条的文字")
    public String getTextOfPlayerBossBar(@Comment(value = "玩家对象") Player player){
        for(long bar:player.getDummyBossBars().keySet()){
            return player.getDummyBossBar(bar).getText();
        }
        return "NULL";
    }
    @Comment(value = "获取玩家指定id的boss血条的长度")
    public String getTextOfPlayerBossBar(@Comment(value = "玩家对象") Player player,@Comment(value = "血条id") long id){
        DummyBossBar bar = player.getDummyBossBar(id);
        if(bar==null)return "NULL";else return bar.getText();
    }
    //here 6/26
    @Comment(value = "设置玩家名下计分板内容")
    public void setBelowName(@Comment(value = "玩家") Player player,@Comment(value = "内容") String str){
        player.setScoreTag(str);
    }
    @Comment(value = "设置tips的动态变量")
    public void makeTipsVar(@Comment(value = "变量名") String varname,@Comment(value = "回调函数，参数(cn.nukkit.Player要求提供变量的玩家)，返回值将用作变量的替换内容")
            @CallbackFunction(classes = "cn.nukkit.Player", parameters = "player", comments = "要将变量显示给的玩家") String provider){
        tipsVar.put(varname,"function->"+provider);
    }
    @Comment(value = "设置tips的静态变量")
    public void makeTipsStatic(@Comment(value = "变量名") String varname,@Comment(value = "替换内容") String toReplace){
        tipsVar.put(varname, toReplace);
    }
    //here 8/7
    @Comment(value = "强制清除玩家的所有打开的物品栏和对话框窗口")
    public void forceClearWindow(@Comment(value = "玩家对象") Player player){
        player.removeAllWindows();
    }
    //here 8/18
    @Comment(value = "设置玩家暂停屏幕看到的玩家列表的玩家")
    public void setPauseScreenList(@Comment(value = "玩家名，以;分割") String list){
        Map p = Server.getInstance().getOnlinePlayers();
        for(String each:list.split("(?<!\\\\);")){
            each = each.replaceAll("\\\\;",";");
            if(p.keySet().contains(each)){
                Player player = Server.getInstance().getPlayer(each);
                Server.getInstance().updatePlayerListData(UUID.randomUUID(), Entity.entityCount++,each,player.getSkin(),player.getLoginChainData().getXUID());
            }else {
                Server.getInstance().updatePlayerListData(UUID.randomUUID(), Entity.entityCount++,each,new Skin());
            }
        }
    }
    //here 11/21
    @Comment(value = "向玩家发送指定玩家的xbox信息对话框")
    public void sendPlayerXboxInfo(@Comment(value = "内容来源") Player from,@Comment(value = "被发送的玩家") Player to){
        ShowProfilePacket profilePacket = new ShowProfilePacket();
        profilePacket.xuid = from.getLoginChainData().getXUID();
        to.dataPacket(profilePacket);
    }
    @Comment(value = "让指定玩家的屏幕上开始播放终末之诗")
    public void startEndPoem(@Comment(value = "玩家对象") Player player){
        ShowCreditsPacket creditsPacket = new ShowCreditsPacket();
        creditsPacket.eid = player.getId();
        creditsPacket.status = ShowCreditsPacket.STATUS_START_CREDITS;
        player.dataPacket(creditsPacket);
    }
    @Comment(value = "设置swing GUI框架的主题")
    public void setSwingStyle(@Comment(value = "主题，可以为Darcula/Intellij/Metal/Motif/Multi/Nimbus/OS") String style){
        switch (style){
            case "Darcula":
                try {
                    UIManager.setLookAndFeel(new FlatDarculaLaf());
                } catch( Exception ex ) {
                    System.err.println("Failed to initialize Darcula");
                }
                break;
            case "Intellij":
                try {
                    UIManager.setLookAndFeel(new FlatIntelliJLaf());
                } catch( Exception ex ) {
                    System.err.println( "Failed to initialize IntelliJ" );
                }
                break;
            case "Metal":
                try {
                    UIManager.setLookAndFeel(new MetalLookAndFeel());
                } catch( Exception ex ) {
                    System.err.println( "Failed to initialize Metal" );
                }
                break;
            case "Motif":
                try {
                    UIManager.setLookAndFeel(new MotifLookAndFeel());
                } catch( Exception ex ) {
                    System.err.println( "Failed to initialize Motif" );
                }
                break;
            case "Multi":
                try {
                    UIManager.setLookAndFeel(new MultiLookAndFeel());
                } catch( Exception ex ) {
                    System.err.println( "Failed to initialize Multi" );
                }
                break;
            case "Nimbus":
                try {
                    UIManager.setLookAndFeel(new NimbusLookAndFeel());
                } catch( Exception ex ) {
                    System.err.println( "Failed to initialize Multi" );
                }
                break;
            case "OS":
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch( Exception ex ) {
                    System.err.println( "Failed to initialize Multi" );
                }
                break;
        }
    }
    @Comment(value = "获取设置了主题的swing窗口")
    public JFrame getStyledSwingWindow(@Comment(value = "窗口标题") String title,@Comment(value = "宽(像素)") int width,@Comment(value = "高(像素)") int height,@Comment(value = "图标图片路径，png/jpg格式") String iconPath){
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame jf = new JFrame(title);
        jf.setMinimumSize(new Dimension(width,height));
        jf.setSize(new Dimension(width, height));
        
        if(iconPath == null || iconPath.equals("")){
            jf.setIconImage(Toolkit.getDefaultToolkit().getImage(com.blocklynukkit.loader.Loader.class.getResource("/BlocklyNukkit.png")));
            return jf;
        }
        try {
            jf.setIconImage(Toolkit.getDefaultToolkit().getImage(new File(iconPath).toURI().toURL()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return jf;
    }
}
