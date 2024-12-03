package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.dto.ArticleDto
import com.aymen.store.model.Enum.CompanyCategory

data class Article(
    var id: Long? = null,
    var libelle: String? = "",
    var code: String? = "",
    var discription: String? = "",
    var barcode: String? = null,
    var tva: Double? = 0.0,
    var image: String? = null,
    var isDiscounted : Boolean? = false,
    var category : CompanyCategory? = CompanyCategory.DAIRY,
){
    fun toArticleDto() : ArticleDto{
        return ArticleDto(
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