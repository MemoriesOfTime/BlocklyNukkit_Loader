package com.blocklynukkit.loader.other.debug;

import com.blocklynukkit.loader.Loader;
import com.blocklynukkit.loader.other.debug.data.CommandInfo;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CommandViewer {
    public JSplitPane infosPane ;
    public JScrollPane cmdsPane;
    public JScrollPane cmdInfoPane;
    public JTree cmdsTree;
    public JTextArea infoArea;
    public Debuger parent;
    public Map<String,Map<String,String>> data = new HashMap<>();
    private long lastUpdated = System.currentTimeMillis();
    public CommandViewer(Debuger parent){
        this.parent = parent;
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("命令管理器");
        for(String each:Loader.plugincmdsmap.keySet()){
            DefaultMutableTreeNode pl = new DefaultMutableTreeNode(each,true);
            rootNode.add(pl);
            data.put(each,Loader.plugincmdsmap.get(each).getCallsTime(100));
            for(String entry:data.get(each).keySet()){
                pl.add(new DefaultMutableTreeNode(entry));
            }
        }
        cmdsTree = new JTree(rootNode);
        cmdsTree.expandRow(0);
        cmdsTree.setEditable(false);
        cmdsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        DefaultTreeCellRenderer renderer = ((DefaultTreeCellRenderer)cmdsTree.getCellRenderer());
        ImageIcon imageIcon = new ImageIcon(Loader.class.getResource("/command.png"));
        imageIcon = change(imageIcon,(renderer.getPreferredSize().height/128d)*0.9);
        renderer.setOpenIcon(imageIcon);
        renderer.setClosedIcon(imageIcon);
        cmdsTree.setCellRenderer(renderer);
//        cmdsList = new JList<>();
//        cmdsList.setListData(Loader.plugincmdsmap.keySet().toArray(new String[Loader.plugincmdsmap.keySet().size()]));
//        cmdsList.setSelectedIndex(0);
//        cmdsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cmdsPane = new JScrollPane(cmdsTree);
        infoArea = new JTextArea("");
        infoArea.setEditable(false);
        cmdInfoPane = new JScrollPane(infoArea);
        infosPane = new JSplitPane();
        infosPane.setContinuousLayout(false);
        //填充左右分割组件
        infosPane.setLeftComponent(cmdsPane);
        infosPane.setRightComponent(cmdInfoPane);
        infosPane.setOneTouchExpandable(true);
        infosPane.setDividerLocation(200);
        //监听选中命令事件
        cmdsTree.addTreeSelectionListener(e -> {
            if(System.currentTimeMillis() - lastUpdated<200){
                return;
            }
            if(e.getNewLeadSelectionPath()==null){
                return;
            }
            if(e.getNewLeadSelectionPath().getParentPath()==null){
                return;
            }
            if(e.getNewLeadSelectionPath().getParentPath().getPathComponent(e.getNewLeadSelectionPath().getParentPath().getPathCount()-1)==null){
                return;
            }
            String cmdName = (String) ((DefaultMutableTreeNode)e.getNewLeadSelectionPath().getParentPath().getPathComponent(e.getNewLeadSelectionPath().getParentPath().getPathCount()-1)).getUserObject();
            String queryName = (String)((DefaultMutableTreeNode)e.getNewLeadSelectionPath().getPathComponent(e.getNewLeadSelectionPath().getPathCount()-1)).getUserObject();
            Map<String,String> infomap = this.data.get(cmdName);
            if(infomap==null){
                return;
            }
            String info = infomap.get(queryName);
            if(info==null){
                return;
            }
            infoArea.setText(info);
            lastUpdated = System.currentTimeMillis();
        });
    }
    public ImageIcon change(ImageIcon image,double i){//  i 为放缩的倍数
        int width=(int) (image.getIconWidth()*i);
        int height=(int) (image.getIconHeight()*i);
        Image img=image.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);//第三个值可以去查api是图片转化的方式
        ImageIcon image2=new ImageIcon(img);
        return image2;

    }
}
