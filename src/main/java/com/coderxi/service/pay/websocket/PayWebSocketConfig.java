package com.coderxi.service.pay.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
public class PayWebSocketConfig {

  @Bean
  ServerEndpointExporter serverEndpointExporter() {
    return new ServerEndpointExporter();
  }

}
