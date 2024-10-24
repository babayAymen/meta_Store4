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
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapSubCategoryToRoomSubCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.entity.converterRealmToApi.mapArticelDtoToRoomArticle
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRoomArticleCompany
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Comment
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val realm : Realm,
    private val sharedViewModel : SharedViewModel,
    private val room : AppDatabase
)
    : ViewModel() {

    var companyId by mutableLongStateOf(0)
    private val _adminArticles = MutableStateFlow<List<ArticleCompany>>(emptyList())
    var adminArticles : StateFlow<List<ArticleCompany>> = _adminArticles

    private val _randomArticles = MutableStateFlow<List<ArticleCompany>>(emptyList())
    val randomArticles: StateFlow<List<ArticleCompany>> = _randomArticles

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles

    private val _articleFlow = MutableStateFlow("")
    val response : Flow<List<ArticleCompany>> = _articleFlow
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
                val response = repository.getAllArticlesByCategory()
                val respons = repository.getAllArticlesByCategor()
                if(response.isSuccessful) {
                    Log.e("getAllArticlesByCategory","size response body: ${response.body()?.size}")
                    response.body()?.forEach {
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                    respons.body()?.forEach {

                        room.articleDao().insertArticle(it)
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllArticlesByCategory","exception: $ex")
            }
            _articles.value = repository.getAllArticlesByCategoryLocaly(sharedViewModel.company.value.id!!,
                sharedViewModel.company.value.category!!)
            val art = room.articleDao().getAllArticles()
            Log.e("getAllArticlesByCategory","size llist articleroom: ${art.size}")
        }
    }

    fun getAllMyArticleContaining(articleLibel : String){
        _articleFlow.value = articleLibel
    }

    private fun getAllArticlesContainingFromServer(articleLibel : String,searchType :SearchType):Flow<List<ArticleCompany>>{
        return flow {
            delay(500)
            emit(repository.getAllArticlesContainingg(articleLibel,searchType).body()?: emptyList())
        }
    }





    var searchArticles by mutableStateOf(emptyList<ArticleCompany>())
    private var offset by mutableIntStateOf(1)
    private var pageSize by mutableIntStateOf(1)
    var articleCompany by mutableStateOf(ArticleCompany())
    var article by mutableStateOf(Article())
    var allComments by mutableStateOf(emptyList<Comment>())
    var myComment by mutableStateOf("")

    fun clearArticles() {
        _randomArticles.value = emptyList()
    }

    fun getAllMyArticlesApi() {
            try {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val response = repository.getAl(sharedViewModel.company.value.id!!, offset, pageSize)
                            val respons = repository.getAll(sharedViewModel.company.value.id!!, offset, pageSize)
                            respons.body()?.forEach {
                              insert(it)
                            }
                            if(response.isSuccessful) {
                                response.body()?.forEach { article ->
                                    realm.write {
                                        copyToRealm(article, UpdatePolicy.ALL)
                                    }
                                }
                            }
                        } catch (ex: Exception) {
                            Log.e("aymenbabayarticle", "error is : $ex")
                        }
                        _adminArticles.value = repository.getAllArticlesLocaly(sharedViewModel.company.value.id!!)
                        val re = room.articleDao().getAllArticles()
                        val r = room.articleCompanyDao().getAllArticles()
                        Log.e("aymenbabayarticle","${_adminArticles.value.size} and article room size ${re.size} and articlecompany size : ${r.size}")
                    }
                }

            } catch (_: Exception) { }
    }


    fun getAllArticlesApi(companyId : Long) {
        try {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val respons = repository.getAl(companyId, offset, pageSize)
                        if(respons.isSuccessful) {
                            respons.body()?.forEach { article ->
                                realm.write {
                                    copyToRealm(article, UpdatePolicy.ALL)
                                }
                            }
                        }
                        val response = repository.getAll(companyId, offset , pageSize)
                        if(response.isSuccessful){
                            response.body()?.forEach {
                              insert(it)
                            }
                        }
                    } catch (ex: Exception) {
                        Log.e("aymenbabayarticle", "error is : $ex")
                    }
                    _adminArticles.value = repository.getAllArticlesLocaly(companyId)
                }
            }

        } catch (_: Exception) { }
    }

    fun getRandomArticles(){
        viewModelScope.launch(Dispatchers.IO) {
                try {
                    val respons = repository.getRandomArticless()
                    if(respons.isSuccessful) {
                        respons.body()?.forEach {
                            realm.write {
                                val articled = ArticleCompany().apply {
                                    id = it.id
                                    isRandom = true
                                    sharedPoint = it.sharedPoint
                                    quantity = it.quantity
                                    cost = it.cost
                                    sellingPrice = it.sellingPrice
                                    category = it.category
                                    subCategory = it.subCategory
                                    company = it.company
                                    isFav = it.isFav
                                    likeNumber = it.likeNumber
                                    commentNumber = it.commentNumber
                                    article = it.article

                                }
                                copyToRealm(articled, UpdatePolicy.ALL)
                            }
                        }
                    }
                    val response = repository.getRandomArticles()
                    if(response.isSuccessful){
                        response.body()?.forEach {
                           insert(it)
                        }
                    }
                } catch (_ex: Exception) {
                    Log.e("aymenbabaycategory", "random article size ${_ex.message}")
                }
            _randomArticles.value = repository.getRandomArticleLocally()
            Log.e("aymenbabaycategory", "random article is fav _random : ${_randomArticles.value.size} ${response.count()}")
        }
    }

    fun getRandomArticlesByCompanyCategory(categName : String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respons = repository.getRandomArticlesByCompanyCategoryy(categName)
                if(respons.isSuccessful) {
                    respons.body()?.forEach {
                        realm.write {
                            val articled = ArticleCompany().apply {
                                id = it.id
                                isRandom = true
                                sharedPoint = it.sharedPoint
                                quantity = it.quantity
                                cost = it.cost
                                sellingPrice = it.sellingPrice
                                category = it.category
                                subCategory = it.subCategory
                                company = it.company
                                isFav = it.isFav
                                likeNumber = it.likeNumber
                                commentNumber = it.commentNumber
                                article = it.article
                                isEnabledToComment = it.isEnabledToComment

                            }
                            copyToRealm(articled, UpdatePolicy.ALL)
                        }
                    }
                }
                val response = repository.getRandomArticlesByCompanyCategory(categName)
                if(response.isSuccessful){
                    response.body()?.forEach {
                       insert(it)
                    }
                }
            } catch (_ex: Exception) {
                Log.e("aymenbabaycategory", "random article size ${_ex.message}")
            }
            _randomArticles.value = repository.getRandomArticleByCompanyCategoryLocally(categName)
        }
    }

    fun getRandomArticlesByCategory(categoryId : Long, companyId : Long){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val respons = repository.getRandomArticlesByCategoryy(categoryId,companyId)
                if(respons.isSuccessful){
                    respons.body()?.forEach{
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                }
                val response  = repository.getRandomArticlesByCategory(categoryId,companyId)
                if(response.isSuccessful){
                    response.body()?.forEach{
                     insert(it)
                    }
                }
            }catch(ex : Exception){
                Log.e("getRandomArticlesByCategory", "exception : ${ex.message}")
            }
            _adminArticles.value = repository.getRandomArticlesByCategoryLocally(categoryId, companyId)
        }
    }

    fun getRandomArticlesBySubCategory(categoryId : Long, companyId : Long){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val respons  = repository.getRandomArticlesBySubCategoryy(categoryId,companyId)
                if(respons.isSuccessful){
                    respons.body()?.forEach{
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                }
                val response  = repository.getRandomArticlesBySubCategory(categoryId,companyId)
                if(response.isSuccessful){
                    response.body()?.forEach{
                       insert(it)
                    }
                }
            }catch(ex : Exception){
                Log.e("getRandomArticlesByCategory", "exception : ${ex.message}")
            }
            _adminArticles.value = repository.getRandomArticlesBySubCategoryLocally(categoryId, companyId)
        }
    }

    fun addArticle(articlee : ArticleCompany,article : String , file : File){
        viewModelScope.launch (Dispatchers.IO){
            try{
            val response = repository.addArticle(article, file)
                if(response.isSuccessful){
               insertArticleLocally(Article(),articlee)
                _adminArticles.value = repository.getAllArticlesLocaly(companyId)
        }
            }catch (ex : Exception){
                Log.e("addarticlefun","exeprtion : ${ex.message}")
            }
        }
    }

    fun addArticleWithoutImage(articlee : ArticleCompany,articl: String) {
        viewModelScope.launch (Dispatchers.IO){
            val result: Result<Response<Void>> = runCatching {
                repository.addArticleWithoutImage(articl, article.id?:0L)
            }
            result.fold(
                onSuccess = { success ->
                    if (success.isSuccessful) {
                        insertArticleLocally(Article(),articlee)
                        _articles.value.drop(1)
                        _adminArticles.value = repository.getAllArticlesLocaly(companyId)
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
        val art = ArticleCompany().apply {
            isRandom = false
            sharedPoint = art.sharedPoint
            quantity = art.quantity
            cost = art.cost
            sellingPrice = art.sellingPrice
            category = art.category
            subCategory?.id = art.subCategory?.id
            isFav = false
            article = articlee
            company = art.company
        }
        realm.write {
            copyToRealm(art, UpdatePolicy.ALL)
        }
    }
    fun getAllArticlesContaining(search : String, searchType: SearchType){
        searchArticles = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                   val articles = repository.getAllArticlesContainingg(search,searchType).body()!!
                if(articles.isNotEmpty()){
                    searchArticles = articles
                }
                   val response = repository.getAllArticlesContaining(search,searchType)
                if(response.isSuccessful){
                    response.body()?.forEach {
                       insert(it)
                    }
                }
            }catch (ex : Exception){
                Log.e("exception", "error is : $ex")
            }
        }
    }

    fun makeItAsFav(article : ArticleCompany){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            article.id?.let { repository.likeAnArticle(it, !article.isFav) }
                room.articleCompanyDao().chageIsFav(article.id!! , !article.isFav)
         repository.makeItAsFav(article)
                _randomArticles.value = repository.getRandomArticleLocally()
            _adminArticles.value = repository.getAllArticlesLocaly(companyId)
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

    // a complete ulter to change to room
    fun getAllArticleComments(){
        viewModelScope.launch(Dispatchers.IO){
                try {
                    val comment = articleCompany.id?.let { repository.getComments(it) }
                    if (comment != null) {
                        if (comment.isSuccessful) {
                            comment.body()!!.forEach {
                                realm.write {
                                    copyToRealm(it, UpdatePolicy.ALL)
                                }
                            }
                        }
                    }
                    val comments = articleCompany.id?.let { repository.getComments(it) }
                    if (comments != null) {
                        if (comments.isSuccessful) {
                            comments.body()!!.forEach {
//                                room.
                            }
                        }
                    }
                }catch (_ex : Exception){
                    Log.e("aymenbabaycomment", "comment locally exception : $_ex")
                }
                        allComments = articleCompany.id?.let { repository.getCommentsLocally(it) }!!

        }
    }

    suspend fun insert(article : ArticleCompanyDto){
        room.categoryDao().insertCategory(mapCategoryToRoomCategory(article.category))
        room.subCategoryDao().insertSubCategory(mapSubCategoryToRoomSubCategory(article.subCategory))
        room.userDao().insertUser(mapUserToRoomUser(article.company.user))
        article.provider.user.let {
        room.userDao().insertUser(mapUserToRoomUser(article.provider.user))
        }
        room.companyDao().insertCompany(mapCompanyToRoomCompany(article.provider))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(article.company))
        room.articleDao().insertArticle(mapArticelDtoToRoomArticle(article.article))
        room.articleCompanyDao().insertArticle(
            mapArticleCompanyToRoomArticleCompany(article)
        )
    }


}
