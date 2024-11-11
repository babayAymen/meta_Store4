package com.aymen.store.ui.screen.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ProviderViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.InputTextField
import com.aymen.store.ui.component.resolveUriToFile
import com.google.gson.Gson


@Composable
fun AddProviderScreen() {
    val appViewModel : AppViewModel = viewModel()
    val providerViewModel : ProviderViewModel = viewModel()
    val context  = LocalContext.current
    val gson = Gson()
    val provider = CompanyDto()
    var image by remember {
        mutableStateOf<Uri?>(null)
    }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri -> image = uri }
    )
    var companyName by remember {
        mutableStateOf("")
    }
    var companyCode by remember {
        mutableStateOf("")
    }
    var companyMatriculeFiscal by remember {
        mutableStateOf("")
    }
    val companysector by remember {
        mutableStateOf(CompanyCategory.DAIRY)
    }
    var companycapital by remember {
        mutableStateOf("")
    }
    var companyBankAccount by remember {
        mutableStateOf("")
    }
    var companyaddress by remember {
        mutableStateOf("")
    }
    var companyPhone by remember {
        mutableStateOf("")
    }
    var companyEmail by remember {
        mutableStateOf("")
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        LazyColumn {
            item {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                ) {
                    InputTextField(
                        labelValue = companyName,
                        label = "name",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            companyName = it
                        }
                        , onImage = {}
                    ) {

                    }
                    InputTextField(
                        labelValue = companyCode,
                        label = "code",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            companyCode = it
                        }
                        , onImage = {}
                    ) {

                    }
                    InputTextField(
                        labelValue = companyMatriculeFiscal,
                        label = "mat fisc",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            companyMatriculeFiscal = it
                        }
                        , onImage = {}
                    ) {

                    }
                    InputTextField(
                        labelValue = companysector.toString(),
                        label = "sector",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
//                            companysector = it
                        }
                        , onImage = {}
                    ) {

                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                ) {
                    InputTextField(
                        labelValue = companycapital,
                        label = "capital",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            companycapital = it
                        }
                        , onImage = {}
                    ) {

                    }
                    InputTextField(
                        labelValue = companyBankAccount,
                        label = "bank account",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            companyBankAccount = it
                        }
                        , onImage = {}
                    ) {

                    }
                    InputTextField(
                        labelValue = companyaddress,
                        label = "address",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            companyaddress = it
                        }
                        , onImage = {}
                    ) {

                    }
                    InputTextField(
                        labelValue = companyPhone,
                        label = "phone",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            companyPhone = it
                        }
                        , onImage = {}
                    ) {

                    }
                    InputTextField(
                        labelValue = companyEmail,
                        label = "email",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            companyEmail = it
                        }
                        , onImage = {}
                    ) {

                    }
                }
            }
            Row {
                Column {

                    ButtonSubmit(labelValue = "add photo", color = Color.Cyan, enabled = true) {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    AsyncImage(
                        model = image,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        ButtonSubmit(labelValue = "Cancel", color = Color.Red, enabled = true) {
                            appViewModel.updateShow("provider")
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        ButtonSubmit(labelValue = "Submit", color = Color.Green, enabled = true) {
                            provider.name = companyName
                            provider.code = companyCode
                            provider.matfisc = companyMatriculeFiscal
                            provider.category = companysector
                            provider.capital = companycapital
                            provider.bankaccountnumber = companyBankAccount
                            provider.address = companyaddress
                            provider.phone = companyPhone
                            provider.email = companyEmail

                            val photo = resolveUriToFile(image, context)
                            val providerJsonString = gson.toJson(provider)
                            if (providerJsonString.isNotEmpty() && photo != null) {
                                providerViewModel.addProvider(providerJsonString,photo)
                            } else {
                                providerViewModel.addProviderWithoutImage(providerJsonString)
                            }
                            appViewModel.updateShow("provider")
                        }
                    }

            }
                }
            }
        }




    }
}