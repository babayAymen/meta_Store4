package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.Article
import com.aymen.store.model.Enum.CompanyCategory

data class ArticleDto(
    var id: Long? = null,
    var libelle: String = "",
    var code: String = "",
    var discription: String = "",
    var barcode: String? = null,
    var tva: Double = 0.0,
    var image: String = "",
    var isDiscounted : Boolean = false,
    var category : CompanyCategory? = CompanyCategory.DAIRY
) {
    fun toArticle() : Article {
    return Article(
        id = id,
        libelle = libelle,
        code = code,
        discription = discription,
        barcode = barcode,
        tva = tva,
        image = image,
        isDiscounted = isDiscounted,
        category = category
    )
    }
    fun toArticleModel() : com.aymen.metastore.model.entity.model.Article {
    return com.aymen.metastore.model.entity.model.Article(
        id = id,
        libelle = libelle,
        code = code,
        discription = discription,
        barcode = barcode,
        tva = tva,
        image = image,
        isDiscounted = isDiscounted,
        category = category
    )
    }
}
