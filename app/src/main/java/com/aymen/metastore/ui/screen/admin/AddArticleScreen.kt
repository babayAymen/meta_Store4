package com.aymen.store.ui.screen.admin

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
import androidx.compose.material3.Button
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.Enum.UnitArticle
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.SubCategory
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ArticleViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.store.model.repository.ViewModel.CompanyViewModel
import com.aymen.store.model.repository.ViewModel.SubCategoryViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.CheckBoxComponent
import com.aymen.store.ui.component.DropDownCategory
import com.aymen.store.ui.component.DropDownCompany
import com.aymen.store.ui.component.DropDownSubCategory
import com.aymen.store.ui.component.RadioButtons
import com.aymen.store.ui.component.dropDownItems
import com.aymen.store.ui.component.InputTextField
import com.aymen.store.ui.component.resolveUriToFile
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.google.gson.Gson
import com.google.gson.GsonBuilder

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
        val subCategoryViewModel : SubCategoryViewModel = hiltViewModel()
        val articleViewModel : ArticleViewModel = hiltViewModel()
        val article = articleViewModel.article
        var image by remember {
            mutableStateOf<Uri?>(null)
        }
        val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = {uri -> image = uri }
        )
        LaunchedEffect(Unit){
            categoryViewModel.getAllCategoryByCompany()
            companyViewModel.getAllMyProvider()
        }
        val categories = categoryViewModel.categories
        LaunchedEffect(categoryViewModel.category) {
            // Reload subcategories when categoryId changes
            categoryViewModel.category.id?.let { subCategoryViewModel.insertsubcategory(it) }
            categoryViewModel.category.id?.let {
                subCategoryViewModel.getAllSubCtaegoriesByCategory(
                    it
                )
            }
        }
        val subcategories = subCategoryViewModel.subCategories
        val context = LocalContext.current
        val articleCompany = ArticleCompany()
        val gson = Gson()
        var tva by remember {
            mutableDoubleStateOf(0.0)
        }
        var quantity by remember {
            mutableDoubleStateOf(0.0)
        }
        var minQuantity by remember {
            mutableDoubleStateOf(0.0)
        }
        var cost by remember {
            mutableDoubleStateOf(0.0)
        }
        var sellingPrice by remember {
            mutableDoubleStateOf(0.0)
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
        var subCategory by remember {
            mutableStateOf(SubCategory())
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(1) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {

                    if (article.id == null) {
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
                            labelValue = if (quantity == 0.0) "" else quantity.toString(),
                            label = "quantity",
                            singleLine = true,
                            maxLine = 1,
                            KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            onValueChange = {
                                quantity = it.toDouble()
                            }, onImage = {}, true
                        ) {

                        }
                        InputTextField(
                            labelValue = if (minQuantity == 0.0) "" else minQuantity.toString(),
                            label = "minQuantity",
                            singleLine = true,
                            maxLine = 1,
                            KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            onValueChange = {
                                minQuantity = it.toDouble()
                            }, onImage = {}, true
                        ) {

                        }
                        InputTextField(
                            labelValue = if (cost == 0.0) "" else cost.toString(),
                            label = "cost",
                            singleLine = true,
                            maxLine = 1,
                            KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            onValueChange = {
                                cost = it.toDouble()
                            }, onImage = {}, true
                        ) {

                        }
                        InputTextField(
                            labelValue = if (sellingPrice == 0.0) "" else sellingPrice.toString(),
                            label = "selling price",
                            singleLine = true,
                            maxLine = 1,
                            KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            onValueChange = {
                                sellingPrice = it.toDouble()
                            }, onImage = {}, true
                        ) {
                        }
                        if (article.id == null) {
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
                    unitItem = dropDownItems(list = unitList)
                }

                Row {
                    DropDownCategory(list = categories)
                }

                Row {
                    categoryViewModel.category.id?.let { it1 ->
                        DropDownSubCategory(list = subcategories,
                            it1
                        ){
                            subCategory = it
                        }
                    }
                }
                Row {
                    DropDownCompany(list = companyViewModel.providers)
                }
                if(article.id == null) {
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
                            articleCompany.quantity = quantity
                            articleCompany.minQuantity = minQuantity
                            articleCompany.cost = cost
                            articleCompany.sellingPrice = sellingPrice
                            articleCompany.category?.id = categoryViewModel.category.id
                            articleCompany.subCategory?.id = subCategory.id
                            articleCompany.provider?.id = companyViewModel.myCompany.id
                            articleCompany.unit = unitItem.toString()
                            articleCompany.isVisible = privacy.toString()
                            val photo = resolveUriToFile(image, context)
                            val articleJsonString = gson.toJson(articleCompany)
                            val projsonstring = gson.toJson(companyViewModel.myCompany)
                            val arstring = articleJsonString+projsonstring
                            if (articleJsonString.isNotEmpty() && photo != null && article.id != null) {
//                                articleViewModel.addArticle(articleCompany,articleJsonString, photo)
                            } else {
                                articleViewModel.addArticleWithoutImage(articleCompany,articleJsonString)
                            }
                            appViewModel.updateShow("article")
                        }
                    }

                }
            }
            }
        }
    }
}