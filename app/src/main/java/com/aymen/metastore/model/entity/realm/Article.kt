package com.aymen.store.model.entity.realm

import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.Enum.UnitArticle
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable

class Article : RealmObject {
    @PrimaryKey
    var id: Long? = null
    var libelle: String = ""
    var code: String = ""
    var discription: String = ""
    var barcode: String? = null
    var tva: Double = 0.0
    var image: String = ""
    var isDiscounted : Boolean = false
    var category : String = CompanyCategory.DAIRY.toString()
}