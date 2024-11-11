package com.aymen.metastore.model.entity.realm

import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.Enum.UnitArticle
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Category
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.realm.SubCategory
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class ArticleCompany : RealmObject{
    @PrimaryKey
    var id : Long? = null
    var unit: String? = UnitArticle.U.toString()
    var cost: Double? = 0.0
    var quantity: Double? = 0.0
    var minQuantity: Double? = 0.0
    var sharedPoint: Long? = null
    var sellingPrice: Double? = 0.0
    var category: Category? = null
    var subCategory: SubCategory? = SubCategory()
    var provider : Company? = Company()
    var company: Company? = Company()
    var isRandom : Boolean? = false
    var isFav : Boolean? = false
    var likeNumber : Long? = null
    var commentNumber : Long? = null
    var isVisible : String? = PrivacySetting.ONLY_ME.toString()
    var article : Article? = null
    var isEnabledToComment : Boolean? = false
}