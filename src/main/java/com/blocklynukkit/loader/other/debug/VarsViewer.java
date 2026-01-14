package com.blocklynukkit.loader.other.debug;

import com.blocklynukkit.loader.Loader;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.internal.runtime.JSType;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class VarsViewer {
    public JSplitPane varsPane ;
    public JScrollPane pluginsPane;
    public JScrollPane varInfoPane;
    public JList<String> pluginsList;
    public Debuger parent;
    private long lastUpdated = System.currentTimeMillis();
    public VarsViewer(Debuger parent){
        this.parent = parent;
        pluginsList = new JList<>();
        pluginsList.setListData(Loader.bnpluginset.toArray(new String[Loader.bnpluginset.size()]));
        pluginsList.setSelectedIndex(0);
        pluginsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pluginsPane = new JScrollPane(pluginsList);
        //出现bug
        varInfoPane = new JScrollPane(makeTable(new Object[][]{{"","",""}}));
        varsPane = new JSplitPane();
        varsPane.setContinuousLayout(false);
        //填充左右分割组件
        varsPane.setLeftComponent(pluginsPane);
        varsPane.setRightComponent(varInfoPane);
        varsPane.setOneTouchExpandable(true);
        varsPane.setDividerLocation(200);
        //添加选择监听器
        pluginsList.addListSelectionListener(e -> {
            if(System.currentTimeMillis() - lastUpdated<200){
                return;
            }
            int[] indices = pluginsList.getSelectedIndices();
            ListModel<String> listModel = pluginsList.getModel();
            String pluginName = listModel.getElementAt(indices[0]);
            if(!pluginName.contains(".")){
                varInfoPane = new JScrollPane(makeTable(new Object[][]{{"预编译jar包无法解析","",""}}));
                varsPane.setRightComponent(varInfoPane);
                return;
            }
            JDialog dialog = new JDialog(parent.jf, "请稍后", false);
            // 设置对话框的宽高
            dialog.setSize(200, 0);
            // 设置对话框大小不可改变
            dialog.setResizable(false);
            // 设置对话框相对显示的位置
            dialog.setLocationRelativeTo(parent.jf);
            // 显示对话框
            dialog.setVisible(true);
            // 结束
            Bindings bindings;
            if(pluginName.endsWith(".php")){
                bindings = Loader.engineMap.get(pluginName).getBindings(300);
            }else {
                bindings = Loader.engineMap.get(pluginName).getBindings(ScriptContext.GLOBAL_SCOPE);
            }
            if(bindings==null){
                bindings = Loader.engineMap.get(pluginName).getBindings(ScriptContext.ENGINE_SCOPE);
            }
            Set<Map.Entry<String,Object>> entries = bindings.entrySet();
            Set<Map.Entry<String,Object>> copy = new HashSet<>();
            for(Map.Entry<String,Object> each:entries){
                if(each.getKey().startsWith("javax.")||each.getKey().startsWith("__name__")||each.getKey().startsWith("__builtins__")){
                    continue;
                }
                copy.add(each);
            }
            Object[][] datas = new Object[copy.size()][3];
            int it = 0;
            for(Map.Entry<String,Object> entry:copy){
                if(entry.getKey()==null||entry.getValue()==null)continue;
                datas[it] = new Object[]{entry.getKey(),entry.getValue().getClass().getSimpleName(),transToStr(entry.getValue())};
                it++;
            }
            varInfoPane = new JScrollPane(makeTable(datas));
            varsPane.setRightComponent(varInfoPane);
            dialog.dispose();
            lastUpdated = System.currentTimeMillis();
        });
    }
    public JTable makeTable(Object[][] rowData){
        Object[] columnNames = {"变量名称", "变量类型", "关联内存", "变量内容"};
        if(/*!RamUsageEstimator.isSupportedJVM()*/true){//出现bug
            columnNames = new Object[]{"变量名称", "变量类型", "变量内容"};
        }
        JTable jTable = new JTable(rowData, columnNames);
        TableColumn tableColumn = jTable.getColumnModel().getColumn(0);
        tableColumn.setWidth(150);
        tableColumn.sizeWidthToFit();
        tableColumn = jTable.getColumnModel().getColumn(1);
        tableColumn.setWidth(150);
        tableColumn.sizeWidthToFit();
        return jTable;
    }
    public String transToStr(Object object){
        if(object instanceof ScriptObjectMirror){
            ScriptObjectMirror tmp = ((ScriptObjectMirror)object);
            if(tmp.isArray())
            return JSType.toString(tmp);
        }
        return object.toString();
    }
}
