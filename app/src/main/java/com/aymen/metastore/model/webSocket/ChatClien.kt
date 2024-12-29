package com.aymen.metastore.model.webSocket

import com.aymen.metastore.util.BASE_URL
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatClient  @Inject constructor() {

    private val stompClient: StompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://${BASE_URL}/ws/websocket")
    private val compositeDisposable = CompositeDisposable()

    fun setupStompClient() {
//        stompClient.lifecycle()
//            .subscribeOn(Schedulers.io())
//            .subscribe { lifecycleEvent ->
//                when (lifecycleEvent.type) {
//                    StompClient.LifecycleEvent.Type.OPENED -> println("Stomp Connection Opened")
//                    StompClient.LifecycleEvent.Type.CLOSED -> println("Stomp Connection Closed")
//                    StompClient.LifecycleEvent.Type.ERROR -> println("Stomp Connection Error: ${lifecycleEvent.exception}")
//                    else -> {}
//                }
//            }.let { compositeDisposable.add(it) }

        stompClient.connect()
    }

    fun sendMessage(message: String) {
     //   stompClient.send("/app/chat", message).subscribe()
    }

    fun cleanup() {
        stompClient.disconnect()
        compositeDisposable.dispose()
    }
}