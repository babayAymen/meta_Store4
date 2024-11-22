package com.aymen.metastore.ui.screen.guest

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Scaffold
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
import com.aymen.metastore.model.entity.dto.AuthenticationRequest
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.SignInViewModel
import com.aymen.metastore.ui.component.ButtonComponent
import com.aymen.metastore.ui.component.ClickableLoginTextComponent
import com.aymen.metastore.ui.component.DividerTextComponent
import com.aymen.metastore.ui.component.HeadingText
import com.aymen.metastore.ui.component.NormalText
import com.aymen.metastore.ui.component.passwordTextField
import com.aymen.metastore.ui.component.textField
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen

@Composable
fun SignInScreen(){

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp, 26.dp)
    ){

        MyScaffold()
    }
}

@Composable
fun MyScaffold(
) {
    val viewModel : SignInViewModel = viewModel()
    val appViewModel : AppViewModel = viewModel()
    val context = LocalContext.current
    val signin = AuthenticationRequest(
        username = "",
        password = ""
    )
    var userName by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var isEnabled by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),

        ) {value ->

        Column(modifier = Modifier.fillMaxSize())
        {
            HeadingText(value = "Welcome back")
             textField(label = "user name",labelValue = userName, icon = Icons.Outlined.Person ){
                userName = it
            }
            Spacer(modifier = Modifier.height(10.dp))
            password = passwordTextField(
                labelValue = "Password",
                icon = Icons.Outlined.Lock ,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
                )
            NormalText(value = "Forget Your Password?", aligne = TextAlign.End)
            Spacer(modifier = Modifier.height(20.dp))
            if(password.isNotEmpty() && userName.isNotEmpty()){
                isEnabled = true
            }
            signin.username = userName
            signin.password = password
            ButtonComponent(value = "Login",isEnabled,
                clickAction = {
                    viewModel.signIn(signin){
                        if(it){
                            RouteController.navigateTo(Screen.HomeScreen)
                        }else{
                            Toast.makeText(context, "username or password is wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            DividerTextComponent()
            NormalText(value = "You do not have an account?", aligne = TextAlign.Center )
            ClickableLoginTextComponent ("Register Now!",
                onTextSelected = {
                    RouteController.navigateTo(Screen.SignUpScreen)
                }
            )
        }
    }
}