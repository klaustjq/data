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
public class WSServer {

    private Timer reconnectTimer;

    @Bean
    public WebSocketClient kaleidoWebSocketClient() {
        WebSocketClient ws = null;
        try {
            Map<String, String> httpHeaders = new HashMap<>();
            // Basic dTBzNnd1dzl1dTpjdS1qMEVfTmJhTTdXTjV1MkhlOW9lY1VqdnFPWnE3cEw4cllpY1ZqMkI0
            String username = "u0werewl6n";
            String password = "xP44RHCb5thxlEbuy_tjMoMNCGgZoBJyunQ52U4kevg";
            httpHeaders.put("Authorization", "Basic " + Base64.encode(username + ":" + password));
            ws = new WebSocketClient(new URI("wss://u0werewl6n:xP44RHCb5thxlEbuy_tjMoMNCGgZoBJyunQ52U4kevg@u0dp08kjkt-u0uywqoxjt-connect.us0-aws.kaleido.io/ws"), httpHeaders) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("TOPIC open!!!");
                    this.send("{\"type\": \"listen\", \"topic\": \"BridgeEventTopic\"}");
                    cancelReconnectTimer();
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("receive message: " + message);
                    this.send("{\"type\":\"ack\",\"topic\":\"BridgeEventTopic\"}");
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