package com.aymen.store.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.Dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.room.ArticleCompany
import com.aymen.store.model.entity.dto.ArticleDto
import com.aymen.store.model.entity.dto.CompanyDto


fun mapArticelDtoToRoomArticle(art : ArticleDto):com.aymen.metastore.model.entity.room.Article{
    return com.aymen.metastore.model.entity.room.Article(
        libelle = art.libelle,
        code = art.code,
        discription = art.discription,
        barcode = art.barcode,
        tva = art.tva,
        image = art.image,
        id = art.id,
        category = art.category
    )
}

fun mapRoomArticleToArticleDto(art : ArticleCompany): ArticleCompanyDto{
    return ArticleCompanyDto(
        id = art.id,
        cost = art.cost,
        quantity = art.quantity,
        minQuantity = art.minQuantity,
        sharedPoint = art.sharedPoint,
        margin = art.sellingPrice,
        sellingPrice = art.sellingPrice,
        isEnabledToComment = art.isEnabledToComment,
        company = CompanyDto(id = art.companyId)
    )
}

fun mapArticleCompanyToRoomArticleCompany(art : ArticleCompanyDto?): ArticleCompany{
    return ArticleCompany(
        id = art?.id,
        cost = art?.cost,
        quantity = art?.quantity,
        minQuantity = art?.minQuantity,
        sharedPoint = art?.sharedPoint,
        sellingPrice = art?.sellingPrice,
        companyId = art?.company?.id,
        categoryId = art?.category?.id,
        subCategoryId = art?.subCategory?.id,
        providerId = art?.provider?.id,
        articleId = art?.article?.id,
        isEnabledToComment = art?.isEnabledToComment
    )
}







