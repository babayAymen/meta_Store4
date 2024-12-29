package com.aymen.metastore.model.entity.model

data class Bill (

    var id : Long? = null,

    var number: String = "",

    var amount: Double? = null,

    var agency: String = "",

    var bankAccount: String = "",

    var delay: String? = null,

    var invoice: Invoice? = null,

    var createdDate : String = "",

    var lastModifiedDate : String = "",
)