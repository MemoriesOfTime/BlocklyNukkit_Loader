package dls.icesight.blocklynukkit;

import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.sun.net.httpserver.HttpServer;
import dls.icesight.blocklynukkit.other.MyHttpHandler;
import dls.icesight.blocklynukkit.other.SocketServer;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Utils {
    public static void makeHttpServer(int port){
        HttpServer httpServer = null;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //创建一个HttpContext，将路径为/myserver请求映射到MyHttpHandler处理器
        httpServer.createContext("/", new MyHttpHandler());

        //设置服务器的线程池对象
        httpServer.setExecutor(Executors.newFixedThreadPool(10));

        //启动服务器
        httpServer.start();
    }
    public String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
            return new String(filecontent, encoding);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String randomDeveloper(){
        String[] list = new String[]{"冰凉","电池酱","企鹅","红楼君","夏亚","亦染","WetABQ","HBJ","你的旺财","若水","神奇的YYT"
        ,"pqguanfang","泥土怪","P(屁)爷"};
        return list[(int)Math.floor(list.length*Math.random())];
    }
    public static void checkupdate(){
        try {
            File jar = new File(Loader.plugin.getDataFolder()+"/BlocklyNukkit.jar");
            Utils.downLoadFromUrl("https://blocklynukkitxml-1259395953.cos.ap-beijing.myqcloud.com/jar/BlocklyNukkit.jar","BlocklyNukkit.jar",Loader.plugin.getDataFolder().getPath());
            File pl = new File(Server.getInstance().getPluginPath()+"/BlocklyNukkit.jar");
            if(!check(jar,pl)){
                Loader.getlogger().warning(TextFormat.YELLOW+"您的BlocklyNukkit解释器不是最新版！");
                Loader.getlogger().warning(TextFormat.WHITE+"最新版BlocklyNukkit已经下载到了 "+jar.getPath());
                Loader.getlogger().warning(TextFormat.WHITE+"请您手动更换掉plugin文件夹的旧版本！");
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public static void download(String downloadUrl, File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            URL url = new URL(downloadUrl);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            int length = 0;
            byte[] bytes = new byte[1024];
            while ((length = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, length);
            }
            fileOutputStream.close();
            inputStream.close();
            if(isWindows()){
                Loader.getlogger().info(TextFormat.YELLOW+"正在为windows转码... "+TextFormat.GREEN+"作者对微软的嘲讽：(sb Windows,都老老实实用utf编码会死吗？)");
            }
        } catch (IOException e) {
            Loader.getlogger().error("download error ! url :{"+downloadUrl+"}, exception:{"+e+"}");
        }
        Loader.getlogger().info(TextFormat.GREEN+"成功同步："+file.getName());
    }
    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
    }
    public static String readToString(File file) {
        String encoding = "UTF-8";

        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("操作系统不支持 " + encoding);
            e.printStackTrace();
            return null;
        }
    }
    public static boolean check(File file1, File file2) {
        boolean isSame = false;
        String img1Md5 = getMD5(file1);
        String img2Md5 = getMD5(file2);
        if (img1Md5.equals(img2Md5)) {
            isSame = true;
        } else {
            isSame = false;
        }
        return isSame;
    }

    public static byte[] getByte(File file) {
        // 得到文件长度
        byte[] b = new byte[(int) file.length()];
        try {
            InputStream in = new FileInputStream(file);
            try {
                in.read(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return b;
    }

    public static String getMD5(byte[] bytes) {
        // 16进制字符
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] strTemp = bytes;
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            // 移位 输出字符串
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMD5(File file) {
        return getMD5(getByte(file));
    }
    public static void  downLoadFromUrl(String urlStr,String fileName,String savePath,String toekn) throws IOException{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        conn.setRequestProperty("lfwywxqyh_token",toekn);

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        File file = new File(saveDir+File.separator+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }


        System.out.println("info:"+url+" download success");

    }
    /**
     * 从网络Url中下载文件
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static void  downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        File file = new File(saveDir+File.separator+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }

    }
    public static void downloadPlugin(String urlStr) throws IOException {
        File jar = new File(Server.getInstance().getPluginPath(), urlStr.substring(urlStr.lastIndexOf('/')+1,urlStr.length()));
        if (jar.exists()){
            return;
        }
        File tmp = new File(jar.getPath()+".au");
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3*1000);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        InputStream is = conn.getInputStream();
        int totalSize = conn.getContentLength();
        ProgressBarThread pbt = new ProgressBarThread(totalSize);
        new Thread(pbt).start();
        FileOutputStream os = new FileOutputStream(tmp);
        byte[] buf = new byte[4096];
        int size = 0;
        while((size = is.read(buf)) != -1) {
            os.write(buf, 0, size);
            pbt.updateProgress(size);
        }
        is.close();
        os.flush();
        os.close();
        if(jar.exists())
            jar.delete();
        tmp.renameTo(jar);
        Server.getInstance().getPluginManager().loadPlugin(jar.getPath());
    }
    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            Loader.getlogger().warning(TextFormat.RED+"BlackBE的云黑数据库炸了！快去https://ban.bugmc.com/求救！");
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

}
