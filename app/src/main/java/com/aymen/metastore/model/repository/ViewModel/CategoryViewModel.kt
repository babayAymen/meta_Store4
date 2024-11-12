package com.aymen.store.model.repository.ViewModel

import android.util.Log
import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.room.Transaction
import com.aymen.metastore.model.entity.converterRealmToApi.mapCategoryToRoomCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.Category
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.entity.dto.CategoryDto
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor (
    private val repository: GlobalRepository,
    private val room : AppDatabase,
     sharedViewModel : SharedViewModel,
    private val useCases: MetaUseCases
): ViewModel() {

    private val _categories : MutableStateFlow<PagingData<CategoryDto>> = MutableStateFlow(PagingData.empty())
    val categories : StateFlow<PagingData<CategoryDto>> get() = _categories


//    private val _categories = MutableStateFlow<List<Category>>(emptyList())
//    var categories : StateFlow<List<Category>> = _categories

init {
    viewModelScope.launch {
        useCases.getPagingCategoryByCompany(pageSize = PAGE_SIZE)
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
            .collect { _categories.value = it }
    }

}

    var category by mutableStateOf(CategoryDto())
    val myCompany = sharedViewModel.company.value
@SuppressLint("SuspiciousIndentation")
fun getAllCategoryByCompany(companyId : Long){
                    viewModelScope.launch (Dispatchers.IO){
                            try {
                                val response = repository.getAllCategoryByCompany(companyId)
                                if(response.isSuccessful) {
                                    response.body()?.forEach {
                                        insertCategory(it)
                                    }
                                }
                            } catch (ex: Exception) {
                                Log.e("aymenbabaycategory", "exception : ${ex.message}")
                            }
//                            _categories.value = room.categoryDao().getAllCategoriesByCompanyId(companyId) // test for pagination



                    }
    }


    @Transaction
    suspend fun insertCategory(category : CategoryDto){
        room.userDao().insertUser(mapUserToRoomUser(category.company?.user))
        room.companyDao().insertCompany(mapCompanyToRoomCompany(category.company))
        room.categoryDao().insertCategory(mapCategoryToRoomCategory(category))
    }

    fun addCtagory(category: String, file: File) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            repository.addCategoryApiWithImage(category,file)
            }
        }
    }

    fun addCategoryWithoutImage(category: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            repository.addCategoryApiWithoutImeg(category)
            }
        }
    }

}