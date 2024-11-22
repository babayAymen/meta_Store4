package com.aymen.metastore.model.entity.model

data class Check (

    var id :Long? = null,

    var number: String? = null,

    var amount: Double? = null,

    var agency: String? = null,

    var delay: String? = null,

    var bankAccount: String? = null,
    var invoice: Invoice? = null,

    var createdDate : String = "",

    var lastModifiedDate : String = ""
)