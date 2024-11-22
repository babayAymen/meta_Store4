package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class GetPagingArticleCompanyByCompany(private val repository : ArticleRepository) {
     operator fun invoke(companyId: Long): Flow<PagingData<ArticleWithArticleCompany>> {
        return repository.getAllMyArticles(companyId = companyId)
    }
}