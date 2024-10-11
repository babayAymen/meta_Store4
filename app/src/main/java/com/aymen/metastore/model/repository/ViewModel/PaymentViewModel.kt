package com.aymen.store.model.repository.ViewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.entity.realm.Payment
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val realm: Realm,
    private val companyViewModel: CompanyViewModel,
    private val sharedViewModel: SharedViewModel
):ViewModel() {

    var payments by mutableStateOf(emptyList<Payment>())

    private val _paymentsEspece = MutableStateFlow<List<PaymentForProviders>>(emptyList())
    val paymentsEspece: StateFlow<List<PaymentForProviders>> = _paymentsEspece

    private val _myAllInvoice = MutableStateFlow<List<Invoice>>(emptyList())
    val myAllInvoice: StateFlow<List<Invoice>> = _myAllInvoice


    var isLoding by mutableStateOf(false)
    fun getAllMyPaymentsEspeceByDate(date : String, finDate : String){
        isLoding = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllMyPaymentsEspeceByDate(date, finDate)
                if(response.isSuccessful){
                    isLoding = false
                Log.e("getAllMyPaymentsEspeceByDateException","size ${response.body()!!.size}")
                    _paymentsEspece.value = response.body()?: emptyList()
                    response.body()!!.forEach {
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllMyPaymentsEspeceByDateException","${ex.message}")
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun getAllMyPayments(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val payment = repository.getAllMyPayments()
                    if (payment.isSuccessful) {
                        payment.body()?.forEach {
                            realm.write {
                                copyToRealm(it, UpdatePolicy.ALL)
                            }
                        }
                    }
                } catch (ex: Exception) {

                }
                payments = repository.getAllMyPaymentsLocally()
            }
        }
    }

    fun getAllMyPaymentsEspece(id : Long?){
        isLoding = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
               val response = repository.getAllMyPaymentsEspece(id?:0L)
                if (response.isSuccessful) {
                    isLoding = false
                    response.body()?.forEach {
                        Log.e("aymenbabat",it.purchaseOrderLine?.purchaseorder?.id.toString())
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                }
            }catch (ex : Exception){
                Log.e("aymenbabat",ex.message.toString())
            }
                isLoding = false
            if(sharedViewModel.accountType == AccountType.COMPANY) {
                companyViewModel.getMyCompany { company ->
                    _paymentsEspece.value = repository.getAllMyPaymentsEspeceLocally(company?.id!!)
                }
            }
            else{
                _paymentsEspece.value = repository.getAllMyPaymentsHistoryLocally(sharedViewModel.user.value.id!!)
            }

        }
    }


    fun getAllMyPaymentFromInvoice(status : PaymentStatus){
        isLoding = true
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = repository.getAllMyInvoicesAsProviderAndStatus(companyId = sharedViewModel.company.value.id!!, status)
                if(response.isSuccessful){
                    response.body()?.forEach {
                        realm.write {
                            copyToRealm(it, UpdatePolicy.ALL)
                        }
                    }
                }
            }catch(ex : Exception){
                Log.e("getAllMyPaymentFromInvoice","exception is : ${ex.message}")
            }
            _myAllInvoice.value = repository.getAllMyInvoicesAsProviderAndStatusLocally(companyId = sharedViewModel.company.value.id!!, status)
        }
    }

}