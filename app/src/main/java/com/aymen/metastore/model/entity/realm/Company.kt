package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.Enum.InvoiceType
import com.aymen.metastore.model.entity.realm.User
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.PrivacySetting
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey

class Company : RealmObject {

    @PrimaryKey
    var id : Long? = null
    var name: String = ""
    var code: String? = ""
    var matfisc: String? = ""
    var address: String? = ""
    var phone: String? = ""
    var bankaccountnumber: String? = ""
    var email: String? = ""
    var capital: String? = ""
    var logo: String? = ""
    var workForce: Int? = 0
    var virtual : Boolean? = false
    var rate: Double? = 0.0
    var raters: Int? = 0
    var isVisible: String? = PrivacySetting.PUBLIC.toString()
    var category : String? = CompanyCategory.DAIRY.toString()
    var balance : Double? = 0.0
    var isPointsSeller: Boolean? = false
    var user : User? = null
    var longitude : Double? = 0.0
    var latitude : Double? = 0.0
    var metaSeller : Boolean? = false
    var createdDate : String? = ""
    var lastModifiedDate : String? = ""
    var invoiceType : String? = InvoiceType.NOT_SAVED.toString()
//    var parentCompany: com.aymen.metastore.model.entity.room.Company? = null
    // var branshes: Set<com.aymen.metastore.model.entity.room.Company> = emptySet()
override fun toString(): String {
    return "com.aymen.metastore.model.entity.room.Company(id=$id, name=$name, address=$address, phone=$phone, email=$email)"
}
}