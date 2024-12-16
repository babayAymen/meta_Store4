package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.CommentDto
import com.aymen.metastore.model.entity.model.Comment
import com.aymen.metastore.model.entity.roomRelation.CommentWithArticleAndUserOrCompany
import com.aymen.store.model.repository.remoteRepository.articleRepository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class GetArticleComment(private val repository: ArticleRepository) {

    operator fun invoke(articleId : Long ): Flow<PagingData<CommentWithArticleAndUserOrCompany>>{
        return repository.getArticleComments(articleId)
    }
}