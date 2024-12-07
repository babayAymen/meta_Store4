package com.aymen.store.model.repository.ViewModel

import android.util.Log
import android.annotation.SuppressLint
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.metastore.model.entity.model.Category
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.User
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
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

    private val _categories : MutableStateFlow<PagingData<Category>> = MutableStateFlow(PagingData.empty())
    val categories : StateFlow<PagingData<Category>> get() = _categories

    private val _companyCategories : MutableStateFlow<PagingData<Category>> = MutableStateFlow(PagingData.empty())
    val companyCategories : StateFlow<PagingData<Category>> get() = _companyCategories


    var category by mutableStateOf(Category())

    val company: StateFlow<Company?> = sharedViewModel.company
    val user: StateFlow<User?> = sharedViewModel.user

init {
    viewModelScope.launch {
        useCases.getPagingCategoryByCompany(sharedViewModel.company.value.id?:0)
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
            .collect { _categories.value = it.map { category -> category } }
    }

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

    fun getCategoryByCompany(companyId : Long){
        viewModelScope.launch {
            useCases.getPagingCategoryByCompany(companyId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect { _companyCategories.value = it.map { category -> category } }
        }
    }
}