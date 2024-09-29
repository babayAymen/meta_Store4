package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.store.model.entity.realm.Provider
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProviderViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val realm : Realm
) :ViewModel(){

    var providers by mutableStateOf(emptyList<Provider>())
    val companyId by mutableLongStateOf(1)

    fun getAllMyProviders(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val provider = repository.getAllMyProvider(companyId)
                    if (provider.isSuccessful) {
                        provider.body()?.forEach {
                            realm.write {
                                copyToRealm(it, UpdatePolicy.ALL)
                            }
                        }
                    }
                } catch (_ex: Exception) {
                }
                providers = repository.getAllMyProviderLocally()
            }
        }
    }

    fun addProvider(provider: String, file : File){
        viewModelScope.launch {
            try {
                repository.addProvider(provider,file)
            }catch (_ex : Exception){}
            getAllMyProviders()
        }
    }

    fun addProviderWithoutImage(provider: String){
        viewModelScope.launch {
            try {
                repository.addProviderWithoutImage(provider)
            }catch (_ex : Exception){}
            getAllMyProviders()
        }
    }


}