package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.entity.Dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapCategoryToRoomCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapCommentToRoomComment
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapSubCategoryToRoomSubCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.Article
import com.aymen.metastore.model.entity.room.ArticleCompany
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.room.User
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.entity.converterRealmToApi.mapArticelDtoToRoomArticle
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRoomArticleCompany
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repository: GlobalRepository,
//    private val realm : Realm,
    private val sharedViewModel : SharedViewModel,
    private val room : AppDatabase
)
    : ViewModel() {

    var companyId by mutableLongStateOf(0)
    private val _adminArticles = MutableStateFlow<List<ArticleCompany>>(emptyList())
    var adminArticles : StateFlow<List<ArticleCompany>> = _adminArticles

    private val _articlesByArticleId = MutableStateFlow<Map<Long, Article>>(emptyMap())
    val articlesByArticleId: StateFlow<Map<Long, Article>> = _articlesByArticleId


    private val _userComment = MutableStateFlow(User())
    val userComment: StateFlow<User> = _userComment

    private val _companyComment = MutableStateFlow(Company())
    val companyComment: StateFlow<Company> = _companyComment


    private val _randomArticles = MutableStateFlow<List<ArticleWithArticleCompany>>(emptyList())
    val randomArticles: StateFlow<List<ArticleWithArticleCompany>> = _randomArticles

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles

    private val _articleFlow = MutableStateFlow("")
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val response : Flow<List<ArticleCompanyDto>> = _articleFlow
        .debounce(300)
        .filter { query ->
            query.isNotEmpty()
        }
        .flatMapLatest { query ->
            getAllArticlesContainingFromServer(query,SearchType.MY)
        }
        .catch { e -> emit(emptyList()) }


    fun addQuantityArticle(quantity : Double , articleId : Long){
       viewModelScope.launch(Dispatchers.IO) {
           try {
               val response = repository.addQuantityArticle(quantity, articleId)
               if (response.isSuccessful) {

               }
           } catch (ex: Exception) {
               Log.e("addQuantityArticle", "exception is : ${ex.message}")
           }
       }
    }


    fun getAllArticlesByCategory(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                 repository.getAllArticlesByCategor().also {
                    it.body()?.forEach { art ->

                        room.articleDao().insertArticle(art)
                    }


                }
            }catch (ex : Exception){
                Log.e("getAllArticlesByCategory","exception: $ex")
            }
                Log.e("getAllArticlesByCategory","exception: ${sharedViewModel.company.value.category!!}")
            _articles.value = room.articleDao().getAllArticlesByCategory(sharedViewModel.company.value.category!!)
        }
    }

    fun getAllMyArticleContaining(articleLibel : String){
        _articleFlow.value = articleLibel
    }

    private fun getAllArticlesContainingFromServer(articleLibel : String,searchType :SearchType):Flow<List<ArticleCompanyDto>>{
        return flow {
            delay(500)
            emit(repository.getAllArticlesContaining(articleLibel,searchType).body()?: emptyList())
        }
    }





    var searchArticles by mutableStateOf(emptyList<ArticleCompany>())
    private var offset by mutableIntStateOf(1)
    private var pageSize by mutableIntStateOf(1)
    var articleCompany by mutableStateOf(ArticleCompany())
    var article by mutableStateOf(Article())
    var allComments by mutableStateOf(emptyList<com.aymen.metastore.model.entity.room.Comment>())
    var myComment by mutableStateOf("")

    fun clearArticles() {
        _randomArticles.value = emptyList()
    }

    fun getAllMyArticlesApi() {
            try {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val response = repository.getAll(sharedViewModel.company.value.id!!, offset, pageSize)
                            response.body()?.forEach {
                              insert(it)
                            }
                        } catch (ex: Exception) {
                            Log.e("aymenbabayarticle", "error is : $ex")
                        }
                        _adminArticles.value = room.articleCompanyDao().getAllArticlesByCompanyId(sharedViewModel.company.value.id!!)
                    }
                }

            } catch (_: Exception) { }
    }


    fun getAllArticlesApi(companyId : Long) {
        try {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val response = repository.getAll(companyId, offset , pageSize)
                        if(response.isSuccessful){
                            response.body()?.forEach {
                              insert(it)
                            }
                        }
                    } catch (ex: Exception) {
                        Log.e("aymenbabayarticle", "error is : $ex")
                    }
                    _adminArticles.value = room.articleCompanyDao().getAllArticlesByCompanyId(companyId)
                }
            }

        } catch (_: Exception) { }
    }

    fun getRandomArticles(){
        viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = repository.getRandomArticles()
                    if(response.isSuccessful){
                        response.body()?.forEach {
                           insert(it)
                        }
                    }
                } catch (ex: Exception) {
                    Log.e("aymenbabaycategory", "random article size ${ex.message}")
                }
            _randomArticles.value = room.articleCompanyDao().getAllArticles()
        }
    }

    fun getRandomArticlesByCompanyCategory(categName : CompanyCategory){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getRandomArticlesByCompanyCategory(categName.name)
                if(response.isSuccessful){
                    response.body()?.forEach {
                       insert(it)
                    }
                }
            } catch (ex: Exception) {
                Log.e("aymenbabaycategory", "random article size ${ex.message}")
            }
            _randomArticles.value = room.articleCompanyDao().getArticlesByCompanyCategory(categName)
        }
    }

    fun getRandomArticlesByCategory(categoryId : Long, companyId : Long){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response  = repository.getRandomArticlesByCategory(categoryId,companyId)
                if(response.isSuccessful){
                    response.body()?.forEach{
                     insert(it)
                    }
                }
            }catch(ex : Exception){
                Log.e("getRandomArticlesByCategory", "exception : ${ex.message}")
            }
            _adminArticles.value = room.articleCompanyDao().getAllArticlesByCategoryIdAndCompanyId(categoryId, companyId)
        }
    }

    fun getRandomArticlesBySubCategory(subCategoryId : Long, companyId : Long){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response  = repository.getRandomArticlesBySubCategory(subCategoryId,companyId)
                if(response.isSuccessful){
                    response.body()?.forEach{
                       insert(it)
                    }
                }
            }catch(ex : Exception){
                Log.e("getRandomArticlesByCategory", "exception : ${ex.message}")
            }
            _adminArticles.value = room.articleCompanyDao().getAllArticlesBySubCategoryIdAndCompanyId(subCategoryId , companyId)
        }
    }

    fun addArticleWithoutImage(articlee : ArticleCompanyDto,articl: String) {
        viewModelScope.launch (Dispatchers.IO){
            val result: Result<Response<Void>> = runCatching {
                repository.addArticleWithoutImage(articl, article.id?:0L)
            }
            result.fold(
                onSuccess = { success ->
                    if (success.isSuccessful) {
                        insertArticleLocally(Article(),
                            mapArticleCompanyToRoomArticleCompany(articlee)
                        )
                        _articles.value.drop(1)
                        _adminArticles.value = room.articleCompanyDao().getAllArticlesByCompanyId(sharedViewModel.company.value.id!!)
                    } else {
                        // Handle unsuccessful response
                    }
                },
                onFailure = { exception ->
                    Log.e("addnewarticle","exception is : ${exception.message}")
                }
            )
//           val success = repository.addArticleWithoutImage(article)
//                if(success.isSuccessful){
//                   insertArticleLocally(articlee)
//                    adminArticles = repository.getAllArticlesLocaly(companyId)
//                }
        }
    }

    private suspend fun insertArticleLocally(articlee : Article, art : ArticleCompany){
        art.articleId = articlee.id
       room.articleCompanyDao().insertArticle(art)
    }
    fun getAllArticlesContaining(search : String, searchType: SearchType){
        searchArticles = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                   val response = repository.getAllArticlesContaining(search,searchType)
                if(response.isSuccessful){
                    response.body()?.forEach {
                       insert(it)
                    }
                }
            }catch (ex : Exception){
                Log.e("exception", "error is : $ex")
            }
            searchArticles = room.articleCompanyDao().getAllArticlesContaining(search)
        }
    }

    fun makeItAsFav(article : ArticleCompany){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            article.id?.let { repository.likeAnArticle(it, !article.isFav!!) }
                room.articleCompanyDao().chageIsFav(article.id!! , !article.isFav!!)
                _randomArticles.value = room.articleCompanyDao().getAllArticles()
            _adminArticles.value = room.articleCompanyDao().getAllArticlesByCompanyId(companyId)
            }
        }
    }

    fun sendComment(comment : String,articleId : Long){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.sendComment(comment, articleId)
            }
        }
    }

    fun getAllArticleComments(){
        viewModelScope.launch(Dispatchers.IO){
                try {
                    val comments = articleCompany.id?.let { repository.getComments(it) }
                    if (comments != null) {
                        if (comments.isSuccessful) {
                            comments.body()!!.forEach {
                                room.commentDao().insertComment(mapCommentToRoomComment(it))
                            }
                        }
                    }
                }catch (_ex : Exception){
                    Log.e("aymenbabaycomment", "comment locally exception : $_ex")
                }
                        allComments = articleCompany.id?.let {
                            room.commentDao().getAllCommentByArticleId(it)
                        }!!

        }
    }

    suspend fun insert(article : ArticleCompanyDto){
        room.userDao().insertUser(mapUserToRoomUser(article.category?.company?.user))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(article.category?.company))
        room.categoryDao().insertCategory(mapCategoryToRoomCategory(article.category!!))
        room.subCategoryDao().insertSubCategory(mapSubCategoryToRoomSubCategory(article.subCategory!!))
        article.provider?.user.let {
        room.userDao().insertUser(mapUserToRoomUser(article.provider?.user))
        }
        room.companyDao().insertCompany(mapCompanyToRoomCompany(article.provider))
        room.articleDao().insertArticle(mapArticelDtoToRoomArticle(article.article!!))
        room.articleCompanyDao().insertArticle(
            mapArticleCompanyToRoomArticleCompany(article)
        )
    }


    fun fetchArticlesByArticleId(articleCompanyId: Long, articleId: Long) {
        viewModelScope.launch {
            val article = getArticleByIdLocally(articleId)
            _articlesByArticleId.value += (articleCompanyId to article)
        }
    }
    private suspend fun getArticleByIdLocally(articleId: Long): Article {
        return withContext(Dispatchers.IO) {
            room.articleDao().getArticleById(articleId)
        }
    }

    fun getArticleById(articleId : Long?){
        viewModelScope.launch {
            article =  room.articleDao().getArticleById(articleId?:articleCompany.articleId!!)
        }
    }

    fun getArticleCompany(articleId : Long){
        viewModelScope.launch {
            articleCompany = room.articleCompanyDao().getArticleCompanyById(articleId)
        }
    }
    fun getCompanyAndUserByIds(companyId : Long?, userId : Long?){
        viewModelScope.launch {
          companyId?.let{ _companyComment.value = room.companyDao().getCompanyById(it)}
           userId?.let{ _userComment.value = room.userDao().getUserById(it)}
        }
    }

}
