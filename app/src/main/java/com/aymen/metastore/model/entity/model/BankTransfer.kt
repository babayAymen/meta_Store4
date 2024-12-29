package com.aymen.metastore.model.entity.model

data class BankTransfer(

    var id : Long? = null,

    var transactionId: String = "",

    var amount: Double? = null,

    var agency: String = "",

    var invoice: Invoice? = null,

    var bankAccount: String = "",

    var createdDate : String = "",

    var lastModifiedDate : String = "",
)