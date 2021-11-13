package com.coderxi.service.pay.websocket;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/pay/{mode}/websocket")
public class PayWebSocket {

  static final Map<String, Session> sessions = new ConcurrentHashMap<>();

  @OnOpen
  public void onOpen(Session session) {
    sessions.put(keyFor(session), session);
  }

  @OnClose
  public void onClose(Session session) {
    sessions.remove(keyFor(session));
  }

  @SneakyThrows(IOException.class)
  public static void close(String mode, String outTradeNo) {
    sessions.get(mode + outTradeNo).close();
  }

  static String keyFor(Session session) {
    return session.getPathParameters().get("mode") + session.getRequestParameterMap().get("outTradeNo").get(0);
  }

}
