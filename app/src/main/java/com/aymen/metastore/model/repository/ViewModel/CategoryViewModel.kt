package com.aymen.store.model.repository.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.dto.CategoryDto
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.metastore.model.entity.model.Category
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.ErrorResponse
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.entity.room.remoteKeys.CategoryRemoteKeysEntity
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor (
    private val repository: GlobalRepository,
    private val room : AppDatabase,
     private val sharedViewModel : SharedViewModel,
    private val useCases: MetaUseCases,
    private val context : Context
): ViewModel() {

    private val categoryDao = room.categoryDao()

    private val _categories : MutableStateFlow<PagingData<Category>> = MutableStateFlow(PagingData.empty())
    val categories : StateFlow<PagingData<Category>> get() = _categories

    private val _companyCategories : MutableStateFlow<PagingData<Category>> = MutableStateFlow(PagingData.empty())
    val companyCategories : StateFlow<PagingData<Category>> get() = _companyCategories

    private val _categoryForUpdate : MutableStateFlow<Category> = MutableStateFlow(Category())
    val categoryForUpdate : StateFlow<Category> get() = _categoryForUpdate

    var category by mutableStateOf(Category())
    var update by mutableStateOf(false)
    val company: StateFlow<Company?> = sharedViewModel.company
    val user: StateFlow<User?> = sharedViewModel.user

init {
    viewModelScope.launch {
        useCases.getPagingCategoryByCompany(sharedViewModel.company.value.id?:0)
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
            .collect { _categories.value = it.map { category -> category } }
    }
    getCategoryByCompany(sharedViewModel.company.value.id?:0)

}

    fun assignCategoryForUpdate(item : Category){
        _categoryForUpdate.value = item
    }

    fun deleteCategory(){
        Toast.makeText(context, "sorry you can not delete category", Toast.LENGTH_SHORT).show()
    }

    fun addCtagory(item : Category , category: String, file: File?) {
        viewModelScope.launch {
            val lastCategoryId = categoryDao.getLatestCategoryId(sharedViewModel.company.value.id!!)
            val id = if(lastCategoryId != null) lastCategoryId + 1 else 1
            val categoryCount = categoryDao.getCategoryCount(sharedViewModel.company.value.id!!)
            val page = categoryCount.div(PAGE_SIZE)
            val remainder = categoryCount % PAGE_SIZE
            val remoteKey = CategoryRemoteKeysEntity(
                id = id,
                prevPage = if(page == 0)null else page-1,
                nextPage = if(remainder!=0) 1 else page+1,
                lastUpdated = null
            )
            room.withTransaction {
                categoryDao.insertSingCategory(item.copy(id = id, company = sharedViewModel.company.value).toCategoryEntity())
                categoryDao.insertSingelKey(remoteKey)
            }
            val result : Result<Response<CategoryDto>> = runCatching {
                repository.addCategory(category,file)
            }
            result.fold(
                onSuccess = {success ->
                    val response = success.body()
                    if(success.isSuccessful){
                        if(response != null) {
                            room.withTransaction {
                                categoryDao.deleteCategoryById(id)
                                categoryDao.deleteCategoryRemoteKeyById(id)
                                categoryDao.insertSingCategory(response.toCategory())
                                categoryDao.insertSingelKey(remoteKey.copy(
                                    id = response.id!!
                                ))
                            }
                        }
                    }else{
                        room.withTransaction {
                            categoryDao.deleteCategoryById(id)
                            categoryDao.deleteCategoryRemoteKeyById(id)
                        }
                        val errorBodyString = success.errorBody()?.string()
                        errorBlock(errorBodyString)
                    }
                },
                onFailure = {failure ->

                }
            )




        }
    }

    fun updateCategory(item : Category, category : String, file: File?){
        viewModelScope.launch {
            Log.e("updatecategory","category image ois : ${item.image}")
            categoryDao.insertSingCategory(if(item.image == null)item.copy(image = categoryForUpdate.value.image).toCategoryEntity()
            else item.toCategoryEntity())
            val result : Result<Response<CategoryDto>> = runCatching {
                repository.updateCategory(category,file)
            }
            result.fold(
                onSuccess = { success ->
                    val response = success.body()
                    if (success.isSuccessful) {
                        if (response != null) {
                            categoryDao.insertSingCategory(response.toCategory())
                        }
                    }
                },
                onFailure = {failure ->

                }
            )
        }
    }


    fun getCategoryByCompany(companyId : Long){
        viewModelScope.launch {
            useCases.getCategoryTemp(companyId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect { _companyCategories.value = it }
        }
    }

    private fun errorBlock(error : String?){
        viewModelScope.launch{
            val re = Gson().fromJson(error, ErrorResponse::class.java)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "error : ${re.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}