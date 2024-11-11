package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.store.model.entity.dto.CompanyDto

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

fun mapRoomCompanyToCompanyDto(company : com.aymen.metastore.model.entity.room.Company): CompanyDto{
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
        isVisible = company.isVisible,
        category = company.category,
        balance = company.balance,
        isPointsSeller = company.isPointsSeller,
        longitude = company.longitude,
        latitude = company.latitude,
        createdDate = company.createdDate,
        lastModifiedDate = company.lastModifiedDate,
        invoiceType = company.invoiceType,
        metaSeller = company.metaSeller,
    )
}

