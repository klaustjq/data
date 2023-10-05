package pri.tjq.kaleido;

import cn.hutool.core.codec.Base64;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class WebSocketFlowableExample {

    public static void main(String[] args) {
        Flowable<String> flowable = createWebSocketFlowable();

        flowable.subscribe(
                message -> {
                    System.out.println("Received message: " + message);
                },
                Throwable::printStackTrace,
                () -> System.out.println("WebSocket connection closed")
        );
    }

    private static Flowable<String> createWebSocketFlowable() {
        return Flowable.create(emitter -> {
            try {
//                String serverUrl = "ws://34.38.96.81:8080/ws";
//                WebSocketClient client = new WebSocketClient(new URI(serverUrl)) {
                Map<String, String> httpHeaders = new HashMap<>();
                String username = "u0werewl6n";
                String password = "xP44RHCb5thxlEbuy_tjMoMNCGgZoBJyunQ52U4kevg";
                httpHeaders.put("Authorization", "Basic " + Base64.encode(username + ":" + password));
                WebSocketClient ws = new WebSocketClient(new URI("wss://u0werewl6n:xP44RHCb5thxlEbuy_tjMoMNCGgZoBJyunQ52U4kevg@u0dp08kjkt-u0uywqoxjt-connect.us0-aws.kaleido.io/ws"), httpHeaders) {
                    @Override
                    public void onOpen(ServerHandshake handshakeData) {
                        // WebSocket connection is established
                        System.out.println("open!!!");
                        this.send("{\"type\": \"listen\", \"topic\": \"BridgeEventTopic\"}");
                        emitter.setCancellable(this::close);
                    }

                    @Override
                    public void onMessage(String message) {
                        // Received a message from the WebSocket server
                        this.send("{\"type\":\"ack\",\"topic\":\"BridgeEventTopic\"}");
                        emitter.onNext(message);
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        // WebSocket connection is closed
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(Exception ex) {
                        // An error occurred in WebSocket communication
                        emitter.onError(ex);
                    }
                };

                // Establish WebSocket connection
                ws.connect();

            } catch (URISyntaxException e) {
                emitter.onError(e);
            }
        }, BackpressureStrategy.BUFFER);
    }
}