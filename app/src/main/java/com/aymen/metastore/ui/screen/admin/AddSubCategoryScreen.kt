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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.aymen.metastore.R
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.model.entity.dto.SubCategoryDto
import com.aymen.metastore.model.entity.model.Category
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.metastore.model.repository.ViewModel.SubCategoryViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.DropDownCategory
import com.aymen.metastore.ui.component.InputTextField
import com.aymen.metastore.ui.component.resolveUriToFile
import com.google.gson.Gson

@Composable
fun AddSubCategoryScreen() {
    val gson = Gson()
    val context = LocalContext.current
    val appViewModel : AppViewModel = hiltViewModel()
    val categoryViewModel : CategoryViewModel = hiltViewModel()
    val subCategoryViewModel : SubCategoryViewModel = hiltViewModel()
    val categories = categoryViewModel.companyCategories.collectAsLazyPagingItems()
    val subCategoryForUpdate by subCategoryViewModel.subCategoryForUpdate.collectAsStateWithLifecycle()
    val subCategory = SubCategory()
    val update = subCategoryViewModel.update
    var libelle by remember {
        mutableStateOf("")
    }
    var code by remember {
        mutableStateOf("")
    }
    var category by remember {
        mutableStateOf(Category())
    }
    var image by remember {
        mutableStateOf<Uri?>(null)
    }
    DisposableEffect(key1 = Unit) {
        onDispose {
            subCategoryViewModel.update = false
        }
    }
    if(update){
        libelle = subCategoryForUpdate.libelle?:""
        code = subCategoryForUpdate.code?:""
        category = subCategoryForUpdate.category?:Category()
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri -> image = uri }
    )
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {

            InputTextField(
                labelValue = libelle,
                label = stringResource(id = R.string.label),
                singleLine = true,
                maxLine = 1,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                onValueChange = {
                    libelle = it
                }
                , onImage = {}
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
                }
                , onImage = {}
            ) {

            }
            Row {
                DropDownCategory(category,categories ){
                    category = it
                }
            }
            ButtonSubmit(labelValue = stringResource(id = R.string.add_photo), color = Color.Cyan, enabled = true) {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
            Row (
                modifier = Modifier.fillMaxWidth()
            ){
                AsyncImage(
                    model = image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
                val navText = stringResource(id = R.string.subcategory)
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
                Column (
                    modifier = Modifier.weight(1f)
                ){

                    ButtonSubmit(labelValue = stringResource(id = R.string.submit), color = Color.Green, enabled = true){
                        subCategory.libelle = libelle
                        subCategory.code = code
                        subCategory.category = category
                        if(update){
                            subCategory.id = subCategoryForUpdate.id
                        }
                        val photo =  resolveUriToFile(image, context)
                        val subCategoryJsonString = gson.toJson(subCategory)
                        if (subCategoryJsonString.isNotEmpty() && photo != null)
                            if(update) subCategoryViewModel.updateSubCategory(subCategory, subCategoryJsonString, photo)
                            else subCategoryViewModel.addSubCategory(subCategory,subCategoryJsonString,photo)
                        else
                           if(update) subCategoryViewModel.updateSubCategory(subCategory, subCategoryJsonString, null)
                            else subCategoryViewModel.addSubCategory(subCategory,subCategoryJsonString, null)
                        subCategoryViewModel.update = false
                        appViewModel.updateShow(navText)
                    }
                }

            }

            }
        }
    }
}
