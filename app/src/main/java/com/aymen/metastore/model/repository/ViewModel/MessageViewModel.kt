package com.aymen.store.model.repository.ViewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.aymen.metastore.model.Enum.MessageType
import com.aymen.metastore.model.entity.converterRealmToApi.mapCompanyToRoomCompany
import com.aymen.metastore.model.entity.converterRealmToApi.mapConversationToRoomConversation
import com.aymen.metastore.model.entity.converterRealmToApi.mapMessageToRoomMessage
import com.aymen.metastore.model.entity.converterRealmToApi.mapRoomCompanyToCompanyDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapRoomConversationToConversationDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapRoomUserToUserDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapUserToRoomUser
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.room.Conversation
import com.aymen.metastore.model.entity.room.Message
import com.aymen.metastore.model.entity.room.User
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.entity.dto.ConversationDto
import com.aymen.store.model.entity.dto.MessageDto
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val room : AppDatabase,
    private val sharedViewModel: SharedViewModel
): ViewModel(){

    private var _myAllConversations = MutableStateFlow<List<Conversation>>(emptyList())
    val myAllConversations : StateFlow<List<Conversation>> = _myAllConversations
    private var _myAllConversationsDto = MutableStateFlow<List<ConversationDto>>(emptyList())
    var myAllConversationsDto : StateFlow<List<ConversationDto>> = _myAllConversationsDto

    var _myAllMessages = MutableStateFlow<List<Message>>(emptyList())
    val myAllMessages : StateFlow<List<Message>> = _myAllMessages

    var receiverUser by mutableStateOf(User())
    var receiverCompany by mutableStateOf(Company())
    var sendMessage by mutableStateOf(MessageDto())
    var receiverAccountType by mutableStateOf(AccountType.COMPANY)
    var conversation by mutableStateOf(ConversationDto())
    var messageType : MessageType = MessageType.USER_SEND_COMPANY
    val user by mutableStateOf(sharedViewModel.user.value)
    val company by mutableStateOf(sharedViewModel.company.value)
    var fromConve by mutableStateOf(false)



    val realmMutex = Mutex()

    fun getAllMyConversation() {
        viewModelScope.launch(Dispatchers.IO) {
            realmMutex.withLock {
                try {
                    val conversationsResponse = repository.getAllMyConversations()
                    if (conversationsResponse.isSuccessful) {
                        val conversations = conversationsResponse.body()
                        if (conversations != null) {
                            conversations.forEach {
                              insertConversation(it)
                            }
                        } else {

                        }
                    }else{

                    }
                } catch (ex: Exception) {
                    Log.e("aymenbabaymessage", "Exception: $ex")
                }
            }
            _myAllConversations.value = room.conversationDao().getAllConversations()
            fullMappingConversation(_myAllConversations.value)
        }
    }

    fun accountTypeBlock(){
        if(receiverAccountType == AccountType.COMPANY && sharedViewModel.accountType == AccountType.COMPANY){
            messageType = MessageType.COMPANY_SEND_COMPANY
        }
        if(receiverAccountType == AccountType.USER && sharedViewModel.accountType == AccountType.USER){
            messageType = MessageType.USER_SEND_USER
        }
        if(receiverAccountType == AccountType.USER && sharedViewModel.accountType == AccountType.COMPANY){
            messageType = MessageType.COMPANY_SEND_USER
        }
    }

    fun getAllMessageByCaleeId(id : Long){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                accountTypeBlock()
                val response = repository.getAllMessageByCaleeId(id,receiverAccountType)
                if(response.isSuccessful) {
                    val messages = response.body()
                    if (!messages.isNullOrEmpty()) {
                        response.body()?.forEach {
                            insertMessage(it)
                        }
                    }
                }
            }catch (ex : Exception){
                Log.e("getAllMessageByCaleeId","getAllMessageByCaleeId exception : ${ex.message}")
            }
            if(conversation.id != null){
            _myAllMessages.value = room.messageDao().getAllMessagesByConversationId(conversation.id!!)
            }
        }
    }

    @Transaction
    suspend fun insertConversation(conversation : ConversationDto){
        if (conversation.user1 != null &&
            (sharedViewModel.accountType == AccountType.COMPANY ||
                    (sharedViewModel.accountType == AccountType.USER && conversation.user1?.id != user.id))
        ) {
            room.userDao().insertUser(mapUserToRoomUser(conversation.user1))
        }

        if (conversation.user2 != null && (sharedViewModel.accountType ==  AccountType.COMPANY ||
                    (sharedViewModel.accountType == AccountType.USER && conversation.user2?.id != user.id))) {
            room.userDao().insertUser(mapUserToRoomUser(conversation.user2))
        }

        if (conversation.company1 != null &&
            (sharedViewModel.accountType == AccountType.USER ||
                    (sharedViewModel.accountType == AccountType.COMPANY && conversation.company1?.id != company.id))) {
            room.companyDao().insertCompany(mapCompanyToRoomCompany(conversation.company1))
        }
        if (conversation.company2 != null && (sharedViewModel.accountType == AccountType.USER ||
                    (sharedViewModel.accountType == AccountType.COMPANY && conversation.company2?.id != company.id))) {
            room.companyDao().insertCompany(mapCompanyToRoomCompany(conversation.company2))
        }
        room.conversationDao().insertConversation(
            mapConversationToRoomConversation(conversation)
        )
    }

    @Transaction
    suspend fun insertMessage(message : MessageDto){
        insertConversation(message.conversation!!)
        room.messageDao().insertMessage(mapMessageToRoomMessage(message))
    }


    fun getAllMyMessageByConversationId() {
        viewModelScope.launch (Dispatchers.IO){
                try {
                    conversation.id?.let { id ->
                        val response = repository.getAllMyMessageByConversationId(id)
                        if (response.isSuccessful) {
                            response.body()?.forEach { messageDto ->
                                insertMessage(messageDto)
                            }
                        }
                    }
                } catch (_ex: Exception) {
                    Log.e("aymenabbaymessage", "Error message by con: $_ex")
                }

                _myAllMessages.value = room.messageDao().getAllMessagesByConversationId(conversation.id?:0)
        }
    }



    @SuppressLint("SuspiciousIndentation")
    fun sendMessage(message : String){
        sendMessage.conversation = conversation
        sendMessage.createdBy = sharedViewModel.user.value.id
        sendMessage.id = (sendMessage.id?.plus(1L))?:0L
            val roomMessage = mapMessageToRoomMessage(sendMessage)
        _myAllMessages.value += roomMessage
                viewModelScope.launch (Dispatchers.IO){
            try {
               room.messageDao().insertMessage(roomMessage)
                var conversationForSend by mutableStateOf(ConversationDto())
                conversationForSend = sendMessage.conversation?:ConversationDto()
                conversationForSend.message = message
                conversationForSend.type = messageType
                conversationForSend.user2 =  mapRoomUserToUserDto(receiverUser)
                conversationForSend.company2 = mapRoomCompanyToCompanyDto(receiverCompany)

               val response = repository.sendMessage(conversationForSend)
                if(response.isSuccessful){

                }
        sendMessage = MessageDto()
            }catch (ex : Exception){
                Log.e("aymenbabaymessage","error message by con: ${ex.message}")
            }
        }
    }

    private fun fullMappingConversation(conversations: List<Conversation>){
        viewModelScope.launch {
            _myAllConversationsDto.value = emptyList()
            conversations.forEach {
                val conversationDto = mapRoomConversationToConversationDto(it)
                val user1 = it.user1Id?.let { it1 -> room.userDao().getUserById(it1) }
                val user2 = it.user2Id?.let { it1 -> room.userDao().getUserById(it1) }
                val company1 = it.company1Id?.let { it1 -> room.companyDao().getCompanyById(it1) }
                val company2 = it.company2Id?.let { it1 -> room.companyDao().getCompanyById(it1) }
                conversationDto.user1 = user1?.let { it1 -> mapRoomUserToUserDto(it1) }
                conversationDto.user2 = user2?.let { it1 -> mapRoomUserToUserDto(it1) }
                conversationDto.company1 = company1?.let { it1 -> mapRoomCompanyToCompanyDto(it1) }
                conversationDto.company2 = company2?.let { it1 -> mapRoomCompanyToCompanyDto(it1) }
                _myAllConversationsDto.value += conversationDto
            }
        }
    }

}