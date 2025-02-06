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
import com.aymen.metastore.model.Enum.PaymentType
import com.aymen.metastore.model.entity.model.NotificationMessage
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.ui.screen.user.UserScreen
import com.aymen.metastore.util.CLIENT_TYPE
import com.aymen.metastore.util.INVOICE_ID
import com.aymen.metastore.util.IS_SEND
import com.aymen.metastore.util.NOTIFICATION_TYPE
import com.aymen.metastore.util.PAYMENT_TYPE
import com.aymen.metastore.util.STATUS
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.Status
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
        Log.e("devicetoken","onMessageReceived fun called")
        val notificationMessage = NotificationMessage()
        if (message.data.isNotEmpty()) {
            notificationMessage.notificationType = message.data["type"] ?: "Default Title"
            notificationMessage.balnce = message.data["balance"]?.toDouble() ?: 0.0
            notificationMessage.orderOrInvoiceId = message.data["id"]?.toLong()?:0
            notificationMessage.clientType = AccountType.valueOf( message.data["clientType"]?:"NULL")
            notificationMessage.isSend = ( message.data["isSend"]?:"false").toBoolean()
            notificationMessage.isMeta = ( message.data["isMeta"]?:"false").toBoolean()
            notificationMessage.status = Status.valueOf( message.data["status"]?:"NULL")
            notificationMessage.paymentType = PaymentType.valueOf(message.data["paymentType"]?:"NULL")
            if(notificationMessage.balnce != 0.0 && notificationMessage.isMeta == false){
                sharedViewModel.updateBalance(notificationMessage.balnce!!.toBigDecimal())
            }
            when(notificationMessage.notificationType) {
                NotificationType.INVOICE.name -> if (notificationMessage.isSend == true) sharedViewModel.setInvoiceCountNotification(
                    false
                ) else sharedViewModel.setInvoiceAsClientCountNotification(false)

                NotificationType.ORDER.name -> sharedViewModel.setOrderCountNotification(false)
                NotificationType.INVITATION.name -> sharedViewModel.setInvitationCountNotification(
                    false
                )

                NotificationType.PAYMENT.name -> {
                    if (notificationMessage.paymentType == PaymentType.INVOICE)
                        sharedViewModel.setInvoiceAsClientCountNotification(false)
                    else {
                        if (notificationMessage.isMeta != true) sharedViewModel.setPaymentCountNotification(
                            false
                        ) else sharedViewModel.setReglementCountNotification(false)
                    }
                }
            }
        }
            notificationMessage.title = message.data["title"]?: "Default Title"
            notificationMessage.body = message.data["body"] ?: "Default Body"

            showNotification(notificationMessage)
    }


private fun showNotification(notificationMessage : NotificationMessage) {
    val (channelId, channelName) = when (notificationMessage.notificationType) {
        "PAYMENT" -> "transaction_alerts" to "Transaction Notifications"
        "BALANCE" -> "balance_alerts" to "Balance"
        "ORDER" -> "order_alerts" to "Order"
        else -> "default_channel_id" to "Default Notifications"
    }
    val notificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId, channelName, NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        putExtra(NOTIFICATION_TYPE, notificationMessage.notificationType)
        putExtra("balance", notificationMessage.balnce.toString())
        putExtra(IS_SEND, notificationMessage.isSend.toString())
        putExtra(CLIENT_TYPE, notificationMessage.clientType.toString())
        putExtra(STATUS, notificationMessage.status.toString())
        putExtra(PAYMENT_TYPE, notificationMessage.paymentType.toString())
        putExtra(INVOICE_ID, notificationMessage.orderOrInvoiceId.toString())
    }
    val pendingIntent = PendingIntent.getActivity(
        this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Build the notification
    val notification = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app icon
        .setContentTitle(notificationMessage.title)//
        .setContentText(notificationMessage.body)//
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    // Show the notification
    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
}
}
