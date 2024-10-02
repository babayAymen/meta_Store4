package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.SearchType
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
    private val sharedViewModel : SharedViewModel
)
    : ViewModel() {

    var companyId by mutableLongStateOf(0)
    var adminArticles by mutableStateOf(emptyList<ArticleCompany>())

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
                if(response.isSuccessful) {
                    Log.e("getAllArticlesByCategory","size response body: ${response.body()?.size}")
                    response.body()?.forEach {
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllArticlesByCategory","exception: $ex")
            }
            _articles.value = repository.getAllArticlesByCategoryLocaly(sharedViewModel.company.value.id!!,
                sharedViewModel.company.value.category!!)
            Log.e("getAllArticlesByCategory","size locally: ${articles.value.size}")
        }
    }

    fun getAllMyArticleContaining(articleLibel : String){
        _articleFlow.value = articleLibel
    }

    private fun getAllArticlesContainingFromServer(articleLibel : String,searchType :SearchType):Flow<List<ArticleCompany>>{
        return flow {
            delay(500)
            emit(repository.getAllArticlesContaining(articleLibel,searchType).body()?: emptyList())
        }
    }




private val _randomArticles = MutableStateFlow<List<ArticleCompany>>(emptyList())
    val randomArticles: StateFlow<List<ArticleCompany>> = _randomArticles

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
                            val response = repository.getAll(companyId, offset, pageSize)
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
                        adminArticles = repository.getAllArticlesLocaly(companyId)
                    }
                }

            } catch (_: Exception) { }
    }

    fun getRandomArticles(){
        viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = repository.getRandomArticles().body()!!
                    response.forEach {
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
                val response = repository.getRandomArticlesByCompanyCategory(categName).body()!!
                response.forEach {
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
            } catch (_ex: Exception) {
                Log.e("aymenbabaycategory", "random article size ${_ex.message}")
            }
            _randomArticles.value = repository.getRandomArticleByCompanyCategoryLocally(categName)
        }
    }



    fun addArticle(articlee : ArticleCompany,article : String , file : File){
        viewModelScope.launch (Dispatchers.IO){
            try{
            val response = repository.addArticle(article, file)
                if(response.isSuccessful){
               insertArticleLocally(Article(),articlee)
                adminArticles = repository.getAllArticlesLocaly(companyId)
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
                        adminArticles = repository.getAllArticlesLocaly(companyId)
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
        var articles = emptyList<ArticleCompany>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                    articles = repository.getAllArticlesContaining(search,searchType).body()!!
                if(articles.isNotEmpty()){
                    searchArticles = articles
                }
            }catch (_ex : Exception){
                Log.e("exception", "error is : $_ex")
            }
        }
    }

    fun makeItAsFav(article : ArticleCompany){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            article.id?.let { repository.likeAnArticle(it, !article.isFav) }
         repository.makeItAsFav(article)
                _randomArticles.value = repository.getRandomArticleLocally()
            adminArticles = repository.getAllArticlesLocaly(companyId)
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
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val comments = articleCompany.id?.let { repository.getComments(it) }
                    if (comments != null) {
                        if (comments.isSuccessful) {
                            Log.e("aymenbabaycomment", "comment api size : ${comments.body()!!.size}")
                            Log.e("aymenbabaycomment", "article id : ${articleCompany.id}")
                            comments.body()!!.forEach {
//                                val comment = Comment().apply {
//                                    id = it.id
//                                    content = it.content
//                                    user = it.user
//                                    company = it.company
//                                    article = it.article
//                                }
                                realm.write {
                                    copyToRealm(it, UpdatePolicy.ALL)
                                }
                            }
                        }
                    }
                        allComments = articleCompany.id?.let { repository.getCommentsLocally(it) }!!
//                        Log.e("aymenbabaycomment", "comment locally size : ${allComments[0].id}")
                }catch (_ex : Exception){
                    Log.e("aymenbabaycomment", "comment locally exception : $_ex")
                }
            }
        }
    }


}
