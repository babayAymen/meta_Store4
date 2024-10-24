package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.entity.converterRealmToApi.mapCategoryToRoomCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapInventoryToRoomInventor
import com.aymen.metastore.model.entity.converterRealmToApi.mapSubCategoryToRoomSubCategory
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.store.model.entity.converterRealmToApi.mapArticelDtoToRoomArticle
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRoomArticleCompany
import com.aymen.store.model.entity.dto.InventoryDto
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
     private val realm : Realm,
            private val room : AppDatabase
)
    : ViewModel() {

    var inventories by mutableStateOf(emptyList<Inventory>())
    val companyId by mutableLongStateOf(0) //this changes are when i try to change to room database it was 1

    fun getInventory(){
        viewModelScope.launch {
            try {
                val inventor = repository.getInventoryy(companyId)
                if (inventor.isSuccessful) {
                    inventor.body()?.forEach {
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                }
                val response = repository.getInventory(companyId)
                if (response.isSuccessful) {
                    response.body()?.forEach {inventory ->
                        insertInventory(inventory)
                    }
                }
            }catch (exception : Exception){
                Log.e("getInventory","exception is : ${exception.message}")
            }
            inventories = repository.getInventoryLocally()
        }
    }

    suspend fun insertInventory(inventory : InventoryDto){
//        inventory.bestClient.let {  } because of multi type of client company and user
        room.categoryDao().insertCategory(mapCategoryToRoomCategory(inventory.article?.category!!))
        room.subCategoryDao().insertSubCategory(mapSubCategoryToRoomSubCategory(inventory.article.subCategory))
        room.articleDao().insertArticle(mapArticelDtoToRoomArticle(inventory.article.article))
        room.articleCompanyDao().insertArticle(mapArticleCompanyToRoomArticleCompany(inventory.article))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(inventory.company))
        room.inventoryDao().insertInventory(mapInventoryToRoomInventor(inventory))
    }

}