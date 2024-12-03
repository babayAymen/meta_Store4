package com.aymen.metastore

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.aymen.metastore.dependencyInjection.AccountTypeDtoSerializer
import com.aymen.metastore.dependencyInjection.CompanyDtoSerializer
import com.aymen.metastore.dependencyInjection.UserDtoSerializer
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.User
import com.aymen.store.app.MetaStore
import com.aymen.store.dependencyInjection.TokenSerializer
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.ui.theme.MetaStoreTheme
import dagger.hilt.android.AndroidEntryPoint

val Context.datastore: DataStore<AuthenticationResponse> by dataStore("setting.json", TokenSerializer)
val Context.companydtodatastore: DataStore<Company> by dataStore("companydto.json", CompanyDtoSerializer)
val Context.userdtodatastore: DataStore<User> by dataStore("userdto.json", UserDtoSerializer)
val Context.accounttypedtodatastore: DataStore<AccountType> by dataStore("accounttype.json", AccountTypeDtoSerializer)

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
