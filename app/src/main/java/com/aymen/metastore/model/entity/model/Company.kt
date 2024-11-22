package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.Enum.InvoiceType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.PrivacySetting

data class Company (

    var id : Long? = null,
    var name: String = "",
    var code: String? = "",
    var matfisc: String? = "",
    var address: String? = "",
    var phone: String? = "",
    var bankaccountnumber: String? = "",
    var email: String? = "",
    var capital: String? = "",
    val logo: String? = "",
    val workForce: Int? = 0,
    val virtual : Boolean? = false,
    val rate: Double? = 0.0,
    val raters: Int? = 0,
    val user: User? = null,
    val parentCompany: Company? = null,
    var category : CompanyCategory? = CompanyCategory.DAIRY,
    val balance : Double? = 0.0,
    val isPointsSeller : Boolean? = false,
    val metaSeller : Boolean? = false,
    var longitude : Double? = 0.0,
    var latitude : Double? = 0.0,
    val isVisible: PrivacySetting? = PrivacySetting.PUBLIC,
    val createdDate : String? = "",
    val lastModifiedDate : String? = "",
    val invoiceType : InvoiceType? = InvoiceType.NOT_SAVED

)
