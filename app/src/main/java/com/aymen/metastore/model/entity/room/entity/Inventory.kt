package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.Inventory

@Entity(tableName = "inventory",
    foreignKeys = [
        ForeignKey(entity = Article::class, parentColumns = ["id"], childColumns = ["articleId"]),
        ForeignKey(
            entity = Company::class,
            parentColumns = ["companyId"],
            childColumns = ["bestClientId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Company::class,
            parentColumns = ["companyId"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
    ])
data class Inventory(

    @PrimaryKey
    val id : Long? = null,
    val out_quantity : Double? = null,
    val in_quantity : Double? = null,
    val bestClientId : Long? = null,
    val articleCost : Double? = null,
    val articleSelling : Double? = null,
    val discountOut : Double? = null,
    val discountIn : Double? = null,
    val companyId : Long? = null,
    val articleId : Long? = null
){
    fun toInventory(company : com.aymen.metastore.model.entity.model.Company?,
                    article : com.aymen.metastore.model.entity.model.ArticleCompany) : Inventory{
        return Inventory(
            id = id,
            out_quantity = out_quantity,
            in_quantity = in_quantity,
            articleCost = articleCost,
            articleSelling = articleSelling,
            discountOut = discountOut,
            discountIn = discountIn,
            company = company,
            article = article
        )
    }
}
