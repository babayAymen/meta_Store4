package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.store.model.entity.dto.PurchaseOrderDto


    fun mapPurchaseOrderToRoomPurchaseOrder(purchaseOrder: PurchaseOrderDto): com.aymen.metastore.model.entity.room.PurchaseOrder {
        return com.aymen.metastore.model.entity.room.PurchaseOrder(

            id = purchaseOrder.id!!,
            orderNumber = purchaseOrder.orderNumber,
            createdDate = purchaseOrder.createdDate,
            companyId = purchaseOrder.company?.id,
            userId = purchaseOrder.person?.id,
            clientId = purchaseOrder.client?.id

        )

    }
