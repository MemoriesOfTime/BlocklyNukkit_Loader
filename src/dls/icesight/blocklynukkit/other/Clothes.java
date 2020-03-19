package dls.icesight.blocklynukkit.other;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.utils.Config;
import dls.icesight.blocklynukkit.Loader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

public class Clothes {
    public String skinname;
    public String playername;
    public Player player;
    public Skin skin;
    public String gen = null;
    public Clothes(String skid,String p){
        skinname = skid;playername = p;
        player = Server.getInstance().getPlayer(playername);
        skin = Server.getInstance().getPlayer(playername).getSkin();
    }
    public Skin build(){
        skin.setSkinId(skinname);
        try{
            File fileskin = new File(Loader.plugin.getDataFolder()+"/skin/"+skinname+".png");
            if(fileskin.exists()){
                if(Loader.skinimagemap.get(skinname)!=null){
                    skin.setSkinData(Loader.skinimagemap.get(skinname));
                }else {
                    BufferedImage bi = (BufferedImage) ImageIO.read(fileskin);
                    skin.setSkinData(bi);
                    Loader.skinimagemap.put(skinname,bi);
                }
                File filegeo = new File(Loader.plugin.getDataFolder()+"/skin/"+skinname+".json");
                if(Loader.playergeonamemap.get(skinname)!=null){
                    skin.setGeometryName(Loader.playergeonamemap.get(skinname));
                    gen = Loader.playergeonamemap.get(skinname);
                }else {
                    if(filegeo.exists()){
                        Map<String, Object> skinJson = (new Config(Loader.plugin.getDataFolder()+"/skin/"+skinname+".json", Config.JSON)).getAll();
                        String geometryName = null;
                        for (Map.Entry<String, Object> entry1: skinJson.entrySet()){
                            if(geometryName == null){
                                geometryName = entry1.getKey();
                                gen = geometryName;
                            }
                        }
                        Loader.playergeonamemap.put(skinname,geometryName);
                        skin.setGeometryName(geometryName);
                    }
                }
                if(Loader.playergeojsonmap.get(skinname)!=null){
                    skin.setGeometryData(Loader.playergeojsonmap.get(skinname));
                }else {
                    if(filegeo.exists()){
                        BufferedReader reader = new BufferedReader(new FileReader(filegeo));
                        String geotext = "";
                        for (String line=reader.readLine();line!=null;line=reader.readLine()){
                            geotext+=line;
                        }
                        reader.close();
                        Loader.playergeojsonmap.put(skinname,geotext);
                        skin.setGeometryData(geotext);
                        Loader.getlogger().info("build_in_4D");
                    }
                }
                skin.generateSkinId(skinname);
                Loader.getlogger().info("build_"+skinname+"_skin_for:"+playername);
                return skin;
            }else {
                Loader.getlogger().warning("没有该皮肤！");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return skin;
    }
}
