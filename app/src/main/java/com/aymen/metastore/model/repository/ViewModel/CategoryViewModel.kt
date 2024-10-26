package com.aymen.store.model.repository.ViewModel

import android.util.Log
import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.aymen.metastore.model.entity.converterRealmToApi.mapCategoryToRoomCategory
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.Category
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.entity.dto.CategoryDto
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
class CategoryViewModel @Inject constructor (
    private val repository: GlobalRepository,
    private val room : AppDatabase,
     sharedViewModel : SharedViewModel
): ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    var categories : StateFlow<List<Category>> = _categories
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
                            _categories.value = room.categoryDao().getAllCategoriesByCompanyId(companyId)


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