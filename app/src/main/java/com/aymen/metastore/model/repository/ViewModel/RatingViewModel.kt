package com.aymen.metastore.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.aymen.metastore.model.entity.Dto.RatingDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapRatingToRoomRating
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.realm.Rating
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val room : AppDatabase,
): ViewModel() {

    var rate by mutableStateOf(0)
    var _allRating = MutableStateFlow<List<Rating>>(emptyList())
    val allRating : StateFlow<List<Rating>> = _allRating

    var rating by mutableStateOf(false)
    var enableToRating by mutableStateOf(false)
    var enableToComment by mutableStateOf(false)
    fun getAllRating(id : Long, type : AccountType){
        viewModelScope.launch(Dispatchers.IO) {
            try {
               val respons = repository.getAllMyRatingg(id,type)
                _allRating.value = respons.body()!!
               val response = repository.getAllMyRating(id,type)
                if(response.isSuccessful){
                    response.body()?.forEach {
                        insertRating(it)
                    }
                }
            }catch (_ex : Exception){
                Log.e("aymenbabayRating","getAllRating exption: $_ex")
            }
        }
    }

    @Transaction
    suspend fun insertRating(rating : RatingDto){
        rating.rateeUser?.let {
            room.userDao().insertUser(mapUserToRoomUser(it))
        }
        rating.raterUser?.let {
            room.userDao().insertUser(mapUserToRoomUser(it))
        }
        rating.rateeCompany?.let {
            room.userDao().insertUser(mapUserToRoomUser(it.user))
            room.companyDao().insertCompany(mapCompanyToRoomCompany(it))
        }
        rating.raterCompany?.let {
            room.userDao().insertUser(mapUserToRoomUser(it.user))
            room.companyDao().insertCompany(mapCompanyToRoomCompany(it))
        }
        room.ratingDao().insertRating(mapRatingToRoomRating(rating))
    }

    fun doRate(rating : String, image : File?){
        viewModelScope.launch(Dispatchers.IO) {
            try {
               val response = repository.doRating(rating,image)
            }catch (_ex : Exception){
                Log.e("aymenbabayRating","doRating exption: $_ex")
            }
        }
    }

    fun enabledToCommentCompany(companyId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("aymenbabayRating","respons")
            try {
                val response = repository.enabledToCommentCompany(companyId)
                Log.e("aymenbabayRating","response : ${response.body()!!}")
                enableToRating = response.body()!!
            }catch (ex : Exception){
                Log.e("aymenbabayRating","enabledToCommentCompany exption: $ex")
            }
        }
    }

    fun enabledToCommentUser(userId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("aymenbabayRating","respons")
            try {
                val response = repository.enabledToCommentUser(userId)
                enableToRating = response.body()!!
                Log.e("aymenbabayRating","response : ${response.body()!!}")
            }catch (ex : Exception){
                Log.e("aymenbabayRating","enabledToCommentUser exption: $ex")
            }
        }
    }

    fun enabledToCommentArticle(companyId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("aymenbabayRating","respons")
            try {
                val response = repository.enabledToCommentArticle(companyId)
                enableToComment = response.body()!!
                Log.e("aymenbabayRating","response : ${response.body()!!}")
            }catch (ex : Exception){
                Log.e("aymenbabayRating","enabledToCommentUser exption: $ex")
            }
        }
    }

}