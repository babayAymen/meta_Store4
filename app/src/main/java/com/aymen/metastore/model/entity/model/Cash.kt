package com.aymen.metastore.model.entity.model

import com.aymen.store.model.Enum.Status
data class Cash (

     var id : Long? = null,

     var transactionId: String = "",

     var amount: Double? = null,

     var status: Status? = Status.INWAITING,

     var invoice: Invoice? = null,

     var createdDate : String = "",

     var lastModifiedDate : String = "",
)