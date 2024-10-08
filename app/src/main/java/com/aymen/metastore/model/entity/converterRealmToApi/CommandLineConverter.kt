package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.store.model.entity.api.CommandLineDto
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToDto
import com.aymen.store.model.entity.converterRealmToApi.mapRealmArticleToApi
import com.aymen.store.model.entity.realm.CommandLine

fun mapCommandLineToCommandLineDto(commandLine :CommandLine) : CommandLineDto{
    return CommandLineDto(
        id = commandLine.id,
        quantity = commandLine.quantity,
        totTva = commandLine.totTva,
        prixArticleTot = commandLine.prixArticleTot,
        discount = commandLine.discount,
        createdDate = commandLine.createdDate,
        lastModifiedDate = commandLine.lastModifiedDate,
        article = mapArticleCompanyToDto(commandLine.article!!),
        invoice = mapInvoiceToInvoiceDto(commandLine.invoice!!)

    )
}