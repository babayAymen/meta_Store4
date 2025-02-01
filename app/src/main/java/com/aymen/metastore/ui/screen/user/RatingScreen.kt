
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.Rating
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.ui.component.DividerComponent
import com.aymen.metastore.ui.component.NotImage
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.metastore.util.BASE_URL
import com.aymen.metastore.util.IMAGE_URL_COMPANY
import com.aymen.metastore.util.IMAGE_URL_RATING
import com.aymen.metastore.util.IMAGE_URL_USER
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen

@Composable
fun RatingScreen(
    accountType: AccountType,
    company: Company?,
    user: User?,
    modifier: Modifier = Modifier
) {
    val ratingViewModel : RatingViewModel = hiltViewModel() // Assume a ViewModel is available for ratings
    val allRating = ratingViewModel.allRating.collectAsLazyPagingItems()
    val ratings = allRating.itemSnapshotList.items
    var id by remember { mutableLongStateOf(0) }
        LaunchedEffect(Unit) {
        id = if (company != null) {
            company.id!!
        } else {
            user?.id!!
        }
            ratingViewModel.getAllRating(id, accountType)
    }
    DisposableEffect(key1 = Unit) {
        onDispose { ratingViewModel.rating = false }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        ratings.forEach { rating ->
            RatingItem(rating = rating)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun RatingItem(rating: Rating) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // User or Company details
        rating.raterUser?.let { user ->
            Text(text = user.username?:"", style = MaterialTheme.typography.bodySmall)
        }
        rating.raterCompany?.let { company ->
            Text(text = company.name, style = MaterialTheme.typography.bodySmall)
        }

        // Rating Comment
        Text(text = rating.comment ?: "", style = MaterialTheme.typography.bodySmall)

        // Rating Photo (if available)
        rating.photo?.let { photo ->
            AsyncImage(
                model = String.format(IMAGE_URL_RATING, photo, rating.raterUser?.id),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp)
            )
        }
    }
}
