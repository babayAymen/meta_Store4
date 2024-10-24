package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.room.Inventory
import com.aymen.store.model.entity.dto.InventoryDto

fun mapInventoryToRoomInventor(inventory : InventoryDto) :Inventory{
    return Inventory(

     id = inventory.id,

     out_quantity = inventory.out_quantity  ,

     in_quantity = inventory.in_quantity ,

     bestClientId   = inventory.bestClient?.id,

     articleCost  = inventory.articleCost ,

     articleSelling  = inventory.articleSelling ,

     discountOut   = inventory.discountOut,

     discountIn  = inventory.discountIn ,

     companyId   = inventory.company?.id,

     articleId   = inventory.article?.id
    )
}