package com.hyena.coretext.samples;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 17/5/15.
 */

public class WebSocketManager {

    public static WebSocketManager instance;
    private WebSocketClient client;

    private WebSocketManager() {
    }

    public static WebSocketManager getInstance() {
        if (instance == null)
            instance = new WebSocketManager();
        return instance;
    }

    private void initClient() throws URISyntaxException {
        if (client != null && !client.isClosed())
            return;

        client = new WebSocketClient(new URI("ws://192.168.10.97:8080/testWeb/pub-question")) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                for (int i = 0; i < listeners.size(); i++) {
                    listeners.get(i).onOpen();
                }
            }

            @Override
            public void onMessage(String s) {
                for (int i = 0; i < listeners.size(); i++) {
                    listeners.get(i).onMessage(s);
                }
            }

            @Override
            public void onClose(int status, String s, boolean b) {
                Log.v("WebSocket", "Connection closed by " + ( b ? "remote peer" : "us" ) + ", info=" + s);
                for (int i = 0; i < listeners.size(); i++) {
                    listeners.get(i).onClose();
                }
            }

            @Override
            public void onError(Exception e) {
                for (int i = 0; i < listeners.size(); i++) {
                    listeners.get(i).onError();
                }
            }
        };
    }

    public List<WebSocketListener> listeners = new ArrayList<>();

    public void registListener(WebSocketListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    private void unRegistListener(WebSocketListener listener) {
        listeners.remove(listener);
    }

    interface WebSocketListener {
        void onOpen();
        void onClose();
        void onMessage(String s);
        void onError();
    }

    public void connect() throws URISyntaxException {
        initClient();
        new Thread(){
            @Override
            public void run() {
                client.connect();
            }
        }.start();
    }

    public void close() {
        try {
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {}
        finally {
            client = null;
        }
    }
}
