package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.ArticleCompany
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Inventory : RealmObject {
    @PrimaryKey
    var id : Long? = null

    var article : ArticleCompany? = null
    var quantityIn : Double = 0.0
    var quantityOut : Double = 0.0
    var articleCost : Double = 0.0
    var articleSelling : Double = 0.0
    var discountOut : Double = 0.0
    var discointIn : Double = 0.0

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}