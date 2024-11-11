package com.aymen.store.model.repository.remoteRepository.articleRepository

import com.aymen.metastore.model.entity.Dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.Dto.CommentDto
import com.aymen.metastore.model.entity.room.Article
import com.aymen.store.model.Enum.SearchType
import retrofit2.Response
import java.io.File

interface ArticleRepository {
    suspend fun getRandomArticles(): Response<List<ArticleCompanyDto>>
    suspend fun getRandomArticlesByCompanyCategory(categName : String): Response<List<ArticleCompanyDto>>
     suspend fun getRandomArticlesByCategory(categoryId : Long,  companyId : Long ) : Response<List<ArticleCompanyDto>>
    suspend fun getRandomArticlesBySubCategory(subcategoryId : Long , companyId: Long) : Response<List<ArticleCompanyDto>>
    suspend fun getAll(companyId : Long, offset : Int, pageSize : Int): Response<List<ArticleCompanyDto>>

    suspend fun deleteArticle(id: String): Response<Void>

    suspend fun addArticle(article:String, file : File):Response<Void>

    suspend fun addArticleWithoutImage(article: String, articleId : Long):Response<Void>
    suspend fun getAllArticlesContaining(search : String, searchType: SearchType) : Response<List<ArticleCompanyDto>>
    suspend fun likeAnArticle(articleId : Long, isFav : Boolean) : Response<Void>
    suspend fun sendComment(comment : String, articleId : Long) : Response<Void>
    suspend fun getComments(articleId : Long) : Response<List<CommentDto>>
    suspend fun getAllArticlesByCategor():Response<List<Article>>
    suspend fun addQuantityArticle(quantity : Double, articleId : Long) : Response<Void>
}