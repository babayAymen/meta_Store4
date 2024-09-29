package com.aymen.store.ui.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class Screen{
    data object SignUpScreen : Screen()
    data object TermConditionScreen : Screen()
    data object SignInScreen : Screen()
    data object HomeScreen : Screen()
    data object AddCompanyScreen : Screen()
    data object NotificationScreen : Screen()
    data object PaymentScreen : Screen()
    data object MenuScreen : Screen()
    data object ShoppingScreen : Screen()
    data object ConversationScreen : Screen()
    data object DashBoardScreen : Screen()
    data object CompanyScreen : Screen()
    data object ArticleDetailScreen : Screen()
    data object UserScreen : Screen()

}

object RouteController {
    var current by mutableStateOf(Screen.SignInScreen)
    var currentScreen : MutableState<Screen> = mutableStateOf(current)

    fun  navigateTo(destination: Screen){
        currentScreen.value = destination
    }

    fun home(){
        currentScreen.value = Screen.HomeScreen
    }

}
