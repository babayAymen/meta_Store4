package com.aymen.store.model.repository.remoteRepository.articleRepository

import android.util.Log
import com.aymen.metastore.model.entity.Dto.CommentDto
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.globalRepository.ServiceApi
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor
    (private val api: ServiceApi)
    :ArticleRepository
{
    override suspend fun getRandomArticles() = api.getRandomArticles()

    override suspend fun getRandomArticlesByCompanyCategory(categName : String) = api.getRandomArticlesByCompanyCategory(categName)
    override suspend fun getRandomArticlesByCategory(categoryId: Long, companyId : Long) = api.getRandomArticlesByCategory(categoryId, companyId)
    override suspend fun getRandomArticlesBySubCategory(
        subcategoryId: Long,
        companyId: Long
    ) = api.getRandomArticlesBySubCategory(subcategoryId, companyId)
  override suspend fun getAll(companyId : Long, offset : Int, pageSize : Int) = api.getAl(companyId = companyId, offset = offset, pageSize = pageSize)
    override suspend fun deleteArticle(id: String) =api.deleteArticle(id)
    override suspend fun addArticle(article: String, file: File): Response<Void> {
        Log.e("aymenbabayarticle","c bon ")
       return api.addArticle(article,
            file = MultipartBody.Part
                .createFormData(
                    "file",
                    file.name,
                    file.asRequestBody()
                )
        )
    }
    override suspend fun addArticleWithoutImage(article: String, articleId : Long) = api.addArticleWithoutImage(articleId,article)
    override suspend fun getAllArticlesContaining(search: String, searchType: SearchType) = api.getAllArticlesContaining(search,searchType)
    override suspend fun likeAnArticle(articleId: Long, isFav : Boolean) = api.likeAnArticle(articleId,isFav)
    override suspend fun sendComment(comment: String, articleId: Long) = api.sendComment(comment, articleId)
    override suspend fun getComments(articleId: Long): Response<List<CommentDto>> = api.getComments(articleId)
    override suspend fun getAllArticlesByCategor() = api.getAllArticlesByCategor()
    override suspend fun addQuantityArticle(quantity: Double, articleId: Long) = api.addQuantityArticle(quantity, articleId)

}