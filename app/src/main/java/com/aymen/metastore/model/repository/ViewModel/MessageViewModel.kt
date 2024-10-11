package com.aymen.store.model.repository.ViewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymen.metastore.model.Enum.MessageType
import com.aymen.store.model.entity.realm.Conversation
import com.aymen.store.model.entity.realm.Message
import com.aymen.metastore.model.entity.realm.User
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.entity.api.CompanyDto
import com.aymen.store.model.entity.api.ConversationDto
import com.aymen.store.model.entity.api.MessageDto
import com.aymen.store.model.entity.api.UserDto
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.repository.globalRepository.GlobalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val repository: GlobalRepository,
    private val realm: Realm,
    private val appViewModel: AppViewModel,
    private val sharedViewModel: SharedViewModel
): ViewModel(){

    var myAllConversations by mutableStateOf(emptyList<Conversation>())
    var _myAllMessages = MutableStateFlow<List<Message>>(emptyList())
    val myAllMessages : StateFlow<List<Message>> = _myAllMessages

    var receiverUser by mutableStateOf(User())
    var receiverCompany by mutableStateOf(Company())
    var sendMessage by mutableStateOf(Message())
    var receiverAccountType by mutableStateOf(AccountType.COMPANY)
    var conversation by mutableStateOf(Conversation())
    var messageType : MessageType = MessageType.USER_SEND_COMPANY
    val user by mutableStateOf(sharedViewModel.user.value)
    val company by mutableStateOf(sharedViewModel.company.value)
    var fromConve by mutableStateOf(false)

    fun mapConversationToConversationDto(conversation: Conversation): ConversationDto {
        return ConversationDto(
            id = conversation.id,
            user1 = conversation.user1?.let { mapUserToUserDto(it) },
            user2 = conversation.user2?.let { mapUserToUserDto(it) },
            company1 = conversation.company1?.let { mapCompanyToCompanyDto(it) },
            company2 = conversation.company2?.let { mapCompanyToCompanyDto(it) },
            message = conversation.message,
            type = conversation.type?.let { MessageType.valueOf(it) }
        )
    }

    fun mapUserToUserDto(user: User): UserDto {
        // Mapping logic from User to UserDto
        return UserDto(
            id = user.id,
            username = user.username,
            image = user.image
        )
    }

    fun mapCompanyToCompanyDto(company: Company): CompanyDto {
        // Mapping logic from Company to CompanyDto
        return CompanyDto(
            id = company.id,
            name = company.name,
            logo = company.logo,
            user = company.user?.let { mapUserToUserDto(it) }
        )
    }

    val realmMutex = Mutex()

    fun getAllMyConversation() {
        viewModelScope.launch(Dispatchers.IO) {
            realmMutex.withLock {
                try {
                    val conversationsResponse = repository.getAllMyConversations()
                    if (conversationsResponse.isSuccessful) {
                        val conversations = conversationsResponse.body()
                        if (conversations != null) {
                             realm.write {
                            conversations.forEach {
                                 var user5 = User()
                                var company5 = Company()
                                    if (it.user1 != null &&
                                        (sharedViewModel.accountType ==  AccountType.COMPANY ||
                                                (sharedViewModel.accountType == AccountType.USER && it.user1?.id != user.id))) {
                                        val user = User().apply {
                                            id = it.user1?.id
                                            username = it.user1?.username!!
                                            image = it.user1?.image
                                        }
                                        user5 = user
                                        copyToRealm(user, UpdatePolicy.ALL)
                                    }
                                    if (it.user2 != null && (sharedViewModel.accountType ==  AccountType.COMPANY ||
                                                (sharedViewModel.accountType == AccountType.USER && it.user2?.id != user.id))) {
                                        val user3 = User().apply {
                                            id = it.user2?.id
                                            username = it.user2?.username!!
                                            image = it.user2?.image
                                        }
                                        user5 = user3
                                        copyToRealm(user3, UpdatePolicy.ALL)
                                    }
Log.e("aymenbabayviewModel","user5 : ${user5.image}")
                                    if (it.company1 != null &&
                                        (sharedViewModel.accountType == AccountType.USER ||
                                                (sharedViewModel.accountType == AccountType.COMPANY && it.company1?.id != company.id))) {
                                        val user0 = User().apply {
                                            id = it.company1?.user?.id
                                        }
                                        copyToRealm(user0, UpdatePolicy.ALL)
                                        val company3 = Company().apply {
                                            id = it.company1?.id
                                            name = it.company1?.name!!
                                            logo = it.company1?.logo
                                            user = user0

                                        }
                                        company5 = company3
                                        copyToRealm(company3, UpdatePolicy.ALL)
                                    }
                                    if (it.company2 != null && (sharedViewModel.accountType == AccountType.USER ||
                                                (sharedViewModel.accountType == AccountType.COMPANY && it.company2?.id != company.id))) {
                                        val user0 = User().apply {
                                            id = it.company2?.user?.id
                                        }
                                        copyToRealm(user0, UpdatePolicy.ALL)
                                        val company4 = Company().apply {
                                            id = it.company2?.id
                                            name = it.company2?.name!!
                                            logo = it.company2?.logo
                                            user = user0
                                        }

                                        company5 = company4
                                        copyToRealm(company4, UpdatePolicy.ALL)
                                    }
                                val conversation = Conversation().apply {
                                    id = it.id
                                    user2 = user5
                                    company2 = company5
                                    lastMessage = it.message!!
                                    type = it.type.toString()
                                    lastModifiedDate = it.lastModifiedDate
                                }
                                copyToRealm(conversation, UpdatePolicy.ALL)
                            }
                        }
                        } else {

                        }
                    }else{

                    }
                } catch (ex: Exception) {
                    Log.e("aymenbabaymessage", "Exception: $ex")
                }
            }
            myAllConversations = repository.getAllMyConversationsLocally()
        }
    }

    fun accountTypeBlock(){
        Log.e("accounttypeblock","receiver type : $receiverAccountType and my type ${sharedViewModel.accountType}")
        if(receiverAccountType == AccountType.COMPANY && sharedViewModel.accountType == AccountType.COMPANY){
            messageType = MessageType.COMPANY_SEND_COMPANY
            Log.e("accounttypeblock","receiver type : $receiverAccountType and my type ${sharedViewModel.accountType} and messagetype = $messageType")
        }
        if(receiverAccountType == AccountType.USER && sharedViewModel.accountType == AccountType.USER){
            Log.e("accounttypeblock","receiver type : $receiverAccountType and my type ${sharedViewModel.accountType}")
            messageType = MessageType.USER_SEND_USER
        }
        if(receiverAccountType == AccountType.USER && sharedViewModel.accountType == AccountType.COMPANY){
            Log.e("accounttypeblock","receiver type : $receiverAccountType and my type ${sharedViewModel.accountType}")
            messageType = MessageType.COMPANY_SEND_USER
        }
    }

    fun getConversationByCaleeId(id : Long){
        accountTypeBlock()
        viewModelScope.launch(Dispatchers.IO) {
            try {
               accountTypeBlock()
                val response = repository.getConversationByCaleeId(id, messageType)
                if(response.isSuccessful){
                    conversation = response.body()?:Conversation()
                }
            }catch (ex : Exception){
                Log.e("getconversationbycalee","fun exception :${ex.message}")
            }
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
                        conversation = response.body()?.get(0)?.conversation!!
                        response.body()?.forEach {
                            Log.e("getAllMessageByCalee", it.createdDate)
                            realm.write {
                                copyToRealm(it, UpdatePolicy.ALL)
                            }
                        }
                    }else{

                    }
                }
            }catch (ex : Exception){
                Log.e("getAllMessageByCaleeId","getAllMessageByCaleeId exception : ${ex.message}")
            }
            if(conversation.id != null){
            _myAllMessages.value = repository.getAllMyMessageByConversationIdLocally(conversation.id!!)
                Log.e("disposemessage","my all message size locally from by calee id ${myAllMessages.value.size}")
            }
        }
    }

    fun getAllMyMessageByConversationId() {

        viewModelScope.launch (Dispatchers.IO){
                try {

                    conversation.id?.let { id ->
                        Log.e("conversation", id.toString())
                        val response = repository.getAllMyMessageByConversationId(id)
                        if (response.isSuccessful) {
                            response.body()?.forEach { messageDto ->
                                realm.write {
                                    copyToRealm(messageDto, UpdatePolicy.ALL)
                                }
                            }
                        }
                    }
                } catch (_ex: Exception) {
                    Log.e("aymenabbaymessage", "Error message by con: $_ex")
                }

                // Now check the locally saved messages
                _myAllMessages.value = repository.getAllMyMessageByConversationIdLocally(conversation.id!!)
            Log.e("disposemessage","my all message size locally from by conv id ${myAllMessages.value.size}")
        }
    }



    @SuppressLint("SuspiciousIndentation")
    fun sendMessage(message : String){
                Log.e("aymenbabaymessage","error message by con")
        sendMessage.conversation = conversation
        sendMessage.createdBy = sharedViewModel.user.value.id
        sendMessage.id = (sendMessage.id?.plus(1L))?:0L
        _myAllMessages.value +=  sendMessage
        sendMessage = Message()
                viewModelScope.launch (Dispatchers.IO){
            try {
                realm.write {
                    Message().apply {
                        copyToRealm(sendMessage,UpdatePolicy.ALL)
                    }
                }
                var conversationForSend by mutableStateOf(ConversationDto())
                conversationForSend = mapConversationToConversationDto(conversation)
                conversationForSend.message = message
                conversationForSend.type = messageType
                conversationForSend.user2 =  mapUserToUserDto(receiverUser)
                conversationForSend.company2 = mapCompanyToCompanyDto(receiverCompany)
                        Log.e("aymenbabaymessage","receiverCompany is :${receiverCompany}")

               val response = repository.sendMessage(conversationForSend)
                if(response.isSuccessful){

                }
            }catch (ex : Exception){
                Log.e("aymenbabaymessage","error message by con: ${ex.message}")
            }
        }
    }

   fun updateLastMessage(conversation: Conversation){
       viewModelScope.launch {
//           withContext(Dispatchers.IO){
               repository.updateLastMessage(conversation)
               myAllConversations = repository.getAllMyConversationsLocally()
           }
//       }
   }
}