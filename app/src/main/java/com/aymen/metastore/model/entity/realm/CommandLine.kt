package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.ArticleCompany
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class CommandLine :RealmObject {

    @PrimaryKey
    var id : Long ? = null

    var quantity: Double = 0.0

    var totTva: Double? = null

    var prixArticleTot: Double = 0.0

    var discount: Double? = null

    var article: ArticleCompany? = null

    var invoice: Invoice? = null

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}