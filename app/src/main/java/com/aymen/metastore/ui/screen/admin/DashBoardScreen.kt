package com.aymen.metastore.ui.screen.admin

import android.os.Build
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.ui.screen.admin.AddSubCategoryScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashBoardScreen() {
    val viewModel : AppViewModel = hiltViewModel()
    val companyViewModel : CompanyViewModel = hiltViewModel()
    val invoiceViewModel : InvoiceViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val user by sharedViewModel.user.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val show by viewModel.show
    val asClient = viewModel.asClient
    when (show) {
        "dash" -> {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .background(color = Color.White)
            ) {
                val items = listOf(
                    "article",
                    "client",
                    "provider",
                    "payment",
                    "category",
                    "subcategory",
                    "order",
                    "invoice",
                    "inventory",
                )+ if (user.role != RoleEnum.WORKER) {
                listOf("worker", "parent", "update")
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
        "ADD_ARTICLE" -> {
            ArticlesScreenForCompanyByCategory()
        }
        "ADD_ARTICLE_FOR_COMPANY" -> {
            AddArticleScreen()
        }
        "article" -> {
            ArticleScreen()
        }
        "category" -> {
            CategoryScreen()
        }
        "add category" -> {
            AddCategoryScreen()
        }
        "subcategory" -> {
            SubCategoryScreen()
        }
        "add subCategory" -> {
            AddSubCategoryScreen()
        }
        "client" -> {
            ClientScreen()
        }
        "add client" -> {
            AddClientScreen()
        }
        "inventory" -> {
            InventoryScreen()
        }
        "provider" -> {
            ProviderScreen()
        }
        "add provider" -> {
            AddProviderScreen()
        }
        "payment" -> {
            viewModel.updateScreen(IconType.WALLET)
            if(user.role == RoleEnum.WORKER){
                viewModel.updateView("buyhistory")
                viewModel.updateShow("allHistory")
            }else viewModel.updateView("payment")
            PaymentScreen()
        }
        "order" -> {
            viewModel.updateScreen(IconType.SHOPPING)
            ShoppingScreen()
        }
            "worker" -> {
        if(user.role != RoleEnum.WORKER) {
                WorkerScreen()
            }
        }
            "parent" -> {
                if (user.role != RoleEnum.WORKER) {
                    ParentScreen()
                }
            }
            "update" -> {
                if (user.role != RoleEnum.WORKER) {
                    AddCompanyScreen(update = true)
                }
            }
        "invoice" -> {
            InvoiceScreenAsProvider(asClient)
        }
        "add invoice" -> {
            AddInvoiceScreen()
        }


    }

}



@Composable
fun screenWidth(): Int {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp
}

