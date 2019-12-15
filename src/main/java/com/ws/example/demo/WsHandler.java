package com.ws.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.singletonList;

public class WsHandler extends TextWebSocketHandler {

    private Map<String, List<String>> chunks = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        if (!message.isLast()) {
            System.out.println("received next chunk");
            chunks.merge(session.getId(), new ArrayList<>(singletonList(message.getPayload())), this::mergeLists);
            return;
        }
        List<String> storedChunks = this.chunks.remove(session.getId());
        List<String> chunks = new ArrayList<>();
        if (storedChunks != null) {
            chunks.addAll(storedChunks);
        }
        chunks.add(message.getPayload());

        StringBuilder sb = new StringBuilder();
        chunks.forEach(sb::append);
        String content = sb.toString();
        System.out.printf("Received %.2f MB: %s...%n", ((double)content.getBytes().length) / 1024 / 1024, sb.substring(0, 400));
        try {
            Data data = new ObjectMapper().readValue(content, Data.class);
            session.sendMessage(new TextMessage("Received " + data.getChars().length() + " chars over WS"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

    private <T> List<T> mergeLists(List<T> first, List<T> second) {
        ArrayList<T> result = new ArrayList<>(first);
        result.addAll(second);
        return result;
    }
}
