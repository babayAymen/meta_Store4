package com.aymen.store.model.repository.ViewModel

import android.util.Log
import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Category
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor (
    private val repository: GlobalRepository,
    private val realm: Realm,
    private val sharedViewModel : SharedViewModel
): ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    var categories : StateFlow<List<Category>> = _categories

    var category by mutableStateOf(Category())
//    @SuppressLint("SuspiciousIndentation")
@SuppressLint("SuspiciousIndentation")
fun getAllCategoryByCompany(companyId : Long?){
                    viewModelScope.launch (Dispatchers.IO){
                            try {
                                val response = repository.getAllCategoryByCompany(sharedViewModel.company.value.id?:0,companyId?:0)
                                if(response.isSuccessful) {
                                    Log.e(
                                        "aymenbabaycategory",
                                        "categories size ${response.body()?.size}"
                                    )
                                    response.body()?.forEach {
                                        realm.write {
                                            copyToRealm(it, UpdatePolicy.ALL)
                                        }
                                    }
                                }
                            } catch (ex: Exception) {
                                Log.e("aymenbabaycategory", "exception : ${ex.message}")
                            }
                            _categories.value = repository.getAllCategoriesLocally(companyId?:0)
                            Log.e(
                                "aymenbabaycategory",
                                "categories size locally ${categories.value.size} and companyId $companyId"
                            )

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

}