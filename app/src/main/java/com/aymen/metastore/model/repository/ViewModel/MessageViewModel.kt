package com.aymen.metastore.model.repository.ViewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.room.Transaction
import com.aymen.metastore.model.Enum.MessageType
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.Conversation
import com.aymen.metastore.model.entity.model.Message
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.usecase.MetaUseCases
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
    private val sharedViewModel: SharedViewModel,
    private val useCases: MetaUseCases
): ViewModel(){

    private var _myAllConversations : MutableStateFlow<PagingData<Conversation>> = MutableStateFlow(PagingData.empty())
    val myAllConversations : StateFlow<PagingData<Conversation>> = _myAllConversations

    var _myAllMessages : MutableStateFlow<PagingData<Message>> = MutableStateFlow(PagingData.empty())
    val myAllMessages : StateFlow<PagingData<Message>> = _myAllMessages

    var receiverUser by mutableStateOf(User())
    var receiverCompany by mutableStateOf(Company())
    var sendMessage by mutableStateOf(Message())
    var receiverAccountType by mutableStateOf(AccountType.COMPANY)
    var conversation by mutableStateOf(Conversation())
    var messageType : MessageType = MessageType.USER_SEND_COMPANY
    val user by mutableStateOf(sharedViewModel.user.value)
    val company by mutableStateOf(sharedViewModel.company.value)
    var fromConve by mutableStateOf(false)


    init {
        getAllConversations()
    }

    fun getAllConversations(){
        viewModelScope.launch {
            useCases.getAllConversation()
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _myAllConversations.value = it.map { conversation -> conversation.toConversation() }
                }
        }
    }
    fun getAllMessages(conversationId : Long, accountType: AccountType){
        viewModelScope.launch {
            useCases.getAllMessagesByConversation(conversationId, accountType)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect{
                    _myAllMessages.value = it.map { message -> message.toMessage() }
                }
        }
    }

    fun accountTypeBlock(){
        if(receiverAccountType == AccountType.COMPANY && sharedViewModel.accountType.value == AccountType.COMPANY){
            messageType = MessageType.COMPANY_SEND_COMPANY
        }
        if(receiverAccountType == AccountType.USER && sharedViewModel.accountType.value == AccountType.USER){
            messageType = MessageType.USER_SEND_USER
        }
        if(receiverAccountType == AccountType.USER && sharedViewModel.accountType.value == AccountType.COMPANY){
            messageType = MessageType.COMPANY_SEND_USER
        }
    }


    @SuppressLint("SuspiciousIndentation")
    fun sendMessage(message : String){
        sendMessage.conversation = conversation
        sendMessage.createdBy = sharedViewModel.user.value.id
        sendMessage.id = (sendMessage.id?.plus(1L))?:0L
//        _myAllMessages.value += roomMessage
                viewModelScope.launch (Dispatchers.IO){
            try {
                var conversationForSend by mutableStateOf(Conversation())
                conversationForSend = sendMessage.conversation?: Conversation()
                conversationForSend.message = message
                conversationForSend.type = messageType
                conversationForSend.user2 =  receiverUser
                conversationForSend.company2 = receiverCompany

//               val response = repository.sendMessage(conversationForSend)
//                if(response.isSuccessful){
//
//                }
        sendMessage = Message()
            }catch (ex : Exception){
                Log.e("aymenbabaymessage","error message by con: ${ex.message}")
            }
        }
    }

    fun getAllMessageByCaleeId(id : Long){

    }
}