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
import com.aymen.metastore.model.Enum.NotificationType
import com.aymen.metastore.model.entity.model.NotificationMessage
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.ui.screen.user.UserScreen
import com.aymen.store.model.Enum.AccountType
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
        val notificationMessage = NotificationMessage()
        if (message.data.isNotEmpty()) {
            notificationMessage.notificationType = message.data["type"] ?: "Default Title"
            notificationMessage.balnce = message.data["balance"]?.toDouble() ?: 0.0
            notificationMessage.orderOrInvoiceId = message.data["id"]?.toLong()?:0
            notificationMessage.clientType = AccountType.valueOf( message.data["clientType"]?:"NULL")
            notificationMessage.isSend = ( message.data["isSend"]?:"false").toBoolean()
            notificationMessage.isMeta = ( message.data["isMeta"]?:"false").toBoolean()
            if(notificationMessage.balnce != 0.0 && notificationMessage.isMeta == false){
                sharedViewModel.updateBalance(notificationMessage.balnce!!.toBigDecimal())
            }
            when(notificationMessage.notificationType){
                NotificationType.INVOICE.name -> if(notificationMessage.isSend == true) sharedViewModel.setInvoiceCountNotification(false) else sharedViewModel.setInvoiceAsClientCountNotification(false)
                NotificationType.ORDER.name -> sharedViewModel.setOrderCountNotification(false)
                NotificationType.INVITATION.name -> sharedViewModel.setInvitationCountNotification(false)
                NotificationType.PAYMENT.name -> if(notificationMessage.isMeta != true) sharedViewModel.setPaymentCountNotification(false) else sharedViewModel.setReglementCountNotification(false)
            }
        }
        if (message.notification != null) {
            notificationMessage.title = message.notification!!.title ?: "Default Title"
            notificationMessage.body = message.notification!!.body ?: "Default Body"

            showNotification(notificationMessage)
        }
    }

private fun showNotification(notificationMessage : NotificationMessage) {
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

        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        putExtra("notificationType", notificationMessage.notificationType)
        putExtra("balance", notificationMessage.balnce.toString())
        putExtra("isSend", notificationMessage.isSend.toString())
        putExtra("clientType", notificationMessage.clientType.toString())
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
