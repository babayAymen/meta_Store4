
package com.aymen.metastore.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.aymen.metastore.model.Enum.RateType
import com.aymen.metastore.model.repository.ViewModel.RatingViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.ui.component.InputTextField
import com.google.gson.Gson
import android.net.Uri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.Rating
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.dependencyInjection.BASE_URL
import com.aymen.metastore.ui.component.ShowImage

@Composable
fun RatingScreen(mode: AccountType, company: Company?, user: User?) {

    val current = LocalContext.current
    var height = LocalConfiguration.current.screenHeightDp.dp
    val ratingViewModel: RatingViewModel = hiltViewModel()
    val appViewModel: AppViewModel = hiltViewModel()
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
            ratingViewModel._allRating.value = PagingData.empty()
            ratingViewModel.rating = false
        }
    }
    val allRating = ratingViewModel.allRating.collectAsLazyPagingItems()

    var comment by remember { mutableStateOf("") }
    val rating by remember {
        mutableStateOf(
            Rating(
                rateeCompany = Company(),
                rateeUser = User()
            )
        )
    }
    var imageHeight by remember { mutableStateOf(0.dp) }
    var imageBitmap by remember { mutableStateOf<Uri?>(null) }

    Surface(
        modifier = Modifier
            .padding(3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
            ) {
                items(allRating.itemCount) { index ->
                    val rate = allRating[index]
                    rate?.raterUser?.let { user ->
                        ShowImage(image = "${BASE_URL}werehouse/image/${user.image}/user/${user.id}")
                    }
                    rate?.raterCompany?.let { company ->
                        ShowImage(image = "${BASE_URL}werehouse/image/${company.logo}/company/${company.user?.id}")
                    }
                    Text(text = rate?.raterUser?.username ?: rate?.raterCompany?.name ?: "")
                    Text(text = rate?.comment ?: "")
                      ShowImage(image = "${BASE_URL}werehouse/image/${rate?.photo}/rating/${rate?.raterCompany?.user?.id?:rate?.raterUser?.id}")
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color.White)
                    .fillMaxWidth()
            ) {
//                imageBitmap?.let {
//                    Row {
//
//                        AsyncImage(
//                            model = imageBitmap,
//                            contentDescription = null,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(200.dp)
//                                .padding(bottom = 8.dp)
//                        )
//                        Text(text = "x")
//                    }
//                }
                if (ratingViewModel.rating && ratingViewModel.enableToRating)
                    Column {
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
                            ratingViewModel.enableToRating = false
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

