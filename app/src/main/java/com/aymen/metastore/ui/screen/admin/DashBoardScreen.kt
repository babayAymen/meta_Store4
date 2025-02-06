package com.aymen.metastore.ui.screen.admin

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aymen.metastore.R
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.store.model.Enum.IconType
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.repository.ViewModel.ShoppingViewModel
import com.aymen.metastore.ui.component.Item
import com.aymen.metastore.ui.screen.user.AddCompanyScreen
import com.aymen.metastore.ui.screen.user.PaymentScreen
import com.aymen.metastore.ui.screen.user.ShoppingScreen
import com.aymen.metastore.util.ADD_ARTICLE
import com.aymen.metastore.util.ADD_ARTICLE_FOR_COMPANY
import com.aymen.metastore.util.ADD_CATEGORY
import com.aymen.metastore.util.ADD_CLIENT
import com.aymen.metastore.util.ADD_INVOICE
import com.aymen.metastore.util.ADD_PROVIDER
import com.aymen.metastore.util.ADD_SUBCATEGORY
import com.aymen.metastore.util.ALL_HISTORY
import com.aymen.metastore.util.ARTICLE
import com.aymen.metastore.util.BUY_HISTORY
import com.aymen.metastore.util.CLIENT
import com.aymen.metastore.util.DASH
import com.aymen.metastore.util.INVENTORY
import com.aymen.metastore.util.INVOICE
import com.aymen.metastore.util.ORDER
import com.aymen.metastore.util.PAYMENT
import com.aymen.metastore.util.PROVIDER
import com.aymen.metastore.util.SUBCATEGORY
import com.aymen.metastore.util.UPDATE
import com.aymen.metastore.util.WORKER
import com.aymen.metastore.util.article
import com.aymen.metastore.util.category
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.ui.screen.admin.AddSubCategoryScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashBoardScreen() {
    val viewModel : AppViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val user by sharedViewModel.user.collectAsStateWithLifecycle()
    val show by viewModel.show
    when (show) {
        DASH -> {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .background(color = Color.White)
            ) {
                val items = listOf(
                    NavigationItem(R.string.article, ARTICLE),
                    NavigationItem(R.string.client, CLIENT),
                    NavigationItem(R.string.provider, PROVIDER),
                    NavigationItem(R.string.payment, PAYMENT),
                    NavigationItem(R.string.category, category),
                    NavigationItem(R.string.subcategory, SUBCATEGORY),
                    NavigationItem( R.string.order, ORDER),
                    NavigationItem(R.string.invoice, INVOICE),
                    NavigationItem(R.string.inventory, INVENTORY)
                )+ if (user.role != RoleEnum.WORKER) {
                listOf(
                    NavigationItem(R.string.worker, WORKER),
                    NavigationItem( R.string.update, UPDATE),
                    NavigationItem(R.string.parent,"parent")
                    )
            }else{
                emptyList()
                }

                val screenWidthDp = screenWidth()
                val itemWidthDp = 110.dp
                val itemCountPerRow = (screenWidthDp / itemWidthDp.value).toInt()
                val rows = items.chunked(itemCountPerRow)
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(rows) { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            rowItems.forEach { item ->
                                Item(label = item)
                            }
                        }
                    }
                }
            }
        }
        ADD_ARTICLE -> {
            ArticlesScreenForCompanyByCategory()
        }
        ADD_ARTICLE_FOR_COMPANY -> {
            AddArticleScreen()
        }
        article -> {
            ArticleScreen()
        }
        category -> {
            CategoryScreen()
        }
        ADD_CATEGORY -> {
            AddCategoryScreen()
        }
        SUBCATEGORY -> {
            SubCategoryScreen()
        }
        ADD_SUBCATEGORY -> {
            AddSubCategoryScreen()
        }
        CLIENT -> {
            ClientScreen()
        }
        ADD_CLIENT -> {
            AddClientScreen()
        }
        INVENTORY -> {
            InventoryScreen()
        }
        PROVIDER -> {
            ProviderScreen()
        }
        ADD_PROVIDER -> {
            AddProviderScreen()
        }
        PAYMENT -> {
            viewModel.updateScreen(IconType.WALLET)
            if(user.role == RoleEnum.WORKER){
                viewModel.updateShow(BUY_HISTORY)
                viewModel.updateView(ALL_HISTORY)
            }else viewModel.updateShow(PAYMENT)
            PaymentScreen()
        }
        ORDER -> {
            viewModel.updateScreen(IconType.SHOPPING)
            ShoppingScreen()
        }
            WORKER -> {
        if(user.role != RoleEnum.WORKER) {
                WorkerScreen()
            }
        }
            "parent" -> {
                if (user.role != RoleEnum.WORKER) {
                    ParentScreen()
                }
            }
            UPDATE -> {
                if (user.role != RoleEnum.WORKER) {
                    AddCompanyScreen(update = true)
                }
            }
        INVOICE -> {
            InvoiceScreenAsProvider()
        }
        ADD_INVOICE -> {
            AddInvoiceScreen()
        }


    }

}

data class NavigationItem(
    val labelResId: Int,
    val destination: String // or any other relevant property
)

@Composable
fun screenWidth(): Int {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp
}

