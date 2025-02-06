package com.aymen.metastore.ui.screen.guest

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aymen.metastore.R
import com.aymen.metastore.model.repository.ViewModel.SignInViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.InputTextField
import com.aymen.metastore.ui.component.textField
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.aymen.store.ui.navigation.SystemBackButtonHandler

@Composable
fun PhoneSignInScreen() {
    val signInViewModel: SignInViewModel = hiltViewModel()
    var phoneNumber by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    Surface {

        Column {
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("phone number") })
            Button(onClick = {
                if (phoneNumber.isNotEmpty()) {
                    signInViewModel.signInWithPhoneNumber(phoneNumber, context)
                }
            }) {
                Text(text = "get code")
            }
        }
    }
}

@Composable
fun ForgetPasswordScreen(modifier: Modifier = Modifier) {
    val signInViewModel: SignInViewModel = hiltViewModel()
    val context = LocalContext.current
    var username by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var verifCode by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var showVerifField by remember {
        mutableStateOf(false)
    }
    var enbaledUsername by remember {
        mutableStateOf(true)
    }
    var enbaledCode by remember {
        mutableStateOf(true)
    }
    var verified by remember {
        mutableStateOf(false)
    }
    Surface {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            Arrangement.Center,
            Alignment.CenterHorizontally,

        ) {
            InputTextField(
                labelValue = username,
                label = stringResource(id = R.string.your_username),
                singleLine = true ,
                maxLine = 1,
                keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),onValueChange = {
                username = it
                },
                onImage = {},
                enabled = enbaledUsername
            ){

            }
            InputTextField(
                labelValue = email,
                label = stringResource(id = R.string.email),
                singleLine = true ,
                maxLine = 1,
                keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),onValueChange = {
                email = it
                },
                onImage = {},
                enabled = enbaledUsername
            ){

            }
            if (showVerifField)
                InputTextField(
                    labelValue = verifCode,
                    label = stringResource(id = R.string.code),
                    singleLine = true ,
                    maxLine = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),onValueChange = {
                        verifCode = it
                    },
                    onImage = {},
                    enabled = enbaledCode
                ){

                }
            if (verified)
                InputTextField(
                    labelValue = password,
                    label = stringResource(id = R.string.password),
                    singleLine = true ,
                    maxLine = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),onValueChange = {
                        password = it
                    },
                    onImage = {},
                    enabled = true
                ){

                }

            val usernamewrong = stringResource(id = R.string.user_name_wrong)
            val there_is_problem = stringResource(id = R.string.there_is_problem)
            val code_wrong = stringResource(id = R.string.code_wrong)
            ButtonSubmit(
                labelValue = stringResource(id = if(!showVerifField) R.string.get_code else if(verified) R.string.submit else R.string.verifCode),
                enabled = enbaledUsername || verifCode.length == 6 || verified,
                color = Color.Green,
            ) {
                 enbaledUsername = false
                if (!showVerifField) {
                    signInViewModel.sendVerificationCodeViaEmail(username, email){
                        if(it) {
                            showVerifField = true
                            enbaledCode = true
                        }
                        else{
                            Toast.makeText(context, usernamewrong, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                if(verified){

                    signInViewModel.changePassword(username, email, password){
                        if(it)
                            RouteController.navigateTo(Screen.HomeScreen)
                        else
                            Toast.makeText(context, there_is_problem, Toast.LENGTH_SHORT).show()
                    }
                }
                if(verifCode.length == 6 && !verified){
                    signInViewModel.verificationCode(username, email,verifCode){
                     if(it){
                    verified = true
                    enbaledCode = false
                     }else{
                         Toast.makeText(context, code_wrong, Toast.LENGTH_SHORT).show()
                     }
                    }
                }
            }
            ButtonSubmit(labelValue = stringResource(id = R.string.cancel), color = Color.Red, enabled = true) {
                showVerifField = false
                enbaledUsername = true
            }
        }
    }
    SystemBackButtonHandler {
        RouteController.navigateTo(Screen.SignInScreen)
    }
}