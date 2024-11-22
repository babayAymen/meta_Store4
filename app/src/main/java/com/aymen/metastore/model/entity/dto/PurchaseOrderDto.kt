package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.PurchaseOrder

data class PurchaseOrderDto(

    val id : Long? = null,
    val company : CompanyDto? = null,
    val client : CompanyDto? = null,
    val person : UserDto? = null,
    val createdDate : String? = null,
    val orderNumber : Long? = 0
){
    fun toPurchaseOrder() : PurchaseOrder {

        return PurchaseOrder(
            id = id,
            companyId = company?.id,
            clientId = client?.id,
            userId = person?.id,
            createdDate = createdDate,
            orderNumber = orderNumber
        )
    }
}