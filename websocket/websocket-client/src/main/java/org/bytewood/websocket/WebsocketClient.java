package org.bytewood.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;

public class WebsocketClient {

    static volatile boolean stop = false;
    public static void main(String[] args) throws InterruptedException {
        WebSocket ws = HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(URI.create("ws://localhost:9080"), new WebSocketListener()).join();

        System.console().readLine();
        ws.sendText("hello", false);
        ws.abort();
    }

    private static void pressEnterToTerminate() {
        try {
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            stop = true;
        }
    }

    static class WebSocketListener implements WebSocket.Listener {

        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("opening...");
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            System.out.println(data);
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
            return WebSocket.Listener.super.onBinary(webSocket, data, last);
        }

        @Override
        public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
            return WebSocket.Listener.super.onPing(webSocket, message);
        }

        @Override
        public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
            return WebSocket.Listener.super.onPong(webSocket, message);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            System.out.printf("%d , %s", statusCode, reason);
            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            error.printStackTrace();
            WebSocket.Listener.super.onError(webSocket, error);
        }
    }
}