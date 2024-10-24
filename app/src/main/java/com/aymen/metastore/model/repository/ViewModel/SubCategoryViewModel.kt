package com.aymen.store.model.repository.ViewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.aymen.metastore.model.entity.converterRealmToApi.mapCategoryToRoomCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapSubCategoryToRoomSubCategory
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.store.model.entity.dto.SubCategoryDto
import com.aymen.store.model.entity.realm.SubCategory
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
class SubCategoryViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val realm : Realm,
    private val room : AppDatabase,
    private val companyViewModel: CompanyViewModel
) : ViewModel() {

    var subCategoryId by mutableStateOf(0L)
//    val companyId by mutableLongStateOf(1)


    private val _subCategories = MutableStateFlow<List<SubCategory>>(emptyList())
    var subCategories: StateFlow<List<SubCategory>> = _subCategories

    @SuppressLint("SuspiciousIndentation")
    fun insertsubcategory(categoryId : Long) {// a verifier
        if (categoryId != 0L) {
            companyViewModel.getMyCompany{
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val subCategoriesResponse =
                            it?.let { it1 ->
                                repository.getSubCategoryByCategoryy(
                                    categoryId,
                                    it1.id!!
                                )
                            }
                        if (subCategoriesResponse != null) {
                            if (subCategoriesResponse.isSuccessful) {
                                subCategoriesResponse.body()!!.forEach { subCategory ->
                                    realm.write {
                                        copyToRealm(subCategory, UpdatePolicy.ALL)
                                    }
                                }
                            } else {
                            }
                        }

                    } catch (_ex: Exception) {
                        Log.e("aymenbabaysubcategoty", "error $_ex")
                    }
                }
            }

            }
        }
        else{
            companyViewModel.getMyCompany {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val subCategoriesResponse = repository.getAllSubCategoriess(it?.id!!)
                            if (subCategoriesResponse.isSuccessful) {
                                subCategoriesResponse.body()!!.forEach { subCategory ->
                                    realm.write {
                                        copyToRealm(subCategory, UpdatePolicy.ALL)
                                    }
                                }
                            }
//                            getAllSubCategories()
                        } catch (_ex: Exception) {
                        }
                    }
                }
            }
        }
    }

    fun getAllSubCategories(companyId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respons = repository.getAllSubCategoriess(companyId = companyId)
                if(respons.isSuccessful){
                    respons.body()!!.forEach { subCategory ->
                        realm.write {
                            copyToRealm(subCategory, UpdatePolicy.ALL)
                        }
                    }
                }
                val response = repository.getAllSubCategories(companyId = companyId)
                if(response.isSuccessful){
                    response.body()!!.forEach { subCategory ->
                        insertSubCategory(subCategory)
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllSubCategories","exception : ${ex.message}")
            }
        _subCategories.value = repository.getAllSubCategoriesLocally(companyId = companyId)
        }
    }

    @Transaction
    suspend fun insertSubCategory(subCategory: SubCategoryDto){

        room.categoryDao().insertCategory(mapCategoryToRoomCategory(subCategory.category))
        room.subCategoryDao().insertSubCategory(mapSubCategoryToRoomSubCategory(subCategory))
    }
    fun getAllSubCtaegoriesByCategory(categoryId : Long,companyId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val respons = repository.getSubCategoryByCategoryy(categoryId,companyId)
                if(respons.isSuccessful){
                    respons.body()!!.forEach {
                        realm.write {
                            copyToRealm(it,UpdatePolicy.ALL)
                        }
                    }
                }
                val response = repository.getSubCategoryByCategory(categoryId,companyId)
                if(response.isSuccessful){
                    response.body()!!.forEach {
                        insertSubCategory(it)
                    }
                }
            }catch (ex : Exception){
            Log.e("getAllSubCtaegoriesByCategory","exception : ${ex.message}")
            }
            _subCategories.value = repository.getSubCategoriesByCategoryLocally(categoryId, companyId)
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
}