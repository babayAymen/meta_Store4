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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.aymen.metastore.R
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
import com.aymen.metastore.ui.component.ArticleDialog
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
fun AddArticleScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(1.dp)
    ) {
        val appViewModel: AppViewModel = hiltViewModel()
        val categoryViewModel: CategoryViewModel = hiltViewModel()
        val companyViewModel: CompanyViewModel = hiltViewModel()
        val sharedViewModel: SharedViewModel = hiltViewModel()
        val subCategoryViewModel: SubCategoryViewModel = hiltViewModel()
        val articleViewModel: ArticleViewModel = hiltViewModel()
        val providerViewModel: ProviderViewModel = hiltViewModel()
        val company by sharedViewModel.company.collectAsStateWithLifecycle()
        val article = articleViewModel.article
        var image by remember {
            mutableStateOf<Uri?>(null)
        }
        val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri -> image = uri }
        )
        val categories = categoryViewModel.companyCategories.collectAsLazyPagingItems()
        val subcategories = subCategoryViewModel.allSubCategories.collectAsLazyPagingItems()
        val providers = providerViewModel.providers.collectAsLazyPagingItems()
        val subArticlesChile = articleViewModel.subArticlesChilds.collectAsLazyPagingItems()
        var category by remember {
            mutableStateOf(Category())
        }
        LaunchedEffect(key1 = Unit) {
            providerViewModel.isAll = false
            providerViewModel.getAllMyProviders()
            categoryViewModel.setFilter(company.id ?: 0)
        }
        LaunchedEffect(key1 = category) {
            subCategoryViewModel.getAllSubCategoriesByCategoryId(
                category.id ?: 0,
                sharedViewModel.company.value.id ?: 0
            )
        }
        DisposableEffect(key1 = Unit) {
            onDispose {
                articleViewModel.remiseAZeroSubArticle()
            }
        }

        var subCategory by remember {
            mutableStateOf(SubCategory())
        }

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
        var articleId by remember {
            mutableLongStateOf(0L)
        }
        var upDate by remember {
            mutableStateOf(false)
        }
        var isBought by remember {
            mutableStateOf(true)
        }
        if (articleViewModel.upDate) {
            articleViewModel.getArticlesChilds()
            upDate = true
            val articlee by articleViewModel.articleCompany.collectAsStateWithLifecycle()
            articleCompany = articlee!!
            minQuantity =
                if (articlee?.unit == UnitArticle.U) (articlee?.minQuantity ?: 0.0).toInt()
                    .toString() else (articlee?.minQuantity ?: 0.0).toString()
            qte = articlee?.quantity!!
            minQte = articlee?.minQuantity!!
            costFieald = (articlee?.cost ?: 0.0).toString()
            cost = articlee?.cost!!
            sellingPriceFieald = (articlee?.sellingPrice ?: 0.0).toString()
            sellingPrice = articlee?.sellingPrice!!
            unitItem = articlee?.unit!!
            quantity = if (articlee?.unit == UnitArticle.U) (articlee?.quantity ?: 0.0).toInt()
                .toString() else (articlee?.quantity ?: 0.0).toString()
            privacy = articlee?.isVisible ?: PrivacySetting.PUBLIC
            category = articlee?.category ?: Category()
            subCategory = articlee?.subCategory ?: SubCategory()
            provider = articlee?.provider!!
            articleId = articlee?.id!!
            isBought = articlee?.isBought!!
            articleViewModel.upDate = false
        }
        var showArticleDailog by remember {
            mutableStateOf(false)
        }
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
                                label = stringResource(id = R.string.label),
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
                                label = stringResource(id = R.string.code),
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
                                label = stringResource(id = R.string.barcode),
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
                                label = stringResource(id = R.string.description),
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
                                label = stringResource(id = R.string.tva),
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
                            label = stringResource(id = R.string.quantity),
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
                                        } else {
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
                            label = stringResource(id = R.string.minQuantity),
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
                                        } else if (normalizedInput.endsWith(".")) {
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
                        if (isBought)
                            InputTextField(
                                labelValue = costFieald,
                                label = stringResource(id = R.string.cost),
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
                                        cost = if (normalizedInput.startsWith(".")) {
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
                            label = stringResource(id = R.string.selling_price),
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
                                    sellingPrice = if (normalizedInput.startsWith(".")) 0.0
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
                        if (false) {
                            Row {
                                Checkbox(checked = isDiscounted,
                                    onCheckedChange = {
                                        isDiscounted = !isDiscounted
                                    })
                                Text(text = "Is this product bought?")
                            }
                        }
                        Row {
                            Checkbox(checked = isBought,
                                onCheckedChange = {
                                    isBought = !isBought
                                })
                            Text(text = "does this product is bought?")
                        }
                    }
                }
            }
            item {
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
                        Text(text = stringResource(id = R.string.visibility))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp)
                    ) {
                        RadioButtons(privacy) { selectedPrivacy ->
                            privacy = selectedPrivacy
                        }
                    }
                    Row {
                        val unitList = UnitArticle.entries
                        unitItem = dropDownItems(unitItem, list = unitList)
                    }

                    Row {
                        DropDownCategory(category, pagingItems = categories) {
                            category = it
                        }
                    }

                    Row {

                        DropDownSubCategory(
                            subCategory, list = subcategories,
                            category.id ?: 0
                        ) {
                            subCategory = it
                        }

                    }
                    Row {
                        DropDownCompany(provider = provider, list = providers) {
                            provider = it
                        }
                    }
                    if (false) {
                        ButtonSubmit(
                            labelValue = stringResource(id = R.string.add_photo),
                            color = Color.Cyan,
                            enabled = true
                        ) {
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
                            val articleText = stringResource(id = R.string.article)
                            ButtonSubmit(
                                labelValue = stringResource(id = R.string.cancel),
                                color = Color.Red,
                                enabled = true
                            ) {
                                appViewModel.updateShow(articleText)
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            ButtonSubmit(
                                labelValue = stringResource(id = R.string.submit),
                                color = Color.Green,
                                enabled = true
                            ) {
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
                                articleCompany.isBought = isBought
                                if (articleId != 0L) {
                                    articleCompany.id = articleId
                                }
                                val photo = resolveUriToFile(image, context)
                                val articleJsonString = gson.toJson(articleCompany)
                                val projsonstring = gson.toJson(companyViewModel.myCompany)
                                val arstring = articleJsonString + projsonstring

                                if (articleCompany.id == null) {
                                    if (articleJsonString.isNotEmpty() && photo != null && article.id != null) {
//                                articleViewModel.addArticle(articleCompany,articleJsonString, photo)
                                    } else {
                                        articleViewModel.addArticleCompany(
                                            articleCompany
                                        )
                                    }
                                } else {
                                    articleViewModel.updateArticle(articleCompany)
                                }
                            }
                        }

                    }
                }

            }
            if (upDate)
                item {
                    Column {
                        ButtonSubmit(
                            labelValue = stringResource(id = R.string.add_article),
                            color = Color.Green,
                            enabled = upDate
                        ) {
                            showArticleDailog = true
                        }
                        if (showArticleDailog)
                            ArticleDialog(
                                update = false,
                                openDialo = showArticleDailog,
                                asProvider = true,
                                providerId = company.id!!,
                                isSubArticle = true,
                            ) { article, quantity ->
                                showArticleDailog = false
                                articleViewModel.addIdToSubArticleIds(article, quantity)
                            }
                    }
                }
            items(count = subArticlesChile.itemCount,
                key = subArticlesChile.itemKey { it.id!! }
            ) { index ->
                val subArticleChild = subArticlesChile[index]
                if (subArticleChild != null)

                    Row {
                        Text(text = "sub article ${subArticleChild.childArticle?.article?.libelle} with sub relation quantity")
                    }
            }
        }
    }
}