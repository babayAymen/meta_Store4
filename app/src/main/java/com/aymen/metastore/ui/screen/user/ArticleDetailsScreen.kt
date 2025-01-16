package com.aymen.metastore.ui.screen.user

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.ArticleViewModel
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.metastore.ui.component.ArticleCardForAdmin
import com.aymen.metastore.ui.component.DividerTextComponent
import com.aymen.metastore.ui.component.InputTextField
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.aymen.store.ui.navigation.SystemBackButtonHandler
import com.aymen.metastore.R
import com.aymen.metastore.model.entity.model.Comment
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.repository.ViewModel.RatingViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.metastore.model.repository.ViewModel.ClientViewModel
import com.aymen.metastore.ui.component.notImage
import com.aymen.metastore.util.BASE_URL
import com.aymen.metastore.util.IMAGE_URL_ARTICLE
import com.aymen.metastore.util.IMAGE_URL_COMPANY
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.model.Enum.Type
import kotlinx.coroutines.flow.map

@Composable
fun ArticleDetailsScreen() {
    val articleViewModel : ArticleViewModel = hiltViewModel()
    val companyViewModel : CompanyViewModel = hiltViewModel()
    val appViewModel : AppViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val clientViewModel : ClientViewModel = hiltViewModel()
    val ratingViewModel : RatingViewModel = hiltViewModel()
    val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()
    val company by sharedViewModel.hisCompany.collectAsStateWithLifecycle()
    val myUser by sharedViewModel.user.collectAsStateWithLifecycle()
    val myAccountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
    val article by articleViewModel.articleCompany.collectAsStateWithLifecycle()
    val userComment by articleViewModel.userComment.collectAsStateWithLifecycle()
    val companyComment by articleViewModel.companyComment.collectAsStateWithLifecycle()
    val comments = articleViewModel.commentArticle.collectAsLazyPagingItems()
    var showComment by remember {
        mutableStateOf(false)
    }
    var hisParent by remember {
        mutableStateOf(false)
    }
    var hisWorker by remember {
        mutableStateOf(false)
    }
    var hisClient by remember {
        mutableStateOf(false)
    }
    var hisProvider by remember {
        mutableStateOf(false)
    }
    var creditAsClient by remember {
        mutableDoubleStateOf(0.0)
    }
    var creditAsProvider by remember {
        mutableDoubleStateOf(0.0)
    }
    val relationList = clientViewModel.relationList
        .map { list ->
            list.map { invitation ->
                invitation.client?.let {cli ->
                    if(myAccountType == AccountType.USER) {
                        if(invitation.type == Type.USER_SEND_CLIENT_COMPANY || invitation.type == Type.COMPANY_SEND_PROVIDER_USER)
                            hisProvider = true
                        else
                            hisWorker = true
//                        creditAsProvider = invitation.credit!!
                    }
                }
                invitation.companyReceiver?.let {pro ->
                    if(pro.id == company.id) {
                        if(invitation.type == Type.COMPANY_SEND_CLIENT_COMPANY)
                            hisClient = true
                        if(invitation.type == Type.COMPANY_SEND_PROVIDER_COMPANY)
                            hisProvider = true
//                        creditAsClient = invitation.credit!!
                    }
                }
                invitation.companySender?.let {pro ->
                    if(pro.id == company.id) {
                        if(invitation.type == Type.COMPANY_SEND_PROVIDER_COMPANY)
                            hisClient = true
                        if(invitation.type == Type.COMPANY_SEND_CLIENT_COMPANY)
                            hisProvider = true
                    }
                }

            }
        }.collectAsStateWithLifecycle(emptyList())

    LaunchedEffect(key1 = Unit) {
        ratingViewModel.enabledToCommentArticle(company.id!!)
        clientViewModel.checkRelation(id = company.id!!, AccountType.COMPANY)
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
                                sharedViewModel.setHisCompany(company)
                                articleViewModel.companyId = company.id!!
                                RouteController.navigateTo(Screen.CompanyScreen)
                            }
                    ) {
                        if (company.logo != null) {
                            ShowImage(image = String.format(IMAGE_URL_COMPANY,company.logo, company.user?.id))
                        } else {
                            notImage()
                        }
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "verification account",
                            tint = Color.Green
                        )
                        Text(text = company.name)

                    }
                 CompanyDetails(
                     sharedViewModel = sharedViewModel,
                     clientViewModel = clientViewModel,
                     companyViewModel = companyViewModel,
                     hisClient = hisClient,
                     hisProvider = hisProvider,
                     hisParent = hisParent,
                     hisWorker = hisWorker,
                     ratingViewModel = ratingViewModel,
                     company = company,
                     isPointsSeller = myCompany.isPointsSeller!!,
                     appViewModel = appViewModel
                 ) {

                 }
                    company.address?.let { it1 -> Text(text = it1) }
                    Row {
                    company.phone?.let { it1 -> Text(text = it1) }
                    company.email?.let { Text(text = it) }
                    }
                    company.code?.let { it1 -> Text(text = it1) }
                    company.matfisc?.let { it1 -> Text(text = it1) }
                    //to
                    ArticleCardForAdmin(
                        article!!,
                        image = String.format(IMAGE_URL_ARTICLE,article!!.article?.image,article!!.company?.category?.ordinal)
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
                            val com = Comment()
                            com.content = comment
                            com.article = article
                            article?.id?.let { articleViewModel.sendComment(com) }
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