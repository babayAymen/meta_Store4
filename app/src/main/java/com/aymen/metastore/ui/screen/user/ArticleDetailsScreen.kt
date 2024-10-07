package com.aymen.store.ui.screen.user

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.Enum.IconType
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ArticleViewModel
import com.aymen.store.model.repository.ViewModel.CompanyViewModel
import com.aymen.store.model.repository.ViewModel.MessageViewModel
import com.aymen.store.ui.component.AddTypeDialog
import com.aymen.store.ui.component.ArticleCardForAdmin
import com.aymen.store.ui.component.DividerTextComponent
import com.aymen.store.ui.component.InputTextField
import com.aymen.store.ui.component.ShowImage
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.aymen.store.ui.navigation.SystemBackButtonHandler
import com.aymen.metastore.R
import com.aymen.metastore.model.repository.ViewModel.RatingViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.repository.ViewModel.ClientViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

@Composable
fun ArticleDetailsScreen() {
    val articleViewModel : ArticleViewModel = hiltViewModel()
    val companyViewModel : CompanyViewModel = hiltViewModel()
    val messageViewModel : MessageViewModel = hiltViewModel()
    val appViewModel : AppViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val clientViewModel : ClientViewModel = hiltViewModel()
    val ratingViewModel : RatingViewModel = hiltViewModel()
    var myCompany by remember {
        mutableStateOf(Company())
    }
    val company = companyViewModel.myCompany
    val article = articleViewModel.articleCompany
    var showComment by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        ratingViewModel.enabledToCommentArticle(company.id!!)
        if (sharedViewModel.accountType == AccountType.COMPANY) {
            myCompany = sharedViewModel._company.value
        }
    }
    LaunchedEffect(articleViewModel.articleCompany) {
        articleViewModel.getAllArticleComments()
    }
    var comment by remember {
        mutableStateOf("")
    }
    DisposableEffect(Unit) {
        onDispose {
            articleViewModel.allComments = emptyList()
        }
    }
    val listState = rememberLazyListState()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp, 36.dp, 3.dp, 10.dp)
    ) {
        Column (modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                item {
                    // from
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                companyViewModel.myCompany = company
                                articleViewModel.companyId = company.id!!
                                RouteController.navigateTo(Screen.CompanyScreen)
                            }
                    ) {
                        if (company.logo == null) {
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
                 companyDetails(
                     messageViewModel = messageViewModel,
                     appViewModel = appViewModel,
                     clientViewModel = clientViewModel,
                     companyViewModel = companyViewModel,
                     ratingViewModel = ratingViewModel,
                     company = company,
                     isMePointSeller = myCompany.isPointsSeller!!
                 ) {

                 }
                    company.address?.let { it1 -> Text(text = it1) }
                    company.phone?.let { it1 -> Text(text = it1) }
                    company.email?.let { Text(text = it) }
                    company.code?.let { it1 -> Text(text = it1) }
                    company.matfisc?.let { it1 -> Text(text = it1) }
                    //to
                    ArticleCardForAdmin(
                        article = article,
                        image = "${BASE_URL}werehouse/image/${article.article!!.image}/article/${
                            CompanyCategory.valueOf(
                                article.company?.category!!
                            ).ordinal
                        }"
                    ) {}
                }
                if (articleViewModel.allComments.isNotEmpty()) {
                    items(articleViewModel.allComments) {
                        Text(text = if (it.user == null) it.company?.name!! else it.user?.username!!)
                        Text(text = it.content)
                        DividerTextComponent()
                    }

                }
                if (showComment) {
                    item {

                        Row {
                            Text(text = if (company.name != "") company.name else "aymen babay")
                            Text(text = articleViewModel.myComment)
                            DividerTextComponent()
                        }
                    }
                }
            }
            if (ratingViewModel.enableToComment) {
                Row {

                    InputTextField(
                        labelValue = comment,
                        label = "Type a comment",
                        singleLine = false,
                        maxLine = 6,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Send
                        ),
                        onValueChange = {
                            comment = it
                        }, onImage = {}, true
                    ) {
                        if (comment.isNotEmpty()) {
                            showComment = true
                            articleViewModel.myComment = comment
                            article.id?.let { articleViewModel.sendComment(comment, it) }
                            comment = ""
                            ratingViewModel.enableToComment = false
                        }
                    }
                }
            }
        }
        SystemBackButtonHandler {
            RouteController.navigateTo(Screen.HomeScreen)
        }
    }
}