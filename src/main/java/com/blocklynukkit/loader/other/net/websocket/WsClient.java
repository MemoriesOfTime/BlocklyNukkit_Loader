package com.blocklynukkit.loader.other.net.websocket;

import com.blocklynukkit.loader.Loader;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class WsClient extends WebSocketClient {
    private String serverUrl = "";
    private String openCallback = null;
    private String closeCallback = null;
    private String messageStringCallback = null;
    private String messageDataCallback = null;

    public WsClient(URI serverURI) {
        super(serverURI);
    }

    public WsClient(String serverUrl, String openCallback, String closeCallback, String messageStringCallback, String messageDataCallback){
        super(URI.create(serverUrl));
        this.serverUrl = serverUrl;
        this.openCallback = openCallback;
        this.closeCallback = closeCallback;
        this.messageStringCallback = messageStringCallback;
        this.messageDataCallback = messageDataCallback;
        this.run();
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        Loader.plugin.call(openCallback, this, this.getConnection());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Loader.plugin.call(closeCallback, this, this.getConnection(), code, reason, remote);
    }

    @Override
    public void onMessage(String message) {
        Loader.plugin.call(messageStringCallback, this, this.getConnection(), message);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        Loader.plugin.call(messageDataCallback, this, this.getConnection(), message);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("An error occurred on websocket connection:" + ex);
    }
}
