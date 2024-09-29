package com.aymen.metastore

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.aymen.metastore.model.Location.LocationService
import com.aymen.metastore.model.Location.hasLocationPermission
import com.aymen.store.app.MetaStore
import com.aymen.store.dependencyInjection.CompanySerializer
import com.aymen.store.dependencyInjection.TokenSerializer
import com.aymen.store.dependencyInjection.UserSerializer
import com.aymen.store.model.entity.api.AuthenticationResponse
import com.aymen.store.model.entity.realm.Company
import com.aymen.metastore.model.entity.realm.User
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.ui.theme.MetaStoreTheme
import dagger.hilt.android.AndroidEntryPoint

val Context.datastore: DataStore<AuthenticationResponse> by dataStore("setting.json", TokenSerializer)
val Context.datastore1: DataStore<Company> by dataStore("company.json", CompanySerializer)
val Context.userdatastore: DataStore<User> by dataStore("user.json", UserSerializer)
//val Context.accounttypedatastore: DataStore<AccountType> by dataStore("accounttype.json", AccountTypeSerializer)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MetaStoreTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "AYMEN",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    MetaStore()
}
