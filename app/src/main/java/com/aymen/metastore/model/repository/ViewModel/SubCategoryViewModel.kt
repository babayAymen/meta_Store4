package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
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
import com.aymen.store.model.Enum.AccountType
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
    private val sharedViewModel: SharedViewModel,
    private val useCases: MetaUseCases
) : ViewModel() {

    var subCategoryId by mutableStateOf(0L)
    var myCompany by mutableStateOf(sharedViewModel.company.value)
    private val _subCategories : MutableStateFlow<PagingData<SubCategory>> = MutableStateFlow(PagingData.empty())
    val subCategories: StateFlow<PagingData<SubCategory>> get() = _subCategories

    private val _companySubCategories : MutableStateFlow<PagingData<SubCategory>> = MutableStateFlow(PagingData.empty())
    val companySubCategories: StateFlow<PagingData<SubCategory>> get() = _companySubCategories

    private val _allSubCategories : MutableStateFlow<PagingData<SubCategory>> = MutableStateFlow(PagingData.empty())
    var allSubCategories : StateFlow<PagingData<SubCategory>> = _allSubCategories

    init {
        viewModelScope.launch {
            useCases.getPagingSubCategoryByCompany(sharedViewModel.company.value.id?:0)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect { _subCategories.value = it.map {subcategory -> subcategory.toSubCategory() }
                }
        }
    }

    fun deleteSubCategories(){
        _companySubCategories.value = PagingData.empty()
    }

    fun getAllSubCategoriesByCategoryId(categoryId : Long, companyId : Long){
        viewModelScope.launch {
            Log.e("affetcsubcategory","category from view model id $categoryId and company is $companyId")
            useCases.getAllSubCategoryByCategoryId(categoryId, companyId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _allSubCategories.value = it
                    _companySubCategories.value = it
                }
        }
    }

    fun addSubCategoryWithImage(sousCategory : String, file : File){
        viewModelScope.launch(Dispatchers.IO) {
           val response = repository.addSubCtagoryWithImage(sousCategory,file)


        }
    }

    fun addSubCategoryWithoutImage(sousCategory : String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            repository.addSubCategoryWithoutImage(sousCategory)
            }
        }
    }

    fun getAllSubCategoriesByCompanyId(companyId : Long){
        viewModelScope.launch {
            Log.e("subcategoryviewModel","call getAllSubCategoriesByCompanyId1")
            useCases.getPagingSubCategoryByCompany(companyId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect { _companySubCategories.value = it.map {subcategory -> subcategory.toSubCategory() }
                }
        }
    }


}







