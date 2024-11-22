package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aymen.metastore.model.entity.model.ClientProviderRelation
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.SearchHistory
import com.aymen.metastore.model.entity.model.User
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.Type
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val room : AppDatabase,
    private  val sharedViewModel: SharedViewModel,
    private val useCases: MetaUseCases
): ViewModel() {
    private var _histories: MutableStateFlow<PagingData<SearchHistory>> =
        MutableStateFlow(PagingData.empty())
    val histories: StateFlow<PagingData<SearchHistory>> get() = _histories

    private val _myClients: MutableStateFlow<PagingData<ClientProviderRelation>> = MutableStateFlow(PagingData.empty())
    val myClients: StateFlow<PagingData<ClientProviderRelation>> = _myClients

    private val _searchPersons: MutableStateFlow<PagingData<ClientProviderRelation>> =
        MutableStateFlow(PagingData.empty())
    var searchPersons: StateFlow<PagingData<ClientProviderRelation>> = _searchPersons

    val company: StateFlow<Company?> = sharedViewModel.company
    val user: StateFlow<User?> = sharedViewModel.user
    init {
        getAllMyClient()
    }

    fun emptyClient() {
        _myClients.value = PagingData.empty()
    }

    fun getAllMyClient() {
        viewModelScope.launch(Dispatchers.IO) {
            useCases.getAllMyClient(company.value?.id!!)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _myClients.value = it.map { relation -> relation.toCompanyWithCompanyClient() }
                }
        }

    }

    fun getAllMyClientContaining(clientname: String) {
        viewModelScope.launch(Dispatchers.IO) {
            useCases.getAllMyClientContaining(sharedViewModel.company.value.id!!, clientname)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _myClients.value = it.map { relation -> relation.toCompanyWithCompanyClient() }
                }
        }
    }

    fun getAllPersonContaining(
        personName: String,
        searchType: SearchType,
        searchCategory: SearchCategory
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            useCases.getAllPersonContaining(personName, searchType, searchCategory)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _searchPersons.value =
                        it.map { relation -> relation.toCompanyWithCompanyClient() }
                }
        }
    }
        fun addClient(client: String, file: File) {
            viewModelScope.launch {
                try {
                    repository.addClient(client, file)
                } catch (_ex: Exception) {
                }
                getAllMyClient()
            }
        }

        fun addClientWithoutImage(client: String) {
            viewModelScope.launch {
                try {
                    repository.addClientWithoutImage(client)
                } catch (_ex: Exception) {
                }
                getAllMyClient()
            }
        }

        fun sendClientRequest(id: Long, type: Type) {
            Log.e(
                "aymenbabayclient",
                "id : $id type : $type in client view model send client request"
            )
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.sendClientRequest(id, type)
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

        fun getAllSearchHistory() {
            viewModelScope.launch(Dispatchers.IO) {

            }
        }

//    suspend fun insertSearchHistary(search : SearchHistoryDto){
//        if(search.user != null) {
//            room.userDao().insertUser(mapUserToRoomUser(search.user))
//        }
//        if(search.company != null) {
//            room.userDao().insertUser(mapUserToRoomUser(search.company.user))
//            room.companyDao().insertCompany(mapCompanyToRoomCompany(search.company))
//        }
//        if(search.article != null) {
//            room.userDao().insertUser(mapUserToRoomUser(search.article.company?.user))
//            room.companyDao().insertCompany(mapCompanyToRoomCompany(search.article.company))
//            room.categoryDao().insertCategory(mapCategoryToRoomCategory(search.article.category!!))
//            room.subCategoryDao().insertSubCategory(mapSubCategoryToRoomSubCategory(search.article.subCategory!!))
//            room.articleDao().insertArticle(mapArticelDtoToRoomArticle(search.article.article!!))
//            room.articleCompanyDao().insertArticle(mapArticleCompanyToRoomArticleCompany(search.article))
//        }
//        room.searchHistoryDao().insertSearchHistory(mapSearchToSearchRoom(search))
//    }

}