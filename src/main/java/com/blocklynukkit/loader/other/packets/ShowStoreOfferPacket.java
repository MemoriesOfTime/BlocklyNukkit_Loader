package com.blocklynukkit.loader.other.packets;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;

public class ShowStoreOfferPacket extends DataPacket {
    public String string = null;
    public boolean bool;

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public boolean getBool(){
        return this.bool;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getString(){
        return this.string;
    }

    @Override
    public byte pid() {
        return ProtocolInfo.SHOW_STORE_OFFER_PACKET;
    }

    @Override
    public void decode() {
        string = this.getString();
        bool = this.getBool();
    }

    @Override
    public void encode() {
        this.putString(string);
        this.putBoolean(bool);
    }
}
