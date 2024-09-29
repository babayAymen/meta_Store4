package com.aymen.store.ui.screen.user

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import com.aymen.store.model.Enum.CompanyCategory

@Composable
fun ArticleDetailsScreen() {
    val articleViewModel : ArticleViewModel = viewModel()
    val companyViewModel : CompanyViewModel = viewModel()
    val messageViewModel : MessageViewModel = viewModel()
    val appViewModel : AppViewModel = viewModel()
    val company = companyViewModel.myCompany
    val article = articleViewModel.articleCompany
    var showComment by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(articleViewModel.articleCompany) {
        Log.e("aymenbabaycomment", "article details: ${article.id}")
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
            .padding(1.dp)
    ) {
        Column (modifier = Modifier.fillMaxSize()){
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                item {
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 2.dp)
                        ) {
                            AddTypeDialog(isOpen = false, company.id!!,true){}
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Icon(imageVector = Icons.Outlined.Send,
                                contentDescription = "send a message",
                                Modifier.clickable {
                                    messageViewModel.receiverCompany = company
                                    messageViewModel.getAllMyMessageByConversationId()
                                    RouteController.navigateTo(Screen.HomeScreen)
                                    appViewModel.updateShow("message")
                                    appViewModel.updateScreen(IconType.MESSAGE)
                                }
                            )
                        }
                    }
                    company.address?.let { it1 -> Text(text = it1) }
                    company.phone?.let { it1 -> Text(text = it1) }
                    company.email?.let { Text(text = it) }
                    company.code?.let { it1 -> Text(text = it1) }
                    company.matfisc?.let { it1 -> Text(text = it1) }
                    ArticleCardForAdmin(
                        article = article,
                        image = "${BASE_URL}werehouse/image/${article.article!!.image}/article/${CompanyCategory.valueOf(article.company?.category!!).ordinal}"
                    ){}
                    if(articleViewModel.allComments.isNotEmpty()) {
                        Column {
                            articleViewModel.allComments.forEach {
                                Text(text = if (it.user == null) it.companie?.name!! else it.user?.username!!)
                                Text(text = it.content)
                                DividerTextComponent()
                            }
                        }
                    }
                    if(showComment){

                    Row {
                        Text(text = if(company.name != "") company.name else "aymen babay")
                        Text(text = articleViewModel.myComment)
                        DividerTextComponent()
                    }
                    }
                }
            }
                    Row{

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
                            }
                            , onImage = {}
                            , true
                        ) {
                            if (comment.isNotEmpty()) {
                                Log.e("comment", "comment : $comment")
                                showComment = true
                                articleViewModel.myComment = comment
                                article.id?.let { articleViewModel.sendComment(comment, it) }
                                comment = ""
                            }
                }
            }
        }
        SystemBackButtonHandler {
            RouteController.navigateTo(Screen.HomeScreen)
        }
    }
}