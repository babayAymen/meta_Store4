package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.store.model.entity.api.PurchaseOrderDto
import com.aymen.store.model.entity.realm.PurchaseOrder

fun mapPurchaseOrderDtoToPurchaseOrderRealm(purchaseOrder: PurchaseOrderDto): PurchaseOrder{
    return PurchaseOrder().apply {
        id = purchaseOrder.id
        company = mapcompanyDtoToCompanyRealm(purchaseOrder.company)
        client = purchaseOrder.client?.let { mapcompanyDtoToCompanyRealm(it) }
        person = purchaseOrder.person?.let { mapUserDtoToUserRealm(it) }
        createdDate = purchaseOrder.createdDate
        orderNumber = purchaseOrder.orderNumber
    }
}

fun mapPuerchaseOrderRealmToPurchaseOrderDto(purchaseOrder: PurchaseOrder): PurchaseOrderDto{
    return PurchaseOrderDto(
                id = purchaseOrder.id,
                company = mapCompanyToCompanyDto(purchaseOrder.company!!),
                client = purchaseOrder.client?.let { mapCompanyToCompanyDto(it) },
                person = purchaseOrder.person?.let { mapUserToUserDto(it) },
                createdDate = purchaseOrder.createdDate,
                orderNumber = purchaseOrder.orderNumber!!,
    )
}