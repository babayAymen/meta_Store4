package com.aymen.store.app

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.CompanyViewModel
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.aymen.store.ui.screen.admin.DashBoardScreen
import com.aymen.store.ui.screen.guest.SignInScreen
import com.aymen.store.ui.screen.guest.SignUpScreen
import com.aymen.store.ui.screen.guest.TermConditionScreen
import com.aymen.store.ui.screen.user.AddCompanyScreen
import com.aymen.store.ui.screen.user.ArticleDetailsScreen
import com.aymen.store.ui.screen.user.CompanyScreen
import com.aymen.store.ui.screen.user.HomeScreen
import com.aymen.store.ui.screen.user.MenuScreen
import com.aymen.store.ui.screen.user.ConversationScreen
import com.aymen.store.ui.screen.user.NotificationScreen
import com.aymen.store.ui.screen.user.PaymentScreen
import com.aymen.store.ui.screen.user.ShoppingScreen
import com.aymen.store.ui.screen.user.UserScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MetaStore (){
    val companyViewModel : CompanyViewModel = hiltViewModel()
    val appViewModel : AppViewModel = hiltViewModel()
    var isLoggedIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
         appViewModel.isLoggedIn{
        if (it){
        RouteController.home()
        }
             isLoggedIn = true
         }

    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 0.dp)
        ,
        color = Color.White,

        ) {
    if(isLoggedIn){
        Crossfade(targetState = RouteController.currentScreen) { currentState ->
            when (currentState.value) {
                is Screen.SignInScreen -> {
                    SignInScreen()
                }

                is Screen.TermConditionScreen -> {
                    TermConditionScreen()
                }

                is Screen.SignUpScreen -> {
                    SignUpScreen()
                }

                is Screen.HomeScreen -> {
                    HomeScreen()
                }

                is Screen.AddCompanyScreen -> {
                    AddCompanyScreen(update = false)
                }

                is Screen.NotificationScreen -> {
                    NotificationScreen()
                }

                is Screen.ConversationScreen -> {
                    ConversationScreen()
                }

                is Screen.PaymentScreen -> {
                    PaymentScreen()
                }

                is Screen.ShoppingScreen -> {
                    ShoppingScreen()
                }

                is Screen.MenuScreen -> {
                    MenuScreen()
                }

                is Screen.DashBoardScreen -> {
                    DashBoardScreen()
                }

                is Screen.CompanyScreen -> {
                    val company = companyViewModel.myCompany
                    CompanyScreen(company = company)
                }

                is Screen.ArticleDetailScreen -> {
                    ArticleDetailsScreen()
                }

                is Screen.UserScreen -> {
                    UserScreen()
                }

            }
        }

        }
    }
}