package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.entity.Dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.Dto.SearchHistoryDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapCategoryToRoomCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapRelationToRoomRelation
import com.aymen.metastore.model.entity.converterRealmToApi.mapSearchToSearchRoom
import com.aymen.metastore.model.entity.converterRealmToApi.mapSubCategoryToRoomSubCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.Enum.Type
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.SearchHistory
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.entity.converterRealmToApi.mapArticelDtoToRoomArticle
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRoomArticleCompany
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val room : AppDatabase,
    private  val sharedViewModel: SharedViewModel
): ViewModel()
{
    var histories by mutableStateOf(emptyList<SearchHistory>())

    val _myClients = MutableStateFlow(emptyList<CompanyWithCompanyClient>())
    val myClients: StateFlow<List<CompanyWithCompanyClient>> = _myClients


    fun emptyClient(){
        _myClients.value = emptyList()
    }
    fun getAllMyClient(){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = repository.getAllMyClient(sharedViewModel.company.value.id!!)
                if(response.isSuccessful){
                    response.body()?.forEach{client ->
                       insertRelation(client)
                    }
                }
            }catch (_ex : Exception){
                Log.e("aymenbabayclient","error is : $_ex")
            }
            _myClients.value = room.clientProviderRelationDao().getAllClientsByProviderId(sharedViewModel.company.value.id!!)
            }

    }

    fun getAllMyClientContaining(clientname : String){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = repository.getAllMyClientContaining(clientname, sharedViewModel.company.value.id!!)
                if(response.isSuccessful){
                    response.body()?.forEach{client ->
                        insertRelation(client)
                    }

                }

            }catch (ex:Exception){
                Log.e("aymenbabayclient","error is : $ex")
            }


            _myClients.value = room.clientProviderRelationDao().getAllClientsUserContaining(clientname, sharedViewModel.company.value.id!!)
            _myClients.value += room.clientProviderRelationDao().getAllClientsCompanyContaining(clientname, sharedViewModel.company.value.id!!)
        }
    }

    suspend fun insertRelation(relationDto: ClientProviderRelationDto){
        relationDto.client?.let {
            room.companyDao().insertCompany(mapCompanyToRoomCompany(it))
        }
        relationDto.person?.let {
            room.userDao().insertUser(mapUserToRoomUser(it))
        }
        relationDto.provider.let {
            room.companyDao().insertCompany(mapCompanyToRoomCompany(it))
        }
        room.clientProviderRelationDao().insertClientProviderRelation(
            mapRelationToRoomRelation(relationDto)
        )
    }


    fun getAllClientsCompanyContaining(search : String, searchType : SearchType, searchCategory: SearchCategory){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllClientContaining(search,searchType,searchCategory)
                if(response.isSuccessful){
                    response.body()?.forEach {company ->
                        room.userDao().insertUser(mapUserToRoomUser(company.user))
                        room.companyDao().insertCompany(mapCompanyToRoomCompany(company))
                    }
                }
            }catch (_ex : Exception){
                Log.e("aymenbabayclients","error is : $_ex")
            }
            _myClients.value = room.clientProviderRelationDao().getAllClientsCompanyContaining(search, sharedViewModel.company.value.id!!)
        }
    }

    fun getAllClientsUserContaining(search : String, searchType : SearchType, searchCategory: SearchCategory){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllClientUserContaining(search,searchType,searchCategory)
                if(response.isSuccessful){
                    response.body()?.forEach {user ->
                        room.userDao().insertUser(mapUserToRoomUser(user))
                    }
                }
            }catch (_ex : Exception){
                Log.e("aymenbabayclients","error is : $_ex")
            }
            _myClients.value = room.clientProviderRelationDao().getAllClientsUserContaining(search, sharedViewModel.company.value.id!!)

        }
    }
    fun addClient(client : String, file : File){
        viewModelScope.launch {
            try {
                repository.addClient(client,file)
            }catch (_ex : Exception){}
            getAllMyClient()
        }
    }

    fun addClientWithoutImage(client : String){
    viewModelScope.launch {
        try {
            repository.addClientWithoutImage(client)
        }catch (_ex : Exception){}
        getAllMyClient()
    }
    }

    fun sendClientRequest(id : Long, type : Type){
        Log.e("aymenbabayclient","id : $id type : $type in client view model send client request")
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            repository.sendClientRequest(id,type)
            }
        }
    }

    fun saveHitory( category : SearchCategory,id : Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.saveHistory(category,id)
            }
        }
    }

    fun getAllSearchHistory(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllHistory()
                if(response.isSuccessful){
                    response.body()?.forEach{
                        insertSearchHistary(it)
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllSearchHistory", "exception : ${ex.message}")
            }
                histories = room.searchHistoryDao().getAllSearchHistories()

        }
    }

    suspend fun insertSearchHistary(search : SearchHistoryDto){
        if(search.user != null) {
            room.userDao().insertUser(mapUserToRoomUser(search.user))
        }
        if(search.company != null) {
            room.userDao().insertUser(mapUserToRoomUser(search.company.user))
            room.companyDao().insertCompany(mapCompanyToRoomCompany(search.company))
        }
        if(search.article != null) {
            room.userDao().insertUser(mapUserToRoomUser(search.article.company?.user))
            room.companyDao().insertCompany(mapCompanyToRoomCompany(search.article.company))
            room.categoryDao().insertCategory(mapCategoryToRoomCategory(search.article.category!!))
            room.subCategoryDao().insertSubCategory(mapSubCategoryToRoomSubCategory(search.article.subCategory!!))
            room.articleDao().insertArticle(mapArticelDtoToRoomArticle(search.article.article!!))
            room.articleCompanyDao().insertArticle(mapArticleCompanyToRoomArticleCompany(search.article))
        }
        room.searchHistoryDao().insertSearchHistory(mapSearchToSearchRoom(search))
    }
}