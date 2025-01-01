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
import androidx.room.withTransaction
import com.aymen.metastore.model.Enum.RateType
import com.aymen.metastore.model.entity.dto.RatingDto
import com.aymen.metastore.model.entity.model.ErrorResponse
import com.aymen.metastore.model.entity.model.Rating
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.RatingRemoteKeys
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.AccountType
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
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val repository : GlobalRepository,
    private val room : AppDatabase,
    private val sharedViewModel: SharedViewModel,
    private val useCases : MetaUseCases,
    private val context : Context
): ViewModel() {

    private val ratingDao = room.ratingDao()
    private val userDao = room.userDao()
    private val companyDao = room.companyDao()

    var rate by mutableStateOf(0)

    var _allRating : MutableStateFlow<PagingData<Rating>> = MutableStateFlow(PagingData.empty())
    val allRating : StateFlow<PagingData<Rating>> get() = _allRating

    var rating by mutableStateOf(false)
    var enableToRating by mutableStateOf(false)
    var enableToComment by mutableStateOf(false)
    fun getAllRating(id : Long, type : AccountType){
        viewModelScope.launch(Dispatchers.IO) {
                useCases.getRateeRating(id , type)
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    .collect{
                        _allRating.value = it
                    }

        }
    }


    fun doRate(ratingEntity : Rating ,ratingString : String, image : File?){
        viewModelScope.launch(Dispatchers.IO) {
                val latestRemoteKey = ratingDao.getLatestRatingRemoteKey()
                val latestId = if(latestRemoteKey == null) 1 else latestRemoteKey.id!!+1
                val recordsCount = ratingDao.getRecordsCount()
                val page = recordsCount.div(PAGE_SIZE)
                val remain = recordsCount % PAGE_SIZE
                val newRemoteKey = RatingRemoteKeys(
                    id = latestId,
                    prevPage = if(page == 0) null else page - 1,
                    nextPage = if(remain != 0) null else page + 1
                )
            when(ratingEntity.type){
                RateType.USER_RATE_COMPANY ,
                RateType.COMPANY_RATE_COMPANY -> {
                    val ratersNumber = ratingEntity.rateeCompany?.raters
                    val rateValue = BigDecimal(ratingEntity.rateeCompany?.rate!!).multiply(BigDecimal(ratersNumber!!))
                    val newRateValue = (rateValue.add(BigDecimal(ratingEntity.rateValue!!))).divide(BigDecimal(ratersNumber).add(BigDecimal(1)),2,RoundingMode.HALF_UP).toDouble()
                    sharedViewModel.setHisCompany(ratingEntity.rateeCompany!!.copy(rate = newRateValue, raters = ratersNumber+1))
                }
                RateType.COMPANY_RATE_USER ,
                RateType.META_RATE_USER -> {
                   val ratersNumber = ratingEntity.rateeUser?.rater
                    val rateValue = BigDecimal(ratingEntity.rateeUser?.rate!!).multiply(BigDecimal(ratersNumber!!))
                    val newRateValue = (rateValue.add(BigDecimal(ratingEntity.rateValue!!))).divide(BigDecimal(ratersNumber).add(BigDecimal(1)),2,RoundingMode.HALF_UP).toDouble()
                    sharedViewModel.setHisUser(ratingEntity.rateeUser!!.copy(rate = newRateValue , rater = ratersNumber + 1))
                }
                null -> {}
            }
            room.withTransaction {
                ratingDao.insertRatingRemoteKeys(listOf(newRemoteKey))
                ratingDao.insertRating(listOf(ratingEntity.copy(id = latestId).toRatingEntity()))
            }
               val result :Result<Response<RatingDto>> = runCatching {
                              repository.doRating(ratingString,image)
                   }
            result.fold(
                onSuccess = {success ->
                    ratingDao.deleteRatingById(latestId)
                    ratingDao.deleteRemoteKeyById(latestId)
                    if(success.isSuccessful){
                        val response = success.body()
                        if(response != null){
                            room.withTransaction {
                                companyDao.insertCompany(listOf(response.rateeCompany?.toCompany()))
                                userDao.insertUser(listOf(response.rateeUser?.toUser()))
                                ratingDao.insertRatingRemoteKeys(listOf(newRemoteKey.copy(id = response.id)))
                                ratingDao.insertRating(listOf(response.toRating()))
                            }
                        }
                    }else{
                        val errorBodyString = success.errorBody()?.string()
                        errorBlock(errorBodyString)
                    }
                },
                onFailure = {failure ->

                }
            )

        }
    }

    fun enabledToCommentCompany(companyId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.enabledToCommentCompany(companyId)
                enableToRating = response.body()!!
            }catch (ex : Exception){
                Log.e("aymenbabayRating","enabledToCommentCompany exption: $ex")
            }
        }
    }

    fun enabledToCommentUser(userId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.enabledToCommentUser(userId)
                enableToRating = response.body()!!
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
            }catch (ex : Exception){
                Log.e("aymenbabayRating","enabledToCommentUser exption: $ex")
            }
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