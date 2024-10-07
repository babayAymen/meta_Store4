package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.Enum.Type
import com.aymen.store.model.entity.realm.ClientProviderRelation
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.realm.SearchHistory
import com.aymen.metastore.model.entity.realm.User
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
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
    private val realm : Realm
): ViewModel()
{
//    var myClients = mutableStateListOf(emptyList<ClientProviderRelation>())
    var clientsCompany by mutableStateOf(emptyList<Company>())
    var clientsUser by mutableStateOf(emptyList<User>())
    val companyId by mutableLongStateOf(0)
    var histories by mutableStateOf(emptyList<SearchHistory>())

    val _myClients = MutableStateFlow(emptyList<ClientProviderRelation>())
    val myClients: StateFlow<List<ClientProviderRelation>> = _myClients
    fun getAllMyClient(){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = repository.getAllMyClient(companyId)
                if(response.isSuccessful){
                    response.body()?.forEach{client ->
                        realm.write {
                            copyToRealm(client, UpdatePolicy.ALL)
                        }
                    }
                }
            }catch (_ex : Exception){
                Log.e("aymenbabayclient","error is : $_ex")
            }
                _myClients.value = repository.getAllMyClientLocally()
            }

    }

    fun getAllMyClientContaining(clientname : String){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = repository.getAllMyClientContaining(clientname,companyId)
                if(response.isSuccessful){
                    response.body()?.forEach{client ->
                        Log.e("clientcontaining","client name = ${client.person?.username}")
                    }
                    if(response.body()?.isNotEmpty() == true){
                    _myClients.value = response.body()?: emptyList()
                    }
                }

            }catch (ex:Exception){
                Log.e("aymenbabayclient","error is : $ex")
            }
        }
    }

    fun getAllClientsCompanyContaining(search : String, searchType : SearchType, searchCategory: SearchCategory){

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllClientContaining(search,searchType,searchCategory)
                if(response.isSuccessful){
                    response.body()?.forEach {
                        realm.write {
                            copyToRealm(it,UpdatePolicy.ALL)
                        }
                    }
                }
                clientsCompany = response.body()!!
                Log.e("aymenbabayclients","my all companies size : ${clientsCompany.size}")
            }catch (_ex : Exception){
                Log.e("aymenbabayclients","error is : $_ex")
            }
        }
    }

    fun getAllClientsUserContaining(search : String, searchType : SearchType, searchCategory: SearchCategory){

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllClientUserContaining(search,searchType,searchCategory)
                if(response.isSuccessful){
                    response.body()?.forEach {
                        realm.write {
                            copyToRealm(it,UpdatePolicy.ALL)
                        }
                    }
                }
                clientsUser = response.body()!!
                Log.e("aymenbabayclients","my all companies size : ${clientsCompany.size}")
            }catch (_ex : Exception){
                Log.e("aymenbabayclients","error is : $_ex")
            }
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
                Log.e("getAllSearchHistory",response.body()?.size.toString())
                if(response.isSuccessful){
                    response.body()!!.forEach{
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllSearchHistory", "exception : ${ex.message}")
            }
            histories = repository.getAllHistoryLocally()
            Log.e("getAllSearchHistory",histories.size.toString())
        }
    }
}