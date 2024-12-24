package com.aymen.metastore.model.webSocket

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.naiksoftware.stomp.StompClient
import javax.inject.Inject
@HiltViewModel
class WebSocketViewModel  @Inject constructor(
    private val client: HttpClient
) : ViewModel() {


    fun connectToWebSocket() {
        viewModelScope.launch(Dispatchers.IO) {
            client.webSocket("ws://192.168.1.5:8080/ws") {
                // Log the connection attempt

                println("Connecting to WebSocket...")

                try {
                    send(Frame.Text("""SUBSCRIBE
                    destination:/topîc/public
                    id:sub-0
                    """.trimIndent()))

                    // Send a message to the server
                    send(Frame.Text("Hello, WebSocket Server!"))
                    println("Message sent to WebSocket")

                    // Listen for incoming frames
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val receivedMessage = frame.readText()
                            println("Received message: $receivedMessage")
                        }
                    }
                } catch (e: Exception) {
                    println("Error connecting to WebSocket: ${e.localizedMessage}")
                }
            }
        }


    }
}