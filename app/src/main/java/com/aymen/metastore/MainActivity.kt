package com.aymen.metastore

import android.Manifest
import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.aymen.metastore.app.MetaStore
import com.aymen.metastore.dependencyInjection.AccountTypeDtoSerializer
import com.aymen.metastore.dependencyInjection.CompanyDtoSerializer
import com.aymen.metastore.dependencyInjection.NetworkUtil
import com.aymen.metastore.dependencyInjection.UserDtoSerializer
import com.aymen.metastore.model.entity.dto.AuthenticationResponse
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.NotificationMessage
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.webSocket.ChatClient
import com.aymen.store.dependencyInjection.TokenSerializer
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.ui.theme.MetaStoreTheme
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
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

    private var extrasState = mutableStateOf<Map<String, Any?>>(emptyMap())
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkUtil.isOnline(this)
        val languageCode = getSavedLanguage(this)?: "en"
        setLanguage(this, languageCode)
        requestNotificationPermission()
        FirebaseInstallations.getInstance().id
            .addOnCompleteListener { task: Task<String> ->
                if (task.isSuccessful) {
                    val fid = task.result
                    // Use this FID as a unique identifier for the device
                    Log.d("FirebaseInstallationID", "FID: $fid")
                } else {
                    Log.e("FirebaseInstallationID", "Failed to retrieve FID", task.exception)
                }
            }

        enableEdgeToEdge()
        setContent {
            MetaStoreTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "META",
                        extrasState = extrasState.value,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        updateExtras(intent)
    }

    private fun updateExtras(intent: Intent) {
        val extras = intent.extras
        if (extras != null) {
            val map = extras.keySet().associateWith { key ->
                 extras.getString(key)
            }
            extrasState.value = map
        } else {
            extrasState.value = emptyMap()
        }
    }

    private fun requestNotificationPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            val hasPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if(!hasPermission){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
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
fun Greeting(name: String,extrasState :  Map<String, Any?> , modifier: Modifier = Modifier) {
    Column {
        val notificationMessage = extrasState["notificationMessage"] as? NotificationMessage
        Log.e("device","balance from main activity $notificationMessage")

        MetaStore(extrasState)
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
            Text(text = "French")
        }
    }
}














