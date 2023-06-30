package com.example.simplewebsocketserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SimpleWebsocketServerApplication

fun main(args: Array<String>) {
    runApplication<SimpleWebsocketServerApplication>(*args)
}
