
package com.aymen.metastore.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.metastore.model.Enum.RateType
import com.aymen.metastore.model.entity.api.RatingDto
import com.aymen.metastore.model.repository.ViewModel.RatingViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.model.entity.api.CompanyDto
import com.aymen.store.model.entity.realm.Company
import com.aymen.metastore.model.entity.realm.User
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.ui.component.InputTextField
import com.google.gson.Gson
import android.net.Uri
import coil.compose.AsyncImage
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.ui.component.ShowImage

@Composable
fun RatingScreen(mode: AccountType, company: Company?, user: User?) {

    val current = LocalContext.current
    var height = LocalConfiguration.current.screenHeightDp.dp
    val ratingViewModel: RatingViewModel = viewModel()
    val appViewModel: AppViewModel = viewModel()
    var id by remember { mutableLongStateOf(0) }
    val gson = Gson()

    LaunchedEffect(Unit) {
        id = if (company != null) {
            company.id!!
        } else {
            user?.id!!
        }
        ratingViewModel.getAllRating(id, mode)
    }
    DisposableEffect(Unit) {
        onDispose {
        ratingViewModel.allRating = emptyList()
            ratingViewModel.rating = false
        }
    }

    var comment by remember { mutableStateOf("") }
    var rating by remember {
        mutableStateOf(
            RatingDto(
                rateeCompany = CompanyDto(),
                rateeUser = com.aymen.store.model.entity.api.UserDto()
            )
        )
    }
    var imageHeight by remember { mutableStateOf(0.dp) }
    var imageBitmap by remember { mutableStateOf<Uri?>(null) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp)
            ) {
                items(ratingViewModel.allRating) {
                    Text(
                        text = it.rateeCompany?.name ?: it.rateeUser?.username ?: "no company name"
                    )
                    Text(text = it.raterUser?.username ?: it.raterCompany?.name ?: "no name")
                    Text(text = it.comment ?: "no comment")
                    ShowImage(image = "${BASE_URL}werehouse/image/${it.photo}/rating/${it.raterCompany?.user?.id?:it.raterUser?.id}")
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                imageBitmap?.let {
                    Row {

                        AsyncImage(
                            model = imageBitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(bottom = 8.dp)
                        )
                        Text(text = "x")
                    }
                }
                if (ratingViewModel.rating) {
                    InputTextField(
                        labelValue = comment,
                        label = "Add a comment",
                        singleLine = false,
                        maxLine = 6,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Send
                        ),
                        onValueChange = {
                            comment = it
                        },
                        onImage = { bitmap ->
                            imageHeight = 100.dp
                            imageBitmap = bitmap
                        }
                    ) {
                        rating.comment = comment
                        if (mode == AccountType.COMPANY) {
                            if (appViewModel.userRole == RoleEnum.ADMIN) {
                                rating.type = RateType.COMPANY_RATE_COMPANY
                                rating.rateeCompany?.id = company?.id
                            } else {
                                rating.type = RateType.USER_RATE_COMPANY
                                rating.rateeCompany?.id = company?.id
                            }
                        } else {
                            if (appViewModel.userRole == RoleEnum.ADMIN) {
                                rating.type = RateType.COMPANY_RATE_USER
                                rating.rateeUser?.id = user?.id
                            } else {
                                rating.type = null
                            }
                        }
                        rating.rateValue = ratingViewModel.rate
                        val ratingJson = gson.toJson(rating)
                        ratingViewModel.doRate(ratingJson, it)
                        comment = ""
                        imageBitmap = null
                    }
                }
            }
        }
    }
}
