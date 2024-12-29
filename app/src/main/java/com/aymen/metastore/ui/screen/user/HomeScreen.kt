package com.aymen.metastore.ui.screen.user

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.aymen.metastore.LanguageSwither
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
import java.math.RoundingMode

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val sharedViewModel : SharedViewModel = hiltViewModel()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ){
        MyScaffold(context, sharedViewModel)
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScaffold(context : Context, sharedViewModel: SharedViewModel) {
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

    if(triggerLocationCheck || triggerLocationCheck2) {
        CheckLocation(type, user, company, context)
    }
    LaunchedEffect(key1 = Unit) {
        if(randomArticles.itemCount == 0) {
            articleViewModel.fetchRandomArticlesForHomePage(categoryName = CompanyCategory.DAIRY)
        }
    }

    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
        ,
        topBar = {
            MyTopBar(scrollBehavior, context, sharedViewModel)
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
                IconType.MESSAGE -> {
                    ConversationScreen()
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
fun MyTopBar(scrollBehavior: TopAppBarScrollBehavior, context : Context,sharedViewModel : SharedViewModel)   {
    val viewModel : AppViewModel = hiltViewModel()
    val selectedIcon by viewModel.currentScreen
    val user by sharedViewModel.user.collectAsStateWithLifecycle()
    val company by sharedViewModel.company.collectAsStateWithLifecycle()
    val accountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
    val userRole = viewModel.userRole
    val historySelected by viewModel.historySelected
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
                            if (company.logo != null && company.logo != "" ) {
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
                            if (user.image != null && user.image != "") {
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
                            if (accountType == AccountType.USER && company.id == 0L) {
                                DropdownMenuItem(
                                    text = { Text(text = "add company") },
                                    onClick = { RouteController.navigateTo(Screen.AddCompanyScreen) })
                            } else {
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
                        badgeCount = 0, // Example of a message badge
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
                            badgeCount = 1,
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
                        badgeCount = 0, // Example of a message badge
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
                        badgeCount = 0, // Example of a message badge
                        onClick = {
                            viewModel.updateScreen(IconType.WALLET)
                            if(accountType == AccountType.COMPANY && user.role == RoleEnum.WORKER){
                                viewModel.updateShow("buyhistory")
                                viewModel.updateView("allHistory")
                            }else viewModel.updateShow("payment")
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
                            badgeCount = 0,
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
                "add article" -> {
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
                    viewModel.updateShow("subCategory")
                }
                "add invoice" -> {
                    viewModel.updateShow("invoice")
                }"add worker" -> {
                viewModel.updateShow("worker")
            }
                "add parent" -> {
                    viewModel.updateShow("parent")
                }
                "as client" -> {
                    viewModel.updateShow("invoice")
                }
                "message" -> {
                    viewModel.updateShow("conversation")
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
    LazyRow {
        items(CompanyCategory.entries){ categ ->
            Card(onClick = {
                Log.e("categoryarticle","category : ${categ.ordinal}")
                articleViewModel.fetchRandomArticlesForHomePage(categ)
                           },
                modifier = Modifier.height(50.dp))
            {
            Text(text = categ.name)
            }
            Spacer(modifier = Modifier.size(6.dp))
        }
    }
}

