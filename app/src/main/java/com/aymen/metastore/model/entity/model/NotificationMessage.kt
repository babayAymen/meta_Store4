package com.aymen.metastore.model.entity.model

import com.aymen.store.model.Enum.AccountType
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
): Serializable
