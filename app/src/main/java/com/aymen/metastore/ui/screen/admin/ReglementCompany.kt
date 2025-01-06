package com.aymen.metastore.ui.screen.admin

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.model.Enum.SearchPaymentEnum
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.metastore.ui.component.SearchField

@Composable
fun ReglementCompany(modifier: Modifier = Modifier) {
    val invoiceViewModel = hiltViewModel<InvoiceViewModel>()
    var searchPayment by remember {
        mutableStateOf(SearchPaymentEnum.CODE)
    }
    var searchText by remember {
        mutableStateOf("")
    }
    val invoices = invoiceViewModel.invoiceForSearch.collectAsLazyPagingItems()

    Column {
        DropDownSearchPayment {
            searchPayment = it
        }
        SearchField(label = "search",
            labelValue = searchText,
            value = {
                searchText = it
            }
        ){
            invoiceViewModel.searchInvoice(searchPayment, searchText)
        }
        LazyColumn {
            items(count = invoices.itemCount,
                key = invoices.itemKey { it.id!! }) { index ->
                val invoice = invoices[index]
                if (invoice != null) {
                    Text(text = invoice.code.toString())
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownSearchPayment(onSelect: (SearchPaymentEnum) -> Unit) {

    val type = SearchPaymentEnum.entries
    var itemSelected by remember {
        mutableStateOf(type[0])
    }
    var isExpanded by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier.wrapContentHeight()
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = !isExpanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    value = itemSelected.toString(),
                    onValueChange = {
                        onSelect(itemSelected)
                    },
                    readOnly = true,
                    colors =  TextFieldDefaults.colors(
                        focusedLabelColor = Color.Black,
                        cursorColor = Color.Magenta
                    ),
//                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) } this to display the icon at the end
                    leadingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }

                )

                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }) {
                    type.forEachIndexed { index, text ->
                        DropdownMenuItem(
                            text = { Text(text.toString()) },
                            onClick = {
                                itemSelected = type[index]
                                onSelect(itemSelected)
                                isExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
}
