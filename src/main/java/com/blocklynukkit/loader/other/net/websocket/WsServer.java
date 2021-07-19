package com.blocklynukkit.loader.other.net.websocket;

import com.blocklynukkit.loader.Loader;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class WsServer extends WebSocketServer {
    private int port = 8080;
    private String openCallback = null;
    private String closeCallback = null;
    private String messageStringCallback = null;
    private String messageDataCallback = null;

    public WsServer(InetSocketAddress address) {
        super(address);
    }

    public WsServer(int port, String openCallback, String closeCallback, String messageStringCallback, String messageDataCallback){
        super(new InetSocketAddress("0.0.0.0", port));
        this.port = port;
        this.openCallback = openCallback;
        this.closeCallback = closeCallback;
        this.messageStringCallback = messageStringCallback;
        this.messageDataCallback = messageDataCallback;
        this.run();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Loader.plugin.call(openCallback, this, conn);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Loader.plugin.call(closeCallback, this, conn, code, reason, remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Loader.plugin.call(messageStringCallback, this, conn, message);
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        Loader.plugin.call(messageDataCallback, this, conn, message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("An error occurred on websockets connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    @Override
    public void onStart() {
        super.start();
    }
}
