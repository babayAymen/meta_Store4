package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.entity.realm.Client
import com.aymen.store.model.entity.realm.ClientProviderRelation
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
    private val dataStore: DataStore<Company>,
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
            sharedViewModel._company.value = it ?: Company()
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

                    val company = repository.getAllMyProvider(company?.id!!).body()!!
                    company.forEach {
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }

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

    fun getMyParent(){
        getMyCompany { company ->

        viewModelScope.launch {
            withContext(Dispatchers.IO){

            try {
                val parents = repository.getMyParent(company?.id!!)
                if(parents.isSuccessful){
                    realm.write {
                        copyToRealm(parents.body()!!,UpdatePolicy.ALL)
                    }
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
                        appViewModel.storeCompany(company.body()!!)
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
                val companies = repository.getAllCompaniesContaining(search)
                if(companies.isSuccessful){
                    Log.e("aymenbabaycompanies","array size : ${companies.body()?.size}")
                    allCompanies = companies.body()!!
                }
            }catch (_ex : Exception){
                Log.e("aymenbabaycompanies","error is : $_ex")
            }
        }
    }

/*    fun getAllClientsContaining(search : String, searchType : SearchType, searchCategory: SearchCategory){
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
                myAllCompanies = response.body()!!
                Log.e("aymenbabayclients","my all companies size : ${myAllCompanies.size}")
            }catch (_ex : Exception){
                Log.e("aymenbabayclients","error is : $_ex")
            }
        }
    }*/

     fun getMyCompany(onCompanyRetrieved: (Company?) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.Main){
            try {
                dataStore.data
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
            }catch (_ex : Exception){
                Log.e("getTokenError", "Error getting token: ${_ex.message}")
            }
        }
    }
}