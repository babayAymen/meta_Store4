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
import coil.compose.AsyncImage
import com.aymen.metastore.R
import com.aymen.metastore.model.entity.model.Category
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.InputTextField
import com.aymen.metastore.ui.component.resolveUriToFile
import com.google.gson.Gson

@Composable
fun AddCategoryScreen() {
    val gson = Gson()
    val context = LocalContext.current
    val appViewModel : AppViewModel = hiltViewModel()
    val categoryViewModel : CategoryViewModel = hiltViewModel()
    val category = Category()
    val update = categoryViewModel.update
    val categoryText = stringResource(id = R.string.category)
    var libelle by remember {
        mutableStateOf("")
    }
    var code by remember {
        mutableStateOf("")
    }
    var image by remember {
        mutableStateOf<Uri?>(null)
    }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri -> image = uri }
    )
    val categoryForUpdate = categoryViewModel.categoryForUpdate.collectAsStateWithLifecycle()
    if(update){
        libelle = categoryForUpdate.value.libelle?:""
        code = categoryForUpdate.value.code?:""
    }
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
                ButtonSubmit(labelValue = stringResource(id = R.string.add_photo), color = Color.Cyan, enabled = true) {
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
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        ButtonSubmit(labelValue = stringResource(id = R.string.cancel), color = Color.Red, enabled = true) {
                            appViewModel.updateShow(categoryText)
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        ButtonSubmit(labelValue = stringResource(id = R.string.submit), color = Color.Green, enabled = true) {
                            category.libelle = libelle
                            category.code = code
                            if(update) category.id = categoryForUpdate.value.id
                            val photo = resolveUriToFile(image, context)
                            val categoryJsonString = gson.toJson(category)
                            if (categoryJsonString.isNotEmpty() && photo != null)
                                if(update) categoryViewModel.updateCategory(category, categoryJsonString, photo)
                                else categoryViewModel.addCtagory(category,categoryJsonString, photo)
                            else if(update) categoryViewModel.updateCategory(category, categoryJsonString, null)
                                else categoryViewModel.addCtagory(category,categoryJsonString, null)
                            categoryViewModel.update = false
                            appViewModel.updateShow(categoryText)
                        }
                    }

                }
            }
        }
    }
}