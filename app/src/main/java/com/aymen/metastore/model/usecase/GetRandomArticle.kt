package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class GetRandomArticle(private val repository: ArticleRepository) {

    operator fun invoke(categoryName : CompanyCategory): Flow<PagingData<ArticleWithArticleCompany>> {
        return repository.getRandomArticles(categoryName)
    }

}