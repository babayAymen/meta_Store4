package com.aymen.metastore.model.repository.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SubCategoryViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val room : AppDatabase,
     sharedViewModel: SharedViewModel,
    private val useCases: MetaUseCases
) : ViewModel() {

    var subCategoryId by mutableStateOf(0L)
    var myCompany by mutableStateOf(sharedViewModel.company.value)
    private val _subCategories : MutableStateFlow<PagingData<SubCategory>> = MutableStateFlow(PagingData.empty())
    val subCategories: StateFlow<PagingData<SubCategory>> get() = _subCategories

    private val _allSubCategories : MutableStateFlow<PagingData<SubCategory>> = MutableStateFlow(PagingData.empty())
    var allSubCategories : StateFlow<PagingData<SubCategory>> = _allSubCategories

    init {
        viewModelScope.launch {
            useCases.getPagingSubCategoryByCompany()
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect { _subCategories.value = it.map {subcategory -> subcategory.toSubCategory() } }

        }
    }

    fun getAllSubCategoriesByCategoryId(categoryId : Long){
        viewModelScope.launch {
            useCases.getAllSubCategoryByCategoryId(categoryId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _allSubCategories.value = it.map { subcategory -> subcategory }
                }
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







