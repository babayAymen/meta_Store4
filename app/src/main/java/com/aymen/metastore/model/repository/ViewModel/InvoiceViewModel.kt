package com.aymen.store.model.repository.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.entity.converterRealmToApi.mapCommandLineToCommandLineDto
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.metastore.model.entity.realm.User
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.entity.api.CommandLineDto
import com.aymen.store.model.entity.api.PurchaseOrderLineDto
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val realm : Realm,
    private val sharedViewModel : SharedViewModel
) : ViewModel(){

    private val _myInvoicesAsProvider = MutableStateFlow<List<Invoice>>(emptyList())
    val myInvoicesAsProvider : StateFlow<List<Invoice>> = _myInvoicesAsProvider

    private val _myInvoicesAsClient = MutableStateFlow<List<Invoice>>(emptyList())
    val myInvoicesAsClient: StateFlow<List<Invoice>> = _myInvoicesAsClient

    private val _ordersLine = MutableStateFlow(PurchaseOrderLineDto())
    val ordersLine: StateFlow<PurchaseOrderLineDto> = _ordersLine


    val _ordersLineArray = MutableStateFlow<List<PurchaseOrderLineDto>>(emptyList())
    val ordersLineArray: StateFlow<List<PurchaseOrderLineDto>> = _ordersLineArray


    var lastInvoiceCode by mutableLongStateOf(0)
    var clientCompany by mutableStateOf(Company())
    var clientUser by mutableStateOf(User())
    var clientType by mutableStateOf(AccountType.USER)
    var article by mutableStateOf(ArticleCompany())
    var commandLineDto by mutableStateOf(CommandLineDto())
    var commandLineDtos by  mutableStateOf(emptyList<CommandLineDto>())
    var isLoading by  mutableStateOf(true)
    var discount by mutableDoubleStateOf(0.0)
    val company by mutableStateOf(sharedViewModel.company.value)
    var invoice by mutableStateOf(Invoice())
    var invoiceMode by mutableStateOf(InvoiceMode.CREATE)




    fun getAllMyInvoicesAsProvider(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val invoices = repository.getAllMyInvoicesAsProvider(company.id!!)
                if(invoices.isSuccessful){
                    invoices.body()?.forEach{
                        realm.write {
                            copyToRealm(it,UpdatePolicy.ALL)
                        }
                    }
                }else{

                }
            }catch (ex: Exception){
                Log.e("aymenbabayinvoice","error is :$ex")
            }
           _myInvoicesAsProvider.value = repository.getAllMyInvoicesAsProviderLocally(sharedViewModel.company.value.id!!)
                    isLoading = false

        }
    }
    fun getAllMyInvoicesAsClient(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val invoicesAsClient = repository.getAllMyInvoicesAsClient(company.id?:0)
                Log.e("getAllMyInvoicesAsClient","size: ${invoicesAsClient.body()?.size}")
                if(invoicesAsClient.isSuccessful){
                invoicesAsClient.body()?.forEach {
                    realm.write {
                        copyToRealm(it,UpdatePolicy.ALL)
                    }
                }
                }else{

                }
            }catch (ex: Exception){
                Log.e("getAllMyInvoicesAsClient","exception: ${ex.message}")

            }
            isLoading = false
            _myInvoicesAsClient.value = repository.getAllMyInvoicesAsClientLocally(sharedViewModel.company.value.id!!)
            Log.e("getAllMyInvoicesAsClient","size: ${_myInvoicesAsClient.value.size}")

        }
    }

    fun getLastInvoiceCode(){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val invoiceCode = repository.getLastInvoiceCode()
                if(invoiceCode.isSuccessful){
                    lastInvoiceCode = invoiceCode.body()!!
                }
            }catch (ex:Exception){
                Log.e("aymenabaylastinvoice","error is : $ex")
            }
            }

    }

    fun addInvoice(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                (clientCompany.id?:clientUser.id)?.let {
                    repository.addInvoice(commandLineDtos = commandLineDtos, clientId = it, invoiceCode = lastInvoiceCode, discount = discount, clientType =  clientType, invoiceMode = invoiceMode) }
            }catch (ex : Exception){
                Log.e("aymenbabayerror","error is : $ex")
                }
            }

    }

    fun getAllCommandLineByInvoiceId(){
            isLoading = true
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = repository.getAllCommandLinesByInvoiceId(invoice.id!!)
                if(response.isSuccessful){
                    response.body()?.forEach {
                    commandLineDto = mapCommandLineToCommandLineDto(it)
                        commandLineDtos += commandLineDto
                        isLoading = false
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllCommandLineByInvoiceId","exception: ${ex.message}")
            }
        }
    }


    fun getAllOrdersLineByInvoiceId(){
        Log.e("getAllCommandLineByInvoiceId","exception:nhh")
            isLoading = true
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = repository.getAllOrdersLineByInvoiceId(invoice.id!!)
                Log.e("getAllCommandLineByInvoiceId","size: ${response.body()?.size} invocice id : ${invoice.id}")
                if(response.isSuccessful){
                    response.body()?.forEach {
                        _ordersLineArray.value += it
                        isLoading = false
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllCommandLineByInvoiceId","exception: ${ex.message}")
            }
        }
    }






}