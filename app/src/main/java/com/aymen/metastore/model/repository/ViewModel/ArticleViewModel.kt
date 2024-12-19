package com.aymen.metastore.model.repository.ViewModel

import android.content.Context
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
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.model.Article
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.Comment
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleRemoteKeysEntity
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
    private val appViewModel: AppViewModel,
    private val context: Context
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

    private val _commentArticle : MutableStateFlow<PagingData<Comment>> = MutableStateFlow(PagingData.empty())
    val commentArticle : StateFlow<PagingData<Comment>> get() = _commentArticle

    var article by mutableStateOf(Article())
    var myComment by mutableStateOf("")
    var upDate by mutableStateOf(false)
    init {

            fetchRandomArticlesForHomePage(categoryName = CompanyCategory.DAIRY)
        viewModelScope.launch {
        sharedViewModel.accountType.collect { accountType ->
                    if (accountType == AccountType.COMPANY) {
                        sharedViewModel.company.collect { company ->
                            company.id?.let { companyId ->
                                fetchAllMyArticlesApi(companyId)
                                getArticlesForCompanyByCompanyCategory(companyId, company.category!!)
                            }
                        }
                    }
                }
        }
    }

     fun fetchAllMyArticlesApi(companyId: Long) {
        viewModelScope.launch {
            useCases.getPagingArticleCompanyByCompany(companyId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest  {
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
                    _adminArticles.value = PagingData.empty()
                   articleCompanyDao.upDateQuantity(articleId, quantity)
                   repository.addQuantityArticle(quantity, articleId)

                } catch (ex: Exception) {
                    Log.e("addQuantityArticle", "exception is : ${ex.message}")
                }
            }
        }

    fun assignarticleCompany(item : ArticleCompany){
        _articleCompany.value = item
        article = item.article!!
        upDate = true
    }

    fun deleteArticle(article: ArticleCompany){
        viewModelScope.launch(Dispatchers.IO) {
            val remoteKey = articleCompanyDao.getArticleRemoteKeysById(article.id!!)
            val inventory = inventoryDao.getInventoryByArticleId(article.id)
            val remoteKeysInventory = inventory?.id?.let {
             inventoryDao.getInventoryRemoteKey(it)
            }
            room.withTransaction {
                articleDao.updateArticleById(false , article.article?.id!!)
            articleCompanyDao.clearArticleById(article.id)
            articleCompanyDao.clearRemoteKeyById(article.id)
                if(inventory != null){
                inventoryDao.clearInventoryByArticleId(article.id)
                inventoryDao.clearInventoryRemoteKeysById(inventory.id!!)
                }
            }
            val response = repository.deleteArticle(article.id)
            if(!response.isSuccessful){
                articleCompanyDao.insertSigleArticle(article.toArticleCompanyEntity(true))
                articleCompanyDao.insertSingleKey(remoteKey)
            }
        }
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

        fun getRandomArticlesByCategory(categoryId: Long, companyId: Long, subCategoryId: Long) {
            viewModelScope.launch {
                useCases.getArticlesByCompanyAndCategoryOrSubCategory(companyId, categoryId, subCategoryId)
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    .collect{
                        _companyArticles.value = it.map { article -> article.toArticleCompanyModel() }
                    }
            }
        }



        fun addArticleCompany(articlee: ArticleCompany) {
            viewModelScope.launch(Dispatchers.IO) {
                var remoteKey = ArticleRemoteKeysEntity(0,0,0)
                 var id = 0L
                room.withTransaction{
                    val latestArticleId = articleCompanyDao.getLatestArticleId()
                 id = if (latestArticleId != null) latestArticleId + 1 else 1
                    room.articleDao().updateArticleById(true,articlee.article?.id!!)
                    val articleCount = articleCompanyDao.getArticlesCount()
                    val page = articleCount.div(PAGE_SIZE)
                 remoteKey = ArticleRemoteKeysEntity(
                    id = id,
                    previousPage = if(page == 0) null else page-1,
                    nextPage = page + 1
                )
                    try {

                        room.articleCompanyDao().insertSigleArticle(articlee.copy(id = id).toArticleCompanyEntity(isSync = false))
                        room.articleCompanyDao().insertSingleKey(remoteKey)
                    }catch (ex : Exception){
                        Log.e("azertyiiopo","exc : $ex")
                    }
                }
                val result: Result<Response<ArticleCompanyDto>> = runCatching {
                    repository.addArticleWithoutImage(articlee.toArticleCompanyDto(), article.id ?: 0L)
                }
                result.fold(
                    onSuccess = { success ->
                        if (success.isSuccessful) {
                            room.withTransaction {
                                val serverArticle = success.body()!!
                                Log.e("articleviewlodel","id is $id")
                                val latestRemoteKey = articleCompanyDao.getArticleRemoteKey(id)
                                val remoteKeys = ArticleRemoteKeysEntity(
                                    id = serverArticle.id!!,
                                    previousPage = latestRemoteKey.previousPage,
                                    nextPage = null
                                )
                                room.articleCompanyDao().insertSigleArticle(serverArticle.toArticleCompany(true))
                                room.articleCompanyDao().insertSingleKey(remoteKeys)
                            }
                            inventoryDao.getAllInventories().invalidate()
                            appViewModel.updateShow("article")
                        } else {
                            room.articleCompanyDao().insertSigleArticle(articlee.toArticleCompanyEntity(true))
                            room.articleCompanyDao().insertSingleKey(remoteKey)
                        }
                    },
                    onFailure = { exception ->
//                        withContext(Dispatchers.IO){
//                        Toast.makeText(context, "something went wrong : ${exception.message}", Toast.LENGTH_SHORT).show()
//                        }
                    }
                )
            }
        }

    val articleCompanyDao = room.articleCompanyDao()
    val articleDao = room.articleDao()
    val inventoryDao = room.inventoryDao()
    fun updateArticle(article : ArticleCompany){
        viewModelScope.launch(Dispatchers.IO) {
           val articleCompanyPrev = article
            room.articleCompanyDao().insertSigleArticle(article.toArticleCompanyEntity(isSync = true))
            val response = repository.updateArticle(article.toArticleCompanyDto())
            if(response.isSuccessful){

                appViewModel.updateShow("article")
            }else{
                room.articleCompanyDao().insertSigleArticle(articleCompanyPrev.toArticleCompanyEntity(isSync = true))
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

        fun sendComment(comment : Comment) {
            viewModelScope.launch(Dispatchers.IO) {

                    repository.sendComment(comment.toCommentDto())

            }
        }

        fun getAllArticleComments() {
            viewModelScope.launch(Dispatchers.IO) {
                    useCases.getArticleComment(articleCompany.value?.id!!)
                        .distinctUntilChanged()
                        .cachedIn(viewModelScope)
                        .collect{
                            _commentArticle.value = it.map { comment -> comment.toCommentModel() }
                        }
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
