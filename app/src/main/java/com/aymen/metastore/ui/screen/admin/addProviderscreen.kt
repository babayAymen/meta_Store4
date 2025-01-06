package com.aymen.metastore.ui.screen.admin

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aymen.metastore.R
import com.aymen.metastore.model.entity.model.Company
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ProviderViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.InputTextField
import com.aymen.metastore.ui.component.resolveUriToFile
import com.google.gson.Gson


@Composable
fun AddProviderScreen() {
    val appViewModel : AppViewModel = hiltViewModel()
    val providerViewModel : ProviderViewModel = hiltViewModel()
    val context  = LocalContext.current
    val gson = Gson()
    val provider = Company()
    val update = providerViewModel.update
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
    var companysector by remember {
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
    val providerForUpdate by providerViewModel.providerForUpdate.collectAsStateWithLifecycle()
    if(update) {
        companyName = providerForUpdate.name
        companyCode = providerForUpdate.code?:""
        companyMatriculeFiscal = providerForUpdate.matfisc?:""
        companysector = providerForUpdate.category?:CompanyCategory.DAIRY
        companycapital = providerForUpdate.capital?:""
        companyBankAccount = providerForUpdate.bankaccountnumber?:""
        companyaddress = providerForUpdate.address?:""
        companyPhone = providerForUpdate.phone?:""
        companyEmail = providerForUpdate.email?:""
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
                        label = stringResource(id = R.string.name),
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
                        label = stringResource(id = R.string.code),
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
                        label = stringResource(id = R.string.mat_fisc),
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
                        label = stringResource(id = R.string.sector),
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
                        label = stringResource(id = R.string.capital),
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
                        label = stringResource(id = R.string.bank_account),
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
                        label = stringResource(id = R.string.address),
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
                        label = stringResource(id = R.string.phone),
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
                        label = stringResource(id = R.string.email),
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

                    ButtonSubmit(labelValue = stringResource(id = R.string.add_photo), color = Color.Cyan, enabled = true) {
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
                        val navText = stringResource(id = R.string.provider)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        ButtonSubmit(labelValue = stringResource(id = R.string.cancel), color = Color.Red, enabled = true) {
                            appViewModel.updateShow(navText)
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        ButtonSubmit(labelValue = stringResource(id = R.string.submit), color = Color.Green, enabled = true) {
                            provider.name = companyName
                            provider.code = companyCode
                            provider.matfisc = companyMatriculeFiscal
                            provider.category = companysector
                            provider.capital = companycapital
                            provider.bankaccountnumber = companyBankAccount
                            provider.address = companyaddress
                            provider.phone = companyPhone
                            provider.email = companyEmail
                            if(update){
                                provider.id = providerForUpdate.id
                                provider.logo = providerForUpdate.logo
                                provider.isVisible = providerForUpdate.isVisible
                                provider.virtual = providerForUpdate.virtual
                            }
                            val photo = resolveUriToFile(image, context)
                            val providerJsonString = gson.toJson(provider)
                            if (providerJsonString.isNotEmpty() && photo != null)
                                if(update)providerViewModel.updateProvider(provider, providerJsonString, photo)
                                else providerViewModel.addProvider(provider,providerJsonString,photo)
                             else if(update) providerViewModel.updateProvider(provider, providerJsonString,null)
                                 else providerViewModel.addProvider(provider,providerJsonString, null)
                            providerViewModel.update = false
                            appViewModel.updateShow(navText)
                        }
                    }

            }
                }
            }
        }




    }
}