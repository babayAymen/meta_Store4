package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.SubArticleModel
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class GetArticlesChilds(private val repository: ArticleRepository) {

    operator fun invoke(parentId : Long): Flow<PagingData<SubArticleModel>>{
        return repository.getArticlesChilds(parentId)
    }
}