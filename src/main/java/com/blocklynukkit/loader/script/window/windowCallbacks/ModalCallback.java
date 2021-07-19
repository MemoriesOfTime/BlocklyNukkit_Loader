package com.blocklynukkit.loader.script.window.windowCallbacks;

import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseModal;
import com.blocklynukkit.loader.Loader;

public final class ModalCallback extends WindowCallback {
    public String defaultCallback = null;
    public String yesCallback = null;
    public String noCallback = null;
    public ModalCallback(boolean acceptClose){
        super(acceptClose);
    }

    public void setDefaultCallback(String defaultCallback){
        this.defaultCallback=defaultCallback;
    }

    public boolean hasDefaultCallback(){
        return defaultCallback!=null;
    }

    public void setNoCallback(String noCallback) {
        this.noCallback = noCallback;
    }

    public void setYesCallback(String yesCallback) {
        this.yesCallback = yesCallback;
    }

    @Override
    public void call(PlayerFormRespondedEvent event) {
        if(event.getResponse()==null){
            if(hasDefaultCallback() && event.wasClosed() && isAcceptClose()) {
                Loader.plugin.call(defaultCallback,event);
            }
            return;
        }
        if(!(event.getResponse() instanceof FormResponseModal))return;
        FormResponseModal modal = (FormResponseModal) event.getResponse();
        if(modal.getClickedButtonId()==0 && yesCallback!=null){
            Loader.plugin.call(yesCallback,event);
        }
        if(modal.getClickedButtonId()==1 && noCallback!=null){
            Loader.plugin.call(noCallback,event);
        }
        if(hasDefaultCallback()){
            Loader.plugin.call(defaultCallback,event);
        }
    }
}
