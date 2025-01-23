package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class GetAllMyArticleContaining(private val repository: ArticleRepository) {

    operator fun invoke(libelle : String, searchType: SearchType, companyId : Long, asProvider : Boolean) : Flow<PagingData<ArticleCompany>> {
        return repository.getAllMyArticleContaining(libelle = libelle, searchType = searchType, companyId = companyId, asProvider = asProvider)

    }
}