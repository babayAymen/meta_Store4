package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.store.model.entity.dto.CommandLineDto



fun mapCommandLineToRoomCommandLine(commandLine : CommandLineDto): com.aymen.metastore.model.entity.room.CommandLine{
    return com.aymen.metastore.model.entity.room.CommandLine(
        id = commandLine.id,
        quantity = commandLine.quantity,
        totTva = commandLine.totTva,
        prixArticleTot = commandLine.prixArticleTot,
        discount = commandLine.discount,
        createdDate = commandLine.createdDate,
        lastModifiedDate = commandLine.lastModifiedDate,
        articleId = commandLine.article?.id,
        invoiceId = commandLine.invoice?.id
    )
}