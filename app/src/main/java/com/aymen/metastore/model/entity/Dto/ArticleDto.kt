package com.aymen.store.model.entity.dto

import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.Enum.UnitArticle

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

)
