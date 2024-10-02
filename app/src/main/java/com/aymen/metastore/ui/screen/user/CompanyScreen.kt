package com.aymen.store.ui.screen.user

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.metastore.R
import com.aymen.metastore.model.repository.ViewModel.RatingViewModel
import com.aymen.metastore.ui.screen.user.RatingScreen
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.IconType
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.model.entity.realm.Company
import com.aymen.metastore.model.entity.realm.User
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ArticleViewModel
import com.aymen.store.model.repository.ViewModel.ClientViewModel
import com.aymen.store.model.repository.ViewModel.CompanyViewModel
import com.aymen.store.model.repository.ViewModel.MessageViewModel
import com.aymen.store.ui.component.AddTypeDialog
import com.aymen.store.ui.component.ArticleCardForSearch
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.SendPointDialog
import com.aymen.store.ui.component.ShowImage
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.aymen.store.ui.navigation.SystemBackButtonHandler

@Composable
fun CompanyScreen(company: Company) {
    val articleViewModel: ArticleViewModel = hiltViewModel()
    val appViewModel: AppViewModel = hiltViewModel()
    val messageViewModel: MessageViewModel = hiltViewModel()
    val companyViewModel: CompanyViewModel = hiltViewModel()
    val ratingViewModel: RatingViewModel = hiltViewModel()
    val clientViewModel: ClientViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val context = LocalContext.current
    var myCompany by remember {
        mutableStateOf(Company())
    }
    LaunchedEffect(key1 = Unit) {
        articleViewModel.getAllMyArticlesApi()
        if (sharedViewModel.accountType == AccountType.COMPANY) {
            myCompany = sharedViewModel._company.value
        }
    }
    var rating by remember {
        mutableStateOf(false)
    }
    val listState = rememberLazyListState()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp, 36.dp, 3.dp, 3.dp)
    ) {
        Column {
            LazyColumn(
                state = listState
            ) {
                item {
                    Row {
                        if (company.logo == "") {
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
                            ShowImage(image = "${BASE_URL}werehouse/image/${company.logo}/company/${company.user?.id}")
                        }
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "verification account",
                            tint = Color.Green
                        )
                        Text(text = company.name)

                    }
                    Row(
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 2.dp)
                        ) {
                            AddTypeDialog(isOpen = false, company.id!!, true) {
                                clientViewModel.sendClientRequest(company.id!!, it)
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
                                    messageViewModel.getConversationByCaleeId(company.id!!)
                                    appViewModel.updateShow("message")
                                    appViewModel.updateScreen(IconType.MESSAGE)
                                    messageViewModel.receiverAccountType = AccountType.COMPANY
                                }
                            )

                        }
                        if (myCompany.isPointsSeller!!) {
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                SendPointDialog(isOpen = false, User(), company)
                            }
                        }
                        if (appViewModel.userRole == RoleEnum.AYMEN) {
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                if (!company.isPointsSeller!!) {
                                    ButtonSubmit(
                                        labelValue = "make as seller",
                                        color = Color.Green,
                                        enabled = true
                                    ) {
                                        companyViewModel.MakeAsPointSeller(true, company.id!!)
                                    }
                                } else {

                                    ButtonSubmit(
                                        labelValue = "remove as seller",
                                        color = Color.Red,
                                        enabled = true
                                    ) {
                                        companyViewModel.MakeAsPointSeller(false, company.id!!)
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            if (!rating) {

                                Column(
                                    modifier = Modifier.clickable {
                                        rating = !rating
                                    }
                                ) {

                                    Icon(
                                        imageVector = Icons.Outlined.StarOutline,
                                        contentDescription = "rate"
                                    )
                                }
                            } else {

                                StarRating(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    onRatingChanged = { newRating ->
                                        ratingViewModel.rating = true
                                        ratingViewModel.rate = newRating
                                        // Update the rating state or perform any other action with the new rating
                                    }
                                )
                            }
                            Text(text = company.raters.toString())
                        }
                    }
                }
                item {
                    company.address?.let { it1 -> Text(text = it1) }
                    company.phone?.let { it1 -> Text(text = it1) }
                    company.email?.let { Text(text = it) }
                    company.code?.let { it1 -> Text(text = it1) }
                    company.matfisc?.let { it1 -> Text(text = it1) }
                }




                if (!rating) {
                    items(articleViewModel.adminArticles) {
                        ArticleCardForSearch(article = it) {
                            companyViewModel.myCompany = it.company!!
                            articleViewModel.articleCompany = it
                            RouteController.navigateTo(Screen.ArticleDetailScreen)
                        }
                    }
                } else {
                    item {
                    RatingScreen(AccountType.COMPANY, company, null)
                    }

                }
            }
            SystemBackButtonHandler {
                RouteController.navigateTo(Screen.HomeScreen)
            }
        }
    }
}


@Composable
fun StarRating(
    modifier: Modifier = Modifier,
    starCount: Int = 5,
    onRatingChanged: (Int) -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..starCount) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = "rate",
                tint = if (i <= rating) Color.Yellow else Color.Gray,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        rating = i
                        onRatingChanged(rating)
                    }
            )
        }
    }
}