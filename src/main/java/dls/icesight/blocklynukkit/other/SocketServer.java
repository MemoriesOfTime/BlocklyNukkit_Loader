package dls.icesight.blocklynukkit.other;
import cn.nukkit.Server;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;
import dls.icesight.blocklynukkit.Loader;
import dls.icesight.blocklynukkit.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;

public class SocketServer implements Runnable {
    ServerSocket ss;

    //构造方法
    public SocketServer(ServerSocket ss) {
        this.ss = ss;
    }

    //线程
    public void run() {
        //输入输出流
        while (true){
            OutputStream os = null;
            InputStream in = null;
            try {
                //打开socket输入流，并转为BufferedReader流
                Socket s=ss.accept();
                in = s.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                //接收第一行，得到请求路径
                String requestHeader;
                requestHeader=br.readLine();
                int begin = requestHeader.indexOf("/")+1;
                int end = requestHeader.indexOf("HTTP/");
                String road = requestHeader.substring(begin, end);
                String url = Loader.plugin.getDataFolder()+ "/index.html";

                //打开socket对象的输出流，写入响应头
                os = s.getOutputStream();
                os.write("HTTP/1.1 200 OK\r\n".getBytes());
                os.write("Content-Type:text/html;charset=utf-8\r\n".getBytes());
                os.write("\r\n".getBytes());

                //空一行，写入响应内容。
                File f = new File(url);
                if (f.exists())    //判断请求的文件是否存在
                {
                    String html = Utils.readToString(new File(url));
                    if(html.length()<=3)html="<p>404_error</p>";
                    html = PlaceholderAPI.getInstance().translateString(html);
                    html = html.replaceAll("%request_path%",road);
                    html = html.replaceAll("%random_developer%", Utils.randomDeveloper());
                    for (Map.Entry<String,Object> entry:Loader.easytmpmap.entrySet()){
                        if(html.contains(entry.getKey())){
                            html.replaceAll(entry.getKey(),(String)entry.getValue());
                        }
                    }
                    html = html.replaceAll("##","%");
                    os.write(html.getBytes());
                }else {
                    os.write(("<h1>哦！网页被"+Utils.randomDeveloper()+"偷走了！</h1>").getBytes());
                }
                os.flush();
                //如果os流没有关闭的话，浏览器会以为内容还没传输完成，将一直显示不了内容
                os.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }

    }
}