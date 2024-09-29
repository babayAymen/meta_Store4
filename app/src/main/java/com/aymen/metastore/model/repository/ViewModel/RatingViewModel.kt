package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.entity.api.RatingDto
import com.aymen.metastore.model.entity.realm.Rating
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val realm : Realm,
): ViewModel() {

    var rate by mutableStateOf(0)
    var allRating by mutableStateOf(emptyList<Rating>())
    var rating by mutableStateOf(false)
    fun getAllRating(id : Long, type : AccountType){
        viewModelScope.launch(Dispatchers.IO) {
            try {
               val response = repository.getAllMyRating(id,type)
                allRating = response.body()!!
            }catch (_ex : Exception){
                Log.e("aymenbabayRating","getAllRating exption: $_ex")
            }
        }
    }

    fun doRate(rating : String, image : File?){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                Log.e("aymenbabayRating","getAllRating ratee: ${image?.name}")

            try {
               val response = repository.doRating(rating,image)
            }catch (_ex : Exception){
                Log.e("aymenbabayRating","doRating exption: $_ex")
            }
            }
        }
    }


}