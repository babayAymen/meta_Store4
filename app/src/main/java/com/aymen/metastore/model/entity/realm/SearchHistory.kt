package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.entity.realm.User
import com.aymen.store.model.Enum.SearchCategory
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class SearchHistory : RealmObject {

    @PrimaryKey var id : Long? = null

    var  company : Company? = null

    var article : ArticleCompany? = null

    var user : User? = null

    var searchCategory : String = SearchCategory.OTHER.toString()

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}