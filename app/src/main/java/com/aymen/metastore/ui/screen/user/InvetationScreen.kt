package com.aymen.metastore.ui.screen.user

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.model.entity.model.Invitation
import com.aymen.metastore.model.repository.ViewModel.InvetationViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.NotImage
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.metastore.util.IMAGE_URL_COMPANY
import com.aymen.metastore.util.IMAGE_URL_USER
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type

@Composable
fun InvetationScreen(modifier: Modifier = Modifier) {
    val invetationViewModel : InvetationViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val invitations = invetationViewModel.myAllInvetation.collectAsLazyPagingItems()
    sharedViewModel.setInvitationCountNotification(true)
LaunchedEffect(key1 = Unit) {
    invetationViewModel.getAllMyInvetations()
}

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(count = invitations.itemCount,
                key = invitations.itemKey{it.id!!}){index ->
                val invitation = invitations[index]
                if(invitation != null) {
                    InvetationCard(invitation) { status ->
                        invetationViewModel.RequestResponse(status, invitation.id!!)
                    }
                }
            }
        }
    }
}



@Composable
fun InvetationCard(invitation : Invitation, onClicked: (Status) -> Unit) {
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val company by sharedViewModel.company.collectAsStateWithLifecycle()
    val myAccountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
    var senderName by remember {
        mutableStateOf("")
    }
    var receiverName by remember {
        mutableStateOf("")
    }
    var receiverType by remember {
        mutableStateOf(AccountType.NULL)
    }
    var receiverImage by remember {
        mutableStateOf("")
    }
    var receiverId by remember {
        mutableLongStateOf(0)
    }
    Row(
        modifier = Modifier.fillMaxWidth()
    )
    {

        val type = when (invitation.type) {
            Type.USER_SEND_CLIENT_COMPANY -> {
                    senderName = invitation.client?.username ?: ""
                    receiverName = invitation.companyReceiver?.name ?: ""
                    receiverType = AccountType.COMPANY
                     "client"
            }
            Type.COMPANY_SEND_CLIENT_COMPANY -> {
                    senderName = invitation.companySender?.name ?: ""
                    receiverName = invitation.companyReceiver?.name ?: ""
                    receiverType = AccountType.COMPANY
                      "client"
            }
            Type.COMPANY_SEND_PROVIDER_USER -> {
                    senderName = invitation.companySender?.name ?: ""
                    receiverName = invitation.client?.username ?: ""
                    receiverType = AccountType.USER
                "provider"
            }
            Type.COMPANY_SEND_PROVIDER_COMPANY -> {
                    senderName = invitation.companySender?.name ?: ""
                    receiverName = invitation.companyReceiver?.name ?: ""
                    receiverType = AccountType.COMPANY
                     "provider"
            }
            Type.COMPANY_SEND_PARENT_COMPANY -> "parent"
            Type.COMPANY_SEND_WORKER_USER -> {
                    senderName = invitation.companySender?.name ?: ""
                    receiverName = invitation.client?.username ?: ""
                    receiverType = AccountType.USER
                     "worker"
            }
            Type.USER_SEND_WORKER_COMPANY -> {
                    senderName = invitation.client?.username ?: ""
                    receiverName = invitation.companyReceiver?.name ?: ""
                    receiverType = AccountType.COMPANY
                      "worker"
            }

            Type.OTHER -> TODO()
            null -> TODO()
        }

        receiverId = when {
            myAccountType == AccountType.USER -> {
                invitation.companyReceiver?.id ?: invitation.companySender?.id ?: 0
            }
            company.id == invitation.companyReceiver?.id -> {
                invitation.client?.id ?: invitation.companySender?.id ?: 0
            }
            else -> {
                invitation.companyReceiver?.id ?: invitation.client?.id ?: 0
            }
        }
        receiverImage = when {
            myAccountType == AccountType.USER -> {
                invitation.companyReceiver?.logo ?: invitation.companySender?.logo ?: ""
            }
            company.id == invitation.companyReceiver?.id -> {
                invitation.client?.image ?: invitation.companySender?.logo ?: ""
            }
            else -> {
                invitation.companyReceiver?.logo ?: invitation.client?.image ?: ""
            }
        }


        val image = String.format(
            if (receiverType == AccountType.USER) IMAGE_URL_USER else IMAGE_URL_COMPANY,
            receiverImage,
            receiverId
        )
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(0.2f)) {
                if (receiverImage != "")
                    ShowImage(image)
                else
                    NotImage()
            }
            Row(modifier = Modifier.weight(0.8f)) {
                when (invitation.status) {
                    Status.ACCEPTED -> {
                        if(receiverName == company.name)
                            Text("you have accepted $senderName $type invitation")
                        else
                            Text("$receiverName has accepted your $type invitation")
                    }
                    Status.REFUSED -> {
                        if(receiverName == company.name)
                            Text("you have refused $senderName $type invitation")
                        else
                            Text("$receiverName refused your $type invitation")
                    }
                    Status.CANCELLED -> {
                        if(senderName == company.name)
                            Text("you have cancelled $type invitation to $receiverName")
                        else
                            Text("$senderName has cancelled the $type invitation")
                    }
                    Status.INWAITING -> {
                        Row {
                            if (senderName == company.name) {
                                Column {
                                    Text("you have sent $type invitation to $receiverName ")
                                    ButtonSubmit(
                                        labelValue = "cacel",
                                        color = Color.Red,
                                        enabled = true
                                    ) {
                                        onClicked(Status.CANCELLED)
                                    }
                                }
                            } else {
                                Column {
                                    Text("$senderName has sent $type invitation ")
                                    Row {
                                        Row(modifier = Modifier.weight(1f)) {
                                            ButtonSubmit(
                                                labelValue = "accept",
                                                color = Color.Green,
                                                enabled = true
                                            ) {
                                                onClicked(Status.ACCEPTED)
                                            }
                                        }
                                        Row(modifier = Modifier.weight(1f)) {
                                            ButtonSubmit(
                                                labelValue = "refuse",
                                                color = Color.Red,
                                                enabled = true
                                            ) {
                                                onClicked(Status.REFUSED)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    null -> TODO()
                }
            }
        }

    }
}
