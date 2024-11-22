package com.aymen.metastore.model.usecase

import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.util.Resource
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class GetArticleDetails(private val repository: ArticleRepository) {

    operator fun invoke(id : Long) : Flow<Resource<ArticleCompany>>{
        return repository.getArticleDetails(id)

    }
}