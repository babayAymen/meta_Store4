package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.dto.PurchaseOrderDto
import com.aymen.metastore.model.entity.room.entity.PurchaseOrder

data class PurchaseOrder (

    val id : Long? = null,
    val company : Company? = null,
    val client : Company? = null,
    val person : User? = null,
    val createdDate : String? = null,
    val orderNumber : Long? = 0,
    val isDelivered : Boolean? = false,
    val isTaken : Boolean? = false,
    val discount : Double ? = null,
    val prix_order_tot : Double? = null,
    val tot_tva : Double? = null,
    val prix_article_tot : Double? = null,
){
    fun toPurchaseOrderEntity() : PurchaseOrder{
        return PurchaseOrder(
            purchaseOrderId = id,
            companyId = company?.id,
            userId = person?.id,
            clientId = client?.id,
            createdDate = createdDate,
            orderNumber = orderNumber,
            isDelivered = isDelivered,
            isTaken = isTaken,
            discount = discount,
            prix_order_tot = prix_order_tot,
            tot_tva = tot_tva,
            prix_article_tot = prix_article_tot

        )
    }
    fun toPurchaseOrderDto() : PurchaseOrderDto{
        return PurchaseOrderDto(
            id = id,
            company = company?.toCompanyDto(),
            person = person?.toUserDto(),
            client = client?.toCompanyDto(),
            createdDate = createdDate,
            orderNumber = orderNumber,
            isDelivered = isDelivered,
            isTaken = isTaken,
            discount = discount,
            prix_order_tot = prix_order_tot,
            tot_tva = tot_tva,
            prix_article_tot = prix_article_tot

        )
    }
}