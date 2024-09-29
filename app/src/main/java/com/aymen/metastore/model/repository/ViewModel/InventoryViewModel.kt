package com.aymen.store.model.repository.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.store.model.entity.realm.Inventory
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel  @Inject constructor
    (private val repository: GlobalRepository,
     private val realm : Realm
)
    : ViewModel() {

    var inventories by mutableStateOf(emptyList<Inventory>())
    val companyId by mutableLongStateOf(1)

    fun getInventory(){
        viewModelScope.launch {
            try {
            val inventory = repository.getInventory(companyId)
            if(inventory.isSuccessful){
                inventory.body()?.forEach {
                    realm.write {
                        copyToRealm(it, UpdatePolicy.ALL)
                    }
                }
            }
            }catch (exception : Exception){}
            inventories = repository.getInventoryLocally()
        }
    }

}