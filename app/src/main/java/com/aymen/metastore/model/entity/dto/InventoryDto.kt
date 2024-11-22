package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.Inventory

data class InventoryDto(

    val id : Long? = null,
    val out_quantity : Double? = null,
    val in_quantity : Double? = null,
    val bestClient : CompanyDto? = null,
    val articleCost : Double? = null,
    val articleSelling : Double? = null,
    val discountOut : Double? = null,
    val discountIn : Double? = null,
    val company : CompanyDto? = null,
    val article : ArticleCompanyDto? = null
){
    fun toInventory() : Inventory {

        return Inventory(
            id = id,
            out_quantity = out_quantity,
            in_quantity = in_quantity,
            bestClientId = bestClient?.id,
            articleCost = articleCost,
            articleSelling = articleSelling,
            discountOut = discountOut,
            discountIn = discountIn,
            companyId = company?.id,
            articleId = article?.id

        )
    }
}
