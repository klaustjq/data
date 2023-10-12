package pri.tjq.kaleido.controller;

import cn.hutool.core.codec.Base64;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author tjq
 * @since 2023/9/25
 */
@Configuration
public class WSServerCorda {

    private Timer reconnectTimer;

    @Bean
    public WebSocketClient kaleidoWebSocketClient() {
        WebSocketClient ws = null;
        try {
            Map<String, String> httpHeaders = new HashMap<>();
            ws = new WebSocketClient(new URI("ws://34.38.96.81:8080/ws")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("TOPIC open!!!");
                    this.send("{\"type\":\"listen\",\"topic\":\"eventstream-0-topic\"}");
                    cancelReconnectTimer();
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("receive message111: " + message);
                    this.send("{\"type\":\"ack\",\"topic\":\"eventstream-0-topic\"}");
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Event stream websocket disconnected: " + reason);
                    startReconnectTimer(this);
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("Event stream websocket error. " + ex.getMessage());
                }

            };
            ws.connect();
            return ws;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void startReconnectTimer(WebSocketClient ws) {
        if (reconnectTimer == null) {
            reconnectTimer = new Timer();
            TimerTask reconnectTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        System.out.println("Attempting to reconnect...");
                        ws.reconnectBlocking();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            reconnectTimer.schedule(reconnectTask, 5000);
        }
    }

    private void cancelReconnectTimer() {
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
            reconnectTimer = null;
        }
    }

}