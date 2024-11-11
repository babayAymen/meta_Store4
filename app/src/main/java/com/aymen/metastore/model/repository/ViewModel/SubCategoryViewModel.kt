package com.aymen.store.model.repository.ViewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.aymen.metastore.model.entity.converterRealmToApi.mapCategoryToRoomCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapSubCategoryToRoomSubCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.SubCategory
import com.aymen.metastore.model.entity.roomRelation.CategoryWithCompanyAndUser
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.entity.dto.SubCategoryDto
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
class SubCategoryViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val room : AppDatabase,
     sharedViewModel: SharedViewModel
) : ViewModel() {

    var subCategoryId by mutableStateOf(0L)
    var myCompany by mutableStateOf(sharedViewModel.company.value)
    private val _subCategories = MutableStateFlow<List<SubCategory>>(emptyList())
    var subCategories: StateFlow<List<SubCategory>> = _subCategories
    private val _categoryWithRelations = MutableStateFlow<CategoryWithCompanyAndUser?>(null)
    var categoryWithRelations : StateFlow<CategoryWithCompanyAndUser?> = _categoryWithRelations

    private val _categoryRelationsMap = mutableMapOf<Long, MutableStateFlow<CategoryWithCompanyAndUser?>>()


    @SuppressLint("SuspiciousIndentation")
    fun insertsubcategory(categoryId : Long) {// a verifier
        if (categoryId != 0L) {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val response = repository.getSubCategoryByCategory(categoryId, myCompany.id!!)
                            if (response.isSuccessful) {
                                response.body()?.forEach { subCategory ->
                                    insertSubCategory(subCategory)
                                }
                            }
                        } catch (_ex: Exception) {
                            Log.e("aymenbabaysubcategoty", "error $_ex")
                        }
                    }
                }
        }
        else{
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val response = repository.getAllSubCategories(myCompany.id!!)
                            if (response.isSuccessful) {
                                response.body()?.forEach { subCategory ->
                                   insertSubCategory(subCategory)
                                }
                            }
//                            getAllSubCategories()
                        } catch (ex: Exception) {
                            Log.e("insertsubcategory","exception : ${ex.message}")
                        }
                    }
                }
        }
    }

    fun getAllSubCategories(companyId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllSubCategories(companyId = companyId)
                if(response.isSuccessful){
                    response.body()!!.forEach { subCategory ->
                        insertSubCategory(subCategory)
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllSubCategories","exception : ${ex.message}")
            }
        _subCategories.value = room.subCategoryDao().getAllSubCategoriesByCompanyId(companyId)
        }
    }

    @Transaction
    suspend fun insertSubCategory(subCategory: SubCategoryDto){

        room.userDao().insertUser(mapUserToRoomUser(subCategory.company?.user))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(subCategory.company))
        room.categoryDao().insertCategory(mapCategoryToRoomCategory(subCategory.category!!))
        room.subCategoryDao().insertSubCategory(mapSubCategoryToRoomSubCategory(subCategory))
    }
    fun getAllSubCtaegoriesByCategory(categoryId : Long,companyId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val response = repository.getSubCategoryByCategory(categoryId,companyId)
                if(response.isSuccessful){
                    response.body()!!.forEach {
                        insertSubCategory(it)
                    }
                }
            }catch (ex : Exception){
            Log.e("getAllSubCtaegoriesByCategory","exception : ${ex.message}")
            }
            _subCategories.value = room.subCategoryDao().getAllSubCategoriesByCategoryId(categoryId)
        }
    }

    fun addSubCategoryWithImage(sousCategory : String, file : File){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            repository.addSubCtagoryWithImage(sousCategory,file)
            }
        }
    }

    fun addSubCategoryWithoutImage(sousCategory : String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            repository.addSubCategoryWithoutImage(sousCategory)
            }
        }
    }

     fun getCategoryById(categoryId: Long){

         val stateFlow = _categoryRelationsMap.getOrPut(categoryId) {
             MutableStateFlow(null)
         }

         viewModelScope.launch {
             val result = room.categoryDao().getCategoryWithCompanyAndUser(categoryId)
             stateFlow.value = result
         }


    }

    fun getCategoryFlow(subCategoryId: Long): StateFlow<CategoryWithCompanyAndUser?> {
        return _categoryRelationsMap.getOrPut(subCategoryId) { MutableStateFlow(null) }
    }



}







