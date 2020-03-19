package dls.icesight.blocklynukkit.script;

import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.response.FormResponseSimple;
import dls.icesight.blocklynukkit.script.window.Custom;
import dls.icesight.blocklynukkit.script.window.Modal;
import dls.icesight.blocklynukkit.script.window.Simple;

public class WindowManager {
    public Simple getSimpleWindowBuilder(String title,String context){
        Simple simple=new Simple();
        simple.setTitle(title);
        simple.setContext(context);
        return simple;
    }
    public Modal getModalWindowBuilder(String title,String context){
        Modal modal=new Modal();
        modal.setContext(context);
        modal.setTitle(title);
        return modal;
    }
    public Custom getCustomWindowBuilder(String title){
        Custom custom=new Custom();
        custom.setTitle(title);
        return custom;
    }
    public String getEventResponseText(PlayerFormRespondedEvent event){
        FormResponseSimple simple=(FormResponseSimple)event.getResponse();
        if(simple!=null){
            return simple.getClickedButton().getText();
        }else {
            return "NULL";
        }
    }
    public String getEventResponseModal(PlayerFormRespondedEvent event){
        FormResponseModal modal=(FormResponseModal)event.getResponse();
        if(modal!=null){
            return modal.getClickedButtonText();
        }else {
            return "NULL";
        }
    }
    public String getEventCustomVar(PlayerFormRespondedEvent event,int id,String mode){
        FormResponseCustom custom=(FormResponseCustom)event.getResponse();
        if(mode.equals("input")){
            return custom.getInputResponse(id);
        }else if(mode.equals("toggle")){
            return custom.getToggleResponse(id)?"TRUE":"FALSE";
        }else if(mode.equals("dropdown")){
            return custom.getDropdownResponse(id).getElementContent();
        }else {
            return "NULL";
        }
    }
}
