package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class GetRandomArticle(private val repository: ArticleRepository) {

    operator fun invoke(categoryName : CompanyCategory, companyId : Long?): Flow<PagingData<ArticleCompany>> {
        return repository.getRandomArticles(categoryName, companyId)
    }

}