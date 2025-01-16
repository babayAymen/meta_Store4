package com.aymen.metastore.ui.screen.user

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.R
import com.aymen.metastore.model.entity.model.Category
import com.aymen.metastore.model.entity.model.ClientProviderRelation
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.repository.ViewModel.RatingViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.ArticleViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.metastore.model.repository.ViewModel.ClientViewModel
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.metastore.model.repository.ViewModel.PointsPaymentViewModel
import com.aymen.metastore.model.repository.ViewModel.SubCategoryViewModel
import com.aymen.metastore.ui.component.AddTypeDialog
import com.aymen.metastore.ui.component.ArticleCardForSearch
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.SendPointDialog
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.metastore.ui.component.notImage
import com.aymen.metastore.ui.screen.admin.ReglementScreen
import com.aymen.metastore.util.BASE_URL
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.model.Enum.Type
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.aymen.store.ui.navigation.SystemBackButtonHandler
import kotlinx.coroutines.flow.map

@Composable
fun CompanyScreen(company: Company) {
    val articleViewModel: ArticleViewModel = hiltViewModel()
    val appViewModel: AppViewModel = hiltViewModel()
    val companyViewModel: CompanyViewModel = hiltViewModel()
    val ratingViewModel: RatingViewModel = hiltViewModel()
    val clientViewModel: ClientViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val categoryViewModel : CategoryViewModel = hiltViewModel()
    val subCategoryViewModel : SubCategoryViewModel = hiltViewModel()
    val paymentViewModel : PointsPaymentViewModel = hiltViewModel()
    val subCategories = subCategoryViewModel.companySubCategories.collectAsLazyPagingItems()
    val categories = categoryViewModel.companyCategories.collectAsLazyPagingItems()
    val randomArticles = articleViewModel.companyArticles.collectAsLazyPagingItems()
    val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()
    val myUser by sharedViewModel.user.collectAsStateWithLifecycle()
    val myAccountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
    var hisClient by remember {
        mutableStateOf(false)
    }
    var hisProvider by remember {
        mutableStateOf(false)
    }
    var hisParent by remember {
        mutableStateOf(false)
    }
    var hisWorker by remember {
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

    val view by appViewModel.view
    val show by appViewModel.show
    var category by remember {
        mutableStateOf(Category())
    }




    if(subCategories.itemCount != 0) {
        category = subCategories.peek(0)?.category ?: Category()
    }
    val context = LocalContext.current
    DisposableEffect(key1 = Unit) {
        onDispose {
            ratingViewModel.rating = false
            subCategoryViewModel.setSubCategory()
            clientViewModel.setRelationList()
        }
    }
    LaunchedEffect(key1 = company) {
            if(myAccountType == AccountType.USER && myUser.role == RoleEnum.WORKER  && company.id == myCompany.id)
                hisWorker = true
        appViewModel.updateView("COMPANY_CONTENT")
        articleViewModel.getAllCompanyArticles(companyId = company.id?:0)
        ratingViewModel.enabledToCommentCompany(companyId = company.id?:0)
        clientViewModel.checkRelation(id = company.id?:0, AccountType.COMPANY)
    }
    LaunchedEffect(key1 = categories) {
        if(categories.itemCount != 0){
            articleViewModel.getRandomArticlesByCategory(categories[0]?.id?:0, company.id?:0, 0)
        }
    }
    LaunchedEffect(key1 = subCategories) {
        if(subCategories.itemCount != 0){
            articleViewModel.getRandomArticlesByCategory(0, company.id?:0, subCategories[0]?.id?:0)
        }
    }


    val rating = ratingViewModel.rating
    val listState = rememberLazyListState()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp, 36.dp, 3.dp, 3.dp)
    ) {
        when (view) {
          "COMPANY_CONTENT" ->
            Column {
                LazyColumn(
                    state = listState
                ) {
                    item {
                        Row {
                            if (company.logo != null)
                                ShowImage(image = "${BASE_URL}werehouse/image/${company.logo}/company/${company.user?.id}")
                            else
                                notImage()
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "verification account",
                                tint = if (company.metaSeller == true) Color.Green else Color.Cyan
                            )
                            Text(text = company.name)

                        }
                        Row(
                            modifier = Modifier.padding(2.dp)
                        ) {
                            CompanyDetails(
                                sharedViewModel,
                                clientViewModel,
                                companyViewModel,
                                hisClient,
                                hisProvider,
                                hisParent ,
                                hisWorker,
                                ratingViewModel,
                                company,
                                myCompany.isPointsSeller!!,
                                appViewModel
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
                                        categ.company?.id!!,
                                        0
                                    )
                                    subCategoryViewModel.getAllSubCategoriesByCategoryId(
                                        categoryId = categ.id ?: 0,
                                        companyId = categ.company.id ?: 0
                                    )
                                }
                                ScreenByCompanySubCategory(
                                    items = subCategories,
                                    category = category
                                ) { categ ->
                                    articleViewModel.getRandomArticlesByCategory(
                                        0,
                                        categ.company?.id!!,
                                        categ.id!!
                                    )
                                }
                            }
                        }
                        items(
                            count = randomArticles.itemCount,
                            key = randomArticles.itemKey { it.id!! }
                        ) { index ->
                            val article = randomArticles[index]
                            if (article != null) {
                                ArticleCardForSearch(article) {
                                    companyViewModel.myCompany = article.company!!
                                    articleViewModel.assignArticleCompany(article)
                                    RouteController.navigateTo(Screen.ArticleDetailScreen)
                                }
                            }
                        }
                    }
                }
                if (rating) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        RatingScreen(AccountType.COMPANY, company, null)
                    }
                }
            }
            "REGLEMENT_FOR_PROVIDER" ->{
                ReglementFeature(paymentViewModel = paymentViewModel , appViewModel = appViewModel, company = company)
            }
        }
    }
            SystemBackButtonHandler {
                Log.e("backbutton","view: $view")
                when(view){
                    "COMPANY_CONTENT" ->  RouteController.navigateTo(Screen.HomeScreen)
                    "REGLEMENT_FOR_PROVIDER" -> {
                        when(show){
                            "REGLEMENT_FOR_PROVIDER" -> appViewModel.updateView("COMPANY_CONTENT")
                            "REGLEMENT_SCREEN" -> appViewModel.updateShow("REGLEMENT_FOR_PROVIDER")
                        }
                    }

                }
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
fun CompanyDetails( sharedViewModel: SharedViewModel, clientViewModel: ClientViewModel, companyViewModel: CompanyViewModel,hisClient : Boolean, hisProvider : Boolean,
                 hisParent : Boolean , hisWorker : Boolean  ,ratingViewModel : RatingViewModel, company: Company, isPointsSeller: Boolean, appViewModel: AppViewModel, onRatingChanged: () -> Unit) {
    val accountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
    val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()
    val myUser by sharedViewModel.user.collectAsStateWithLifecycle()
    Row {
            if((myCompany.id != company.id ) || (accountType == AccountType.USER && myUser.role == RoleEnum.WORKER))
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 2.dp)
                ) {
                    AddTypeDialog(isOpen = false, company.id?:0, true, hisClient,hisProvider, hisParent, hisWorker) {type , isDeleted ->
                        clientViewModel.sendClientRequest(company.id!!, type, isDeleted)
                    }
                }
        if (accountType == AccountType.COMPANY && isPointsSeller) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                SendPointDialog(isOpen = false, User(), company)
            }
        }
        if (accountType == AccountType.META) {
            Row(
                modifier = Modifier.weight(2f)
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    if (!company.metaSeller!!) {
                        ButtonSubmit(
                            labelValue = "make as meta seller",
                            color = Color.Green,
                            enabled = true
                        ) {
                            companyViewModel.MakeAsMetaSeller(true, company.id!!)
                        }
                    } else {
                        ButtonSubmit(
                            labelValue = "remove as meta seller",
                            color = Color.Red,
                            enabled = true
                        ) {
                            companyViewModel.MakeAsMetaSeller(false, company.id!!)
                        }
                    }
                }
                Row(modifier = Modifier.weight(1f)) {

                    if (!company.isPointsSeller!!) {
                        ButtonSubmit(
                            labelValue = "make as point seller",
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
                Row(modifier = Modifier.weight(1f)) {
                    ButtonSubmit(
                        labelValue = "reglement",
                        color = Color.Green,
                        enabled = true
                    ) {
                        appViewModel.updateView("REGLEMENT_FOR_PROVIDER")
                        appViewModel.updateShow("REGLEMENT_FOR_PROVIDER")
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
fun ScreenByCompanyCategory(
    items: LazyPagingItems<Category>,
    onCategorySelected: (Category) -> Unit
) {
    var selectedCateg by remember { mutableStateOf(Category()) }

    LazyRow {
        items(
            count = items.itemCount,
            key = items.itemKey { it.id?:0 }
        ) { index ->
            val categ = items[index] // Safely access item

            if (categ != null) { // Handle non-null items
                Spacer(modifier = Modifier.size(6.dp))
                Card(
                    onClick = {
                        selectedCateg = categ
                        onCategorySelected(selectedCateg)
                    },
                    modifier = Modifier.height(30.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedCateg == categ) Color.Yellow else Color.Transparent
                    )
                ) {
                    Text(text = categ.libelle ?: "", color = Color.Red)
                }
                Spacer(modifier = Modifier.size(6.dp))
            } else {
                // Placeholder or Loading state if needed
            }
        }
    }
}

@Composable
fun ScreenByCompanySubCategory(items : LazyPagingItems<SubCategory>, category : Category , onSubCategorySelected : (SubCategory) -> Unit) {
    var subcateg by remember {
        mutableStateOf(SubCategory())
    }
    LazyRow {
        items(items.itemCount) { index ->
            val categ = items[index]
            if (categ != null && categ.category == category){
                Card(
                    onClick = {
                        subcateg = categ
                        onSubCategorySelected(categ)
                    },
                    modifier = Modifier
                        .height(30.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (subcateg == categ) Color.Yellow else Color.Transparent // Set color conditionally
                    )
                )
                {
                    Text(text = categ.libelle!!)
                }
        }
        }
    }
}


@Composable
fun ReglementFeature(paymentViewModel: PointsPaymentViewModel, appViewModel: AppViewModel, company : Company) {
    val companyPayment = paymentViewModel.allMyProfitsPerDay.collectAsLazyPagingItems()
    val show by appViewModel.show
    var idx by remember {
        mutableStateOf(-1)
    }
    LaunchedEffect(key1 = companyPayment) {
        if(companyPayment.itemCount == 0){
            paymentViewModel.getAllMyProfitsPerDay(company.id!!)
        }
    }

    when(show){
        "REGLEMENT_FOR_PROVIDER" -> {
            LazyColumn {
                items(count = companyPayment.itemCount,
                    key = companyPayment.itemKey { it.id!! }
                ) { index ->
                    val payment = companyPayment[index]
                    if (payment != null) {
                        Column(
                            modifier = Modifier.clickable {
                                idx = index
                                appViewModel.updateShow("REGLEMENT_SCREEN")
                            }
                        ) {
                            Text(text = "name : ${payment.receiver?.name}")
                            Text(text = "credit : ${payment.amount}")
                            Text(text = "rest : ${payment.rest}")
                        }
                    }
                }
            }
        }
         "REGLEMENT_SCREEN" -> {
            ReglementScreen(companyPayment[idx], paymentViewModel, appViewModel)
        }
         }
}

