package com.aymen.metastore.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aymen.metastore.LanguageSwither
import com.aymen.metastore.R
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.metastore.util.IMAGE_URL_COMPANY
import com.aymen.metastore.util.META
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.RoleEnum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()
    val myUser by sharedViewModel.user.collectAsStateWithLifecycle()
    val myAccountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showLanguageDialog by remember {
        mutableStateOf(false)
    }
    val desireAccountType = when(myAccountType){
        AccountType.COMPANY -> {AccountType.USER}
        AccountType.USER -> {
            when(myUser.accountType){
                AccountType.COMPANY -> AccountType.COMPANY
                AccountType.USER -> AccountType.USER
                AccountType.META -> AccountType.META
                AccountType.SELLER -> AccountType.COMPANY
                AccountType.DELIVERY -> AccountType.DELIVERY
                AccountType.NULL -> TODO()
                null -> TODO()
            }
        }
        AccountType.META -> {AccountType.USER}
        AccountType.SELLER -> {AccountType.USER}
        AccountType.DELIVERY -> {AccountType.USER}
        AccountType.NULL -> {AccountType.NULL}
    }
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                ProfileTopBar(scrollBehavior, myCompany, myUser, myAccountType)
            },
            contentWindowInsets = WindowInsets(0.dp)
        ) { value ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(value)
            ) {
               LazyColumn(
                   modifier = Modifier.fillMaxSize(),
                   contentPadding = PaddingValues(16.dp)
               ) {
                   item{
                       Row (
                           modifier = Modifier.height(56.dp),
                           verticalAlignment = Alignment.CenterVertically
                       ){
                           Icon(painter = painterResource(id = R.drawable.icon), contentDescription = null)
                           Spacer(modifier = Modifier.width(8.dp))
                           Text(text = stringResource(id = R.string.edit_profile))
                       }
                       Spacer(modifier = Modifier.height(12.dp))
                       Row(
                           modifier = Modifier
                               .height(56.dp)
                               .clickable {
                                   sharedViewModel.changeAccountType(desireAccountType)
                               },
                           verticalAlignment = Alignment.CenterVertically
                       ) {
                           Icon(painter = painterResource(id = R.drawable.exchange_01), contentDescription = null)
                           Spacer(modifier = Modifier.width(8.dp))
                           Text(text = stringResource(id = if(myAccountType == AccountType.COMPANY) R.string.switch_to_user else R.string.switch_to_company))

                       }
                       Spacer(modifier = Modifier.height(12.dp))
                       Row(
                           modifier = Modifier
                               .height(56.dp)
                               .clickable {
                                   showLanguageDialog = !showLanguageDialog
                               },
                           verticalAlignment = Alignment.CenterVertically
                       ) {
                           Icon(painter = painterResource(id = R.drawable.language_circle), contentDescription = null)
                           Spacer(modifier = Modifier.width(8.dp))
                           Text(text = stringResource(id = R.string.change_language))
                       }
                           if(showLanguageDialog){
                               Spacer(modifier = Modifier.height(6.dp))
                               LanguageSwither()
                           }
                       Spacer(modifier = Modifier.height(12.dp))
                       Row (
                           modifier = Modifier
                               .height(56.dp)
                               .clickable {
                                   sharedViewModel.logout()
                               },
                           verticalAlignment = Alignment.CenterVertically
                       ){
                           Icon(painter = painterResource(id = R.drawable.logout_01), contentDescription = null)
                           Spacer(modifier = Modifier.width(8.dp))
                           Text(text = stringResource(id = R.string.logout))

                       }
                       Spacer(modifier = Modifier.height(12.dp))
                   }
               }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(scrollBehavior: TopAppBarScrollBehavior, company : Company? , user : User? , accountType: AccountType) {

    TopAppBar(
        title = { Text(text = "profile") },
        navigationIcon = {
            Column{
                Row( modifier = Modifier
                    .background(Color(0xFF000000))
                    .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                ) {

                ShowImage(image = String.format(
                    IMAGE_URL_COMPANY,
                    company?.logo,
                    company?.user?.id
                ),
                    50.dp,
                    50.dp
                )
                    Spacer(modifier = Modifier.width(8.dp))
                Column {
                Text(text = company?.name!!,color = Color.White)
                Text(text = company.email!!,color = Color.White)
                }
                }

            }
        },
    scrollBehavior = scrollBehavior,
        windowInsets = WindowInsets(0.dp)
    )
}

















