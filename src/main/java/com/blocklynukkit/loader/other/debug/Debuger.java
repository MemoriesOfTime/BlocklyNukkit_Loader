package com.blocklynukkit.loader.other.debug;

import com.blocklynukkit.loader.Loader;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Debuger {
    public JFrame jf ;
    public JTabbedPane tab ;
    public Debuger(){
        try {
            UIManager.setLookAndFeel( new FlatDarculaLaf());
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
        JFrame.setDefaultLookAndFeelDecorated( true );
        jf = new JFrame("BlocklyNukkit调试器");
        jf.setMinimumSize(new Dimension(640,360));
        tab = new JTabbedPane(JTabbedPane.BOTTOM);
    }
    public void display(){
        //设置总窗口
        jf.setSize((int)(640*1.5),(int)(360*1.5));
        jf.setIconImage(getIcon());
        //设置变量监视器选项卡
        tab.add("变量监控",new VarsViewer(this).varsPane);
        tab.add("命令监控",new CommandViewer(this).infosPane);
        //设置切换选项卡更新
        tab.addChangeListener(e -> {
            if(tab.getSelectedIndex()==0){
                tab.setComponentAt(0,new VarsViewer(this).varsPane);
            }else if(tab.getSelectedIndex()==1){
                tab.setComponentAt(1,new CommandViewer(this).infosPane);
            }
        });
        //设置
        //将选项卡加到窗口上
        jf.setContentPane(tab);
        //显示窗口
        jf.setVisible(true);
        //设置图标，暂时有问题
        jf.setIconImage(getIcon());
    }

    /**
     * 这玩意好像没用
     * @return Image
     */
    public Image getIcon(){
        URL url = Loader.class.getResource("/BlocklyNukkit.png"); //声明url对象
        Image src = Toolkit.getDefaultToolkit().getImage(url);
        return src;
    }
}
