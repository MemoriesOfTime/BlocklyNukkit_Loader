package com.blocklynukkit.loader.other;
import cn.nukkit.utils.Config;
import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.Utils;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SocketServer implements Runnable {
    public int port;

    //构造方法
    public SocketServer(int p) {
        this.port=p;
    }

    //线程
    public void run() {
        //输入输出流
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Config config=new Config("./serverSocketPort");
                config.set("prePort",port);
            }
        },0,30000);//防挂
        while (true){
            OutputStream os = null;
            InputStream in = null;
            ServerSocket ss=null;
            Socket s=null;
            try {
                //打开socket输入流，并转为BufferedReader流
                ss=new ServerSocket(port);
                Loader.getlogger().info("a");
                s=ss.accept();
                Loader.getlogger().info("b");
                in = s.getInputStream();
                Loader.getlogger().info("c");
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                Loader.getlogger().info("d");
                //接收第一行，得到请求路径
                String requestHeader;
                Loader.getlogger().info("e");
                if(br == null){
                    os.write(("<h1>哦！网页被"+ Utils.randomDeveloper()+"偷走了！</h1>").getBytes());
                    os.flush();
                    s.close();
                    ss.close();
                    continue;
                }
                requestHeader=br.readLine();
                Loader.getlogger().info("f");
                int begin = requestHeader.indexOf("/")+1;
                int end = requestHeader.indexOf("HTTP/");
                Loader.getlogger().info("--f");
                String road = requestHeader.substring(begin, end);
                String url = Loader.plugin.getDataFolder()+ "/index.html";
                Loader.getlogger().info("g");
                //打开socket对象的输出流，写入响应头
                os = s.getOutputStream();
                os.write("HTTP/1.1 200 OK\r\n".getBytes());
                os.write("Content-Type:text/html;charset=utf-8\r\n".getBytes());
                os.write("\r\n".getBytes());
                Loader.getlogger().info("h");
                //空一行，写入响应内容。
                File f = new File(url);
                if (f.exists())    //判断请求的文件是否存在
                {
                    String html = Utils.readToString(new File(url));
                    if(html.length()<=3)html="<p>404_error</p>";

                    for (Map.Entry<String,String> entry:Loader.htmlholdermap.entrySet()){
                        html = html.replaceAll(entry.getKey(),entry.getValue());
                    }

                    html = PlaceholderAPI.getInstance().translateString(html);
                    html = html.replaceAll("%request_path%",road);
                    html = html.replaceAll("%random_developer%", Utils.randomDeveloper());

                    html = html.replaceAll("##","%");
                    os.write(html.getBytes());
                }else {
                    os.write(("<h1>哦！网页被"+Utils.randomDeveloper()+"偷走了！</h1>").getBytes());
                }
                os.flush();
                //如果os流没有关闭的话，浏览器会以为内容还没传输完成，将一直显示不了内容
                os.close();
                s.close();
                ss.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
                try {
                    os.write(("<h1>哦！网页被"+Utils.randomDeveloper()+"偷走了！</h1>").getBytes());
                    os.flush();
                    s.close();
                    continue;
                }catch (Exception e2){
                    e2.printStackTrace();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}