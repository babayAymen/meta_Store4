package com.aymen.store.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.Dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToCompanyDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapcompanyDtoToCompanyRealm
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.store.model.entity.dto.ArticleDto
import com.aymen.store.model.entity.realm.Article

fun mapRealmArticleToApi(articleRealm: Article): ArticleDto {
    return ArticleDto(
        libelle = articleRealm.libelle,
        code = articleRealm.code,
//        unit = articleRealm.unit ?: UnitArticle.U,
        discription = articleRealm.discription,
        barcode = articleRealm.barcode,
        tva = articleRealm.tva,
       image = articleRealm.image,
        id = articleRealm.id,
    )
}

fun mapApiArticleToRealm(article: ArticleDto): Article{
    return Article().apply {
        libelle = article.libelle
        code = article.code
//        unit = articleDto.unit ?: UnitArticle.U,
        discription = article.discription
        barcode = article.barcode
        tva = article.tva
        image = article.image
        id = article.id
    }
}

fun  mapArticleCompanyToDto(art : ArticleCompany):ArticleCompanyDto{
    return ArticleCompanyDto(
        id = art.id,
        cost = art.cost,
        quantity = art.quantity,
        minQuantity = art.minQuantity,
        sharedPoint = art.sharedPoint,
        margin = art.sellingPrice,
        sellingPrice = art.sellingPrice,
        company = mapCompanyToCompanyDto(art.company!!),
        article = mapRealmArticleToApi(art.article!!),
        isEnabledToComment = art.isEnabledToComment
    )
}

fun mapArticleCompanyToRealm(art : ArticleCompanyDto): ArticleCompany{
    return ArticleCompany().apply {
        id = art.id
        cost = art.cost
        quantity = art.quantity
        minQuantity = art.minQuantity
        sharedPoint = art.sharedPoint
        sellingPrice = art.sellingPrice
        company = mapcompanyDtoToCompanyRealm(art.company)
        article = mapApiArticleToRealm(art.article)
        isEnabledToComment = art.isEnabledToComment
    }
}

// room espace

fun mapArticelDtoToRoomArticle(art : ArticleDto):com.aymen.metastore.model.entity.room.Article{
    return com.aymen.metastore.model.entity.room.Article(
        libelle = art.libelle,
        code = art.code,
        discription = art.discription,
        barcode = art.barcode,
        tva = art.tva,
        image = art.image,
        id = art.id,
    )
}

fun mapRoomArticleToArticleDto(art : com.aymen.metastore.model.entity.room.Article): ArticleDto{
    return ArticleDto(
        libelle = art.libelle,
        code = art.code,
        discription = art.discription,
        barcode = art.barcode,
        tva = art.tva,
        image = art.image,
        id = art.id,
    )
}

fun mapArticleCompanyToRoomArticleCompany(art : ArticleCompanyDto?): com.aymen.metastore.model.entity.room.ArticleCompany{
    return com.aymen.metastore.model.entity.room.ArticleCompany(
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







