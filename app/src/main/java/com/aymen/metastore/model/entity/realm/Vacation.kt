package com.aymen.store.model.entity.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.Date

class Vacation : RealmObject {

    @PrimaryKey
    var id : Long ? = null
    var year = 0

    var startdate: String? = null

    var enddate: String? = null

    var worker: Worker? = null

    var createdDate : String = ""

    var lastModifiedDate : String = ""

}