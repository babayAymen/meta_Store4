package com.aymen.metastore.ui.screen.admin

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.R
import com.aymen.metastore.model.entity.model.Worker
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.repository.ViewModel.WorkerViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.NotImage
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.metastore.util.ADD_WORKER
import com.aymen.metastore.util.BASE_URL
import com.aymen.metastore.util.IMAGE_URL_USER
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen


@Composable
fun WorkerScreen() {
    val appViewModel : AppViewModel = hiltViewModel()
    val workerViewModel : WorkerViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val workers = workerViewModel.workers.collectAsLazyPagingItems()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row (
                modifier = Modifier.fillMaxWidth()
            ){

                ButtonSubmit(labelValue = stringResource(id = R.string.add_new_worker), color = Color.Green, enabled = true) { // form vertual worker
                    appViewModel.updateShow(ADD_WORKER)
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth()
            ){
                LazyColumn {
                    items(count = workers.itemCount,
                        key = workers.itemKey{it.id!!}
                        ){index ->
                        val worker = workers[index]
                        if(worker != null) {
                            SwipeToDeleteContainer(
                                worker,
                                onDelete = {
                                    Log.e("aymenbabatdelete", "delete")
                                },
                                onUpdate = {
                                    Log.e("aymenbabatdelete", "delete")

                                }
                            ) {
                                WorkerCard(it, sharedViewModel = sharedViewModel)
                            }
                        }
                    }
                }
            }

        }
    }

}


@Composable
fun WorkerCard(worker: Worker, sharedViewModel: SharedViewModel) {
    Card(

        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .padding(8.dp)
            .clickable {
//                if(worker.is)
                sharedViewModel.setHisUser(worker.user)
                RouteController.navigateTo(Screen.UserScreen)
            }
    ) {
        Column {
            Row {
                if(worker.user.image != null)
                ShowImage(image = String.format(IMAGE_URL_USER, worker.user.image, worker.user.id))
                else
                    NotImage()
                Text(text = worker.user.username!!)
            }
            Row {
                worker.phone?.let { Text(text = stringResource(id = R.string.phone,it)) }
                worker.address?.let { Text(text = stringResource(id = R.string.address, it)) }
                Text(text = stringResource(id = R.string.created_date,worker.createdDate))
            }
        }
    }
}