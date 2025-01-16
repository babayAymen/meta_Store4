package com.aymen.metastore.ui.screen.user

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
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.metastore.ui.component.notImage
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
    val companyId by remember {
        mutableLongStateOf(company.id ?: 0)
    }
    var actionaireName by remember {
        mutableStateOf("")
    }
    var adverbe by remember {
        mutableStateOf("")
    }
    var adverbType by remember {
        mutableStateOf(AccountType.NULL)
    }
    var adverbImage by remember {
        mutableStateOf("")
    }
    var adverbId by remember {
        mutableLongStateOf(0)
    }
    Row(
        modifier = Modifier.fillMaxWidth()
    )
    {

        val type = when (invitation.type) {
            Type.USER_SEND_CLIENT_COMPANY -> {
                if (invitation.companyReceiver?.id == companyId && myAccountType == AccountType.COMPANY) {
                    actionaireName = invitation.companyReceiver?.name ?: ""
                    adverbe = invitation.client?.username ?: ""
                    adverbType = AccountType.USER
                    adverbImage = invitation.client?.image ?: ""
                    adverbId = invitation.client?.id ?: 0
                } else {
                    actionaireName = invitation.client?.username ?: ""
                    adverbe = invitation.companyReceiver?.name ?: ""
                    adverbType = AccountType.COMPANY
                    adverbImage = invitation.companyReceiver?.logo ?: ""
                    adverbId = invitation.companyReceiver?.user?.id ?: 0
                }
                "client"
            }

            Type.COMPANY_SEND_CLIENT_COMPANY -> {
                if (invitation.companyReceiver?.id == companyId && myAccountType == AccountType.COMPANY) {
                    actionaireName = invitation.companyReceiver?.name ?: ""
                    adverbe = invitation.companySender?.name ?: ""
                    adverbType = AccountType.COMPANY
                    adverbImage = invitation.companySender?.logo ?: ""
                    adverbId = invitation.companySender?.user?.id ?: 0
                } else {
                    actionaireName = invitation.companySender?.name ?: ""
                    adverbe = invitation.companyReceiver?.name ?: ""
                    adverbType = AccountType.COMPANY
                    adverbImage = invitation.companyReceiver?.logo ?: ""
                    adverbId = invitation.companyReceiver?.user?.id ?: 0
                }
                "client"
            }

            Type.COMPANY_SEND_PROVIDER_USER -> {
                if (invitation.companySender?.id == companyId && myAccountType == AccountType.COMPANY) {
                    actionaireName = invitation.companySender?.name ?: ""
                    adverbe = invitation.client?.username ?: ""
                    adverbType = AccountType.USER
                    adverbImage = invitation.client?.image ?: ""
                    adverbId = invitation.client?.id ?: 0
                } else {
                    actionaireName = invitation.client?.username ?: ""
                    adverbe = invitation.companySender?.name ?: ""
                    adverbType = AccountType.COMPANY
                    adverbImage = invitation.companySender?.logo ?: ""
                    adverbId = invitation.companySender?.user?.id ?: 0
                }
                "provider"
            }

            Type.COMPANY_SEND_PROVIDER_COMPANY -> {
                if (invitation.companyReceiver?.id == companyId && myAccountType == AccountType.COMPANY) {
                    actionaireName = invitation.companyReceiver?.name ?: ""
                    adverbe = invitation.companySender?.name ?: ""
                    adverbType = AccountType.COMPANY
                    adverbImage = invitation.companySender?.logo ?: ""
                    adverbId = invitation.companySender?.user?.id ?: 0
                } else {
                    actionaireName = invitation.companySender?.name ?: ""
                    adverbe = invitation.companyReceiver?.name ?: ""
                    adverbType = AccountType.COMPANY
                    adverbImage = invitation.companyReceiver?.logo ?: ""
                    adverbId = invitation.companyReceiver?.user?.id ?: 0
                }
                "provider"
            }

            Type.COMPANY_SEND_PARENT_COMPANY ->
                "parent"

            Type.COMPANY_SEND_WORKER_USER -> {
                if (invitation.companySender?.id == companyId && myAccountType == AccountType.COMPANY) {
                    actionaireName = invitation.companySender?.name ?: ""
                    adverbe = invitation.client?.username ?: ""
                    adverbType = AccountType.USER
                    adverbImage = invitation.client?.image ?: ""
                    adverbId = invitation.client?.id ?: 0
                } else {
                    actionaireName = invitation.client?.username ?: ""
                    adverbe = invitation.companySender?.name ?: ""
                    adverbType = AccountType.COMPANY
                    adverbImage = invitation.companySender?.logo ?: ""
                    adverbId = invitation.companySender?.user?.id ?: 0
                }
                "worker"
            }

            Type.USER_SEND_WORKER_COMPANY -> {
                if (invitation.companyReceiver?.id == companyId && myAccountType == AccountType.COMPANY) {
                    actionaireName = invitation.companyReceiver?.name ?: ""
                    adverbe = invitation.client?.username ?: ""
                    adverbType = AccountType.USER
                    adverbImage = invitation.client?.image ?: ""
                    adverbId = invitation.client?.id ?: 0
                } else {
                    actionaireName = invitation.client?.username ?: ""
                    adverbe = invitation.companyReceiver?.name ?: ""
                    adverbType = AccountType.COMPANY
                    adverbImage = invitation.companyReceiver?.logo ?: ""
                    adverbId = invitation.companyReceiver?.user?.id ?: 0
                }
                "worker"
            }

            Type.OTHER -> TODO()
            null -> TODO()
        }
        val image = String.format(
            if (adverbType == AccountType.USER) IMAGE_URL_USER else IMAGE_URL_COMPANY,
            adverbImage,
            adverbId
        )
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(0.2f)) {
                if (adverbImage != "")
                    ShowImage(image)
                else
                    notImage()
            }
            Row(modifier = Modifier.weight(0.8f)) {
                when (invitation.status) {
                    Status.ACCEPTED -> Text("$actionaireName accepted $adverbe $type invitation")
                    Status.REFUSED -> Text("$actionaireName refused $adverbe $type invitation")
                    Status.CANCELLED -> Text("$actionaireName cancelled $adverbe $type invitation")
                    Status.INWAITING -> {
                        Row {
                            if (actionaireName == company.name) {
                                Column {
                                    Text("you have sent $type invitation to $adverbe ")
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

                                    Text("$adverbe has sent $type invitation ")
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
