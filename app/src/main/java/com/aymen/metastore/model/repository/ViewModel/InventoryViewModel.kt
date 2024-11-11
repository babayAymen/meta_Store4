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
import com.aymen.metastore.model.entity.room.Inventory
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.metastore.model.entity.roomRelation.InventoryWithArticle
import com.aymen.store.model.entity.converterRealmToApi.mapArticelDtoToRoomArticle
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRoomArticleCompany
import com.aymen.store.model.entity.dto.InventoryDto
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel  @Inject constructor
    (
    private val repository: GlobalRepository,
    private val room : AppDatabase
)
    : ViewModel() {


    private var _inventories = MutableStateFlow(emptyList<InventoryWithArticle>())
    val inventories: StateFlow<List<InventoryWithArticle>> = _inventories

    val companyId by mutableLongStateOf(0)

    fun getInventory(){
        viewModelScope.launch {
            try {
                val response = repository.getInventory(companyId)
                if (response.isSuccessful) {
                    response.body()?.forEach {inventory ->
                        insertInventory(inventory)
                    }
                }
            }catch (exception : Exception){
                Log.e("getInventory","exception is : ${exception.message}")
            }
            _inventories.value = room.inventoryDao().getAllInventories()
        }
    }

    suspend fun insertInventory(inventory : InventoryDto){
        room.categoryDao().insertCategory(mapCategoryToRoomCategory(inventory.article?.category!!))
        room.subCategoryDao().insertSubCategory(mapSubCategoryToRoomSubCategory(inventory.article.subCategory!!))
        room.articleDao().insertArticle(mapArticelDtoToRoomArticle(inventory.article.article!!))
        room.articleCompanyDao().insertArticle(mapArticleCompanyToRoomArticleCompany(inventory.article))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(inventory.company))
        room.inventoryDao().insertInventory(mapInventoryToRoomInventor(inventory))
    }

}