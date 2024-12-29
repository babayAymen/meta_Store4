package com.aymen.store.ui.screen.guest

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.metastore.R
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.metastore.model.entity.dto.RegisterRequest
import com.aymen.metastore.model.repository.ViewModel.SignInViewModel
import com.aymen.metastore.ui.component.ButtonComponent
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.CheckBoxComponent
import com.aymen.metastore.ui.component.ClickableLoginTextComponent
import com.aymen.metastore.ui.component.DividerTextComponent
import com.aymen.metastore.ui.component.HeadingText
import com.aymen.metastore.ui.component.NormalText
import com.aymen.metastore.ui.component.PhoneField
import com.aymen.metastore.ui.component.dropDownCompanyCategory
import com.aymen.metastore.ui.component.emailField
import com.aymen.metastore.ui.component.passwordTextField
import com.aymen.metastore.ui.component.textField
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.aymen.store.ui.navigation.SystemBackButtonHandler

@Composable
fun SignUpScreen() {

    val viewModel : SignInViewModel = viewModel()
    val registerRequest = RegisterRequest(
        username = "",
        email = "",
        phone = "",
        password = "",
        address = "",
        longitude = 0.0,
        latitude = 0.0,
        category = CompanyCategory.DAIRY,
        type = AccountType.USER
    )
    var userName by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var phone by remember {
        mutableStateOf("")
    }
    var isEnabled = false
    var asCompany by remember {
        mutableStateOf(false)
    }
    var companyCategory by remember {
        mutableStateOf(CompanyCategory.DAIRY)
    }
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp, 26.dp)
    )
    {
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
        ) {
            item {
                HeadingText(value = stringResource(id = R.string.create_an_account))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ButtonSubmit(
                        labelValue = if (asCompany) stringResource(R.string.company) else stringResource(R.string.user),
                        color = Color.Green,
                        enabled = true
                    ) {
                        asCompany = !asCompany
                    }
                }
                textField(
                    label = if (asCompany)
                        stringResource(R.string.company) +stringResource(R.string.name)
                     else stringResource(R.string.user)+stringResource(R.string.name),
                    labelValue = userName,
                    Icons.Outlined.Person
                ) {
                    userName = it
                }
                if (asCompany) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        dropDownCompanyCategory {
                            companyCategory = it
                        }
                    }
                }
                email = emailField(
                    labelValue = stringResource(id = R.string.email),
                    Icons.Outlined.AlternateEmail
                )
                phone = PhoneField(
                    labelValue = stringResource(id = R.string.phone),
                    Icons.Outlined.Phone
                )
                password = passwordTextField(
                    labelValue = stringResource(id = R.string.password),
                    Icons.Outlined.Lock,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
                )
                password = passwordTextField(
                    labelValue = stringResource(id = R.string.re_password),
                    Icons.Outlined.Lock,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
                )
                CheckBoxComponent(
                    value = stringResource(id = R.string.privacy_policy),
                    onTextSelected = {
                        RouteController.navigateTo(Screen.TermConditionScreen)
                    })
                if (userName.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty()) {
                    isEnabled = true
                }
                if (isEnabled) {
                    if (asCompany) {
                        registerRequest.type = AccountType.COMPANY
                    } else {

                        registerRequest.type = AccountType.USER
                    }
                    registerRequest.category = companyCategory
                    registerRequest.password = password
                    registerRequest.username = userName
                    registerRequest.email = email
                    registerRequest.phone = phone

                }
                ButtonComponent(value = stringResource(id = R.string.register), isEnabled,
                    clickAction = {
                         viewModel.signUp(registerRequest){
                             if(it){
                            RouteController.navigateTo(Screen.HomeScreen)
                             }
                         }
                    })
                DividerTextComponent()
                NormalText(value = stringResource(id = R.string.have_account), aligne = TextAlign.Center)
                ClickableLoginTextComponent(
                    stringResource(id = R.string.login),
                    onTextSelected = {
                        RouteController.navigateTo(Screen.SignInScreen)
                    }
                )
            }
        }
        SystemBackButtonHandler {
            RouteController.navigateTo(Screen.SignInScreen)
        }

    }
}
