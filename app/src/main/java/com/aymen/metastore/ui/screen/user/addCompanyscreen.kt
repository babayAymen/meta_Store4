package com.aymen.store.ui.screen.user

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.aymen.metastore.model.entity.converterRealmToApi.mapcompanyDtoToCompanyRealm
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.IconType
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.CompanyViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.DropDownCompanyCategory
import com.aymen.store.ui.component.InputTextField
import com.aymen.store.ui.component.resolveUriToFile
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.aymen.store.ui.navigation.SystemBackButtonHandler
import com.google.gson.Gson

@Composable
fun AddCompanyScreen(update : Boolean) {
    val viewModel : CompanyViewModel = viewModel()
    val appViewModel : AppViewModel = viewModel()
    var image by remember {
        mutableStateOf<Uri?>(null)
    }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri -> image = uri }
    )
//    var company = viewModel.myCompany
    val gson = Gson()
//    val companyJsonString = gson.toJson(company)
    var company by remember {
        mutableStateOf(Company())
    }
    var companyId by remember {
        mutableLongStateOf(0)
    }
    if(update) {
        LaunchedEffect(Unit) {
            viewModel.getMyCompany {
                if (it != null) {
                    company = mapcompanyDtoToCompanyRealm(it)
                    companyId = it.id!!
                    Log.e("aymenbabayjsoncompany","company name : ${company.name}")
                }
            }
        }
    }

    var companyName by remember {
        mutableStateOf(company.name)
    }
    val context = LocalContext.current

    var companyCode by remember {
        mutableStateOf("")
    }
    var companyMatriculeFiscal by remember {
        mutableStateOf("")
    }
    var companyCategory by remember {
        mutableStateOf(CompanyCategory.DAIRY.toString())
    }
    var category by remember {
        mutableStateOf("")
    }
//    LaunchedEffect(key1 = category) {
//       companyCategory = CompanyCategory.entries.find { it.name.equals(companyCategory) }!!
//    }
    var companyCapital by remember {
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
    LaunchedEffect(company) {
        companyName = company.name
        companyCode = company.code?:""
        companyMatriculeFiscal = company.matfisc?:""
        companyCategory = company.category.toString()
        companyCapital = company.capital?:""
        companyBankAccount = company.bankaccountnumber?:""
//        companyAddress = company.address
        companyPhone = company.phone?:""
    }
    Surface (
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ){
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        )
        {
            Column (
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
            ){
                InputTextField(
                    labelValue = companyName,
                    label = "name", singleLine = true,
                    maxLine = 1,
                    keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                    onValueChange = {
                        companyName = it
                    }
                    , onImage = {}
                ){

                }
                InputTextField(
                    labelValue = companyMatriculeFiscal,
                    label = "matricule fiscal",
                    singleLine = true,
                    maxLine = 1,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    onValueChange = {
                        companyMatriculeFiscal = it
                    }
                    , onImage = {}
                ){

                }
//                InputTextField(labelValue = companyCategory.toString(), label = "sector", singleLine = true, maxLine = 1, keyboardOptions = KeyboardOptions(
//                    imeAction = ImeAction.Next
//                ),
//                    onValueChange = {
//                        category = it
//                    }
//                ){
//                }
                DropDownCompanyCategory {
                    companyCategory = it.toString()
                }
                InputTextField(labelValue = companyCapital, label = "capital", singleLine = true, maxLine = 1, keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                    onValueChange = {capital ->
                        companyCapital = capital
                    }
                    , onImage = {}
                ){

                }
                ButtonSubmit(
                    labelValue = "Cancel",
                    color = Color.Red
                    , enabled = true,
                    clickAction = {RouteController.navigateTo(Screen.HomeScreen)})
            }
            Column (
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
            ) {

                    InputTextField(labelValue = companyCode, label = "code", singleLine = true, maxLine = 1, keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                        onValueChange = {code  ->
                            companyCode = code
                        }
                        , onImage = {}
                    ){

                    }
                InputTextField(labelValue = companyBankAccount, label = "bank account", singleLine = true, maxLine = 1, keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                    onValueChange = {account ->
                        companyBankAccount = account
                    }
                    , onImage = {}
                ){

                }
                InputTextField(labelValue = companyaddress, label = "address", singleLine = true, maxLine = 1, keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                    onValueChange = {address ->
                        companyaddress = address
                    }
                    , onImage = {}
                ){

                }
                InputTextField(labelValue = companyPhone, label = "phone", singleLine = true, maxLine = 1, keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                    onValueChange = {phone ->
                        companyPhone = phone
                    }
                    , onImage = {}
                ){

                }
                    ButtonSubmit(labelValue = "add photo", color = Color.Cyan, enabled = true, clickAction = {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    })
                    ButtonSubmit(
                        labelValue = "Submit",
                        color = Color.Green
                        , enabled = true,
                        clickAction = {
                            RouteController.navigateTo(Screen.DashBoardScreen)
                            val photo =  resolveUriToFile(image, context)
                            company.code = companyCode
                            company.name = companyName
                            company.matfisc = companyMatriculeFiscal
                            company.category = companyCategory
                            company.capital = companyCapital
                            company.bankaccountnumber = companyBankAccount
                            if(update){
                            company.id = companyId
                            }
                           val companyJsonString = gson.toJson(company)
                            Log.e("aymenbabayjsoncompany",companyJsonString)
                            if (photo != null) {
                                if (update){
                                    viewModel.updateCompany(companyJsonString, photo)
                                }else {
                                    viewModel.addCompany(companyJsonString, photo)
                                    appViewModel.updateScreen(IconType.COMPANY)
                                }
                            }
                        })

                Row {
                    AsyncImage(
                        model = image,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

        }

        if(!update){

        SystemBackButtonHandler {
            RouteController.navigateTo(Screen.HomeScreen)
        }
        }
        else{
            SystemBackButtonHandler {

                RouteController.navigateTo(Screen.HomeScreen)
                appViewModel.updateScreen(IconType.COMPANY)
                appViewModel.updateShow("dash")
            }
        }
    }
}
