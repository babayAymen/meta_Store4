package com.aymen.metastore.ui.screen.admin

import android.net.Uri
import android.util.Log
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
import com.aymen.metastore.model.repository.ViewModel.ClientViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.InputTextField
import com.aymen.metastore.ui.component.resolveUriToFile
import com.google.gson.Gson

@Composable
fun AddClientScreen() {
    val appViewModel : AppViewModel = hiltViewModel()
    val clientViewModel : ClientViewModel = hiltViewModel()
    val context  = LocalContext.current
    val update = clientViewModel.update
    val gson = Gson()
    val client = Company()
    val clientText = stringResource(id = R.string.client)
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
        val  clien by clientViewModel.clientForUpdate.collectAsStateWithLifecycle()
    if(update) {
        companyName = clien?.name?:""
        companyCode = clien?.code?:""
        companyMatriculeFiscal = clien?.matfisc?:""
        companysector = clien?.category?:CompanyCategory.DAIRY
        companycapital = clien?.capital?:""
        companyBankAccount = clien?.bankaccountnumber?:""
        companyaddress = clien?.address?:""
        companyPhone = clien?.phone?:""
        companyEmail = clien?.email?:""
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
//                        companysector = it
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
}
    item {

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
    }
    item {

    Row {

    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {

                ButtonSubmit(labelValue = stringResource(id = R.string.cancel), color = Color.Red, enabled = true) {
                    appViewModel.updateShow(clientText)
                    if(update){
                        clientViewModel.update = false
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {

                ButtonSubmit(labelValue = stringResource(id = R.string.submit), color = Color.Green, enabled = true) {
                    client.name = companyName
                    client.code = companyCode
                    client.matfisc = companyMatriculeFiscal
                    client.category = companysector
                    client.capital = companycapital
                    client.bankaccountnumber = companyBankAccount
                    client.address = companyaddress
                    client.phone = companyPhone
                    client.email = companyEmail
                    if(update){
                        client.id = clien?.id
                    }


                    val photo = resolveUriToFile(image, context)
                    val clientJsonString = gson.toJson(client)
                    if (clientJsonString.isNotEmpty() && photo != null) {
                        if(update) clientViewModel.updateClient(client,clientJsonString, photo)
                        else clientViewModel.addClient(client,clientJsonString,photo)
                    } else if(update) clientViewModel.updateClient(client,clientJsonString, null)
                        else clientViewModel.addClient(client,clientJsonString, null)
                    if(update){
                        clientViewModel.update = false
                    }
                    appViewModel.updateShow(clientText)
                }
            }

        }
    }
    }
    }
}




    }
}