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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.entity.api.RegisterRequest
import com.aymen.store.model.repository.ViewModel.SignInViewModel
import com.aymen.store.ui.component.ButtonComponent
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.CheckBoxComponent
import com.aymen.store.ui.component.ClickableLoginTextComponent
import com.aymen.store.ui.component.DividerTextComponent
import com.aymen.store.ui.component.HeadingText
import com.aymen.store.ui.component.NormalText
import com.aymen.store.ui.component.PhoneField
import com.aymen.store.ui.component.dropDownCompanyCategory
import com.aymen.store.ui.component.emailField
import com.aymen.store.ui.component.passwordTextField
import com.aymen.store.ui.component.textField
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
        longitude = 1.2,
        latitude = 1.2,
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
                HeadingText(value = "Create an account")
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ButtonSubmit(
                        labelValue = if (asCompany) "Company" else "user",
                        color = Color.Green,
                        enabled = true
                    ) {
                        asCompany = !asCompany
                    }
                }
                textField(
                    label = if (asCompany) "Company name" else "user name",
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
                            Toast.makeText(context, "$it tt $companyCategory", Toast.LENGTH_SHORT).show()
                            Log.e("companycateg","$companyCategory ${it.name}")
                        }
                    }
                }
                email = emailField(
                    labelValue = "email",
                    Icons.Outlined.AlternateEmail
                )
                phone = PhoneField(
                    labelValue = "phone",
                    Icons.Outlined.Phone
                )
                password = passwordTextField(
                    labelValue = "password",
                    Icons.Outlined.Lock,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
                )
                password = passwordTextField(
                    labelValue = "re-password",
                    Icons.Outlined.Lock,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
                )
                CheckBoxComponent(
                    value = "By continuing you accept our Privacy Policy and term of use",
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
                ButtonComponent(value = "Register", isEnabled,
                    clickAction = {
                        Toast.makeText(context, "clicked ${registerRequest.category}", Toast.LENGTH_SHORT).show()
                         viewModel.signUp(registerRequest){
                             if(it){
                            RouteController.navigateTo(Screen.HomeScreen)
                             }
                         }
                    })
                DividerTextComponent()
                NormalText(value = "you already have an account? ", aligne = TextAlign.Center)
                ClickableLoginTextComponent("Login",
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
