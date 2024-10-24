package com.aymen.store.model.entity.realm

import com.aymen.store.model.Enum.UnitArticle
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class RandomArticle : RealmObject{
    @PrimaryKey  var id: Long = 0

    var libelle: String = ""
    var code: String = ""

    var unit: String? = UnitArticle.U.toString()
    var discription: String = ""
    var cost: Double = 0.0
    var quantity: Double = 0.0
    var minQuantity: Double = 0.0
    var sharedPoint: String = ""
    var margin: Double = 0.0
    var barcode: String = ""
    var tva: Double = 0.0
//    var category: com.aymen.metastore.model.entity.room.Category? = null
//    var subCategory: com.aymen.metastore.model.entity.room.SubCategory? = null
//    var provider : Provider? = null
//    var company: com.aymen.metastore.model.entity.room.Company? = null
    var image: String = ""
}