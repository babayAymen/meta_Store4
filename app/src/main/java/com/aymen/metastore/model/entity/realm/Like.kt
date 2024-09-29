package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.User
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Like : RealmObject {

    @PrimaryKey var id : Long? = null

    var users: RealmList<User> = realmListOf(User())

     var companies: RealmList<Company> = realmListOf(Company())

     var article: Article? = null

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}