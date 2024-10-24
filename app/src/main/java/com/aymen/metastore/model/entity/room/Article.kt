package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aymen.store.model.Enum.CompanyCategory
@Entity(tableName = "article")
data class Article(

    @PrimaryKey val id: Long? = null,

    val libelle: String = "",
    val code: String = "",
    val discription: String = "",
    val barcode: String? = null,
    val tva: Double = 0.0,
    val image: String = "",
    val isDiscounted : Boolean = false,
    val category : CompanyCategory = CompanyCategory.DAIRY
)
