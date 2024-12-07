package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.metastore.util.Resource
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class GetAllMyArticleContaining(private val repository: ArticleRepository) {

    operator fun invoke(libelle : String, searchType: SearchType, companyId : Long) : Flow<PagingData<ArticleCompanyDto>> {
        return repository.getAllMyArticleContaining(libelle = libelle, searchType = searchType, companyId = companyId)

    }
}