package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.Enum.InvoiceType
import com.aymen.metastore.model.entity.room.entity.Company
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
    val logo: String? = "",
    val workForce: Int? = 0,
    val virtual : Boolean? = false,
    val rate: Double? = 0.0,
    val raters: Int? = 0,
    val user: UserDto? = null,
    val parentCompany: CompanyDto? = null,
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
){
    fun toCompany() : Company {

        return Company(
            companyId = id,
            name = name,
            code = code,
            matfisc = matfisc,
            address = address,
            phone = phone,
            bankaccountnumber = bankaccountnumber,
            email = email,
            capital = capital,
            logo = logo,
            workForce = workForce,
            virtual = virtual,
            rate = rate,
            raters = raters,
            userId = user?.id,
            parentCompanyId = parentCompany?.id,
            category = category,
            balance = balance,
            isPointsSeller = isPointsSeller,
            metaSeller = metaSeller,
            longitude = longitude,
            latitude = latitude,
            isVisible = isVisible,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            invoiceType = invoiceType

        )
    }
    fun toCompanyModel() : com.aymen.metastore.model.entity.model.Company {

        return com.aymen.metastore.model.entity.model.Company(
            id = id,
            name = name,
            code = code,
            matfisc = matfisc,
            address = address,
            phone = phone,
            bankaccountnumber = bankaccountnumber,
            email = email,
            capital = capital,
            logo = logo,
            workForce = workForce,
            virtual = virtual,
            rate = rate,
            raters = raters,
            user = user?.toUserModel(),
//            parentCompany = parentCompany?.id,
            category = category,
            balance = balance,
            isPointsSeller = isPointsSeller,
            metaSeller = metaSeller,
            longitude = longitude,
            latitude = latitude,
            isVisible = isVisible,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            invoiceType = invoiceType

        )
    }
}
