package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.Enum.InvoiceType
import com.aymen.metastore.model.entity.realm.User
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.UserDto
import com.aymen.store.model.entity.realm.Company

fun mapCompanyToCompanyDto(company: Company): CompanyDto {
    return CompanyDto(
        id = company.id,
        name = company.name,
        code = company.code,
        matfisc = company.matfisc,
        address = company.address,
        phone = company.phone,
        bankaccountnumber = company.bankaccountnumber,
        email = company.email,
        capital = company.capital,
        logo = company.logo,
        workForce = company.workForce,
        virtual = company.virtual,
        rate = company.rate,
        raters = company.raters,
        isVisible = PrivacySetting.valueOf(company.isVisible!!),
        category = CompanyCategory.valueOf(company.category!!),
        balance = company.balance,
        isPointsSeller = company.isPointsSeller,
        user = mapUserToUserDto(company.user?: User()),
        longitude = company.longitude,
        latitude = company.latitude,
        createdDate = company.createdDate,
        lastModifiedDate = company.lastModifiedDate,
        invoiceType = InvoiceType.valueOf(company.invoiceType!!),
        metaSeller = company.metaSeller,
    )
}

fun mapcompanyDtoToCompanyRealm(company: CompanyDto): Company{
    return Company().apply {
        id = company.id
        name = company.name
        code = company.code
        matfisc = company.matfisc
        address = company.address
        phone = company.phone
        bankaccountnumber = company.bankaccountnumber
        email = company.email
        capital = company.capital
        logo = company.logo
        workForce = company.workForce?:0
        virtual = company.virtual!!
        rate = company.rate
        raters = company.raters
        isVisible = company.isVisible.toString()
        category = company.category.toString()
        balance = company.balance
        isPointsSeller = company.isPointsSeller
        metaSeller = company.metaSeller
        user = mapUserDtoToUserRealm(company.user?:UserDto())
        longitude = company.longitude
        latitude = company.latitude
        createdDate = company.createdDate
        lastModifiedDate = company.lastModifiedDate
        invoiceType = company.invoiceType.toString()
    }
}

fun mapCompanyToRoomCompany(company : CompanyDto?): com.aymen.metastore.model.entity.room.Company{
    return com.aymen.metastore.model.entity.room.Company(
        id = company?.id,
        name = company?.name!!,
        code = company.code,
        matfisc = company.matfisc,
        address = company.address,
        phone = company.phone,
        bankaccountnumber = company.bankaccountnumber,
        email = company.email,
        capital = company.capital,
        logo = company.logo,
        workForce = company.workForce,
        virtual = company.virtual,
        rate = company.rate,
        raters = company.raters,
        isVisible = company.isVisible,
        category = company.category,
        balance = company.balance,
        isPointsSeller = company.isPointsSeller,
        userId = company.user?.id,
        longitude = company.longitude,
        latitude = company.latitude,
        createdDate = company.createdDate,
        lastModifiedDate = company.lastModifiedDate,
        invoiceType = company.invoiceType,
        metaSeller = company.metaSeller,
    )
}

