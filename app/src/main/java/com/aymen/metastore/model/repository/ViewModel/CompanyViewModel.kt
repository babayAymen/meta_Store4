package com.aymen.metastore.model.repository.ViewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.util.Log
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aymen.metastore.model.ViewModelRunTracker
import com.aymen.metastore.model.entity.model.ClientProviderRelation
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.SearchHistory
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
    private val companyDataStore: DataStore<Company>,
    private val appViewModel: AppViewModel,
    private  val sharedViewModel: SharedViewModel,
    private val useCases: MetaUseCases
) : ViewModel() {

    var providerId by mutableLongStateOf(0)
    var parent by mutableStateOf(Company())
    var myCompany by mutableStateOf(sharedViewModel.company.value)
    var update by mutableStateOf(false)

    init {

        getMyCompany {
        viewModelScope.launch(Dispatchers.IO) {
            room.userDao().insertUser(listOf(it?.user!!.toUserEntity()))
            room.companyDao().insertCompany(listOf(it.toCompanyEntity()))
        }
        }
    }


    fun addCompany(company: String, file: File) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.addCompany(company, file)
            }
        }
    }

    fun updateCompany(company: String, file: File) {
        Log.e("aymenbabayupdate", "c bon update company $company")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateCompany(company, file)
            }
        }
    }


    fun getMyParent() {
        viewModelScope.launch {

        }
    }



    fun getMyCompany(onCompanyRetrieved: (Company?) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    companyDataStore.data
                        .catch { exception ->
                            Log.e("getTokenError", "Error getting token: ${exception.message}")
                            onCompanyRetrieved(null)
                        }
                        .collect { company ->
                            onCompanyRetrieved(company)
                        }
                } catch (e: Exception) {
                    Log.e("getTokenError", "Error getting token: ${e.message}")
                    onCompanyRetrieved(null)
                }
            }
        }
    }

    fun MakeAsPointSeller(status: Boolean, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.makeAsPointSeller(status, id)
            } catch (ex: Exception) {
                Log.e("getTokenError", "Error getting token: ${ex.message}")
            }
        }
    }
    fun MakeAsMetaSeller(status: Boolean, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.makeAsMetaSeller(status, id)
            } catch (ex: Exception) {
                Log.e("getTokenError", "Error getting token: ${ex.message}")
            }
        }
    }

    fun getBitmapFromURL(src: String, onRetreived : (Bitmap?) ->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
             try {
                val url = URL(src)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                val r = BitmapFactory.decodeStream(input)
                 onRetreived(r)
            } catch (e: IOException) {
                e.printStackTrace()
                onRetreived(null)
            }
        }
    }












}