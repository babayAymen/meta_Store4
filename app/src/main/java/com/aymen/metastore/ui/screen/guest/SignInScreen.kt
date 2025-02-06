package com.aymen.metastore.ui.screen.guest

import android.os.Handler
import android.os.Looper
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
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aymen.metastore.R
import com.aymen.metastore.model.entity.dto.AuthenticationRequest
import com.aymen.metastore.model.repository.ViewModel.SignInViewModel
import com.aymen.metastore.ui.component.ButtonComponent
import com.aymen.metastore.ui.component.ClickableLoginTextComponent
import com.aymen.metastore.ui.component.DividerTextComponent
import com.aymen.metastore.ui.component.HeadingText
import com.aymen.metastore.ui.component.NormalText
import com.aymen.metastore.ui.component.PasswordTextField
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
    val viewModel : SignInViewModel = hiltViewModel()
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
            HeadingText(value = stringResource(id = R.string.wellcome_back))
             textField(label = stringResource(id = R.string.user_name),labelValue = userName, icon = Icons.Outlined.Person ){
                userName = it
            }
            Spacer(modifier = Modifier.height(10.dp))
             PasswordTextField(
                labelValue = stringResource(id = R.string.password),
                icon = Icons.Outlined.Lock ,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
                ){
                 password = it
            }
            ClickableLoginTextComponent(stringResource(id = R.string.forget_password),Alignment.TopEnd){
                RouteController.navigateTo(Screen.ForgetPasswordScreen)
            }
            Spacer(modifier = Modifier.height(20.dp))
            if(password.isNotEmpty() && userName.isNotEmpty()){
                isEnabled = true
            }
            signin.username = userName
            signin.password = password
            ButtonComponent(value = stringResource(id = R.string.login),isEnabled,
                clickAction = {
                    viewModel.signIn(signin){
                        if(it){
                            RouteController.navigateTo(Screen.HomeScreen)
                        }else{
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(
                                    context,
                                    R.string.name_or_password_wrong,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })
            DividerTextComponent()
            NormalText(value = stringResource(id = R.string.dont_have_account), aligne = TextAlign.Center )
            ClickableLoginTextComponent (stringResource(id = R.string.register_now),Alignment.Center,
                onTextSelected = {
                    RouteController.navigateTo(Screen.SignUpScreen)
                }
            )
            if(false)
            Button(onClick = { RouteController.navigateTo(Screen.PhoneSignInScreen) }) {
                Text(text = "phone")
            }
        }
    }
}