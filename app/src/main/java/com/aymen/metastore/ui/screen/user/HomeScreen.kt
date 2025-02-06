package com.aymen.metastore.ui.screen.user

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowDropDown
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
import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.Enum.NotificationType
import com.aymen.metastore.model.Enum.PaymentType
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.IconType
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.ArticleViewModel
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
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
import com.aymen.metastore.util.ADD_ARTICLE
import com.aymen.metastore.util.ADD_ARTICLE_FOR_COMPANY
import com.aymen.metastore.util.ADD_CATEGORY
import com.aymen.metastore.util.ADD_CLIENT
import com.aymen.metastore.util.ADD_INVOICE
import com.aymen.metastore.util.ADD_PARENT
import com.aymen.metastore.util.ADD_PAYMENT
import com.aymen.metastore.util.ADD_PROVIDER
import com.aymen.metastore.util.ADD_SUBCATEGORY
import com.aymen.metastore.util.ADD_WORKER
import com.aymen.metastore.util.ALL
import com.aymen.metastore.util.ALL_HISTORY
import com.aymen.metastore.util.ARTICLE
import com.aymen.metastore.util.AS_CLIENT
import com.aymen.metastore.util.BASE_URL
import com.aymen.metastore.util.BUY_HISTORY
import com.aymen.metastore.util.CLIENT
import com.aymen.metastore.util.CLIENT_TYPE
import com.aymen.metastore.util.DASH
import com.aymen.metastore.util.DELIVERED
import com.aymen.metastore.util.FALSE
import com.aymen.metastore.util.IMAGE_URL_COMPANY
import com.aymen.metastore.util.IMAGE_URL_USER
import com.aymen.metastore.util.INVITATION
import com.aymen.metastore.util.INVOICE
import com.aymen.metastore.util.INVOICE_ID
import com.aymen.metastore.util.IN_COMPLETE
import com.aymen.metastore.util.IS_SEND
import com.aymen.metastore.util.META
import com.aymen.metastore.util.MY_NOT_DELIVERED
import com.aymen.metastore.util.NOTIFICATION_TYPE
import com.aymen.metastore.util.NOT_ACCEPTED
import com.aymen.metastore.util.NOT_DELIVERED
import com.aymen.metastore.util.NOT_PAID
import com.aymen.metastore.util.ORDER
import com.aymen.metastore.util.ORDER_LINE
import com.aymen.metastore.util.PAID
import com.aymen.metastore.util.PARENT
import com.aymen.metastore.util.PAYMENT
import com.aymen.metastore.util.PAYMENT_TYPE
import com.aymen.metastore.util.POINT_ESPECE
import com.aymen.metastore.util.PROFIT
import com.aymen.metastore.util.PROVIDER
import com.aymen.metastore.util.REGLEMENT_FOR_PROVIDER
import com.aymen.metastore.util.REGLEMENT_SCREEN
import com.aymen.metastore.util.SEARCH
import com.aymen.metastore.util.SHOPPING
import com.aymen.metastore.util.STATUS
import com.aymen.metastore.util.SUBCATEGORY
import com.aymen.metastore.util.WORKER
import com.aymen.metastore.util.all_histories_payment_for_provider
import com.aymen.metastore.util.all_histories_payment_for_provider_by_date
import com.aymen.metastore.util.all_profit_payment_for_provider_per_day
import com.aymen.metastore.util.category
import com.aymen.metastore.util.incomplete
import com.aymen.metastore.util.notaccepted
import com.aymen.metastore.util.notpayed
import com.aymen.metastore.util.payed
import com.aymen.metastore.util.profit_by_date
import com.aymen.metastore.util.sum_of_profit_by_date
import com.aymen.store.model.Enum.Status
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
    val invoiceViewModel : InvoiceViewModel = hiltViewModel()
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
            CheckLocation(type, user, company, context, appViewModel)
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
            articleViewModel.fetchRandomArticlesForHomePage(categoryName = articleViewModel.selectedCategory.value)
        }
    }

   LaunchedEffect(key1 = extra) {
       val notificationType = extra[NOTIFICATION_TYPE]
       val clientType = extra[CLIENT_TYPE]
       val isSend = extra[IS_SEND]
       val status = extra[STATUS]
       val paymentType = extra[PAYMENT_TYPE]
       val invoiceId = extra[INVOICE_ID]
       Log.e("testnotification","extra in home page $extra")
       if (notificationType == NotificationType.PAYMENT.name) {
           if(paymentType == PaymentType.RECHARGE.name) {
               appViewModel.updateScreen(IconType.WALLET)
               appViewModel.updateShow(PAYMENT)
           }
           if(paymentType == PaymentType.PROFITS.name){
               appViewModel.updateScreen(IconType.WALLET)
               appViewModel.updateShow(PROFIT)
           }
           if(paymentType == PaymentType.META_REGLEMENT.name){
               appViewModel.updateScreen(IconType.WALLET)
               appViewModel.updateShow(PROFIT)
               appViewModel.updateView(all_profit_payment_for_provider_per_day)
           }
           if(paymentType == PaymentType.INVOICE.name){
                   invoiceViewModel.setInvoice(Invoice(id = invoiceId.toString().toLong()))
               invoiceViewModel.getInvoiceDetails()
               if(type == AccountType.COMPANY){
                   appViewModel.asClient = true
                   appViewModel.updateScreen(IconType.COMPANY)
                   invoiceViewModel.clientType = type
//                   invoiceViewModel.setInvoice(invoice)
//                   invoiceViewModel.discount = invoice.discount ?: 0.0
                   invoiceViewModel.invoiceType = InvoiceDetailsType.COMMAND_LINE
                   invoiceViewModel.setInvoiceMode(InvoiceMode.VERIFY)
                   appViewModel.updateShow(ADD_INVOICE)
               }
               if(clientType == AccountType.USER){
                   appViewModel.updateScreen(IconType.SHOPPING)
                   appViewModel.updateShow(ADD_INVOICE)
                   invoiceViewModel.clientType = type
                   invoiceViewModel.invoiceType = InvoiceDetailsType.COMMAND_LINE
                   invoiceViewModel.setInvoiceMode(InvoiceMode.VERIFY)
               }
           }
       }
       if (notificationType == NotificationType.INVITATION.name)
           appViewModel.updateScreen(IconType.USER)
       if (notificationType == NotificationType.ORDER.name) {
                appViewModel.updateScreen(IconType.SHOPPING)
           if(type == AccountType.DELIVERY){
               appViewModel.updateShow(ORDER_LINE)
               appViewModel.updateView(NOT_DELIVERED)
           }else
                appViewModel.updateShow(SHOPPING)

       }
       if (notificationType == NotificationType.INVOICE.name) {
           val isCompany = clientType == AccountType.COMPANY.name
           if (status == Status.ACCEPTED.name || status == Status.REFUSED.name) {
               if (isSend == true) {
                   appViewModel.updateScreen(IconType.COMPANY)
                   appViewModel.updateShow(INVOICE)
                   appViewModel.asClient = false
               } else {
                   appViewModel.asClient = isCompany
                   appViewModel.updateScreen(if (isCompany) IconType.COMPANY else IconType.SHOPPING)
                   appViewModel.updateShow(if (isCompany) INVOICE else SHOPPING)
               }
           } else {
               appViewModel.asClient = isCompany
               appViewModel.updateScreen(if (isCompany) IconType.COMPANY else IconType.SHOPPING)
               appViewModel.updateShow(if (isCompany) INVOICE else SHOPPING)
           }
       }
   }
    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
        ,
        topBar = { // if i want to use bottom bar just change this to bottom bar
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
            Text(text = META)
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
                            .weight(0.7f)
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
                                    image = String.format(IMAGE_URL_COMPANY,company.logo, company.user?.id),
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
                                    image = String.format(IMAGE_URL_USER,user.image, user.id),
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
                        modifier = Modifier.weight(0.4f)
                    ) {
                        Text(text = stringResource(id = R.string.balance,balance))
                    }
                    Row(
                        modifier = Modifier.weight(0.1f)
                    ) {
                        IconButton(onClick = {
                            isPopupVisible = !isPopupVisible
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(id = R.string.menu),
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
                                    text = { Text(text = stringResource(id = R.string.add_company)) },
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
                            DropdownMenuItem(text = { Text(text = stringResource(id = R.string.logout)) }, onClick = {
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
                        description = stringResource(id = R.string.home)
                    )
                    if (accountType == AccountType.COMPANY) {

                        IconWithBadge(
                            iconType = IconType.COMPANY,
                            selectedIcon = selectedIcon,
                            iconSelected = Icons.Default.HomeWork,
                            iconUnselected = Icons.Outlined.HomeWork,
                            badgeCount = invoiceCount+invoiceAsClientCount,
                            onClick = {
                                viewModel.updateShow(DASH)
                                viewModel.updateScreen(IconType.COMPANY)
                            },
                            description = stringResource(id = R.string.company)
                        )
                    }
                    IconWithBadge(
                        iconType = IconType.SHOPPING,
                        selectedIcon = selectedIcon,
                        iconSelected = Icons.Default.ShoppingCart,
                        iconUnselected = Icons.Outlined.ShoppingCart,
                        badgeCount = orderCount,
                        onClick = {
                            if(accountType == AccountType.DELIVERY) {
                                viewModel.updateShow(ORDER_LINE)
                                viewModel.updateView(NOT_DELIVERED)
                            }else
                                viewModel.updateShow(SHOPPING)
                            viewModel.updateScreen(IconType.SHOPPING)
                        },
                        description = stringResource(id = R.string.shopping)
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
                                viewModel.updateShow(BUY_HISTORY)
                                viewModel.updateView(ALL_HISTORY)
                            }else viewModel.updateShow(PAYMENT)
                        },
                        description = stringResource(id = R.string.wallet)
                    )
                    IconWithBadge(
                        iconType = IconType.SEARCH,
                        selectedIcon = selectedIcon,
                        iconSelected = Icons.Default.SavedSearch,
                        iconUnselected = Icons.Outlined.Search,
                        badgeCount = 0, // Example of a message badge
                        onClick = {
                            viewModel.updateScreen(IconType.SEARCH)
                            viewModel.updateShow(SEARCH)
                        },
                        description = stringResource(id = R.string.search)
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
                                viewModel.updateShow(INVITATION)
                            },
                            description = stringResource(id = R.string.user)
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
        val view by viewModel.view
        val show by viewModel.show
        if( show == DASH || show == PAYMENT || show == ORDER|| view == NOT_DELIVERED || show == SEARCH || show == INVITATION || show == SHOPPING){
            if(historySelected == IconType.COMPANY){
                viewModel.updateShow(DASH)
            }
            viewModel.updateScreen(historySelected)
            viewModel._historySelected.value = IconType.HOME
        }
        else{
            when(show){
                ADD_ARTICLE_FOR_COMPANY -> {
                    viewModel.updateShow(ARTICLE)
                }
                ADD_CATEGORY -> {
                    viewModel.updateShow(category)
                }
                ADD_CLIENT -> {
                    viewModel.updateShow(CLIENT)
                }
                ADD_PROVIDER -> {
                    viewModel.updateShow(PROVIDER)
                }
                ADD_PAYMENT -> {
                    viewModel.updateShow(PAYMENT)
                }
                ADD_SUBCATEGORY -> {
                    viewModel.updateShow(SUBCATEGORY)
                }
                ADD_INVOICE -> {
                    if(accountType == AccountType.DELIVERY) {
                        viewModel.updateShow(ORDER_LINE)
                    }else
                    if(view == BUY_HISTORY)
                     viewModel.updateShow(ALL_HISTORY)
                    else
                        viewModel.updateShow(INVOICE)
                }
                ADD_WORKER -> {
                viewModel.updateShow(WORKER)
            }
                ADD_PARENT -> { // remove it
                    viewModel.updateShow(PARENT)
                }
                AS_CLIENT -> {
                    viewModel.updateShow(INVOICE)
                }
                ORDER_LINE -> {
                    when(view){
                        DELIVERED -> viewModel.updateView(historicView,NOT_DELIVERED)
                        MY_NOT_DELIVERED -> viewModel.updateView(historicView,NOT_DELIVERED)
                    }
                }
                POINT_ESPECE -> {
                    viewModel.updateShow(PAYMENT)
                }
                ADD_ARTICLE ->{
                    viewModel.updateShow(ARTICLE)
                }
                REGLEMENT_SCREEN -> viewModel.updateShow(REGLEMENT_FOR_PROVIDER)
                INVOICE -> {
                    when(view){
                        ALL -> viewModel.updateShow(DASH)
                        PAID -> viewModel.updateView(historicView,ALL)
                        NOT_PAID -> viewModel.updateView(historicView,ALL)
                        IN_COMPLETE -> viewModel.updateView(historicView,ALL)
                        NOT_ACCEPTED -> viewModel.updateView(historicView,ALL)
                    }
                }
                BUY_HISTORY ->{
                    when(view){
                        ALL_HISTORY -> viewModel.updateShow(PAYMENT)
                        payed -> viewModel.updateView(historicView, ALL_HISTORY)
                        incomplete -> viewModel.updateView(historicView,ALL_HISTORY)
                        notpayed -> viewModel.updateView(historicView,ALL_HISTORY)
                        notaccepted -> viewModel.updateView(historicView,ALL_HISTORY)
                    }
                }
                PROFIT ->{
                    when(view){
                        all_histories_payment_for_provider ->viewModel.updateShow(PAYMENT)
                        all_histories_payment_for_provider_by_date ->viewModel.updateView(historicView,all_histories_payment_for_provider)
                        all_profit_payment_for_provider_per_day ->viewModel.updateView(historicView,all_histories_payment_for_provider)
                        profit_by_date ->viewModel.updateView(historicView,all_histories_payment_for_provider)
                        sum_of_profit_by_date ->viewModel.updateView(historicView,all_histories_payment_for_provider)
                    }
                }
                else -> {
                    viewModel.updateShow(DASH)
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
    val selectedCategory by articleViewModel.selectedCategory.collectAsStateWithLifecycle()
    LazyRow(
        modifier = Modifier.padding(3.dp,0.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
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
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    onClick = {
                        articleViewModel.setSelectCategory(categ)
                        articleViewModel.fetchRandomArticlesForHomePage(categ)
                    },
                    modifier = Modifier
                        .size(90.dp, 70.dp),
                )
                {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Image(
                            painter = imagePainter,
                            contentDescription = stringResource(id = R.string.category),
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
                if (selectedCategory == categ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = stringResource(id = R.string.triangle),
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.Green)
                    )
                }
            }
        }
    }
}

