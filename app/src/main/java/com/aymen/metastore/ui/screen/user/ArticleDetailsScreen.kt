package com.aymen.metastore.ui.screen.user

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.aymen.metastore.model.Enum.RateType
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.Comment
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.Rating
import com.aymen.metastore.model.repository.ViewModel.RatingViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.metastore.model.repository.ViewModel.ClientViewModel
import com.aymen.metastore.ui.component.NotImage
import com.aymen.metastore.util.BASE_URL
import com.aymen.metastore.util.COMPANY_CONTENT
import com.aymen.metastore.util.IMAGE_URL_ARTICLE
import com.aymen.metastore.util.IMAGE_URL_COMPANY
import com.aymen.metastore.util.RATING_VIEW
import com.aymen.metastore.util.VERIFICATION_ACCOUNT
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.model.Enum.Type
import com.google.gson.Gson
import kotlinx.coroutines.flow.map

@Composable
fun ArticleDetailsScreen() {
    val articleViewModel : ArticleViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val ratingViewModel : RatingViewModel = hiltViewModel()
    val company by sharedViewModel.hisCompany.collectAsStateWithLifecycle()
    val myAccountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
    val article by articleViewModel.articleCompany.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showComment by remember {
        mutableStateOf(false)
    }
    var rate by remember {
        mutableIntStateOf(0)
    }
    val gson = Gson()

    LaunchedEffect(key1 = Unit) {
        ratingViewModel.enabledToCommentArticle(company.id!!)
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
                            NotImage()
                        }
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = VERIFICATION_ACCOUNT,
                            tint = if (company.metaSeller == true) Color.Green else Color.Cyan
                        )
                        Text(text = company.name)

                    }
                    ArticleDetails(ratingViewModel , article?:ArticleCompany()){
                        rate = it
                    }
                    //to
                    ArticleCardForAdmin(
                        article!!,
                        image = String.format(IMAGE_URL_ARTICLE,article!!.article?.image,article!!.article?.category?.ordinal)
                    ) {}
                }
                item {

                RatingScreen(rateType = RateType.USER_RATE_ARTICLE, article?.id!!)
                }
                if (showComment) {
                    item {

                        Row {
                            Text(text = company.name)
                            Text(text = articleViewModel.myComment)
                            DividerTextComponent()
                        }
                    }
                }
            }
            if (ratingViewModel.enableToComment && ratingViewModel.rating) {
                Row {

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
                        }, onImage = {}, true
                    ) {
                        if (comment.isNotEmpty()) {
                            showComment = true
                            val ratingType = if(myAccountType == AccountType.COMPANY) RateType.COMPANY_RATE_ARTICLE else RateType.USER_RATE_ARTICLE
                            val ratingg = Rating(comment = comment , rateValue = rate, article = article, type = ratingType)
                            val ratingJson = gson.toJson(ratingg)
                            ratingViewModel.doRate(ratingg, ratingJson, image = it, context)
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

@Composable
fun ArticleDetails(ratingViewModel: RatingViewModel, articleCompany: ArticleCompany, onRating : (Int) -> Unit) {
    Column (
        modifier = Modifier
            .clickable {
                ratingViewModel.rating = !ratingViewModel.rating
            }
    ) {
        if (ratingViewModel.rating && ratingViewModel.enableToComment) {
            StarRating(
                articleCompany.rate?.toInt()?:0,
                modifier = Modifier
                    .fillMaxWidth()
                ,
                onRatingChanged = { newRating ->
                    onRating(newRating)
                }
            )
        }else{
            Row {
                Text(text = articleCompany.rate?.toString()?:"20")
                Icon(
                    imageVector = if(articleCompany.rate == 0.0)Icons.Outlined.StarOutline
                    else if(articleCompany.rate == 5.0)Icons.Filled.Star
                    else Icons.AutoMirrored.Filled.StarHalf ,
                    contentDescription = stringResource(id = R.string.rate)
                )
            }
        }
        Text(text = stringResource(id = R.string.reviews,articleCompany.raters?:0))

    }
}