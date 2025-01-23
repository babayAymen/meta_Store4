package com.aymen.metastore.ui.screen.user

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.aymen.metastore.LanguageSwither
import com.aymen.metastore.R
import com.aymen.metastore.model.Enum.NotificationType
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.IconType
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.ArticleViewModel
import com.aymen.metastore.model.repository.ViewModel.SignInViewModel
import com.aymen.metastore.ui.component.ArticleCardForUser
import com.aymen.metastore.ui.component.EmptyImage
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.metastore.ui.component.CheckLocation
import com.aymen.metastore.ui.component.UpdateImageDialog
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.aymen.store.ui.navigation.SystemBackButtonHandler
import com.aymen.metastore.ui.screen.admin.DashBoardScreen
import com.aymen.metastore.util.BASE_URL
import com.aymen.store.ui.screen.user.NotificationScreen
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.math.RoundingMode

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(extra : Map<String , Any?>) {
    val context = LocalContext.current
    val sharedViewModel : SharedViewModel = hiltViewModel()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ){
        MyScaffold(context, sharedViewModel, extra)
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScaffold(context : Context, sharedViewModel: SharedViewModel, extra : Map<String , Any?>) {
    val signInViewModel : SignInViewModel = hiltViewModel()
    val appViewModel : AppViewModel = hiltViewModel()
    val articleViewModel : ArticleViewModel = hiltViewModel()
    val user by sharedViewModel.user.collectAsStateWithLifecycle()
    val company by sharedViewModel.company.collectAsStateWithLifecycle()
    val type by sharedViewModel.accountType.collectAsStateWithLifecycle()
    val currentScreen by appViewModel.currentScreen
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val randomArticles = articleViewModel.randomArticles.collectAsLazyPagingItems()

    val triggerLocationCheck by signInViewModel.showCheckLocationDialog.collectAsStateWithLifecycle()
    val triggerLocationCheck2 by appViewModel.showCheckLocationDialog.collectAsStateWithLifecycle()
    val invoiceNotificationCount by sharedViewModel.invoiceNotificationCount.collectAsStateWithLifecycle()
    val orderNotificationCount by sharedViewModel.orderNotificationCount.collectAsStateWithLifecycle()
    val invitationNotificationCount by sharedViewModel.invitationNotificationCount.collectAsStateWithLifecycle()
    val paymentNotificationCount by sharedViewModel.paymentNotificationCount.collectAsStateWithLifecycle()
    val reglementNotificationCount by sharedViewModel.reglementNotificationCount.collectAsStateWithLifecycle()
    val invoiceAsClientNotificationCount by sharedViewModel.invoiceAsClientNotificationCount.collectAsStateWithLifecycle()
    if(triggerLocationCheck || triggerLocationCheck2) {
        if(type != AccountType.NULL) {
            CheckLocation(type, user, company, context)
        }
    }
//    val clipboardManager = LocalClipboardManager.current on cas ou i need clipboard
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit) {
        scope.launch{
            val token = Firebase.messaging.token.await()
            appViewModel.sendDeviceToken(token)
        }
        if(randomArticles.itemCount == 0) {
            articleViewModel.fetchRandomArticlesForHomePage(categoryName = CompanyCategory.ALL)
        }
    }

   LaunchedEffect(key1 = extra) {
       val notificationType = extra["notificationType"]
       val clientType = extra["clientType"]
       val isSend = extra["isSend"]
       if (notificationType == NotificationType.PAYMENT.name) {
           appViewModel.updateView("payment")
           appViewModel.updateScreen(IconType.WALLET)
       }
       if (notificationType == NotificationType.INVITATION.name)
           appViewModel.updateScreen(IconType.USER)
       if (notificationType == NotificationType.ORDER.name)
           appViewModel.updateScreen(IconType.SHOPPING)
       if (notificationType == NotificationType.INVOICE.name) {
           if(clientType == AccountType.COMPANY.name){
               if(isSend == "false")
                    appViewModel.asClient = true
               appViewModel.updateShow("invoice")
               appViewModel.updateScreen(IconType.COMPANY)
           }else
               appViewModel.updateScreen(IconType.SHOPPING)
       }
   }
    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
        ,
        topBar = {
            MyTopBar(scrollBehavior, context, sharedViewModel,invoiceNotificationCount,orderNotificationCount,
                invitationNotificationCount, paymentNotificationCount, reglementNotificationCount,invoiceAsClientNotificationCount)
        }
    ){value ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(value)
        ){
            when (currentScreen) {
                IconType.HOME ->
                    Column {
                        ScreenByCategory(articleViewModel = articleViewModel)
                        ArticleCardForUser(randomArticles)
                    }
                IconType.NOTIFICATIONS -> {
                    NotificationScreen()
                }
                IconType.COMPANY ->
                        DashBoardScreen()
                IconType.SHOPPING ->
                        ShoppingScreen()
                IconType.WALLET -> {
                    PaymentScreen()
                }
                IconType.MENU ->{}
                        //MenuScreen()
                IconType.USER ->
                        InvetationScreen()
                IconType.SEARCH ->
                    SearchScreen()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(scrollBehavior: TopAppBarScrollBehavior, context : Context,sharedViewModel : SharedViewModel, invoiceCount : Int,
             orderCount : Int, invitationCount : Int, paymentCount : Int, reglementCount : Int, invoiceAsClientCount : Int)   {
    val viewModel : AppViewModel = hiltViewModel()
    val selectedIcon by viewModel.currentScreen
    val user by sharedViewModel.user.collectAsStateWithLifecycle()
    val company by sharedViewModel.company.collectAsStateWithLifecycle()
    val accountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
    val userRole = viewModel.userRole
    val historySelected by viewModel.historySelected
    val historicView by viewModel.historicView
    var isPopupVisible by remember {
        mutableStateOf(false)
    }
    var isAdmin by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = userRole) {
        isAdmin = userRole == RoleEnum.ADMIN
    }
    var isCompany by remember { mutableStateOf(sharedViewModel.accountType.value == AccountType.COMPANY) }
    LaunchedEffect(sharedViewModel.accountType, company, user) {
        isCompany = sharedViewModel.accountType.value == AccountType.COMPANY
}
    val balance = if (isCompany) {
        company.balance!!.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
    } else {
        user.balance!!.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
    }
    var opDialog by remember {
    mutableStateOf(false)
    }
    TopAppBar(
        title = {
            Text(text = "meta")
        },
        Modifier
            .clip(shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
            .fillMaxWidth()
        ,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Green,
        ),
        navigationIcon = {
            Column {

                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .height(42.dp),
                    verticalAlignment = Alignment.CenterVertically,

                ) {
                    Row(
                        modifier = Modifier
                            .weight(0.8f)
                            .clickable {
                                opDialog = !opDialog
                            }
                    ) {
                        if (opDialog) {
                            UpdateImageDialog(isOpen = true) {
                                opDialog = false

                            }
                        }
                        if (isCompany) {
                            Row {
                            if (company.logo != null ) {
                                ShowImage(
                                    image = "${BASE_URL}werehouse/image/${company.logo}/company/${company.user?.id}",
                                    35.dp
                                )
                            } else {
                                EmptyImage()
                            }
                            Text(text = company.name)
                            }
                        } else {
                            Row {
                            if (user.image != null) {
                                ShowImage(
                                    image = "${BASE_URL}werehouse/image/${user.image}/user/${user.id}",
                                    35.dp
                                )
                            } else {
                                EmptyImage()
                            }
                                Text(text = user.username?:"")
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.weight(0.3f)
                    ) {
                        Text(text = "${balance}TDN")
                    }
                    Row(
                        modifier = Modifier.weight(0.1f)
                    ) {
                        IconButton(onClick = {
                            isPopupVisible = !isPopupVisible
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "menu",
                                tint = Color.Black
                            )
                        }
                        DropdownMenu(
                            expanded = isPopupVisible,
                            onDismissRequest = { isPopupVisible = false }) {
                            if(accountType == AccountType.DELIVERY){
                                DropdownMenuItem(text = { Text(text = user.username!!) },
                                    onClick = {
                                        isPopupVisible = false
                                        sharedViewModel.changeAccountType(AccountType.USER)
                                    })
                            }
                            else if (accountType == AccountType.USER && user.accountType == AccountType.DELIVERY){
                                DropdownMenuItem(text = { Text(text = user.username!!) },
                                    onClick = {
                                        isPopupVisible = false
                                        sharedViewModel.changeAccountType(AccountType.DELIVERY)
                                    })
                            }
                            else if (accountType == AccountType.USER && user.role == RoleEnum.USER) {
                                DropdownMenuItem(
                                    text = { Text(text = "add company") },
                                    onClick = { RouteController.navigateTo(Screen.AddCompanyScreen) })
                            }

                            else {
                                DropdownMenuItem(text = { Text(text = if (!isCompany) company.name else user.username!!) },
                                    onClick = {
                                        isPopupVisible = false
                                        if (!isCompany) {
                                            sharedViewModel.changeAccountType(AccountType.COMPANY)
                                        } else
                                            sharedViewModel.changeAccountType(AccountType.USER)

                                        viewModel.updateScreen(IconType.HOME)
                                    })
                            }
                            LanguageSwither()
                            DropdownMenuItem(text = { Text(text = "logout") }, onClick = {
                                sharedViewModel.logout()
                            })
                        }


                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .height(30.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                )
                {
                    IconWithBadge(
                        iconType = IconType.HOME,
                        selectedIcon = selectedIcon,
                        iconSelected = Icons.Default.Home,
                        iconUnselected = Icons.Outlined.Home,
                        badgeCount = 0,
                        onClick = {
                            viewModel.updateScreen(IconType.HOME)
                        },
                        description = "home"
                    )
                    if (accountType == AccountType.COMPANY) {

                        IconWithBadge(
                            iconType = IconType.COMPANY,
                            selectedIcon = selectedIcon,
                            iconSelected = Icons.Default.HomeWork,
                            iconUnselected = Icons.Outlined.HomeWork,
                            badgeCount = invoiceCount+invoiceAsClientCount,
                            onClick = {
                                viewModel.updateShow("dash")
                                viewModel.updateScreen(IconType.COMPANY)
                            },
                            description = "company"
                        )
                    }
                    IconWithBadge(
                        iconType = IconType.SHOPPING,
                        selectedIcon = selectedIcon,
                        iconSelected = Icons.Default.ShoppingCart,
                        iconUnselected = Icons.Outlined.ShoppingCart,
                        badgeCount = orderCount,
                        onClick = {
                            viewModel.updateScreen(IconType.SHOPPING)
                        },
                        description = "shopping"
                    )
                    IconWithBadge(
                        iconType = IconType.WALLET,
                        selectedIcon = selectedIcon,
                        iconSelected = Icons.Default.AccountBalanceWallet,
                        iconUnselected = Icons.Outlined.AccountBalanceWallet,
                        badgeCount = paymentCount+reglementCount,
                        onClick = {
                            viewModel.updateScreen(IconType.WALLET)
                            if(accountType == AccountType.COMPANY && user.role == RoleEnum.WORKER){
                                viewModel.updateView("buyhistory")
                                viewModel.updateShow("allHistory")
                            }else viewModel.updateView("payment")
                        },
                        description = "wallet"
                    )
                    IconWithBadge(
                        iconType = IconType.SEARCH,
                        selectedIcon = selectedIcon,
                        iconSelected = Icons.Default.SavedSearch,
                        iconUnselected = Icons.Outlined.Search,
                        badgeCount = 0, // Example of a message badge
                        onClick = {
                            viewModel.updateScreen(IconType.SEARCH)
                        },
                        description = "search"
                    )
                    if(user.role != RoleEnum.WORKER || !isCompany ) {
                        IconWithBadge(
                            iconType = IconType.USER,
                            selectedIcon = selectedIcon,
                            iconSelected = Icons.Default.GroupAdd,
                            iconUnselected = Icons.Outlined.GroupAdd,
                            badgeCount = invitationCount,
                            onClick = {
                                viewModel.updateScreen(IconType.USER)
                            },
                            description = "user"
                        )
                    }
                }
            }
        },
        scrollBehavior =  scrollBehavior
    )
    val art = stringResource(id = R.string.add_article_for_company)
    SystemBackButtonHandler {
        if (historySelected == selectedIcon && viewModel.currentScreen.value == IconType.HOME) {

            (context as? Activity)?.moveTaskToBack(true)

        }

        if( viewModel.show.value == "dash" || viewModel.show.value == "payment" || viewModel.show.value == "order"){
            if(historySelected == IconType.COMPANY){
                viewModel.updateShow("dash")
            }
            viewModel.updateScreen(historySelected)
            viewModel._historySelected.value = IconType.HOME
        }
        else{
            when(viewModel.show.value){
                art -> {
                    viewModel.updateShow("article")
                }
                "add category" -> {
                    viewModel.updateShow("category")
                }
                "add client" -> {
                    viewModel.updateShow("client")
                }
                "add provider" -> {
                    viewModel.updateShow("provider")
                }
                "add payment" -> {
                    viewModel.updateShow("payment")
                }
                "add subCategory" -> {
                    viewModel.updateShow("subcategory")
                }
                "add invoice" -> {
                    if(viewModel.view.value == "buyhistory")
                    viewModel.updateShow("allHistory")
                    else
                    viewModel.updateShow("invoice")
                }
                "ADD_WORKER" -> {
                viewModel.updateShow("worker")
            }
                "add parent" -> { // remove it
                    viewModel.updateShow("parent")
                }
                "as client" -> {
                    viewModel.updateShow("invoice")
                }
                "orderLine" -> {
                viewModel.updateShow("order")
                }
                "pointespece" -> {
                    viewModel.updateShow("payment")
                }
                "profit" -> {
                    viewModel.updateShow("payment")
                }
                "ADD_ARTICLE" ->{
                    viewModel.updateShow("article")
                }
                "REGLEMENT_SCREEN" -> viewModel.updateShow("REGLEMENT_FOR_PROVIDER")
                "invoice" -> {
                    when(viewModel.view.value){
                        "ALL" -> viewModel.updateShow("dash")
                        "PAID" -> viewModel.updateView(historicView,"ALL")
                        "NOT_PAID" -> viewModel.updateView(historicView,"ALL")
                        "IN_COMPLETE" -> viewModel.updateView(historicView,"ALL")
                        "NOT_ACCEPTED" -> viewModel.updateView(historicView,"ALL")
                    }
                }
                else -> {
                    viewModel.updateShow("dash")
                }

            }
        }

    }

}


@Composable
fun IconWithBadge(
    iconType: IconType,
    selectedIcon: IconType,
    iconSelected: ImageVector,
    iconUnselected: ImageVector,
    badgeCount: Int,
    onClick: () -> Unit,
    description: String
) {
    val size = 38.dp
    IconButton(
        modifier = Modifier
            .size(size)
            .fillMaxSize(),
        onClick = onClick
    ) {
        BadgedBox(badge = {
            if (badgeCount > 0) {
                Badge(
                    modifier = Modifier
                        .offset(x = (-6).dp, y = (-1).dp)
                ) {
                    Text(text = badgeCount.toString())
                }
            }
        }) {
            Icon(
                imageVector = if (selectedIcon == iconType) iconSelected else iconUnselected,
                contentDescription = description,
                tint = Color.Black
            )
        }
    }
}


@Composable
fun ScreenByCategory(articleViewModel: ArticleViewModel) {

    LazyRow (
        modifier = Modifier.padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ){
        items(CompanyCategory.entries) { categ ->
            var categortyName = ""
            val imageResId = when (categ) {
                CompanyCategory.DAIRY -> {
                    categortyName = stringResource(id = R.string.dairy)
                    R.drawable.attar
                }
                CompanyCategory.FISH -> {
                    categortyName = stringResource(id = R.string.fish)
                    R.drawable.hout
                }
                CompanyCategory.GROCER -> {
                    categortyName = stringResource(id = R.string.grocer)
                    R.drawable.boukoul
                }
                CompanyCategory.VEGETABLE -> {
                    categortyName = stringResource(id = R.string.vegetable)
                    R.drawable.khodhra
                }
                CompanyCategory.BUTCHER -> {
                    categortyName = stringResource(id = R.string.butcher)
                    R.drawable.jazzar
                }
                CompanyCategory.ALL -> {
                    categortyName = stringResource(id = R.string.all)
                    R.drawable.hstore
                }
                CompanyCategory.CAKE -> {
                    categortyName = stringResource(id = R.string.cake)
                    R.drawable.cakeshop
                }
                CompanyCategory.RESTAURANT -> {
                    categortyName = stringResource(id = R.string.restautrant)
                    R.drawable.restaurant
                }
                CompanyCategory.POULTERER -> {
                    categortyName = stringResource(id = R.string.restautrant)
                    R.drawable.restaurant
                }
            }

            val imagePainter: Painter = painterResource(id = imageResId)
            Card(
                onClick = {
                    articleViewModel.fetchRandomArticlesForHomePage(categ)
                },
                modifier = Modifier
                    .size(90.dp, 70.dp),
            )
            {
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){

                    Image(
                        painter = imagePainter,
                        contentDescription = "category image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(
                                RoundedCornerShape(5.dp)
                            ),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(1.dp))
                Text(text = categortyName)

                }
            }
            Spacer(modifier = Modifier.size(1.dp))
        }
    }
}

