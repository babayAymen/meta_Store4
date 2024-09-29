package com.aymen.store.model.repository.remoteRepository.articleRepository

import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Comment
import retrofit2.Response
import java.io.File

interface ArticleRepository {
    suspend fun getRandomArticles(): Response<List<ArticleCompany>>

    suspend fun getRandomArticlesByCompanyCategory(categName : String): Response<List<ArticleCompany>>

    suspend fun getAll(companyId : Long, offset : Int, pageSize : Int): Response<List<ArticleCompany>>

    suspend fun deleteArticle(id: String): Response<Void>

    suspend fun addArticle(article:String, file : File):Response<Void>

    suspend fun addArticleWithoutImage(article: String, articleId : Long):Response<Void>

//    suspend fun getAllMyArticleContaining(articleLibel : String) : Response<List<Article>>

    suspend fun getAllArticlesContaining(search : String, searchType: SearchType) : Response<List<ArticleCompany>>

    suspend fun likeAnArticle(articleId : Long, isFav : Boolean) : Response<Void>

    suspend fun sendComment(comment : String, articleId : Long) : Response<Void>

    suspend fun getComments(articleId : Long) : Response<List<Comment>>

    suspend fun getAllArticlesByCategory():Response<List<Article>>

    suspend fun addQuantityArticle(quantity : Double, articleId : Long) : Response<Void>
}