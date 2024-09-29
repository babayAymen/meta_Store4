package com.aymen.metastore.model.entity.api

import com.aymen.store.model.entity.api.CompanyDto
import io.realm.kotlin.types.annotations.PrimaryKey

data class PaymentForProviderPerDayDto (

    @PrimaryKey
    var id : Long ? = null,

    var amount : Double ?= null,

    var provider : CompanyDto ? = null,

    var payed : Boolean ? = false

)