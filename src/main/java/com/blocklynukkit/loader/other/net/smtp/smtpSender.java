package com.blocklynukkit.loader.other.net.smtp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class smtpSender {
    public static void send(String smtpHostAddress, int port, String userName, String password) throws Exception {
        Socket socket=null;
        PrintWriter printWriter=null;
        BufferedReader br=null;
        try {
            //1. 连接smtp邮箱服务器
            socket=new Socket(smtpHostAddress,port);
            printWriter=new PrintWriter(socket.getOutputStream(),true);
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //2. 第一条命令 ehlo
            printWriter.println("ehlo zeng");
            br.readLine();
            //3. 发送,auth
            printWriter.println("auth login");
            br.readLine();
            //4. 用户名和密码
            printWriter.println(userName);
            printWriter.println(password);
            //会有一大串信息返回，如果最后返回235 Authentication successful则成功
            String temp=null;
            long start = System.currentTimeMillis();
            while ((temp=br.readLine())!=null){
                if(System.currentTimeMillis() - start > 1000*10){
                    throw new Exception("Smtp Time out!");
                }
                if ("235 Authentication successful".equals(temp)){
                    break;
                }
            }
            //设置发件人和收件人，敏感信息
            String sentUser="";
            String recUser="";
            printWriter.println("mail from:<"+sentUser+">");
            System.out.println(br.readLine());
            printWriter.println("rcpt to:<"+recUser+">");
            System.out.println(br.readLine());
            //设置data
            printWriter.println("data");
            System.out.println(br.readLine());
            //设置邮件主题
            printWriter.println("subject:test");
            printWriter.println("from:"+sentUser);
            printWriter.println("to:"+recUser);
            //设置邮件格式
            printWriter.println("Content-Type: text/html;charset=\"utf8\"");
            printWriter.println();
            //邮件正文
            printWriter.println("来自java手写smtp邮件客户端");
            printWriter.println(".");
            printWriter.print("");
            System.out.println(br.readLine());
            //退出
            printWriter.println("rset");
            System.out.println(br.readLine());
            printWriter.println("quit");
            System.out.println(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //释放连接
            socket.close();
            printWriter.close();
            br.close();
        }
    }
}
