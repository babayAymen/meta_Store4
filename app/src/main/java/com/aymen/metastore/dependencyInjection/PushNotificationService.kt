package com.aymen.metastore.dependencyInjection
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.aymen.metastore.MainActivity
import com.aymen.metastore.model.entity.model.NotificationMessage
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.ui.screen.user.UserScreen
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class PushNotificationService  : FirebaseMessagingService() {

    @Inject
    lateinit var sharedViewModel: SharedViewModel

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("devicetoken","token is new : $token")
        // should send the new token to the server in order to save it
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("devicetoken","token is : ${message.messageId}")
        val notificationMessage = NotificationMessage()
        var notificationType = ""
        var show = ""
        var title = ""
        var body = ""
        var balance = ""
            // Alternatively, handle data messages
        if (message.data.isNotEmpty()) {
            notificationType = message.data["type"] ?: "Default Title"
             show = message.data["show"] ?: "Default Body"
            notificationMessage.balnce = message.data["balance"]?.toDouble() ?: 0.0
            if(notificationMessage.balnce != 0.0){
                sharedViewModel.updateBalance(notificationMessage.balnce!!.toBigDecimal())
            }
            Log.e("devicetoken","token is : $notificationType")
            Log.e("devicetoken","balance is : $balance")
            // Show the notification
        }
        if (message.notification != null) {
            notificationMessage.title = message.notification!!.title ?: "Default Title"
            notificationMessage.body = message.notification!!.body ?: "Default Body"

            Log.e("devicetoken","token is : $title")
            Log.e("devicetoken","token is : $body")
            // Show the notification
            showNotification(notificationMessage,notificationType)
        }
        // response to the received message
    }

private fun showNotification(notificationMessage : NotificationMessage, notificationType : String) {
    val channelId = "default_channel_id"
    val channelName = "Default Channel"

    val notificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Create notification channel for Android O and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(this, MainActivity::class.java).apply {

        Log.e("devicetoken","view abd show is : $notificationType ${notificationMessage.balnce}")
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        putExtra("notificationType", notificationType)
        putExtra("balance", notificationMessage.balnce.toString())
    }
    val pendingIntent = PendingIntent.getActivity(
        this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )


    // Build the notification
    val notification = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app icon
        .setContentTitle(notificationMessage.title)
        .setContentText(notificationMessage.body)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    // Show the notification
    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
}
}
