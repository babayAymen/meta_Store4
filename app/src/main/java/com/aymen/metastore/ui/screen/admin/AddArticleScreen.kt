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
        val article = articleViewModel.article
        var image by remember {
            mutableStateOf<Uri?>(null)
        }
        val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = {uri -> image = uri }
        )
        val categories = categoryViewModel.categories.collectAsLazyPagingItems()

        LaunchedEffect(categoryViewModel.category) {

        }
        val subcategories = subCategoryViewModel.subCategories.collectAsLazyPagingItems()
        val context = LocalContext.current
        var articleCompany = ArticleCompany()

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
        if(articleViewModel.upDate){
            val articlee = articleViewModel.articleCompany.collectAsStateWithLifecycle()
            Log.e("articleCompany","from view model : $articlee")
            articleCompany = articlee.value!!
            quantity = articlee.value?.quantity?:0.0
            minQuantity = articlee.value?.minQuantity?:0.0
            cost = articlee.value?.cost?:0.0
            sellingPrice = articlee.value?.sellingPrice?:0.0
            unitItem = articlee.value?.unit?:UnitArticle.U
            privacy = articlee.value?.isVisible?:PrivacySetting.PUBLIC
            subCategory = articlee.value?.subCategory?:SubCategory()
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
                    DropDownCategory(pagingItems = categories)
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
                    DropDownCompany(list = providers)
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
                            articleCompany.quantity = quantity
                            articleCompany.minQuantity = minQuantity
                            articleCompany.cost = cost
                            articleCompany.sellingPrice = sellingPrice
                            articleCompany.category?.id = categoryViewModel.category.id
                            articleCompany.subCategory?.id = subCategory.id
                            articleCompany.provider?.id = sharedViewModel.company.value.id
                            articleCompany.unit = unitItem
                            articleCompany.isVisible = privacy
                            val photo = resolveUriToFile(image, context)
                            val articleJsonString = gson.toJson(articleCompany)
                            val projsonstring = gson.toJson(companyViewModel.myCompany)
                            val arstring = articleJsonString+projsonstring
                            if(!articleViewModel.upDate) {
                                if (articleJsonString.isNotEmpty() && photo != null && article.id != null) {
//                                articleViewModel.addArticle(articleCompany,articleJsonString, photo)
                                } else {
                                    articleViewModel.addArticleWithoutImage(
                                        articleCompany,
                                        articleJsonString
                                    )
                                }
                            }else{
                                Log.e("articleCompany","article company : $articleCompany")
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