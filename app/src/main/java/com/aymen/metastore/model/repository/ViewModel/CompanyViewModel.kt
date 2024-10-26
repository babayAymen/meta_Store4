package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.aymen.metastore.model.entity.Dto.ClientProviderRelationDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapRelationToRoomRelation
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.realm.Parent
import com.aymen.store.model.entity.realm.Provider
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val realm : Realm,
    private val room : AppDatabase,
//    private val dataStore: DataStore<Company>,
    private val companyDataStore: DataStore<CompanyDto>,
    private val appViewModel: AppViewModel,
    private  val sharedViewModel: SharedViewModel
) : ViewModel() {

    var providers by mutableStateOf(emptyList<Provider>())
    var allCompanies by mutableStateOf(emptyList<Company>())
    var providerId by mutableLongStateOf(0)
    var parent by mutableStateOf(Parent())
    var myCompany by mutableStateOf(sharedViewModel.company.value)
    var update by mutableStateOf(false)

    init {
        getMyCompany()
        getMyCompany {
            sharedViewModel._company.value = it ?: CompanyDto()
        }
    }


    fun addCompany(company: String, file : File){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            repository.addCompany(company,file)
            }
        }
    }
 fun updateCompany(company: String, file : File){
     Log.e("aymenbabayupdate","c bon update company $company")
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            repository.updateCompany(company,file)
            }
        }
    }

    fun getAllMyProvider() {
                Log.d("aymenbabay", "getAllMyProvider begin")
        getMyCompany { company ->
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val respons = repository.getAllMyProviderr(company?.id!!).body()!!
                    respons.forEach {
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                    val response = repository.getAllMyProvider(company.id!!).body()!!
                    response.forEach {provider ->
                        insertRelation(provider)
                    }
                } catch (_ex: Exception) {
                    Log.e("aymenbabay", "error is : $_ex")
                }
                providers = repository.getAllMyProviderLocally()
                if (providers.isNotEmpty()) {
                    providerId = providers[0].id!!
                }
        }
            }
        }
    }

    @Transaction
    suspend fun insertRelation(relation : ClientProviderRelationDto){

        relation.person?.let {
            room.userDao().insertUser(mapUserToRoomUser(it))
        }
        relation.client?.let {
            room.companyDao().insertCompany(mapCompanyToRoomCompany(it))
        }
        room.companyDao().insertCompany(mapCompanyToRoomCompany(relation.provider))
        room.clientProviderRelationDao().insertClientProviderRelation(mapRelationToRoomRelation(relation))
    }


    @Transaction
    suspend fun insertCompany(company : CompanyDto){
        room.userDao().insertUser(mapUserToRoomUser(company.user))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(company))
    }

    fun getMyParent(){
        getMyCompany { company ->

        viewModelScope.launch {
            withContext(Dispatchers.IO){

            try {
                val parents = repository.getMyParentt(company?.id!!)
                if(parents.isSuccessful){
                    realm.write {
                        copyToRealm(parents.body()!!,UpdatePolicy.ALL)
                    }
                }
                val response = repository.getMyParent(company.id!!)
                if(response.isSuccessful && response.body() != null){
                   insertCompany(response.body()!!)
                }
            }catch (ex : Exception){
                Log.e("aymenbabayparent","c bon error ${ex.message}")
            }
            parent = repository.getMyParentLocally()[0]
        }
            }
        }
    }

    fun getMyCompany(){
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val company = repository.getMyCompany(0)
                    if (company.isSuccessful) {
//                        appViewModel.storeCompany(company.body()!!)
                    }
                    val response = repository.getMeAsCompany()
                    if (response.isSuccessful) {
                        appViewModel.storeCompany(response.body()!!)
                    }
                } catch (ex: Exception) {
                    Log.e("aymenbabaycompany", "c bon error ${ex.message}")
                }
            }
        }
    }

    fun getAllCompaniesContaining(search : String){
        allCompanies = emptyList()
        viewModelScope.launch {
            try {
                val companies = repository.getAllCompaniesContainingg(search)
                if(companies.isSuccessful){
                    allCompanies = companies.body()!!
                }
                val response = repository.getAllCompaniesContaining(search)
                if(response.isSuccessful){
                    response.body()?.forEach {company ->
                        insertCompany(company)
                    }
                    allCompanies = companies.body()!!
                }
            }catch (ex : Exception){
                Log.e("aymenbabaycompanies","error is : ${ex.message}")
            }
        }
    }

     fun getMyCompany(onCompanyRetrieved: (CompanyDto?) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.Main){
            try {
                companyDataStore.data
                    .catch { exception ->
                        Log.e("getTokenError", "Error getting token: ${exception.message}")
                        onCompanyRetrieved(null)
                    }
                    .collect { company ->
                        onCompanyRetrieved(company)
                    }
            } catch (e: Exception) {
                Log.e("getTokenError", "Error getting token: ${e.message}")
                onCompanyRetrieved(null)
            }
            }
        }
    }

    fun MakeAsPointSeller(status : Boolean, id : Long){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.makeAsPointSeller(status,id)
            }catch (ex : Exception){
                Log.e("getTokenError", "Error getting token: ${ex.message}")
            }
        }
    }
}