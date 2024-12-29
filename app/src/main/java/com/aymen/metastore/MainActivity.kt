package com.aymen.metastore

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.hilt.navigation.compose.hiltViewModel
import com.aymen.metastore.dependencyInjection.AccountTypeDtoSerializer
import com.aymen.metastore.dependencyInjection.CompanyDtoSerializer
import com.aymen.metastore.dependencyInjection.NetworkUtil
import com.aymen.metastore.dependencyInjection.UserDtoSerializer
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.app.MetaStore
import com.aymen.store.dependencyInjection.TokenSerializer
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
import com.aymen.metastore.model.webSocket.ChatClient
import com.aymen.metastore.model.webSocket.WebSocketViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.ui.theme.MetaStoreTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

val Context.datastore: DataStore<AuthenticationResponse> by dataStore("setting.json", TokenSerializer)
val Context.companydtodatastore: DataStore<Company> by dataStore("companydto.json", CompanyDtoSerializer)
val Context.userdtodatastore: DataStore<User> by dataStore("userdto.json", UserDtoSerializer)
val Context.accounttypedtodatastore: DataStore<AccountType> by dataStore("accounttype.json", AccountTypeDtoSerializer)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    lateinit var chatClient: ChatClient

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkUtil.isOnline(this)
        val languageCode = getSavedLanguage(this)?: "en"
        setLanguage(this, languageCode)
        enableEdgeToEdge()
        setContent {
            MetaStoreTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "META",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }



}
private fun getSavedLanguage(context: Context): String?{
    val sharedPreferences = context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("language",null)
}

fun setLanguage(context: Context, languageCode: String){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        context.getSystemService(LocaleManager::class.java)
            .applicationLocales = LocaleList.forLanguageTags(languageCode)
    }else{
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }
    saveLanguage(context, languageCode)
}


private fun saveLanguage(context: Context, languageCode: String){
    val sharedPreferences = context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("language", languageCode).apply()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
     val socket : WebSocketViewModel = hiltViewModel()
    Column {
        MetaStore()
//            socket.connectToWebSocket()
    }
}


@Composable
fun LanguageSwither() {
    val context = LocalContext.current
    Column {
        Button(onClick = { setLanguage(context, "en") }) {
            Text(text = "English")
        }
        Button(onClick = {
            setLanguage(context, "ar-TN")
            (context as? Activity)?.recreate()
        }) {
            Text(text = "Tunisian")
        }
        Button(onClick = {
            setLanguage(context, "ar-PS")
            (context as? Activity)?.recreate()
        }) {
            Text(text = "Arabic")
        }
        Button(onClick = {
            setLanguage(context, "fr")
            (context as? Activity)?.recreate() }) {
            Text(text = "Fransh")
        }
    }
}














