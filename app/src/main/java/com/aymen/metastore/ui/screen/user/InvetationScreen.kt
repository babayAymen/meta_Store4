package com.aymen.metastore.ui.screen.user

import android.util.Log
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.model.entity.model.Invitation
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.metastore.model.repository.ViewModel.InvetationViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.metastore.util.BASE_URL
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type

@Composable
fun InvetationScreen(modifier: Modifier = Modifier) {
    val invetationViewModel : InvetationViewModel = hiltViewModel()
    val invitations = invetationViewModel.myAllInvetation.collectAsLazyPagingItems()

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
fun InvetationCard(invetation : Invitation,onClicked: (Status) -> Unit) {
    val companyViewModel : CompanyViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    var companyId by remember {
        mutableLongStateOf(0)
    }
    val role by sharedViewModel.accountType.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        companyViewModel.getMyCompany {
            if (it != null) {
                companyId = it.id!!
            }
        }
    }
    Row (
        modifier = Modifier.fillMaxWidth()
    )
    {
        when (invetation.type) {
            Type.USER_SEND_CLIENT_COMPANY ->
                InvitationTypeClient(invetation, companyId){
                    onClicked(it)
                }
            Type.COMPANY_SEND_CLIENT_COMPANY ->
                InvitationTypeClient(invetation, companyId){
                    onClicked(it)
                }
            Type.COMPANY_SEND_PROVIDER_USER ->
                InvitationTypeProvider(invetation, companyId,role){
                    onClicked(it)
                }
            Type.COMPANY_SEND_PROVIDER_COMPANY ->
                InvitationTypeProvider(invetation, companyId,role){
                    onClicked(it)
                }
            Type.COMPANY_SEND_PARENT_COMPANY ->
                InvitationTypeParent(invetation = invetation, companyId){
                    onClicked(it)
                }
            Type.COMPANY_SEND_WORKER_USER ->
                InvitationTypeWorker(invetation = invetation,role){
                    onClicked(it)
                }
            Type.USER_SEND_WORKER_COMPANY ->
                InvitationTypeWorker(invetation = invetation,role){
                    onClicked(it)
                }

            Type.OTHER -> TODO()
            null -> TODO()
        }
    }

}

@Composable
fun InvitationTypeWorker(invetation: Invitation, role : AccountType,onClicked: (Status) -> Unit) {
    when (invetation.status){
        Status.INWAITING ->
            InWaitingTypeWorker(invetation = invetation, role = role){
                onClicked(it)
            }
        Status.ACCEPTED ->
            AcceptTypeWorker(invetation = invetation, role = role)
        Status.REFUSED ->
            RefuseTypeWorker(invetation = invetation, role = role)
        Status.CANCELLED ->
            CancelTypeWorker(invetation = invetation, role = role)

        null -> TODO()
    }
}
@Composable
fun CancelTypeWorker(invetation: Invitation, role : AccountType) {
    when (role) {
        AccountType.COMPANY -> {
            Row {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    if (invetation.type == Type.USER_SEND_WORKER_COMPANY) {
                        Text(text = "${invetation.client?.username} has canceled a worker invitation")
                    } else {
                        Text(text = "you have canceled a worker invitation to ${invetation.client?.username}")
                    }
                }
            }
        }
        AccountType.USER ->{
            Row{
                Row (
                    modifier = Modifier.weight(1f)
                ){
                    if(invetation.type == Type.USER_SEND_WORKER_COMPANY){
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                    }else{
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                    }
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    if(invetation.type == Type.USER_SEND_WORKER_COMPANY){
                        Text(text = "you have canceled a worker invitation to ${invetation.companyReceiver?.name}")
                    }
                    else{
                        Text(text = "${invetation.companySender?.name} has cnceled a worker invitation")
                    }
                }
            }
        }
        else -> {

        }
    }

}
@Composable
fun RefuseTypeWorker(invetation: Invitation, role : AccountType) {
    when (role){
        AccountType.COMPANY -> {
            Row {
                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    if(invetation.type == Type.USER_SEND_WORKER_COMPANY){
                        Text(text = "you have refused a worker invitation from ${invetation.client?.username}")
                    }else{
                        Text(text = "${invetation.client?.username} has refused your worker invitation")
                    }
                }
            }
        }
        AccountType.USER -> {
            Row {
                Row (
                    modifier = Modifier.weight(1f)
                ){
                    if(invetation.type == Type.USER_SEND_WORKER_COMPANY){
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                    }else{
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")

                    }
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    if(invetation.type == Type.USER_SEND_WORKER_COMPANY){
                        Text(text = "${invetation.companyReceiver?.name} has refused your worker invitation")
                    }
                    else{
                        Text(text = "you have refused a worker invitation from ${invetation.companySender?.name}")

                    }
                }
            }
        }
        else ->{

        }
    }
}
@Composable
fun InWaitingTypeWorker(invetation: Invitation, role: AccountType,onClicked: (Status) -> Unit) {
    when (role) {
        AccountType.COMPANY -> {
            Row {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Column {
                        if (invetation.type == Type.USER_SEND_WORKER_COMPANY) {
                            Text(text = "${invetation.client?.username} has sent a worker invitation")
                            Row {
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ButtonSubmit(
                                        labelValue = "accept",
                                        color = Color.Green,
                                        enabled = true
                                    ) {
                                    }
                                    onClicked(Status.ACCEPTED)
                                }
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ButtonSubmit(
                                        labelValue = "refuse",
                                        color = Color.Red,
                                        enabled = true
                                    ) {
                                        onClicked(Status.REFUSED)
                                    }
                                }
                            }
                        } else {
                            Column {

                                Text(text = "you have sent a worker invitation to ${invetation.client?.username}")
                                ButtonSubmit(
                                    labelValue = "cancel",
                                    color = Color.Red,
                                    enabled = true
                                ) {
                                    onClicked(Status.CANCELLED)
                                }
                            }
                        }
                    }
                }
            }
        }

        AccountType.USER -> {
            Row {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    if (invetation.type == Type.USER_SEND_WORKER_COMPANY) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                    } else
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Column {
                        if (invetation.type == Type.USER_SEND_WORKER_COMPANY) {

                            Text(text = " you have sent a worker invitation to ${invetation.companyReceiver?.name}")
                            ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                                onClicked(Status.CANCELLED)
                            }
                        } else {

                            Text(text = "  ${invetation.companySender?.name} has sent a worker invitation")
                            Row {
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ButtonSubmit(
                                        labelValue = "accept",
                                        color = Color.Green,
                                        enabled = true
                                    ) {
                                        onClicked(Status.ACCEPTED)
                                    }
                                }
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
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
        }

        else -> {
            Text(text = "salem")
        }
    }
}
@Composable
fun AcceptTypeWorker(invetation: Invitation, role: AccountType) {
    when (role) {
        AccountType.COMPANY -> {
            Row {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    if (invetation.type == Type.USER_SEND_WORKER_COMPANY) {
                        Text(text = "you have accepted a worker invitation from ${invetation.client?.username}")
                    } else {
                        Text(text = "${invetation.client?.username} has accepted your worker invitation")
                    }
                }
            }
        }

        AccountType.USER -> {
            Row {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    if (invetation.type == Type.USER_SEND_WORKER_COMPANY) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                    } else {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                    }
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    if (invetation.type == Type.USER_SEND_WORKER_COMPANY) {
                        Text(text = "${invetation.companyReceiver?.name} has accepted your worker invitation")
                    } else {
                        Text(text = "you have accepted a worker invitation from ${invetation.companySender?.name}")
                    }
                }
            }
        }

        else -> {

        }
    }
}
@Composable
fun InvitationTypeParent(invetation : Invitation, companyId : Long, onClicked: (Status) -> Unit) {
    when (invetation.status){
        Status.INWAITING ->
            InWaitingTypeParent(invetation = invetation, companyId = companyId){
                onClicked(it)
            }
        Status.ACCEPTED ->
            AcceptTypeParent(invetation = invetation, companyId = companyId)
        Status.REFUSED ->
            RefuseTypeParent(invetation = invetation, companyId = companyId)
        Status.CANCELLED ->
            CancelTypeParent(invetation = invetation, companyId = companyId)
        null -> TODO()
    }
}
@Composable
fun CancelTypeParent(invetation: Invitation, companyId: Long) {
    if (invetation.companyReceiver?.id == companyId) {

        Row {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
            }
            Row(
                modifier = Modifier.weight(3f)
            ) {
                Text(text = "${invetation.companySender?.name} has canceled a parent invitation")
            }
        }

    } else {
        Row {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
            }
            Row(
                modifier = Modifier.weight(3f)
            ) {
                Text(text = " you have canceled a parent invitation to ${invetation.companyReceiver?.name}")
            }
        }
    }
}
@Composable
fun RefuseTypeParent(invetation: Invitation, companyId: Long) {
    if(invetation.companyReceiver?.id == companyId){
        Row {

            Row (
                modifier = Modifier.weight(1f)
            ){
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
            }
            Row (
                modifier = Modifier.weight(3f)
            ){
                Text(text = " you have refused a parent invitation from ${invetation.companySender?.name}")
            }
        }
    }
    else{
        Row {

            Row (
                modifier = Modifier.weight(1f)
            ){
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
            }
            Row (
                modifier = Modifier.weight(3f)
            ){
                Text(text = "${invetation.companyReceiver?.name} has refused your parent invitation")
            }
        }
    }
}
@Composable
fun InWaitingTypeParent(invetation: Invitation , companyId: Long, onClicked: (Status) -> Unit) {
    if(invetation.companyReceiver?.id == companyId){
        Row {

            Row (
                modifier = Modifier.weight(1f)
            ){
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
            }
            Row (
                modifier = Modifier.weight(3f)
            ){

                Column {

                    Text(text = "${invetation.companySender?.name} has sent a parent invitation")
                    Row {
                        Row (
                            modifier = Modifier.weight(1f)
                        ){

                            ButtonSubmit(labelValue = "accept", color = Color.Green, enabled = true) {
                                onClicked(Status.ACCEPTED)
                            }
                        }
                        Row (
                            modifier = Modifier.weight(1f)
                        ){

                            ButtonSubmit(labelValue = "refuse", color = Color.Red, enabled = true) {
                                onClicked(Status.REFUSED)
                            }
                        }
                    }
                }
            }
        }
    }
    else{
        Row {

            Row (
                modifier = Modifier.weight(1f)
            ){
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
            }
            Row (
                modifier = Modifier.weight(3f)
            ){

                Column {

                    Text(text = " you have sent a parent invitation to ${invetation.companyReceiver?.name}")
                    ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                        onClicked(Status.CANCELLED)
                    }
                }
            }
        }
    }
}
@Composable
fun AcceptTypeParent(invetation: Invitation,companyId: Long) {
    if(invetation.companyReceiver?.id == companyId){
        Row {

            Row (
                modifier = Modifier.weight(1f)
            ){
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
            }
            Row (
                modifier = Modifier.weight(3f)
            ){
                Text(text = " you have accepted a parent invitation from ${invetation.companySender?.name}")
            }
        }
    }
    else{
        Row {

            Row (
                modifier = Modifier.weight(1f)
            ){
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
            }
            Row (
                modifier = Modifier.weight(3f)
            ){
                Text(text = "${invetation.companyReceiver?.name} has accepted your parent invitation")
            }
        }
    }
}
@Composable
fun InvitationTypeProvider(invetation : Invitation, companyId : Long, role : AccountType,onClicked: (Status) -> Unit){
    when (invetation.status){
        Status.INWAITING ->
            InWaitingTypeProvider(invetation = invetation, companyId = companyId){
                onClicked(it)
            }
        Status.ACCEPTED ->
            AcceptTypeProvider(invetation = invetation, companyId = companyId)
        Status.REFUSED ->
            RefuseTypeProvider(invetation = invetation, companyId = companyId, role = role)
        Status.CANCELLED ->
            CancelTypeProvider(invetation = invetation, companyId = companyId, role = role)

        null -> TODO()
    }
}
@Composable
fun CancelTypeProvider(invetation: Invitation, companyId: Long, role: AccountType) {
    if(invetation.type == Type.COMPANY_SEND_PROVIDER_COMPANY){
        if(invetation.companyReceiver?.id == companyId){
            Row {
                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text =  invetation.companySender?.name!! + " has canceled a provider invitation")
                }
            }
        }else{
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = " you have canceled a provider invitation to " + invetation.companyReceiver?.name!!)
                }
            }
        }
    }else{
        if(role == AccountType.COMPANY){
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = " you have canceled a provider invitation to " + invetation.client?.username!!)
                }
            }
        }else{
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = " you have canceled a provider invitation to " + invetation.companyReceiver?.name!!)
                }
            }
        }
    }
}
@Composable
fun RefuseTypeProvider(invetation: Invitation, companyId: Long, role : AccountType) {
    if(invetation.type == Type.COMPANY_SEND_PROVIDER_COMPANY){
        if(invetation.companyReceiver?.id == companyId){
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = " you have refused a provider invitation from " + invetation.companySender?.name!!)
                }
            }
        }else {
            Row {

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Text(text = " you have refused a provider invitation from " + invetation.companyReceiver?.name!!)
                }
            }
        }
    }else {
        if (role == AccountType.COMPANY) {
            Row {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Text(text = invetation.client?.username!! + " has refused your provider invitation")
                }
            }

        } else {
            Row {

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Text(text = "you have refused a provider invitation from ${invetation.companySender?.name!!}")
                }
            }
        }
    }

}
@Composable
fun InWaitingTypeProvider(invetation: Invitation , companyId: Long, onClicked: (Status) -> Unit) {

    if(invetation.type == Type.COMPANY_SEND_PROVIDER_COMPANY ){
        if(invetation.companyReceiver?.id == companyId){
            Row {

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Column {

                        Text(text = "${invetation.companySender?.name} has sent a provider invitation")
                        Row {

                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(labelValue = "accept", color = Color.Green, enabled = true) {
                                    onClicked(Status.ACCEPTED)
                                }

                            }
                            Row(
                                modifier = Modifier.weight(1f)
                            ){
                                ButtonSubmit(labelValue = "refuse", color = Color.Red, enabled = true) {
                                    onClicked(Status.REFUSED)
                                }
                            }
                        }
                    }
                }
            }
        }else{
            Row {

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Column {

                        Text(text = "you have sent a provider invitation to ${invetation.companyReceiver?.name}")
                        ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                            onClicked(Status.CANCELLED)
                        }
                    }
                }
            }
        }
    }else{
        if(invetation.companySender?.id == companyId){
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Column {

                        Text(text = " you have sent a provider invitation to " + invetation.client?.username!!)
                        ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                            onClicked(Status.CANCELLED)
                        }

                    }
                }
            }

        }else{
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Column {
                        Text(text = "${invetation.companySender?.name!!} has sent a provider invitation")
                        Row {
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(labelValue = "accept", color = Color.Green, enabled = true) {
                                    onClicked(Status.ACCEPTED)
                                }
                            }
                            Row (
                                modifier = Modifier.weight(1f)
                            ){
                                ButtonSubmit(labelValue = "refuse", color = Color.Red, enabled = true) {
                                    onClicked(Status.REFUSED)
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}
@Composable
fun AcceptTypeProvider(invetation: Invitation,companyId: Long) {
    if(invetation.type == Type.COMPANY_SEND_PROVIDER_COMPANY){
        Row {
            Row (
                modifier = Modifier.weight(1f)
            ){

                if(invetation.companyReceiver?.id == companyId) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }else{
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                }
            }
            Row (
                modifier = Modifier.weight(3f)
            ){
                if(invetation.companyReceiver?.id == companyId){
                    Text(text = " you have accepted a provider invitation from " + invetation.companySender?.name!!)
                }else{
                    Text(text = invetation.companyReceiver?.name!! + " has accepted your provider invitation")
                }
            }
        }
    }else {
        Row {
            Row(
                modifier = Modifier.weight(1f)
            ) {

                if(invetation.companySender?.id != companyId){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                else {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }

            }
            Row(
                modifier = Modifier.weight(3f)
            ) {
                if (invetation.companySender?.id == companyId) {
                    Text(text = invetation.client?.username!! + " has accepted your provider invitation ")
                } else {
                    Text(text = "you have accepted a provider invitation from ${invetation.companySender?.name!!}")
                }
            }
        }
    }
}
@Composable
fun InvitationTypeClient(invetation : Invitation, companyId : Long, onClicked: (Status) -> Unit){
    when (invetation.status){
        Status.INWAITING ->
            InWaitingTypeClient(invetation = invetation, companyId = companyId){
                onClicked(it)
            }
        Status.ACCEPTED ->
            AcceptTypeClient(invetation = invetation, companyId = companyId)
        Status.REFUSED ->
            RefuseTypeClient(invetation = invetation, companyId = companyId)
        Status.CANCELLED ->
            CancelTypeClient(invetation = invetation, companyId = companyId)
        null -> TODO()
    }
}
@Composable
fun CancelTypeClient(invetation: Invitation, companyId: Long) {
    if(invetation.type == Type.COMPANY_SEND_CLIENT_COMPANY){
        if(invetation.companyReceiver?.id == companyId){
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text =  "${invetation.companySender?.name!!} has canceled a client invitation")
                }
            }
        }else{
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = " you have canceled a client invitation to  ${invetation.companyReceiver?.name!!}")
                }
            }
        }
    }else{
        if(invetation.companyReceiver?.id == companyId){

            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = "${invetation.client?.username!!} has canceled a client invitation")
                }
            }
        }
        else{
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = " you have canceled a client invitation to ${invetation.companyReceiver?.name!!}")
                }
            }
        }
    }


}
@Composable
fun RefuseTypeClient(invetation: Invitation, companyId: Long) {

    if(invetation.type == Type.COMPANY_SEND_CLIENT_COMPANY){
        if(invetation.companyReceiver?.id == companyId){
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = " you have refused a client invitation from ${invetation.companySender?.name!!}")
                }
            }
        }else{
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = invetation.companyReceiver?.name!! + " has refused your client invitation")
                }
            }
        }
    }else{
        if(invetation.companyReceiver?.id == companyId) {
            Row {

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Text(text = "you have refused a client invitation from ${invetation.client?.username!!}")
                }
            }
        }
        else{
            Row {

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Text(text = invetation.companyReceiver?.name!! + " has refused your client invitation")
                }
            }
        }
    }


}
@Composable
fun InWaitingTypeClient(invetation: Invitation , companyId: Long, onClicked: (Status) -> Unit) {
    if(invetation.type == Type.COMPANY_SEND_CLIENT_COMPANY){
        if(invetation.companyReceiver?.id == companyId){
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Column {

                        Text(text =  "${invetation.companySender?.name!!} has sent a client invitation")
                        Row {

                            Row (
                                modifier = Modifier.weight(1f)
                            ){
                                ButtonSubmit(labelValue = "accept", color = Color.Green, enabled = true) {
                                    onClicked(Status.ACCEPTED)
                                }
                            }
                            Row (
                                modifier = Modifier.weight(1f)
                            ){

                                ButtonSubmit(labelValue = "refuse", color = Color.Red, enabled = true) {
                                    onClicked(Status.REFUSED)
                                }
                            }
                        }

                    }
                }
            }
        }else{
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){Log.e("azertyuio","receiver : ${invetation.companyReceiver}, sender : ${invetation.companySender}")
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Column {

                        Text(text = " you have sent a client invitation to ${invetation.companyReceiver?.name?:""}")
                        ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                            onClicked(Status.CANCELLED)
                        }
                    }
                }
            }
        }
    }else{
        if(invetation.companyReceiver?.id == companyId) {
            Row {

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Column {

                        Text(text = "${invetation.client?.username!!} has sent a client invitation ")
                        Row {
                            Row (
                                modifier = Modifier.weight(1f)
                            ){
                                ButtonSubmit(labelValue = "accept", color = Color.Green, enabled = true) {

                                }
                            }
                            Row (
                                modifier = Modifier.weight(1f)
                            ){

                                ButtonSubmit(labelValue = "refuse", color = Color.Red, enabled = true) {

                                }
                            }
                        }
                    }
                }
            }
        }
        else{
            Row {

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Column {

                        Text(text = " you have sent a client invitation to " + invetation.companyReceiver?.name!!)
                        ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                            onClicked(Status.CANCELLED)
                        }

                    }
                }
            }
        }
    }
}
@Composable
fun AcceptTypeClient(invetation: Invitation,companyId: Long) {
    if(invetation.type == Type.COMPANY_SEND_CLIENT_COMPANY){
        if(invetation.companyReceiver?.id == companyId){
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = " you have accepted a client invitation from ${invetation.companySender?.name!!}")
                }
            }
        }else{
            Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = invetation.companyReceiver?.name!! + " has accepted your client invitation")
                }
            }
        }
    }else {
        if (invetation.companyReceiver?.id == companyId) {
            Row {

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Text(text = invetation.client?.username!! + " has accepted your client invitation ")
                }
            }
        }
        else{
            Row {

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReceiver?.logo}/company/${invetation.companyReceiver?.user?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Text(text = invetation.companyReceiver?.name!! + " has accepted your client invitation ")
                }
            }
        }
    }
}
