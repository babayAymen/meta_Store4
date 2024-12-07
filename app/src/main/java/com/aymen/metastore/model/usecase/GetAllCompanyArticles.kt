package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class GetAllCompanyArticles(private val repository : ArticleRepository) {

    operator fun invoke(companyId : Long) : Flow<PagingData<ArticleWithArticleCompany>>{
        return repository.getAllCompanyArticles(companyId)
    }
}