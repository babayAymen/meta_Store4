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
import com.aymen.store.model.entity.realm.Category
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.InputTextField
import com.aymen.store.ui.component.resolveUriToFile
import com.google.gson.Gson

@Composable
fun AddCategoryScreen() {
    val gson = Gson()
    val context = LocalContext.current
    val appViewModel : AppViewModel = viewModel()
    val categoryViewModel : CategoryViewModel = viewModel()
    val category = Category()
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
                    label = "libel",
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
                    label = "code",
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
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        ButtonSubmit(labelValue = "Cancel", color = Color.Red, enabled = true) {
                            appViewModel.updateShow("category")
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        ButtonSubmit(labelValue = "Submit", color = Color.Green, enabled = true) {
                            category.libelle = libelle
                            category.code = code

                            val photo = resolveUriToFile(image, context)
                            val categoryJsonString = gson.toJson(category)
                            if (categoryJsonString.isNotEmpty() && photo != null) {
                                categoryViewModel.addCtagory(categoryJsonString, photo)
                            } else {
                                categoryViewModel.addCategoryWithoutImage(categoryJsonString)
                            }
                            appViewModel.updateShow("category")
                        }
                    }

                }
            }
        }
    }
}