package com.aymen.metastore.ui.screen.user

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.dependencyInjection.BASE_URL
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.ArticleViewModel
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.metastore.model.repository.ViewModel.MessageViewModel
import com.aymen.metastore.ui.component.ArticleCardForAdmin
import com.aymen.metastore.ui.component.DividerTextComponent
import com.aymen.metastore.ui.component.InputTextField
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.aymen.store.ui.navigation.SystemBackButtonHandler
import com.aymen.metastore.R
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.repository.ViewModel.RatingViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.metastore.model.repository.ViewModel.ClientViewModel

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
    val article by articleViewModel.articleCompany.collectAsStateWithLifecycle()
    val userComment by articleViewModel.userComment.collectAsStateWithLifecycle()
    val companyComment by articleViewModel.companyComment.collectAsStateWithLifecycle()
    val comments = articleViewModel.commentArticle.collectAsLazyPagingItems()
    var showComment by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        ratingViewModel.enabledToCommentArticle(company.id!!)
        if (sharedViewModel.accountType.value == AccountType.COMPANY) {
            myCompany = sharedViewModel.company.value
        }
    }
    LaunchedEffect(articleViewModel.articleCompany) {
        articleViewModel.getAllArticleComments()
    }
    var comment by remember {
        mutableStateOf("")
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
                 CompanyDetails(
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
                        article!!,
                        image = "${BASE_URL}werehouse/image/${article!!.article?.image}/article/${article!!.company?.category?.ordinal}"
                    ) {}
                }
                    items(count = comments.itemCount,
                        key = comments.itemKey{it.id!!}
                        ) {index ->
                        val commantaire = comments[index]
                if (commantaire != null) {
                        Text(text = if (userComment.id == null) companyComment.name else userComment.username!!)
                        Text(text = commantaire.content)
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
                            article?.id?.let { articleViewModel.sendComment(comment, it) }
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