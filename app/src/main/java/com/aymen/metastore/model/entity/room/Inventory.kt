package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
@Entity(tableName = "inventory",
    foreignKeys = [
        ForeignKey(entity = Article::class, parentColumns = ["id"], childColumns = ["articleId"]),
        ForeignKey(
            entity = Company::class,
            parentColumns = ["id"],
            childColumns = ["bestClientId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Company::class,
            parentColumns = ["id"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.SET_NULL
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
)
