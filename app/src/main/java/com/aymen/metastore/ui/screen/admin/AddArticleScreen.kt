package com.aymen.metastore.ui.screen.admin

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.Category
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.Enum.UnitArticle
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.ArticleViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.metastore.model.repository.ViewModel.SubCategoryViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.DropDownCategory
import com.aymen.metastore.ui.component.DropDownCompany
import com.aymen.metastore.ui.component.DropDownSubCategory
import com.aymen.metastore.ui.component.RadioButtons
import com.aymen.metastore.ui.component.dropDownItems
import com.aymen.metastore.ui.component.InputTextField
import com.aymen.metastore.ui.component.resolveUriToFile
import com.aymen.store.model.repository.ViewModel.ProviderViewModel
import com.google.gson.Gson

@Composable
fun AddArticleScreen(){
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(1.dp)
    ){
        val appViewModel : AppViewModel = hiltViewModel()
        val categoryViewModel : CategoryViewModel = hiltViewModel()
        val companyViewModel : CompanyViewModel = hiltViewModel()
        val sharedViewModel : SharedViewModel = hiltViewModel()
        val subCategoryViewModel : SubCategoryViewModel = hiltViewModel()
        val articleViewModel : ArticleViewModel = hiltViewModel()
        val providerViewModel : ProviderViewModel = hiltViewModel()
        val company by sharedViewModel.company.collectAsStateWithLifecycle()
        val article = articleViewModel.article
        var image by remember {
            mutableStateOf<Uri?>(null)
        }
        val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = {uri -> image = uri }
        )
        val categories = categoryViewModel.companyCategories.collectAsLazyPagingItems()
        var category by remember {
            mutableStateOf(Category())
        }
        LaunchedEffect(key1 = Unit) {
            categoryViewModel.setFilter(company.id?:0)
        }
        LaunchedEffect(key1 = category) {
            subCategoryViewModel.getAllSubCategoriesByCategoryId(category.id?:0, sharedViewModel.company.value.id?:0)
        }


        var subCategory by remember {
            mutableStateOf(SubCategory())
        }
        val subcategories = subCategoryViewModel.allSubCategories.collectAsLazyPagingItems()

        val context = LocalContext.current
        var articleCompany = ArticleCompany()

        val gson = Gson()
        var tva by remember {
            mutableDoubleStateOf(0.0)
        }
        var qte by remember {
            mutableDoubleStateOf(0.0)
        }
        var quantity by remember {
            mutableStateOf("")
        }
        var minQte by remember {
            mutableDoubleStateOf(0.0)
        }
        var minQuantity by remember {
            mutableStateOf("")
        }
        var cost by remember {
            mutableDoubleStateOf(0.0)
        }
        var costFieald by remember {
            mutableStateOf("")
        }
        var sellingPrice by remember {
            mutableDoubleStateOf(0.0)
        }
        var sellingPriceFieald by remember {
            mutableStateOf("")
        }
        var label by remember {
            mutableStateOf("")
        }
        var code by remember {
            mutableStateOf("")
        }
        var barcode by remember {
            mutableStateOf("")
        }
        var description by remember {
            mutableStateOf("")
        }
        var unitItem by remember {
            mutableStateOf(UnitArticle.U)
        }
        var privacy by remember {
            mutableStateOf(PrivacySetting.PUBLIC)
        }
        var isDiscounted by remember {
            mutableStateOf(false)
        }


        var provider by remember {
            mutableStateOf(Company())
        }
        if(articleViewModel.upDate){
            val articlee by articleViewModel.articleCompany.collectAsStateWithLifecycle()
            Log.e("articleCompany","from view model : $articlee")
            articleCompany = articlee!!
            minQuantity = if(articlee?.unit == UnitArticle.U)(articlee?.minQuantity?:0.0).toInt().toString() else (articlee?.minQuantity?:0.0).toString()
            qte = articlee?.quantity!!
            minQte = articlee?.minQuantity!!
            costFieald = (articlee?.cost?:0.0).toString()
            cost = articlee?.cost!!
            sellingPriceFieald = (articlee?.sellingPrice?:0.0).toString()
            sellingPrice = articlee?.sellingPrice!!
            unitItem = articlee?.unit!!
            quantity = if(articlee?.unit == UnitArticle.U)(articlee?.quantity?:0.0).toInt().toString() else (articlee?.quantity?:0.0).toString()
            privacy = articlee?.isVisible?:PrivacySetting.PUBLIC
            category = articlee?.category!!
            subCategory = articlee?.subCategory!!
            provider = articlee?.provider!!
        }
        val providers = providerViewModel.providers.collectAsLazyPagingItems()

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(1) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {

                    if (false) {
                        Column(
                            modifier = Modifier
                                .padding(2.dp)
                                .weight(1f)
                        ) {

                            InputTextField(
                                labelValue = label,
                                label = "label",
                                singleLine = true,
                                maxLine = 1,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                                onValueChange = {
                                    label = it
                                }, onImage = {}
                            ) {

                            }
                            InputTextField(
                                labelValue = code,
                                label = "code",
                                singleLine = true,
                                maxLine = 1,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                                onValueChange = {
                                    code = it
                                }, onImage = {}
                            ) {

                            }
                            InputTextField(
                                labelValue = barcode,
                                label = "barcode",
                                singleLine = true,
                                maxLine = 1,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                                onValueChange = {
                                    barcode = it
                                }, onImage = {}
                            ) {

                            }
                            InputTextField(
                                labelValue = description,
                                label = "description",
                                singleLine = false,
                                maxLine = 3,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text
                                ),
                                onValueChange = {
                                    description = it
                                }, onImage = {}
                            ) {

                            }
                            InputTextField(
                                labelValue = if (tva == 0.0) "" else tva.toString(),
                                label = "tva",
                                singleLine = true,
                                maxLine = 1,
                                KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                onValueChange = {
                                    tva = it.toDouble()
                                }, onImage = {}
                            ) {

                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f)
                    ) {
                        InputTextField(
                            labelValue = quantity,
                            label = "quantity",
                            singleLine = true,
                            maxLine = 1,
                            KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            onValueChange = {
                                if (unitItem == UnitArticle.U) {
                                    if (it.matches(Regex("^[0-9]*$"))) {
                                        quantity = it
                                        qte = it.toDoubleOrNull() ?: 0.0
                                    }
                                } else {
                                    if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                                        val normalizedInput = it.replace(',', '.')
                                        quantity = normalizedInput
                                        qte = if (normalizedInput.startsWith(".")) {
                                            0.0
                                        }else{
                                            if (normalizedInput.endsWith(".")) {
                                                normalizedInput.let { inp ->
                                                    if (inp.toDouble() % 1.0 == 0.0) inp.toDouble() else 0.0
                                                }
                                            } else {
                                                normalizedInput.toDoubleOrNull() ?: 0.0
                                            }
                                        }
                                    }
                                }

                            }, onImage = {}, true
                        ) {

                        }
                        InputTextField(
                            labelValue = minQuantity,
                            label = "minQuantity",
                            singleLine = true,
                            maxLine = 1,
                            KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            onValueChange = {
                                if (unitItem == UnitArticle.U) {
                                    if (it.matches(Regex("^[0-9]*$"))) {
                                        minQuantity = it
                                        minQte = it.toDoubleOrNull() ?: 0.0
                                    }
                                } else {
                                    if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                                        val normalizedInput = it.replace(',', '.')
                                        minQuantity = normalizedInput
                                        minQte = if (normalizedInput.startsWith(".")) {
                                             0.0
                                        }else if (normalizedInput.endsWith(".")) {
                                            normalizedInput.let { inp ->
                                                if (inp.toDouble() % 1.0 == 0.0) inp.toDouble() else 0.0
                                            }
                                        } else {
                                            normalizedInput.toDoubleOrNull() ?: 0.0
                                        }
                                    }
                                }
                            }, onImage = {}, true
                        ) {

                        }
                        InputTextField(
                            labelValue = costFieald,
                            label = "cost",
                            singleLine = true,
                            maxLine = 1,
                            KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            onValueChange = {
                                if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                                    val normalizedInput = it.replace(',', '.')
                                    costFieald = normalizedInput
                                    cost = if(normalizedInput.startsWith(".")) {
                                        0.0
                                    } else if (normalizedInput.endsWith(".")) {
                                        normalizedInput.let { inp ->
                                            if (inp.toDouble() % 1.0 == 0.0) inp.toDouble() else 0.0
                                        }
                                    } else {
                                        normalizedInput.toDoubleOrNull() ?: 0.0
                                    }
                                }
                            }, onImage = {}, true
                        ) {

                        }
                        InputTextField(
                            labelValue = sellingPriceFieald,
                            label = "selling price",
                            singleLine = true,
                            maxLine = 1,
                            KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            onValueChange = {
                                if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                                    val normalizedInput = it.replace(',', '.')
                                    sellingPriceFieald = normalizedInput
                                    sellingPrice = if(normalizedInput.startsWith(".")) 0.0
                                    else if (normalizedInput.endsWith(".")) {
                                        normalizedInput.let { inp ->
                                            if (inp.toDouble() % 1.0 == 0.0) inp.toDouble() else 0.0
                                        }
                                    } else {
                                        normalizedInput.toDoubleOrNull() ?: 0.0
                                    }
                                }
                            }, onImage = {}, true
                        ) {
                        }
                        if(false){
                            Row {
                                Checkbox(checked = isDiscounted,
                                    onCheckedChange = {
                                        isDiscounted = !isDiscounted
                                    })
                                Text(text = "does this article have abality to discount?")
                            }
                        }
                    }
                }
            }
            item{
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 10.dp)
                ) {
                    Text(text = "Visibility")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp)
                ) {
                    RadioButtons { selectedPrivacy ->
                        privacy = selectedPrivacy
                    }
                }
                Row {
                    val unitList = UnitArticle.entries
                    unitItem = dropDownItems(unitItem,list = unitList)
                }

                Row {
                    DropDownCategory(category,pagingItems = categories){
                        category = it
                    }
                }

                Row {

                        DropDownSubCategory(subCategory ,list = subcategories,
                            category.id?:0
                        ){
                            subCategory = it
                        }

                }
                Row {
                    DropDownCompany(provider = provider, list = providers){
                        provider = it
                    }
                }
                if(false) {
                ButtonSubmit(labelValue = "add photo", color = Color.Cyan, enabled = true) {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = image,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        ButtonSubmit(labelValue = "Cancel", color = Color.Red, enabled = true) {
                            appViewModel.updateShow("article")
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        ButtonSubmit(labelValue = "Submit", color = Color.Green, enabled = true) {
                            articleCompany.quantity = qte
                            articleCompany.minQuantity = minQte
                            articleCompany.cost = cost
                            articleCompany.sellingPrice = sellingPrice
                            articleCompany.category = category
                            articleCompany.subCategory = subCategory
                            articleCompany.provider = provider
                            articleCompany.unit = unitItem
                            articleCompany.isVisible = privacy
                            articleCompany.article = article
                          //  articleCompany.company = company
                            val photo = resolveUriToFile(image, context)
                            val articleJsonString = gson.toJson(articleCompany)
                            val projsonstring = gson.toJson(companyViewModel.myCompany)
                            val arstring = articleJsonString+projsonstring
                            if(!articleViewModel.upDate) {
                                if (articleJsonString.isNotEmpty() && photo != null && article.id != null) {
//                                articleViewModel.addArticle(articleCompany,articleJsonString, photo)
                                } else {
                                    articleViewModel.addArticleCompany(
                                        articleCompany
                                    )
                                }
                            }else{
                                articleViewModel.updateArticle(articleCompany)
                            }
                        }
                    }

                }
            }
            }
        }
    }
}