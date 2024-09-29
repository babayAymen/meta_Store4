package com.aymen.store.model.repository.ViewModel

import android.util.Log
import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.store.model.entity.realm.Category
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor (
    private val repository: GlobalRepository,
    private val realm: Realm,
    private val companyViewModel : CompanyViewModel
): ViewModel() {

    var categories by mutableStateOf(emptyList<Category>())
    var category by mutableStateOf(Category())
//    @SuppressLint("SuspiciousIndentation")
@SuppressLint("SuspiciousIndentation")
fun getAllCategoryByCompany(){
        Log.e("aymenbabaycategory","get all categories begin")
                companyViewModel.getMyCompany{company ->
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            try {
                val categorie = company?.let { it1 -> repository.getAllCategoryByCompany(it1.id!!,it1.id!!).body() }!!
                    Log.e("aymenbabaycategory","categories size ${categorie.size}")
                categories = categorie
                category = categories[0]
                categorie.forEach{
                    Log.e("aymenbabaycategory","categories image ${it.image}")
                    realm.write {
                        copyToRealm(it, UpdatePolicy.ALL)
                    }
                }
            }catch (_ex : Exception){
                Log.e("aymenbabaycategory","exception : $_ex")
            }
            categories =  repository.getAllCategoriesLocally()
                Log.e("aymenbabaycategory","categories size locally ${categories.size}")
            }
                }
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