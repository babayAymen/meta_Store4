package com.aymen.store.model.entity.api

import java.time.LocalDateTime

data class PurchaseOrderDto(

    var id : Long? = null,

    var company : CompanyDto = CompanyDto(),

    var client : CompanyDto? = CompanyDto(),

    var person : UserDto? = UserDto(),

    var createdDate : String? = null,

    var orderNumber : Long = 0
)
