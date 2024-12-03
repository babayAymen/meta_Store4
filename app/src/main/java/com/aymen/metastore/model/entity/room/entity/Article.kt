package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aymen.store.model.Enum.CompanyCategory
@Entity(tableName = "article")
data class Article(

    @PrimaryKey val id: Long? = null,

    val libelle: String? = "",
    val code: String? = "",
    val discription: String? = "",
    val barcode: String? = null,
    val tva: Double? = 0.0,
    val image: String? = "",
    val isDiscounted : Boolean? = false,
    val category : CompanyCategory? = CompanyCategory.DAIRY,
    val isMy : Boolean? = true,
){
    fun toArticle(): com.aymen.metastore.model.entity.model.Article{
        return com.aymen.metastore.model.entity.model.Article(
            id = id,
            libelle = libelle,
            code = code,
            discription = discription,
            barcode = barcode,
            tva = tva,
            image = image,
            isDiscounted = isDiscounted,
            category = category,
        )
    }
}
