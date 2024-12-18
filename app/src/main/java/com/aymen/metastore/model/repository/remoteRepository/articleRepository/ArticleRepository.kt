package com.aymen.store.model.repository.remoteRepository.articleRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.dto.CommentDto
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.room.entity.Article
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.metastore.model.entity.roomRelation.CommentWithArticleAndUserOrCompany
import com.aymen.metastore.model.entity.roomRelation.RandomArticleChild
import com.aymen.metastore.util.Resource
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.SearchType
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.File

interface ArticleRepository {
    suspend fun getRandomArticlesByCompanyCategory(categName : String): Response<List<ArticleCompanyDto>>
     suspend fun getRandomArticlesByCategory(categoryId : Long,  companyId : Long ) : Response<List<ArticleCompanyDto>>
    suspend fun getRandomArticlesBySubCategory(subcategoryId : Long , companyId: Long) : Response<List<ArticleCompanyDto>>

     fun getAllMyArticles(companyId: Long): Flow<PagingData<ArticleWithArticleCompany>>
     fun getRandomArticles(categoryName : CompanyCategory): Flow<PagingData<RandomArticleChild>>
     fun getArticleDetails(id : Long) : Flow<Resource<ArticleCompany>>
     fun getAllMyArticleContaining(libelle : String, searchType: SearchType, companyId : Long) : Flow<PagingData<ArticleCompanyDto>>
     fun getAllArticlesByCategor(companyId : Long, companyCategory: CompanyCategory): Flow<PagingData<Article>>
    suspend fun getArticleByBarcode(bareCode : String) : Response<ArticleCompanyDto>
    fun getAllCompanyArticles(companyId : Long) : Flow<PagingData<ArticleWithArticleCompany>>
    fun getArticlesByCompanyAndCategoryOrSubCategory(companyId : Long , categoryId: Long, subcategoryId: Long) : Flow<PagingData<ArticleCompanyDto>>
    suspend fun deleteArticle(id: Long): Response<Void>

    suspend fun addArticle(article:String, file : File):Response<Void>

    suspend fun addArticleWithoutImage(article: ArticleCompanyDto, articleId : Long):Response<ArticleCompanyDto>
    suspend fun getAllArticlesContaining(search : String, searchType: SearchType) : Response<List<ArticleCompanyDto>>
    suspend fun likeAnArticle(articleId : Long, isFav : Boolean) : Response<Void>
    suspend fun sendComment(comment : CommentDto) : Response<Void>
     fun getArticleComments(articleId : Long) : Flow<PagingData<CommentWithArticleAndUserOrCompany>>
    suspend fun addQuantityArticle(quantity : Double, articleId : Long) : Response<ArticleCompanyDto>
    suspend fun updateArticle(article : ArticleCompanyDto) : Response<ArticleCompanyDto>
}