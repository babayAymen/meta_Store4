
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
import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.Rating
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.ui.component.DividerComponent
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.metastore.util.BASE_URL

@Composable
fun RatingScreen(mode: AccountType, company: Company?, user: User?) {
    val ratingViewModel: RatingViewModel = hiltViewModel()
    val appViewModel: AppViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val accountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
    val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()
    val myUser by sharedViewModel.user.collectAsStateWithLifecycle()
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
                items(count = allRating.itemCount,
                    key = allRating.itemKey{it.id!!}
                ) { index ->
                    val rate = allRating[index]
                    if (rate != null) {
                        rate.raterUser?.let { user ->
                            if(user.image != null) ShowImage(image = "${BASE_URL}werehouse/image/${user.image}/user/${user.id}")
                            else{}
                        }
                        rate.raterCompany?.let { company ->
                            if(company.logo != null) {
                                ShowImage(image = "${BASE_URL}werehouse/image/${company.logo}/company/${company.user?.id}")
                            }else {}
                        }
                        Text(text = rate.raterUser?.username ?: rate.raterCompany?.name ?: "")
                        Text(text = rate.comment ?: "")
                        if(rate.photo != null) ShowImage(image = "${BASE_URL}werehouse/image/${rate.photo}/rating/${rate.raterCompany?.user?.id ?: rate.raterUser?.id}")
                        DividerComponent()
                    }
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
                                if (accountType == AccountType.COMPANY) {
                                    rating.type = RateType.COMPANY_RATE_COMPANY
                                    rating.raterCompany = myCompany
                                } else {
                                    rating.type = RateType.USER_RATE_COMPANY
                                    rating.raterUser = myUser
                                }
                                    rating.rateeCompany = company
                            } else {
                                when (accountType) {
                                    AccountType.COMPANY -> {
                                        rating.type = RateType.COMPANY_RATE_USER
                                        rating.raterCompany = myCompany
                                    }

                                    AccountType.META -> {
                                        rating.type = RateType.META_RATE_USER
                                        rating.raterUser = myUser
                                    }

                                    else ->
                                        rating.type = null
                                }
                                    rating.rateeUser = user
                            }
                            rating.rateValue = ratingViewModel.rate
                            ratingViewModel.enableToRating = false
                            val ratingJson = gson.toJson(rating)
                            ratingViewModel.doRate(rating,ratingJson, it)
                            comment = ""
                            imageBitmap = null
                        }
                    }
            }
        }
    }
}

