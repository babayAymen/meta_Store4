package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.Enum.MessageType
import com.aymen.metastore.model.entity.realm.User
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Conversation : RealmObject {

    @PrimaryKey var id : Long? = null

    var user1: User? = null

    var user2: User? = null

    var company1 : Company? = null

    var company2 : Company? = null
//    var message: RealmList<Message> = realmListOf()

    var type : String? = MessageType.USER_SEND_COMPANY.toString()

    var lastMessage : String = ""

    var message : String ? = ""

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}