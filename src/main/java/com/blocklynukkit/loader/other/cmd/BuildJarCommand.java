package com.blocklynukkit.loader.other.cmd;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.utils.Utils;

import java.io.*;

public class BuildJarCommand extends Command {
    public BuildJarCommand() {
        super("buildtojar","构建js插件为jar包","buildtojar jsfilename jarname version author");
    }
    @Override
    public boolean execute(CommandSender sender, String s, String[] args){
        try{
            if(!sender.isPlayer()){
                if(args.length!=4)return false;
                String folder = Loader.plugin.getDataFolder()+"/";
                String jsfilename = args[0];
                String jarname = args[1];
                String version = args[2];
                String author = args[3];
                File comf = new File(folder+"compileout");
                File javasrc = new File(folder+jsfilename+".java");
                File ymlsrc = new File(folder+"compileout/plugin.yml");
                comf.mkdir();
                if(!javasrc.exists())javasrc.createNewFile();
                if(!ymlsrc.exists())ymlsrc.createNewFile();
                FileOutputStream stream = new FileOutputStream((javasrc));
                stream.write(getPluginJAVA(jsfilename, Utils.readToString(new File(folder+jsfilename+".js")).replaceAll("\\\"","\\\\\"").replaceAll("\\n","\\\\n")).getBytes());
                stream.close();
                FileOutputStream stream2 = new FileOutputStream(ymlsrc);
                stream2.write(getPluginYML(jsfilename,version,author).getBytes());
                stream2.close();
                Process p = Runtime.getRuntime().exec("javac -cp "+folder.replaceAll("plugins/BlocklyNukkit/",jarname)+(Utils.isWindows()?";":":")+Server.getInstance().getPluginPath()+"/BlocklyNukkit.jar"+
                        " -encoding utf-8 -d "+
                        Loader.plugin.getDataFolder()+"/compileout"+" "+folder+jsfilename+".java");
                p.waitFor();
                readProcessOutput(p);
                p.destroy();

                String[] arr = new String[]{"/bin/sh", "-c","cd "+folder+"compileout \n jar cvf "+jsfilename+".jar ./"};
                Process p2 = Runtime.getRuntime().exec(arr);
                p2.waitFor();
                readProcessOutput(p2);
                p2.destroy();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    private String getPluginYML(String pluginname,String version,String author){
        String tmp = "name: "+pluginname+"\n" +
                "main: js.blocklynukkit."+pluginname+"\n" +
                "version: \""+version+"\"\n" +
                "author: "+author+"\n" +
                "api: [\"1.0.8\"]\n" +
                "description: "+pluginname+"_build_by_blocklynukkit\n" +
                "load: POSTWORLD\n" +
                "depend:\n" +
                "  - BlocklyNukkit\n";
        return tmp;
    }
    private String getPluginJAVA(String pluginname,String js){
        String tmp = "package js.blocklynukkit;\n" +
                "\n" +
                "import cn.nukkit.plugin.PluginBase;\n" +
                "import Loader;\n" +
                "\n" +
                "import javax.script.ScriptException;\n" +
                "\n" +
                "public class "+pluginname+" extends PluginBase {\n" +
                "    @Override\n" +
                "    public void onEnable(){\n" +
                "        try {\n" +
                "            Loader.engine.eval(\""+js+"\");\n" +
                "            this.getLogger().info(\"成功加载！\");\n" +
                "        } catch (ScriptException e) {\n" +
                "            e.printStackTrace();\n" +
                "        }\n" +
                "    }\n" +
                "}\n";
        return tmp;
    }
    private static void readProcessOutput(final Process process) {
        // 将进程的正常输出在 System.out 中打印，进程的错误输出在 System.err 中打印
        read(process.getInputStream(), System.out);
        read(process.getErrorStream(), System.err);
    }

    // 读取输入流
    private static void read(InputStream inputStream, PrintStream out) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
