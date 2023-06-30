package com.example.simplewebsocketserver

import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler
import javax.annotation.PostConstruct

@Component
class WebsocketManager(val kafkaConsumerManager: KafkaConsumerManager): TextWebSocketHandler() {
    private val sessionList = ArrayList<WebSocketSession>()

    @PostConstruct
    fun init() {
        this.kafkaConsumerManager.kafkaReceiver
            .receiveAutoAck()
            .concatMap{ it }
            .onErrorContinue { t, u -> t.printStackTrace() }
            .subscribe { record ->
                sessionList.forEach { session ->
                    println("session: ${session.id}, send >> ${record.value()}")
                    session.sendMessage(TextMessage(record.value()))
                }
            }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        println("payload: ${message.payload}")
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessionList.add(session)
        println("add session: ${session.id}")
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionList.remove(session)
        println("remove session: ${session.id}")
    }
}

@Configuration
@EnableWebSocket
class WebsocketConfig(val websocketManager: WebsocketManager): WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(websocketManager, "test").setAllowedOrigins("*")
    }
}