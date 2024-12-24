package com.aymen.metastore.model.repository.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.dto.SearchHistoryDto
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.ErrorResponse
import com.aymen.metastore.model.entity.model.SearchHistory
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.AllSearchRemoteKeysEntity
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val useCases: MetaUseCases,
    private val sharedViewModel: SharedViewModel,
    private val room : AppDatabase,
    private val context : Context
): ViewModel() {

    private val _histories: MutableStateFlow<PagingData<SearchHistory>> = MutableStateFlow(PagingData.empty())
    val histories: StateFlow<PagingData<SearchHistory>> get() = _histories

    private val _searchCompanies: MutableStateFlow<PagingData<Company>> = MutableStateFlow(PagingData.empty())
    val searchCompanies: StateFlow<PagingData<Company>> get() = _searchCompanies

    private val _searchPersons: MutableStateFlow<PagingData<User>> = MutableStateFlow(PagingData.empty())
    val searchPersons: StateFlow<PagingData<User>> get() = _searchPersons

    private var _searchArticles : MutableStateFlow<PagingData<ArticleCompany>> = MutableStateFlow(PagingData.empty())
    val searchArticles : StateFlow<PagingData<ArticleCompany>> get() = _searchArticles

    private val searchDao = room.searchHistoryDao()
    private val articleDao = room.articleDao()
    private val articleCompanyDao = room.articleCompanyDao()
    private val userDao = room.userDao()
    private val companyDao = room.companyDao()

    init {
        getAllSearchHistory()
    }

    private fun getAllSearchHistory() {
        val id = when(sharedViewModel.accountType.value){
            AccountType.USER -> sharedViewModel.user.value.id
            AccountType.COMPANY -> sharedViewModel.company.value.id
            AccountType.META -> sharedViewModel.user.value.id
            AccountType.NULL -> TODO()
            AccountType.SELLER -> TODO()
        }
        viewModelScope.launch {
            Log.e("srhsrth","idd $id")
            useCases.getAllSearchHistory(id!!)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _histories.value = it
                }
        }
    }


    fun search(searchType: SearchType, searchCategory: SearchCategory, searchText : String){
        when(searchCategory){
            SearchCategory.COMPANY -> {
                when(searchType){
                    SearchType.OTHER ->{
                        getAllCompaniesContaining(searchText,searchType,searchCategory)
                    }
                    SearchType.MY ->{
                        // والله لا تعرف كيفاه تمشي
                    }
                    else ->{
                        getAllCompaniesContaining(searchText,searchType,searchCategory)
                    }
                }
            }
            SearchCategory.USER -> {
                getAllPersonContaining(searchText,searchType)
            }
            SearchCategory.ARTICLE-> {
                getAllMyArticleContaining(searchText,searchType)
            }
            else ->{

            }
        }
    }

    fun getAllCompaniesContaining(
        search: String,
        searchType: SearchType,
        searchCategory: SearchCategory
    ) {
        val id = when(sharedViewModel.accountType.value){
            AccountType.COMPANY -> sharedViewModel.company.value.id
            AccountType.USER -> sharedViewModel.user.value.id
            AccountType.META -> sharedViewModel.user.value.id
            AccountType.NULL -> TODO()
            AccountType.SELLER -> TODO()
        }
        viewModelScope.launch {
            useCases.getAllCompaniesContaining(search, searchType,id!!)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _searchCompanies.value = it.map { company -> company.toCompanyModel() }
                }
        }
    }

    fun getAllPersonContaining(
        personName: String,
        searchType: SearchType,
    ) {
        viewModelScope.launch {
            val id = when(sharedViewModel.accountType.value){
                AccountType.USER -> sharedViewModel.user.value.id
                AccountType.COMPANY -> sharedViewModel.company.value.id
                AccountType.META -> sharedViewModel.user.value.id
                AccountType.NULL -> TODO()
                AccountType.SELLER -> TODO()
            }
            useCases.getAllPersonContaining(id!!,personName, searchType)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _searchPersons.value = it.map { user -> user.toUserModel() }
                    it.map {r ->
                        Log.e("searchscreen","search person $r")}
                }
        }
    }

    fun getAllMyArticleContaining(articleLibel: String, searchType: SearchType) {
        viewModelScope.launch {
            useCases.getAllMyArticleContaining(articleLibel, searchType, sharedViewModel.company.value.id!!)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _searchArticles.value = it.map {article -> article.toArticleCompanyModel() }
                }
        }
    }

    fun saveHitory(category: SearchCategory, id: Long, search : SearchHistory) {
        viewModelScope.launch(Dispatchers.IO) {
                val lastRemoteKey = searchDao.getLatestRemoteKey()
                val searchId = if (lastRemoteKey == null) 1 else lastRemoteKey.id + 1
            if(search.id == null) {
                val countRemoteKeys = searchDao.getRemoteKeysCount()
                val page = if (lastRemoteKey?.prevPage == null) 0 else lastRemoteKey.prevPage + 1
                val prevPage = if (page == 0) null else page - 1
                val remain = countRemoteKeys % PAGE_SIZE
                val nextPage =
                    if (remain < PAGE_SIZE - 1 && lastRemoteKey?.nextPage != null) page + 1 else null
                val newRemoteKey = AllSearchRemoteKeysEntity(
                    id = searchId,
                    prevPage = prevPage,
                    nextPage = nextPage
                )
                room.withTransaction {
                    searchDao.insertSingleRemoteKey(newRemoteKey)
                    when (category) {
                        SearchCategory.COMPANY -> {
                            userDao.insertUser(listOf(search.company?.user?.toUserEntity()))
                            companyDao.insertSingleCompany(search.company?.toCompanyEntity()!!)
                        }

                        SearchCategory.USER -> {
                            userDao.insertUser(listOf(search.user?.toUserEntity()))
                        }

                        SearchCategory.ARTICLE -> {
                            articleDao.insertArticle(listOf(search.article?.article?.toArticleEntity()))
                            articleCompanyDao.insertArticle(
                                listOf(
                                    search.article?.toArticleCompanyEntity(
                                        isSync = false
                                    )
                                )
                            )
                        }

                        SearchCategory.OTHER -> TODO()
                    }
                    searchDao.insertSearch(
                        search.copy(
                            id = searchId,
                            searchCategory = category,
                            lastModifiedDate = System.currentTimeMillis().toString()
                        ).toSearchHistoryEntity()
                    )
                }
            }
            val result : Result<Response<SearchHistoryDto>> = runCatching {
                repository.saveHistory(category, id)
            }
            result.fold(
                onSuccess = {success ->
                    searchDao.deleteRemoteKeyById(searchId)
                    searchDao.deleteSearchHistoryById(searchId)
                    val response = success.body()
                    if(success.isSuccessful){
                        if(response != null) {
                            searchDao.insertSingleRemoteKey(
                                lastRemoteKey?.copy(id = response.id!!)
                                    ?: AllSearchRemoteKeysEntity(response.id!!, null, null)
                            )
                            searchDao.insertSearch(response.toSearchHistory())
                        }
                    }else{
                        val errorBodyString = success.errorBody()?.string()
                        errorBlock(errorBodyString)
                    }
                },
                onFailure = {

                }
            )
        }
    }

    fun deleteSearch(id: Long){
        viewModelScope.launch {
            searchDao.deleteRemoteKeyById(id)
            searchDao.deleteSearchHistoryById(id)
            repository.deleteSearch(id)
        }
    }

    private fun errorBlock(error : String?){
        viewModelScope.launch{
            val re = Gson().fromJson(error, ErrorResponse::class.java)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "error : ${re.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

}