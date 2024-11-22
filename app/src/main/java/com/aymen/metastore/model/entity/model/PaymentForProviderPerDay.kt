package com.aymen.metastore.model.entity.model


data class PaymentForProviderPerDay (

     val id : Long ? = null,
     val amount : Double ?= null,
     val provider : Company? = null,
     val payed : Boolean ? = false,
     val createdDate : String? = "",
     val lastModifiedDate : String? = ""
)