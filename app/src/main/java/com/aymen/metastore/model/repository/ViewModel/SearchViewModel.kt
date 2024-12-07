package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.SearchHistory
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val useCases: MetaUseCases,
    private val sharedViewModel: SharedViewModel
): ViewModel() {

    private var _histories: MutableStateFlow<PagingData<SearchHistory>> = MutableStateFlow(PagingData.empty())
    val histories: StateFlow<PagingData<SearchHistory>> get() = _histories

    private val _searchCompanies: MutableStateFlow<PagingData<Company>> = MutableStateFlow(PagingData.empty())
    val searchCompanies: StateFlow<PagingData<Company>> get() = _searchCompanies

    private val _searchPersons: MutableStateFlow<PagingData<User>> = MutableStateFlow(PagingData.empty())
    val searchPersons: StateFlow<PagingData<User>> get() = _searchPersons

    private var _searchArticles : MutableStateFlow<PagingData<ArticleCompany>> = MutableStateFlow(PagingData.empty())
    val searchArticles : StateFlow<PagingData<ArticleCompany>> get() = _searchArticles

    init {
        getAllSearchHistory()
    }

    fun getAllSearchHistory() {
        val id = when(sharedViewModel.accountType.value){
            AccountType.USER -> sharedViewModel.user.value.id
            AccountType.COMPANY -> sharedViewModel.company.value.id
            AccountType.AYMEN -> TODO()
            AccountType.NULL -> TODO()
        }
        viewModelScope.launch {
            useCases.getAllSearchHistory(id!!)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _histories.value = it.map { search -> search.toSearchHistoryModel() }
                }
        }
    }


    fun search(searchType: SearchType, searchCategory: SearchCategory, searchText : String){
        when(searchCategory){
            SearchCategory.COMPANY -> {
                when(searchType){
                    SearchType.OTHER ->{
                        Log.e("searchscreen","type company other")
                        getAllCompaniesContaining(searchText,searchType,searchCategory)
                    }
                    SearchType.MY ->{
                        Log.e("searchscreen","type company my")
                        // والله لا تعرف كيفاه تمشي
                    }
                    else ->{
                        Log.e("searchscreen","type company else")
                        getAllCompaniesContaining(searchText,searchType,searchCategory)
                    }
                }
            }
            SearchCategory.USER -> {
                Log.e("searchscreen","type user")
                getAllPersonContaining(searchText,searchType)
            }
            SearchCategory.ARTICLE-> {
                Log.e("searchscreen","type article")
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
            AccountType.AYMEN -> TODO()
            AccountType.NULL -> TODO()
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
                AccountType.AYMEN -> TODO()
                AccountType.NULL -> TODO()
            }
            Log.e("searchscreen","search person call")
            useCases.getAllPersonContaining(id!!,personName, searchType)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _searchPersons.value = it.map { user -> user.toUserModel() }
                    Log.e("searchscreen","search person")
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

    fun saveHitory(category: SearchCategory, id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.saveHistory(category, id)
            }
        }
    }

}