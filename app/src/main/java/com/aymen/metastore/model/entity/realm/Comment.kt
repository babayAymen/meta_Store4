package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.entity.realm.User
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Comment :RealmObject {
     @PrimaryKey var id : Long? = null

     var content: String = ""

     var user: User? = null

     var company: Company? = null

     var article: ArticleCompany? = null

     var createdDate : String = ""

     var lastModifiedDate : String = ""
}