package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.Enum.PaymentType
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.Status
import java.io.Serializable

data class NotificationMessage (

    val token : String? = null,
    var title : String? = null,
    var body : String? = null,
    val image : String? = null,
    val data : Map<String , String>? = null,
    var balnce : Double? = null,
    var notificationType : String ? = null,
    var orderOrInvoiceId : Long ? = null,
    var clientType : AccountType? = null,
    var isSend : Boolean ? = null,
    var isMeta : Boolean ? = null,
    var status : Status ? = null,
    var paymentType : PaymentType? = null
): Serializable
