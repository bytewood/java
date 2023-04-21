package org.bytewood.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WebSocketServer {

    public static final String WEBSOCKET_KEY_SEED = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    public static final String WEBSOCKET_KEY_ENCRYPTION_ALGORITHM = "SHA-1";
    public static final Pattern HTTP_HEADER__Sec_WebSocket_Key__REGEX_PATTERN = Pattern.compile("Sec-WebSocket-Key: (.*)");
    public static final Pattern HTTP_GET_METHOD_PATTERN = Pattern.compile("^GET");

    static volatile boolean stop = false;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        final LinkedList<Socket> clientSockets = new LinkedList<>();

        try (final ServerSocket ss = new ServerSocket(9080)) {
            System.out.println("Server started on localhost:9080");

            while (!stop) {
                Socket client = ss.accept();
                clientSockets.push(client);

                try (
                        final InputStream in = client.getInputStream();
                        final OutputStream out = client.getOutputStream();
                        final Scanner s = new Scanner(in, UTF_8);
                ) {

                    final String data = s.useDelimiter("\\r\\n\\r\\n").next();

                    System.out.println(data);

                    final Matcher get = HTTP_GET_METHOD_PATTERN.matcher(data);

                    if (get.find()) {
                        final Matcher key = HTTP_HEADER__Sec_WebSocket_Key__REGEX_PATTERN.matcher(data);
                        if (key.find()) {
                            byte[] response = (
                                    "HTTP/1.1 101 Switching Protocols\r\n"
                                            + "Connection: Upgrade\r\n"
                                            + "Upgrade: websocket\r\n"
                                            + "Sec-WebSocket-Accept: " + acceptHash(key.group(1)) + "\r\n"
                                            + "\r\n"
                            ).getBytes(UTF_8);
                            out.write(response, 0, response.length);
                            out.flush();
                        }
                    }
                }
            }

            pressEnterToTerminate();
        }

        clientSockets.forEach(s -> {
            try {
                s.close();
            } catch (IOException ignored) {

            }
        });
    }

    private static void pressEnterToTerminate() {
        try {
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            stop = true;
        }
    }

    private static String acceptHash(String key) throws NoSuchAlgorithmException {
        return Base64.getEncoder().encodeToString(
                MessageDigest.getInstance(WEBSOCKET_KEY_ENCRYPTION_ALGORITHM)
                        .digest((key + WEBSOCKET_KEY_SEED).getBytes(UTF_8)));
    }
}