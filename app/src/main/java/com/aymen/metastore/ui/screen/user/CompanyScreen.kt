package com.aymen.store.ui.screen.user

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.StarPurple500
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.entity.realm.Category
import com.aymen.store.model.entity.realm.SubCategory
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ArticleViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.store.model.repository.ViewModel.ClientViewModel
import com.aymen.store.model.repository.ViewModel.CompanyViewModel
import com.aymen.store.model.repository.ViewModel.MessageViewModel
import com.aymen.store.model.repository.ViewModel.SubCategoryViewModel
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
    val categoryViewModel : CategoryViewModel = hiltViewModel()
    val subCategoryViewModel : SubCategoryViewModel = hiltViewModel()
    val categories by categoryViewModel.categories.collectAsStateWithLifecycle()
    val subCategories by subCategoryViewModel.subCategories.collectAsStateWithLifecycle()
    val randomArticles by articleViewModel.adminArticles.collectAsStateWithLifecycle()
    var category by remember {
        mutableStateOf(Category())
    }
    val context = LocalContext.current
    var myCompany by remember {
        mutableStateOf(Company())
    }
    DisposableEffect(key1 = Unit) {
        onDispose {
            ratingViewModel.rating = false
        }
    }
    LaunchedEffect(key1 = Unit) {
        ratingViewModel.enabledToCommentCompany(companyId = company.id!!)
        articleViewModel.getAllArticlesApi(company.id!!)
        categoryViewModel.getAllCategoryByCompany(company.id)
//        if (sharedViewModel.accountType == AccountType.COMPANY) {
//            myCompany = sharedViewModel._company.value
//        }
    }
    LaunchedEffect(key1 = categories) {
        if(categories.isNotEmpty()) {
            category = categories[0]
        }
    }
    LaunchedEffect(key1 = category) {
        if(categories.isNotEmpty()) {
            subCategoryViewModel.getAllSubCtaegoriesByCategory(category.id!!,company.id!!)
        }
    }
    val rating = ratingViewModel.rating
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
                            val painter: Painter =
                                painterResource(id = R.drawable.emptyprofile)
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
                        companyDetails(
                            messageViewModel,
                            appViewModel,
                            clientViewModel,
                            companyViewModel,
                            ratingViewModel,
                            company,
                            myCompany.isPointsSeller!!
                        ) {
                        }
                    }
                    Column {
                        company.address?.let { it1 -> Text(text = it1) }
                        company.phone?.let { it1 -> Text(text = it1) }
                        company.email?.let { Text(text = it) }
                        company.code?.let { it1 -> Text(text = it1) }
                        company.matfisc?.let { it1 -> Text(text = it1) }
                    }
                }
                    if (!rating) {
                item {

                        Column {
                            ScreenByCompanyCategory(categories) { categ ->
                                category = categ
                                articleViewModel.getRandomArticlesByCategory(
                                    categ.id!!,
                                    categ.company?.id!!
                                )
                            }
                            ScreenByCompanySubCategory(items = subCategories) { categ ->
                                articleViewModel.getRandomArticlesBySubCategory(
                                    categ.id!!,
                                    categ.category?.company?.id!!
                                )
                            }
                        }
                }
                items(randomArticles) {
                    ArticleCardForSearch(article = it) {
                        companyViewModel.myCompany = it.company!!
                        articleViewModel.articleCompany = it
                        RouteController.navigateTo(Screen.ArticleDetailScreen)

                    }
                    }
                }
            }
            if(rating){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    RatingScreen(AccountType.COMPANY, company, null)
                }
            }
        }
    }
            SystemBackButtonHandler {
                RouteController.navigateTo(Screen.HomeScreen)
            }
}


@Composable
fun StarRating(
    rate : Int,
    modifier: Modifier = Modifier,
    starCount: Int = 5,
    onRatingChanged: (Int) -> Unit
) {
    var rating by remember { mutableIntStateOf(rate) }

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

@Composable
fun companyDetails(messageViewModel: MessageViewModel, appViewModel: AppViewModel,clientViewModel: ClientViewModel,companyViewModel: CompanyViewModel,
                   ratingViewModel : RatingViewModel,company: Company, isMePointSeller : Boolean, onRatingChanged: () -> Unit) {
    Row {
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
                    messageViewModel.receiverAccountType = AccountType.COMPANY
                    messageViewModel.receiverCompany = company
                    messageViewModel.getAllMessageByCaleeId(company.id!!)// from company screen
                    appViewModel.updateShow("message")
                    appViewModel.updateScreen(IconType.MESSAGE)
                }
            )
        }
        if (isMePointSeller) {
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
        Column (
            modifier = Modifier
                .weight(1.8f)
                .clickable {
                    ratingViewModel.rating = !ratingViewModel.rating
                }
        ) {
            if (ratingViewModel.rating && ratingViewModel.enableToRating) {
                StarRating(
                    company.rate?.toInt()!!,
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    onRatingChanged = { newRating ->
//                        ratingViewModel.rating = true
                        ratingViewModel.rate = newRating
                    }
                )
            }else{
                Row {
                    Text(text = company.rate?.toString()!!)
                    Icon(
                        imageVector = if(company.rate == 0.0)Icons.Outlined.StarOutline else if(company.rate == 5.0)Icons.Filled.Star else Icons.AutoMirrored.Filled.StarHalf ,
                        contentDescription = "rating"
                    )
                }
            }
            Text(text = company.raters?.toString()!! +" reviews")

        }
    }
}


@Composable
fun ScreenByCompanyCategory(items : List<Category>, onCategorySelected : (Category) -> Unit) {
    var selectedCateg by remember {
        mutableStateOf(Category())
    }
    LazyRow {
        items(items){ categ ->
            Spacer(modifier = Modifier.size(6.dp))
            Card(onClick = {
                selectedCateg = categ
                onCategorySelected(categ)
            },
                modifier = Modifier
                    .height(30.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedCateg == categ) Color.Yellow else Color.Transparent // Set color conditionally
                )
            )
            {
                Text(text = categ.libelle,
                    color = Color.Red)
            }
            Spacer(modifier = Modifier.size(6.dp))
        }
    }
}

@Composable
fun ScreenByCompanySubCategory(items : List<SubCategory>, onSubCategorySelected : (SubCategory) -> Unit) {
    var subcateg by remember {
        mutableStateOf(SubCategory())
    }
    LazyRow {
        items(items){ categ ->
            Card(onClick = {
                subcateg = categ
                onSubCategorySelected(categ)
                Log.e("screenbycateg",categ.libelle)
            },
                modifier = Modifier
                    .height(30.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (subcateg == categ) Color.Yellow else Color.Transparent // Set color conditionally
                )
            )
            {
                Text(text = categ.libelle)
            }
        }
    }
}