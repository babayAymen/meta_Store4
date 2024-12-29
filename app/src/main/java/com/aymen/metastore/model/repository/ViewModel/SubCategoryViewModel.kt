package com.aymen.metastore.model.repository.ViewModel

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
import com.aymen.metastore.model.entity.dto.SubCategoryDto
import com.aymen.metastore.model.entity.model.ErrorResponse
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.CategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.SubCategoryRemoteKeysEntity
import com.aymen.metastore.model.usecase.MetaUseCases
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
class SubCategoryViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val room : AppDatabase,
    private val sharedViewModel: SharedViewModel,
    private val useCases: MetaUseCases,
    private val context: Context
) : ViewModel() {

    var subCategoryId by mutableStateOf(0L)
    var myCompany by mutableStateOf(sharedViewModel.company.value)
    private val _subCategories : MutableStateFlow<PagingData<SubCategory>> = MutableStateFlow(PagingData.empty())
    val subCategories: StateFlow<PagingData<SubCategory>> get() = _subCategories

    private val _companySubCategories : MutableStateFlow<PagingData<SubCategory>> = MutableStateFlow(PagingData.empty())
    val companySubCategories: StateFlow<PagingData<SubCategory>> get() = _companySubCategories

    private val _allSubCategories : MutableStateFlow<PagingData<SubCategory>> = MutableStateFlow(PagingData.empty())
    var allSubCategories : StateFlow<PagingData<SubCategory>> = _allSubCategories

    private val _subCategoryForUpdate : MutableStateFlow<SubCategory> = MutableStateFlow(SubCategory())
    val subCategoryForUpdate : StateFlow<SubCategory> get() = _subCategoryForUpdate

    var update by mutableStateOf(false)
    init {
        viewModelScope.launch {
            useCases.getPagingSubCategoryByCompany(sharedViewModel.company.value.id?:0)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect { _subCategories.value = it.map {subcategory -> subcategory.toSubCategory() }
                }
        }
    }

    fun setSubCategory(){
        _companySubCategories.value = PagingData.empty()
    }
    fun assignSubCategoryForUpdate(item : SubCategory){
        _subCategoryForUpdate.value = item
    }



    fun deleteSubCategories(){
        Toast.makeText(context, "sorry you can not delete subcategory", Toast.LENGTH_SHORT).show()
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

    private val subCategoryDao = room.subCategoryDao()
    private val categoryDao = room.categoryDao()

    fun addSubCategory(item : SubCategory ,sousCategory : String, file : File?) {
        viewModelScope.launch(Dispatchers.IO) {
            val lastSubCategoryId = subCategoryDao.getLatestSubCategoryId(sharedViewModel.company.value.id!!)
            val id = if (lastSubCategoryId != null) lastSubCategoryId + 1 else 1
            val subCategoryCount =
                subCategoryDao.getSubCategoryCount(sharedViewModel.company.value.id!!)
            val page = subCategoryCount.div(PAGE_SIZE)
            val remainedKey = subCategoryCount % PAGE_SIZE
            val remoteKey = SubCategoryRemoteKeysEntity(
                id = id,
                previousPage = if (page == 0) null else page - 1,
                nextPage = if (remainedKey != 0) 1 else page + 1
            )
            room.withTransaction {
                subCategoryDao.insertSingleSubCategory(item.copy(id = id).toSubCategoryentity())
                subCategoryDao.insertSingleSubCategoryRemoteKey(remoteKey)
            }
            val result: Result<Response<SubCategoryDto>> = runCatching {
                repository.addSubCtagory(sousCategory, file)
            }
            result.fold(
                onSuccess = { success ->
                    room.withTransaction {
                        subCategoryDao.deleteSubCategoryById(id)
                        subCategoryDao.deleteSubCategoryRemoteKey(id)
                    }
                    val response = success.body()
                    if (success.isSuccessful) {
                        if (response != null) {
                            room.withTransaction {
                                subCategoryDao.insertSingleSubCategory(response.toSubCategory(isSubcategory = true))
                                subCategoryDao.insertSingleSubCategoryRemoteKey(remoteKey.copy(id = response.id!!))
                            }
                        }
                    }else {
                        val errorBodyString = success.errorBody()?.string()
                        errorBlock(errorBodyString)
                    }
                },
                onFailure = {}
            )
        }
    }

    fun updateSubCategory(item: SubCategory, sousCategory: String, file: File?){
        viewModelScope.launch {
            Log.e("logfailure","sub categ : $item")
            room.withTransaction {

                categoryDao.insertSingCategory(item.category?.toCategoryEntity()!!)
                categoryDao.insertSingelKey(CategoryRemoteKeysEntity(
                    id = item.category?.id!!,
                    prevPage = null,
                    nextPage = null,
                    lastUpdated = null
                ))
            subCategoryDao.insertSingleSubCategory(if(item.image == null) item.copy(image = subCategoryForUpdate.value.image).toSubCategoryentity()
            else item.toSubCategoryentity())
            }
            val result : Result<Response<SubCategoryDto>> = runCatching {
                repository.updateSubCategory(sousCategory,file)
            }
            result.fold(
                onSuccess = {success ->
                    val response = success.body()
                    if(success.isSuccessful) {
                        if (response != null) {
                            room.withTransaction {
                                subCategoryDao.insertSingleSubCategory(response.toSubCategory(isSubcategory = true))
                                categoryDao.insertSingCategory(response.category?.toCategory(isCategory = false)!!)
                                categoryDao.insertSingelKey(CategoryRemoteKeysEntity(
                                    id = response.category?.id!!,
                                    prevPage = null,
                                    nextPage = null,
                                    lastUpdated = null
                                ))
                            }
                        }
                    }
                    else{
                        val errorBodyString = success.errorBody()?.string()
                        errorBlock(errorBodyString)
                    }
                },
                onFailure = {failure ->
                    Log.e("logfailure","failur : $failure")
                }
            )
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







