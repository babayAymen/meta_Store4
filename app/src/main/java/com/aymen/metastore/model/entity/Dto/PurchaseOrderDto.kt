package com.aymen.store.model.entity.dto


data class PurchaseOrderDto(

    var id : Long? = null,

    var company : CompanyDto? = null,

    var client : CompanyDto? = null,

    var person : UserDto? = null,

    var createdDate : String? = null,

    var orderNumber : Long? = 0
)
