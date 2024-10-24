package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.Dto.SearchHistoryDto
import com.aymen.metastore.model.entity.room.SearchHistory

fun mapSearchToSearchRoom(search : SearchHistoryDto): SearchHistory{
    return SearchHistory(
     id = search.id,
     companyId = search.company?.id,
     articleId = search.article?.id,
     userId = search.user?.id,
     searchCategory = search.searchCategory,
     createdDate = search.createdDate,
     lastModifiedDate = search.lastModifiedDate
    )
}