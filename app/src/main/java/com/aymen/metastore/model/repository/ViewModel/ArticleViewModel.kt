package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.room.Transaction
import com.aymen.metastore.model.entity.model.Article
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.entity.Comment
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val sharedViewModel : SharedViewModel,
    private val room : AppDatabase,
    private val useCases: MetaUseCases,
    private val appViewModel: AppViewModel
)
    : ViewModel() {

    val company: StateFlow<Company?> = sharedViewModel.company
    val user: StateFlow<User?> = sharedViewModel.user
    var companyId by mutableLongStateOf(0)
    private val _adminArticles : MutableStateFlow<PagingData<ArticleCompany>> = MutableStateFlow(PagingData.empty())
    var adminArticles : StateFlow<PagingData<ArticleCompany>> = _adminArticles

    private val _companyArticles : MutableStateFlow<PagingData<ArticleCompany>> = MutableStateFlow(PagingData.empty())
    var companyArticles : StateFlow<PagingData<ArticleCompany>> = _companyArticles

//    private val _articlesByArticleId = MutableStateFlow<Map<Long, Article>>(emptyMap())
//    val articlesByArticleId: StateFlow<Map<Long, Article>> = _articlesByArticleId

    private val _userComment = MutableStateFlow(User())
    val userComment: StateFlow<User> = _userComment

    private val _companyComment = MutableStateFlow(Company())
    val companyComment: StateFlow<Company> = _companyComment

    private val _randomArticles : MutableStateFlow<PagingData<ArticleCompany>> = MutableStateFlow(PagingData.empty())
    val randomArticles: StateFlow<PagingData<ArticleCompany>> = _randomArticles

    private val _articles : MutableStateFlow<PagingData<Article>> = MutableStateFlow(PagingData.empty())
    val articles: StateFlow<PagingData<Article>> = _articles

    private val _searchArticles : MutableStateFlow<PagingData<ArticleCompany>> = MutableStateFlow(adminArticles.value)
    var searchArticles : StateFlow<PagingData<ArticleCompany>> = _searchArticles

    private val _articleCompany : MutableStateFlow<ArticleCompany?> = MutableStateFlow(ArticleCompany())
    var articleCompany : StateFlow<ArticleCompany?>  = _articleCompany

    var article by mutableStateOf(Article())
    var allComments by mutableStateOf(emptyList<Comment>())
    var myComment by mutableStateOf("")
    var upDate by mutableStateOf(false)
    init {

            fetchRandomArticlesForHomePage(categoryName = CompanyCategory.DAIRY)
//        if(sharedViewModel.accountType == AccountType.COMPANY) {
//            sharedViewModel.company.value.id?.let {
//            fetchAllMyArticlesApi(it)
//            }
//        }
    }

     fun fetchAllMyArticlesApi(companyId: Long) {
        viewModelScope.launch {
            useCases.getPagingArticleCompanyByCompany(companyId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect  {
                    _adminArticles.value = it.map { article -> article.toArticleRelation() }
                }
        }
    }

     fun fetchRandomArticlesForHomePage(categoryName : CompanyCategory) {
        viewModelScope.launch{
            useCases.getRandomArticle(categoryName = categoryName)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {pagingData ->
                    _randomArticles.value = pagingData.map { article ->
                        article.toArticleRelation()
                    }
                }
        }
    }
        fun getArticleDetails(id: Long) {
            viewModelScope.launch(Dispatchers.IO) {
                useCases.getArticleDetails(id).collect {
                    _articleCompany.value = it.data!!
                }
            }
        }

        fun addQuantityArticle(quantity: Double, articleId: Long) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                  repository.addQuantityArticle(quantity, articleId)
                } catch (ex: Exception) {
                    Log.e("addQuantityArticle", "exception is : ${ex.message}")
                }
            }
        }

    fun assignarticleCompany(item : ArticleCompany){
        _articleCompany.value = item
        upDate = true
    }
        fun getAllMyArticleContaining(articleLibel: String, searchType: SearchType) {
            viewModelScope.launch {
                useCases.getAllMyArticleContaining(articleLibel, searchType, company.value?.id!!)
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    .collect {
                        _searchArticles.value = it.map {article -> article.toArticleCompanyModel() }
                }
            }
        }



    fun assignArticleCompany(art : ArticleCompany){
        _articleCompany.value = art
    }

        fun getRandomArticlesByCategory(categoryId: Long, companyId: Long) {
            viewModelScope.launch {

            }
        }

        fun getRandomArticlesBySubCategory(subCategoryId: Long, companyId: Long) {
            viewModelScope.launch(Dispatchers.IO) {

            }
        }

        fun addArticleWithoutImage(articlee: ArticleCompany, articl: String) {
            viewModelScope.launch(Dispatchers.IO) {
                val result: Result<Response<Void>> = runCatching {
                    repository.addArticleWithoutImage(articl, article.id ?: 0L)
                }
                result.fold(
                    onSuccess = { success ->
                        if (success.isSuccessful) {
                        } else {
                            // Handle unsuccessful response
                        }
                    },
                    onFailure = { exception ->
                        Log.e("addnewarticle", "exception is : ${exception.message}")
                    }
                )
            }
        }

    val articleCompanyDao = room.articleCompanyDao()

    fun updateArticle(article : ArticleCompany){
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.updateArticle(article.toArticleCompanyDto())
            if(response.isSuccessful){

                appViewModel.updateShow("article")
            }

        }
    }

        fun makeItAsFav(article: ArticleCompany) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    article.id?.let { repository.likeAnArticle(it, !article.isFav!!) }
                    room.articleCompanyDao().chageIsFav(article.id!!, !article.isFav!!)
                }
            }
        }

        fun sendComment(comment: String, articleId: Long) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.sendComment(comment, articleId)
                }
            }
        }

        fun getAllArticleComments() {
            viewModelScope.launch(Dispatchers.IO) {

            }
        }

    fun getArticlesForCompanyByCompanyCategory(companyId : Long, companyCategory: CompanyCategory) {
        viewModelScope.launch {
            useCases.getArticlesForCompanyByCompanyCategory(companyId, companyCategory)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _articles.value = it.map { article -> article.toArticle() }
                }
        }
    }

    fun getAllCompanyArticles(companyId : Long){
        viewModelScope.launch {
            useCases.getAllCompanyArticles(companyId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                   _companyArticles.value = it.map { article -> article.toArticleRelation() }
                }
        }
    }

}
