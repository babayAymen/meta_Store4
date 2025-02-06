package com.aymen.metastore.ui.screen.user

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.ui.component.AddTypeDialog
import com.aymen.metastore.ui.component.SendPointDialog
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.aymen.store.ui.navigation.SystemBackButtonHandler
import com.aymen.metastore.R
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.Rating
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.repository.ViewModel.RatingViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.metastore.model.repository.ViewModel.ClientViewModel
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.InputTextField
import com.aymen.metastore.ui.component.NotImage
import com.aymen.metastore.util.BASE_URL
import com.aymen.metastore.util.IMAGE_URL_USER
import com.aymen.store.model.Enum.Type
import com.google.gson.Gson
import kotlinx.coroutines.flow.map

@Composable
fun UserScreen() { // subtruct the quantity from article when you send an order

    val appViewModel: AppViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val ratingViewModel : RatingViewModel = hiltViewModel()
    val clientViewModel : ClientViewModel = hiltViewModel()
    val company by sharedViewModel.company.collectAsStateWithLifecycle()
    val isPointSeller by remember {
        mutableStateOf(company.isPointsSeller)
    }
    val user by sharedViewModel.hisUser.collectAsStateWithLifecycle()
    var rating by remember {
        mutableStateOf(false)
    }
    var hisClient by remember {
        mutableStateOf(false)
    }
    var hisWorker by remember {
        mutableStateOf(false)
    }
    val relationList = clientViewModel.relationList
        .map { list ->
            list.map { invitation ->
                invitation.companyReceiver?.let {pro ->
                    if(invitation.type == Type.USER_SEND_CLIENT_COMPANY || invitation.type == Type.COMPANY_SEND_PROVIDER_USER)
                        hisClient = true
                    else
                        hisWorker = true
                }
                invitation.companySender?.let {pro ->
                    if(invitation.type == Type.USER_SEND_CLIENT_COMPANY || invitation.type == Type.COMPANY_SEND_PROVIDER_USER)
                        hisClient = true
                    else
                        hisWorker = true
                }
            }
        }.collectAsStateWithLifecycle(emptyList())

    LaunchedEffect(key1 = Unit) {
        ratingViewModel.enabledToCommentUser(user.id!!)
        clientViewModel.checkRelation(user.id!!, AccountType.USER)
    }
    DisposableEffect(key1 = Unit) {
        onDispose {
            clientViewModel.setRelationList()
        }
    }

    var comment by remember { mutableStateOf("") }
    val gson = Gson()
    val ratingg by remember {
        mutableStateOf(
            Rating(
                rateeCompany = Company(),
                rateeUser = User()
            )
        )
    }

    var imageHeight by remember { mutableStateOf(0.dp) }
    var imageBitmap by remember { mutableStateOf<Uri?>(null) }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp, 36.dp, 3.dp, 3.dp),
        bottomBar = {
            if(ratingViewModel.enableToRating)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                // Input field for adding a comment
                InputTextField(
                    labelValue = comment,
                    label = stringResource(id = R.string.type_a_comment),
                    singleLine = false,
                    maxLine = 6,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Send
                    ),
                    onValueChange = {
                        comment = it
                    },
                    onImage = { bitmap ->
                        imageHeight = 100.dp
                        imageBitmap = bitmap
                    }
                ) {
                    ratingg.comment = comment
                    ratingg.rateValue = ratingViewModel.rate
                    ratingg.rateeUser = user
                    ratingg.raterCompany = company
                    ratingViewModel.enableToRating = false
                    val ratingJson = gson.toJson(ratingg)
                    ratingViewModel.doRate(ratingg, ratingJson, it)
                    comment = ""
                    imageBitmap = null
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(2.dp)
                .imePadding()
        ) {
            Column {
            Row {
                if (user.image != null)
                    ShowImage(image = String.format(IMAGE_URL_USER, user.image, user.id))
                else
                    NotImage()
//                    Icon(
//                        imageVector = Icons.Default.Verified,
//                        contentDescription = "verification account",
//                        tint = Color.Green
//                    )
                user.username?.let { Text(text = it) }
            }
            Row(
                modifier = Modifier
                    .padding(2.dp)
                    .imePadding()
            ) {
                UserDetails(
                    clientViewModel,
                    sharedViewModel,
                    ratingViewModel,
                    user,
                    hisClient,
                    hisWorker,
                    isPointSeller!!
                ) {
                }
            }
            user.address?.let { it1 -> Text(text = it1) }

            user.phone?.let { it1 -> Text(text = it1) }

            user.email?.let { Text(text = it) }
        }
            RatingScreen(AccountType.USER, null, user)
        }
    }


        SystemBackButtonHandler {
            RouteController.navigateTo(Screen.HomeScreen)
        }

}

@Composable
fun UserDetails( clientViewModel: ClientViewModel, sharedViewModel: SharedViewModel,
                ratingViewModel : RatingViewModel, user: User, hisClient : Boolean, hisWorker : Boolean, isMePointSeller : Boolean, onRatingChanged: () -> Unit) {
    val accountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
    var rating by remember {
        mutableStateOf(false)
    }
    Row {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 2.dp)
        ) {
            if (accountType != AccountType.USER)//il faux regleé as well
                 {
                AddTypeDialog(
                    isOpen = false,
                    user.id?:0L,
                    false,
                    hisClient,
                    false,
                    false,
                    hisWorker
                ) { type, isDeleted ->
                    clientViewModel.sendClientRequest(user.id!!, type, isDeleted)
                }
        }
        }
        if (accountType == AccountType.COMPANY &&  isMePointSeller) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                SendPointDialog(isOpen = false, user, Company())
            }
        }
        if(accountType == AccountType.META){
            ButtonSubmit(labelValue = if(user.accountType == AccountType.USER) stringResource(id = R.string.add_delivery) else stringResource(
                id = R.string.remove_delivery
            ), color = Color.Green, enabled = true) {
                clientViewModel.addAsDelivery(user.id!!)
            }
        }
        Column (
            modifier = Modifier.weight(1.8f)
        ){
            if (rating && ratingViewModel.enableToRating) {
                StarRating(
                    user.rate?.toInt()!!,
                    modifier = Modifier
                        .fillMaxWidth(),
                    onRatingChanged = { newRating ->
                        ratingViewModel.rating = true
                        ratingViewModel.rate = newRating
                    }
                )
            }else{
                Row (
                    modifier = Modifier.clickable {
                        rating = true
                    }
                ){
                    Text(text = user.rate?.toString()!!)
                    Icon(
                        imageVector = if(user.rate == 0.0)Icons.Outlined.StarOutline else if(user.rate == 5.0)Icons.Filled.Star else Icons.AutoMirrored.Filled.StarHalf ,
                        contentDescription = stringResource(id = R.string.rating)
                    )
                }
            }
            Text(text = stringResource(id = R.string.reviews,user.rater?.toString()?:0))

        }
    }
}
