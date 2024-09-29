package com.aymen.store.model.entity.api

import com.aymen.metastore.model.Enum.InvoiceType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.PrivacySetting

data class CompanyDto(
    var id : Long? = null,
    var name: String = "",
    var code: String? = "",
    var matfisc: String? = "",
    var address: String? = "",
    var phone: String? = "",
    var bankaccountnumber: String? = "",
    var email: String? = "",
    var capital: String? = "",
    var logo: String? = "",
    var workForce: Int? = 0,
    var virtual : Boolean? = false,
    var rate: Double? = 0.0,
    var raters: Int? = 0,
    var user: UserDto? = null,
    var parentCompany: CompanyDto? = null,
    var category : CompanyCategory? = CompanyCategory.DAIRY,
    var balance : Double? = 0.0,
    var isPointsSeller : Boolean? = false,
    var longitude : Double? = 0.0,
    var latitude : Double? = 0.0,
    var isVisible: PrivacySetting = PrivacySetting.PUBLIC,
    var createdDate : String = "",
    var lastModifiedDate : String = "",
    var invoiceType : InvoiceType = InvoiceType.NOT_SAVED
)
