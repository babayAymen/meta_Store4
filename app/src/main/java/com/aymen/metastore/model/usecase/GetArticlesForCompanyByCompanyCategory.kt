package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.room.entity.Article
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class GetArticlesForCompanyByCompanyCategory(private val repository: ArticleRepository) {

    operator fun invoke(companyId : Long, companyCategory: CompanyCategory) : Flow<PagingData<Article>>{
        return repository.getAllArticlesByCategor(companyId = companyId, companyCategory = companyCategory)
    }
}