package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.PurchaseOrder

data class PurchaseOrderDto(

    val id : Long? = null,
    val company : CompanyDto? = null,
    val client : CompanyDto? = null,
    val person : UserDto? = null,
    val createdDate : String? = null,
    val orderNumber : Long? = 0,
    val isDelivered : Boolean? = false,
    val isTaken : Boolean? = false,
    val discount : Double? = null,
    val prix_order_tot : Double? = null,
    val tot_tva : Double? = null,
    val prix_article_tot : Double? = null,
){
    fun toPurchaseOrder() : PurchaseOrder {

        return PurchaseOrder(
            purchaseOrderId = id,
            companyId = company?.id,
            clientId = client?.id,
            userId = person?.id,
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

    fun toPurchaseOrderModel() : com.aymen.metastore.model.entity.model.PurchaseOrder{
        return com.aymen.metastore.model.entity.model.PurchaseOrder(
            id = id,
            company = company?.toCompanyModel(),
            client = client?.toCompanyModel(),
            person = person?.toUserModel(),
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