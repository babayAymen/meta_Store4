package com.aymen.store.ui.screen.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.Enum.IconType
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.MessageViewModel
import com.aymen.store.ui.component.AddTypeDialog
import com.aymen.store.ui.component.SendPointDialog
import com.aymen.store.ui.component.ShowImage
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.aymen.store.ui.navigation.SystemBackButtonHandler
import com.aymen.metastore.R
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.room.User
import com.aymen.metastore.model.repository.ViewModel.RatingViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.ui.screen.user.RatingScreen
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.repository.ViewModel.ClientViewModel
import com.aymen.store.model.repository.ViewModel.CompanyViewModel

@Composable
fun UserScreen() {

    val appViewModel: AppViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val messageViewModel : MessageViewModel = hiltViewModel()
    val companyViewModel : CompanyViewModel = hiltViewModel()
    val ratingViewModel : RatingViewModel = hiltViewModel()
    val clientViewModel : ClientViewModel = hiltViewModel()
    val company by sharedViewModel.company.collectAsStateWithLifecycle()
    val isPointSeller by remember {
        mutableStateOf(company.isPointsSeller)
    }
    val user by appViewModel.user.collectAsStateWithLifecycle()
    var rating by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        ratingViewModel.enabledToCommentUser(user.id!!)
    }
    val listState = rememberLazyListState()
    Surface(modifier = Modifier
        .fillMaxSize()
        .padding(3.dp, 36.dp, 3.dp, 3.dp)
    ) {
            Column {
                Row {
                    if (user.image == "") {
                        val painter: Painter = painterResource(id = R.drawable.emptyprofile)
                        Image(
                            painter = painter,
                            contentDescription = "empty photo profil",
                            modifier = Modifier
                                .size(30.dp)
                                .clip(
                                    RoundedCornerShape(10.dp)
                                )
                        )
                    } else {
                        if (user.id != null) {
                            ShowImage(image = "${BASE_URL}werehouse/image/${user.image}/user/${user.id}")
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "verification account",
                        tint = Color.Green
                    )
                    if (user.id != null) {
                        Text(text = user.username!!)
                    }

                }
                Row(
                    modifier = Modifier.padding(2.dp)
                ) {
                    userDetails(
                        messageViewModel,
                        appViewModel,
                        clientViewModel,
                        companyViewModel,
                        ratingViewModel,
                        user,
                        isPointSeller!!
                    ) {
                    }
                }
                user.address?.let { it1 -> Text(text = it1) }

                user.phone?.let { it1 -> Text(text = it1) }

                user.email?.let { Text(text = it) }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    RatingScreen(AccountType.USER, null, user)
                }
            }
        }


        SystemBackButtonHandler {
            RouteController.navigateTo(Screen.HomeScreen)
        }

}

@Composable
fun userDetails(messageViewModel: MessageViewModel, appViewModel: AppViewModel,clientViewModel: ClientViewModel,companyViewModel: CompanyViewModel,
                   ratingViewModel : RatingViewModel,user: User, isMePointSeller : Boolean, onRatingChanged: () -> Unit) {
    Row {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 2.dp)
        ) {
            AddTypeDialog(isOpen = false, user.id!!, false) {
                clientViewModel.sendClientRequest(user.id, it)
            }
        }
        Row(
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "send a message",
                Modifier.clickable {
//                            messageViewModel.senderId = company.user?.id!!
//                            messageViewModel.getAllMyMessageByConversationId()
//                            RouteController.navigateTo(Screen.HomeScreen)
//                    messageViewModel.messageType = MessageType.COMPANY_SEND_USER
                    messageViewModel.receiverAccountType = AccountType.USER
                    messageViewModel.receiverUser = user
                    messageViewModel.getAllMessageByCaleeId(user.id!!)// from user screen
                    appViewModel.updateShow("message")
                    appViewModel.updateScreen(IconType.MESSAGE)
                }
            )
        }
        if (isMePointSeller) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                SendPointDialog(isOpen = false, user, Company())
            }
        }
        Column (
            modifier = Modifier.weight(1.8f)
        ){
            if (ratingViewModel.rating && ratingViewModel.enableToRating) {
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
                Row {
                    Text(text = user.rate?.toString()!!)
                    Icon(
                        imageVector = if(user.rate == 0.0)Icons.Outlined.StarOutline else if(user.rate == 5.0)Icons.Filled.Star else Icons.AutoMirrored.Filled.StarHalf ,
                        contentDescription = "rating"
                    )
                }
            }
            Text(text = user.rater?.toString()!! +"reviews")

        }
    }
}
