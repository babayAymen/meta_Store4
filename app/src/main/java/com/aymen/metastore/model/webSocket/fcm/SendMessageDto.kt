package com.aymen.metastore.model.webSocket.fcm

data class SendMessageDto(
    val to : String?,
    val notification : NotificationBody,
)

data class NotificationBody(
    val title : String,
    val body : String
)
