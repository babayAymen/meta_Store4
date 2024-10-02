package com.aymen.store.ui.component

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aymen.metastore.R
import com.aymen.metastore.model.Enum.MessageType
import com.aymen.metastore.model.Enum.SubType
import com.aymen.metastore.model.Location.GpsStatusReceiver
import com.aymen.metastore.model.Location.LocationService
import com.aymen.metastore.model.Location.hasLocationPermission
import com.aymen.metastore.model.Location.isGpsEnabled
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.entity.realm.PaymentForProviders
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.Enum.IconType
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.entity.realm.Conversation
import com.aymen.store.model.entity.realm.Invetation
import com.aymen.store.model.entity.realm.Message
import com.aymen.store.model.entity.realm.PurchaseOrderLine
import com.aymen.metastore.model.entity.realm.User
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ArticleViewModel
import com.aymen.store.model.repository.ViewModel.ClientViewModel
import com.aymen.store.model.repository.ViewModel.CompanyViewModel
import com.aymen.store.model.repository.ViewModel.MessageViewModel
import com.aymen.store.model.repository.ViewModel.PointsPaymentViewModel
import com.aymen.store.model.repository.ViewModel.ShoppingViewModel
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.math.BigDecimal
import java.math.RoundingMode


@Composable
fun LodingShape(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}

@Composable
fun ShowImage(image: String, size: Dp? = null){
    val imageSize = size ?: 50.dp
    AsyncImage(
        model = image,
        contentDescription = "article image",
        modifier = Modifier
            .padding(5.dp)
            .size(imageSize)
            .clip(CircleShape)
        ,
        contentScale = ContentScale.Crop,

    )
}


@SuppressLint("DefaultLocale")
@Composable
fun ShowPrice(cost : Double, margin : Double, tva : Double){
    val price = cost*(margin+tva)/100
    val formattedPrice = String.format("%.2f", margin)
    Text(text = "price: $formattedPrice TDN", fontSize = 14.sp, modifier = Modifier.padding(5.dp))
}

@Composable
fun SearchField(label : String, labelValue : String, value : (String) -> Unit, clickAction: () -> Unit){

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp)
        ,
        textStyle = TextStyle(fontSize = 16.sp)
        ,
        shape = RoundedCornerShape(10.dp),
        label = { Text(text = label) },
        colors =  TextFieldDefaults.colors(
            focusedLabelColor = Color.Black,
            cursorColor = Color.Magenta
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                clickAction()
                Log.e("aymenbabaysearch","click action")
            }
        ),
        value = labelValue,
        onValueChange = value
        ,
        trailingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription ="search", Modifier.clickable {
                    clickAction()
                }
                )
        },
        singleLine = true,
        maxLines = 1
    )
}

@Composable
fun InputTextField(labelValue: String, label:String, singleLine: Boolean, maxLine : Int, keyboardOptions: KeyboardOptions, onValueChange: (String) -> Unit, onImage: (Uri?)-> Unit,
                  enabled  : Boolean? = true, onImeAction: (File?) -> Unit ){
    var image by remember {
        mutableStateOf<Uri?>(null)
    }
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val photo =  resolveUriToFile(image, context)
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri -> image = uri }
    )
    LaunchedEffect(key1 = image) {
        onImage(image)
    }
    Column {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (label == "Type a message" || label == "Type a comment") {
                    Modifier.focusRequester(focusRequester)
                } else {
                    Modifier
                }
            ),
        enabled = enabled?:true,
        shape = RoundedCornerShape(20.dp),
        value = labelValue,
        label = { Text( label) },
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onSend = { onImeAction(photo)
            }),
        colors =  TextFieldDefaults.colors(
            focusedLabelColor = Color.Black,
            cursorColor = Color.Magenta
        ),
        onValueChange = onValueChange,
        singleLine = singleLine,
        maxLines = maxLine,
        leadingIcon = if(label == "Type a message" || label == "Type a comment"){
            {
            IconButton(onClick = {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
            Icon(imageVector = Icons.Outlined.Image, contentDescription = "")
            }
                }
        }else{
            null
        },
        trailingIcon = if(label == "Type a message" || label == "Type a comment"){
            {
            IconButton(onClick = {
                onImeAction(photo)
            }) {
                Icon(imageVector = Icons.Outlined.Send, contentDescription = "description")
            }
}
        }else{
            null
        }
    )
    }

}
@Composable
fun DiscountTextField(labelValue: String, label:String, singleLine: Boolean, maxLine : Int, keyboardOptions: KeyboardOptions, enabled: Boolean, onValueChange: (String) -> Unit ){
    OutlinedTextField(
        modifier = Modifier
            .width(100.dp),
        shape = RoundedCornerShape(20.dp),
        value = labelValue,
        label = { Text( label) },
        keyboardOptions = keyboardOptions,
        colors =  TextFieldDefaults.colors(
            focusedLabelColor = Color.Cyan,
            cursorColor = Color.Magenta
        ),
        onValueChange = onValueChange,
        singleLine = singleLine,
        maxLines = maxLine,
        enabled = enabled
    )
}

@Composable
fun ButtonSubmit(labelValue: String, color: Color,enabled:Boolean, clickAction: () -> Unit) {
    Row {
    Button(
        onClick =  clickAction ,
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(color),
        enabled = enabled
    ) {
        Box (
            contentAlignment = Alignment.Center
        ){
            Text(text = labelValue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    }
}

fun resolveUriToFile(uri: Uri?, context: Context): File? {
    if (uri == null) return null
    val contentResolver: ContentResolver = context.contentResolver

    // Obtain the input stream from the Uri
    val inputStream: InputStream = contentResolver.openInputStream(uri) ?: return null

    // Create a temporary file to copy the content
    val tempFile = File.createTempFile("temp_image", ".tmp", context.cacheDir)

    // Copy the input stream to the file
    FileOutputStream(tempFile).use { outputStream ->
        inputStream.copyTo(outputStream)
    }

    return tempFile
}

@Composable
fun ArticleCardForUser(article : List<ArticleCompany>, isEnabled : Boolean) {
    val appViewModel : AppViewModel = hiltViewModel()
    val shoppingViewModel : ShoppingViewModel = hiltViewModel()
   val articleViewModel : ArticleViewModel = hiltViewModel()
    val messageViewModel : MessageViewModel = hiltViewModel()
    val companyViewModel : CompanyViewModel = hiltViewModel()
    Row {
        LazyColumn {
            items(article) {
            Card(
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Row {
                    Row(
                        modifier = Modifier
                            .weight(0.9f)
                            .clickable {
                                companyViewModel.myCompany = it.company!!
                                articleViewModel.companyId = it.company?.id!!
                                RouteController.navigateTo(Screen.CompanyScreen)
                            }
                    ) {
                        if (it.company?.logo != null) {
                            ShowImage(image = "${BASE_URL}werehouse/image/${it.company?.logo}/company/${it.company?.user?.id}",30.dp)
                        } else {
                            val painter: Painter = painterResource(id = R.drawable.emptyprofile)
                            Image(
                                painter = painter,
                                contentDescription = "empty photo profil",
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(
                                        RoundedCornerShape(10.dp)
                                    )
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "verification account",
                            tint = Color.Green
                        )
                        it.company?.let { it1 -> Text(text = it1.name) }
                    }
                    Row(
                        modifier = Modifier.weight(0.1f)
                    ) {
                        Text(text = "${it.likeNumber}")
                        Icon(imageVector = if (it.isFav) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "favorite",
                            Modifier.clickable {
                                articleViewModel.makeItAsFav(it)
                            }
                        )
                    }
                }
                Column(
                    modifier = Modifier.clickable {
                        companyViewModel.myCompany = it.company!!
                        articleViewModel.articleCompany = it
                        RouteController.navigateTo(Screen.ArticleDetailScreen)
                    }
                ) {
                if (it.article!!.image.isEmpty()) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Article, contentDescription = "article photo")
                } else {
                    ShowImage(image = "${BASE_URL}werehouse/image/${it.article!!.image}/article/${CompanyCategory.valueOf(it.company?.category!!).ordinal}")
                }
                NormalText(value = it.article!!.libelle, aligne = TextAlign.Start)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShowPrice(
                        cost = it.cost,
                        margin = it.sellingPrice,
                        tva = it.article!!.tva
                    )
                    ArticleDetails(value = it.quantity.toString(), aligne = TextAlign.Start)
                }
            }
                Row {
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                    if(isEnabled)
                    {

                            ButtonSubmit(
                                labelValue = "send message",
                                color = Color.Cyan,
                                enabled = isEnabled
                            ) {
                                messageViewModel.receiverAccountType = AccountType.COMPANY
                                messageViewModel.getAllMessageByCaleeId(it.company?.id!!)
                                messageViewModel.receiverCompany = it.company!!
//                                messageViewModel.getConversationByCaleeId(it.company?.id!!)
                                appViewModel.updateShow("message")
                                appViewModel.updateScreen(IconType.MESSAGE)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                            ShoppingDialog(it,"add to cart", isOpen = false,shoppingViewModel = shoppingViewModel)
                    }

                }
            }
        }
        }
    }
}

@Composable
fun ArticleCardForSearch(article: ArticleCompany, onClicked: () -> Unit) {
    val companyViewModel : CompanyViewModel = viewModel()
    val articleViewModel : ArticleViewModel = viewModel()
    Card(
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                onClicked()
            }
    ) {
        ShowImage(image = "${BASE_URL}werehouse/image/${article.article!!.image}/article/${CompanyCategory.valueOf(article.company?.category!!).ordinal}")
        NormalText(value = article.article!!.libelle, aligne = TextAlign.Start)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShowPrice(
                cost = article.cost,
                margin = article.sellingPrice,
                tva = article.article!!.tva
            )
            ArticleDetails(value = article.quantity.toString(), aligne = TextAlign.Start)
        }
    }
}

@Composable
fun AddTypeDialog(isOpen : Boolean,id : Long,isCompany : Boolean, onSelected :(Type) -> Unit) {
    val clientViewModel : ClientViewModel = viewModel()
    val appViewModel : AppViewModel = viewModel()
    val sharedViewModel : SharedViewModel = viewModel()
    var openDialog by remember {
        mutableStateOf(isOpen)
    }
    var subType by remember {
        mutableStateOf(SubType.OTHER)
    }
    var type by remember {
        mutableStateOf(Type.OTHER)
    }
    val accountType = sharedViewModel.accountType
    LaunchedEffect(key1 = subType) {
        if(accountType == AccountType.USER) {
            if(isCompany){
            when (subType) {
                SubType.CLIENT -> {
                    type = Type.USER_SEND_CLIENT_COMPANY
//                clientViewModel.sendClientRequest(id,type)
                }

                SubType.WORKER -> {
                    type = Type.USER_SEND_WORKER_COMPANY
//                clientViewModel.sendClientRequest(id,type)
                }
                else -> {
                }
                }
            }
        }
//        if(role == RoleEnum.USER && !isCompany){
//            openDialog = false
//        }
        else {
            if (isCompany) {
                when (subType) {
                    SubType.CLIENT -> {
                        type = Type.COMPANY_SEND_CLIENT_COMPANY
//                        clientViewModel.sendClientRequest(id, type)
                    }

                    SubType.PROVIDER -> {
                        type = Type.COMPANY_SEND_PROVIDER_COMPANY
//                        clientViewModel.sendClientRequest(id, type)
                    }

                    SubType.PARENT -> {
                        type = Type.COMPANY_SEND_PARENT_COMPANY
//                        clientViewModel.sendClientRequest(id, type)
                    }

                    else ->
                        Type.OTHER
                }
            }
            else{
                when (subType) {
                    SubType.CLIENT -> {
                        type = Type.COMPANY_SEND_PROVIDER_USER
//                        clientViewModel.sendClientRequest(id, type)
                    }

                    SubType.WORKER -> {
                        type = Type.COMPANY_SEND_WORKER_USER
//                        clientViewModel.sendClientRequest(id, type)
                    }

                    else ->
                        Type.OTHER
                }

            }
            if (type != Type.OTHER) {
//                clientViewModel.sendClientRequest(id, type)
            }
        }

        onSelected(type)
    }
    Icon(imageVector = Icons.Default.AddBusiness,
        contentDescription = "add as provider",
        Modifier.clickable {
            openDialog = true
        })
    if(openDialog) {
        Dialog(
            onDismissRequest = {
                openDialog = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                Column(
                    modifier = Modifier.padding(2.dp)
                ) {
                    if (isCompany) {

                        ButtonSubmit(
                            labelValue = "add as provider",
                            color = Color.Green,
                            enabled = true
                        ) {
                            openDialog = false
                            subType = SubType.PROVIDER
                        }
                        ButtonSubmit(
                            labelValue = "add as parent",
                            color = Color.Green,
                            enabled = true
                        ) {
                            openDialog = false
                            subType = SubType.PARENT
                        }
                    }else{
                        ButtonSubmit(
                            labelValue = "add as worker",
                            color = Color.Green,
                            enabled = true
                        ) {
                            openDialog = false
                            subType = SubType.WORKER
                        }
                    }
                    ButtonSubmit(
                        labelValue = "add as client",
                        color = Color.Green,
                        enabled = true
                    ) {
                        openDialog = false
                        subType = SubType.CLIENT
                    }
                }
                }
            }
        }

}


@Composable
fun SendPointDialog(isOpen : Boolean, user: User, client : Company) {
    val pointsPaymentViewModel : PointsPaymentViewModel = viewModel()
    var openDialog by remember {
        mutableStateOf(isOpen)
    }
    var dinars by remember {
        mutableFloatStateOf(0F)
    }
    var points by remember {
        mutableFloatStateOf(0f)
    }
    var equalsDinars by remember {
        mutableDoubleStateOf(0.0)
    }
    var launchDinar by remember {
        mutableStateOf(false)
    }
    var launchPoints by remember {
        mutableStateOf(false)
    }
    var launchEquals by remember {
        mutableStateOf(false)
    }
    if(launchDinar){
        LaunchedEffect(key1 = dinars) {
            points = dinars / 3
            equalsDinars = (dinars * 1.1)
        }
    }
    if(launchPoints) {
        LaunchedEffect(key1 = points) {
            dinars = points * 3
            equalsDinars = ( dinars * 1.1)
        }
    }
        if(launchEquals){
            LaunchedEffect(key1 = equalsDinars) {
                val dinar = BigDecimal.valueOf(equalsDinars)
                    .multiply(BigDecimal("10"))
                    .divide(BigDecimal("11"), 2, RoundingMode.HALF_UP)

                // Calculate points and round to 2 decimal places
                val point = dinar.divide(BigDecimal("3"), 2, RoundingMode.HALF_UP)

                dinars = dinar.toFloat()
                 points = dinars / 3
            }
        }
    Icon(imageVector = Icons.Default.AddCard,
    contentDescription = "add points",
        Modifier.clickable {
            openDialog = true
        })
    if(openDialog) {
        Dialog(
            onDismissRequest = {
                openDialog = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                Column(
                    modifier = Modifier.padding(2.dp)
                ) {
                    InputTextField(
                        labelValue = if(dinars == 0F) "" else dinars.toString(),
                        label = "how many do you have money?",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            dinars = it.toFloat()
                            launchDinar = true
                            launchPoints = false
                            launchEquals = false
                        }
                        , onImage = {}
                        ,true
                    ) {

                    }
                    InputTextField(
                        labelValue = if(points == 0f) "" else points.toString(),
                        label = "How many points do you want to buy?",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            points = it.toFloat()
                            launchDinar = false
                            launchPoints = true
                            launchEquals = false
                        }
                        , onImage = {}
                        ,true
                    ) {

                    }
                    InputTextField(
                        labelValue = if(equalsDinars == 0.0) "" else equalsDinars.toString(),
                        label = "How many dinars do you want to buy?",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            equalsDinars = it.toDouble()
                            launchDinar = false
                            launchPoints = false
                            launchEquals = true
                        }
                        , onImage = {}
                        ,true
                    ) {

                    }
                    ButtonSubmit(
                        labelValue = "send",
                        color = Color.Green,
                        enabled = true
                    ) {
                        openDialog = false
                        pointsPaymentViewModel.sendPoints(user,dinars.toLong(),client)
                    }
                    ButtonSubmit(
                        labelValue = "cancel",
                        color = Color.Red,
                        enabled = true
                    ) {
                        openDialog = false
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingDialog(article : ArticleCompany, label: String, isOpen : Boolean,shoppingViewModel : ShoppingViewModel) {
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val accountType = sharedViewModel.accountType
    val company by sharedViewModel.company.collectAsStateWithLifecycle()
    val user by sharedViewModel.user.collectAsStateWithLifecycle()
    val context = LocalContext.current
//     checkLocation(type = accountType, user = user, company = company, context = context)
    var isCompany by remember {
        mutableStateOf(false)
    }
    var openDialog by remember {
        mutableStateOf(isOpen)
    }
    var restBalance by remember {
        if (isCompany){
        mutableStateOf(company.balance!!.toBigDecimal().setScale(2, RoundingMode.HALF_UP))
        }else{
        mutableStateOf(user.balance!!.toBigDecimal().setScale(2, RoundingMode.HALF_UP))
        }
    }
    val gson = Gson()
    LaunchedEffect(key1 = shoppingViewModel.qte, key2 = accountType) {
        isCompany = accountType == AccountType.COMPANY
        restBalance = if(isCompany){
            company.balance!!.toBigDecimal() - shoppingViewModel.qte.toBigDecimal() * article.sellingPrice.toBigDecimal()
        }else {
            user.balance!!.toBigDecimal() - shoppingViewModel.qte.toBigDecimal() * article.sellingPrice.toBigDecimal()
        }
    }
    if(label != ""){
    ButtonSubmit(labelValue = label, color = Color.Blue, enabled = true) {
        openDialog = true
    }
    }
    if(openDialog){
        Dialog(
            onDismissRequest = {openDialog = false
                shoppingViewModel.remiseAZero()
            }
        ){
            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                Column (
                    modifier = Modifier.padding(2.dp)
                ){
                    Row (
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ){
                        ShowImage(image = "${BASE_URL}werehouse/image/${article.article!!.image}/article/${CompanyCategory.valueOf(article.company?.category!!).ordinal}")
                        Text(text = if( restBalance<BigDecimal.ZERO || (shoppingViewModel.qte == 0.0 && restBalance <= 1.toBigDecimal())) "sold insufficient $restBalance pts" else "$restBalance pts")
                    }
                    Row {
                        InputTextField(
                            labelValue = if(shoppingViewModel.qte == 0.0) "" else shoppingViewModel.qte.toString(),
                            label = "Quantity",
                            singleLine = true,
                            maxLine = 1,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            onValueChange = {
                                if(it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))){
                                    shoppingViewModel.qte = it.toDouble()
                                }
                            }
                            , onImage = {}
                            ,true
                        ) {

                        }
                    }
                    Row {
                        InputTextField(
                            labelValue = shoppingViewModel.comment,
                            label = "comment",
                            singleLine = false,
                            maxLine = 3,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            onValueChange = {
                                shoppingViewModel.comment = it
                            }
                            , onImage = {}
                            ,true
                        ) {
                        }
                    }
                    Row {
                        if((restBalance>=BigDecimal.ZERO && shoppingViewModel.qte != 0.0) ) {
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = "add more",
                                    color = Color.Blue,
                                    enabled = true
                                ) {
                                    openDialog = false
                                    shoppingViewModel.randomArtilce = article
                                    shoppingViewModel.submitShopping(restBalance)
                                    val s = gson.toJson(shoppingViewModel.orderArray)
                                }
                            }
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {

                                ButtonSubmit(
                                    labelValue = "send",
                                    color = Color.Green,
                                    enabled = true
                                ) {
                                    openDialog = false
                                    shoppingViewModel.randomArtilce = article
                                    shoppingViewModel.submitShopping(restBalance)
                                    shoppingViewModel.sendOrder(-1)
                                }
                            }
                        }
                        Row (
                            modifier = Modifier.weight(1f)
                        ){

                            ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                                openDialog = false
                                shoppingViewModel.remiseAZero()
                            }
                        }

                        }
                        Row {
                            CheckBoxComp(value = "Delivery",shoppingViewModel.delivery) {isChecked ->
                                shoppingViewModel.delivery = isChecked
                            }
                            }
                    }
                }
            }
        }
    }

@Composable
fun updateImageDialog(isOpen: Boolean, onClose: () -> Unit) {
    val appViewModel : AppViewModel = hiltViewModel()
    val context = LocalContext.current
    var image by remember {
        mutableStateOf<Uri?>(null)
    }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri -> image = uri }
    )


    if(isOpen){
        Dialog(
            onDismissRequest = onClose
        ){
            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                Column {
                    if(image == null){
                Text(text = "update your photo?", modifier = Modifier.clickable {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                })
                    }
                    else{
                        Box(modifier = Modifier.size(200.dp)) {
                            AsyncImage(
                                model = image,
                                contentDescription = null,
                                modifier = Modifier.size(200.dp),
                                contentScale = ContentScale.Crop
                            )

                            IconButton(
                                onClick = onClose,
                                modifier = Modifier
                                    .align(Alignment.BottomStart) // Position the close button at the top right
                                    .size(24.dp) // Size of the close button
                                    .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape) // Optional background
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Image",
                                    tint = Color.White
                                )
                            }
                            IconButton(
                                onClick = {
                                    val photo =  resolveUriToFile(image, context)
                                    appViewModel.updateImage(photo!!)
                                    image = null
                                    onClose()
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(24.dp)
                                    .background(Color.Green.copy(alpha = 0.5f), shape = CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "check Image",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ConversationCard(conversation : List<Conversation>) {

    val messageViewModel : MessageViewModel = viewModel()
    val appViewModel : AppViewModel = viewModel()
    Row {
        LazyColumn {
            items(conversation) {
                Card(
                    elevation = CardDefaults.cardElevation(6.dp),
                    modifier = Modifier.padding(8.dp),
                    onClick = {

//                        messageViewModel.sender = it.company1?.name ?: ""
                        appViewModel.updateShow("message")
                        messageViewModel.conversation = it
                        messageViewModel.receiverCompany = it.company2?:Company()
                        messageViewModel.receiverUser = it.user2?:User()
                        Log.e("usercomponentttype",it.type!!)
                        messageViewModel.messageType = MessageType.valueOf(it.type!!)
                        messageViewModel.getAllMyMessageByConversationId()
                        messageViewModel.receiverAccountType = AccountType.COMPANY
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.weight(0.2f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(100.dp))
                            ) {
                                if (it.company2?.id != null) {
                                    if (it.company2?.logo != null) {
                                        ShowImage(image = "${BASE_URL}werehouse/image/${it.company2?.logo}/company/${it.company2?.user?.id}")
                                    } else {
                                    Log.e("aymenbabaycardscreen","conversation id from company 2 not null : ${it.id}")
                                        val painter: Painter =
                                            painterResource(id = R.drawable.emptyprofile)
                                        Image(
                                            painter = painter,
                                            contentDescription = "empty photo profil",
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(
                                                    RoundedCornerShape(10.dp)
                                                )
                                        )
                                    }
                                }
                                if (it.user2?.id != null) {
                                    if (it.user2?.image != null) {
                                        ShowImage(image = "${BASE_URL}werehouse/image/${it.user2?.image}/user/${it.user2?.id}")
                                    } else {
                                        Log.e("aymenbabaycardscreen","conversation id from user 2 not null : ${it.id} user 2 id  : ${it.user2?.id}")

                                        val painter: Painter =
                                            painterResource(id = R.drawable.emptyprofile)
                                        Image(
                                            painter = painter,
                                            contentDescription = "empty photo profil",
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(
                                                    RoundedCornerShape(10.dp)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .weight(1f)
                        ) {
                            it.user2?.let {
                                NormalText(
                                    value = it.username,
                                    aligne = TextAlign.Start
                                )
                            }
                            it.user1?.let {
                                NormalText(
                                    value = it.username,
                                    aligne = TextAlign.End
                                )
                            }
                            it.company1?.let {
                                NormalText(
                                    value = it.name,
                                    aligne = TextAlign.End
                                )
                            }
                            it.company2?.let {
                                NormalText(
                                    value = it.name,
                                    aligne = TextAlign.End
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))

                            val firstLine = it.lastMessage.takeWhile { it != '\n' }
                            ConversationText(
                                value = firstLine,
                                aligne = TextAlign.Start,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(text = it.lastModifiedDate)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageCard(message: List<Message>) {
    val messageViewModel : MessageViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
//    val conversation = messageViewModel.conversation
    val me by sharedViewModel.user.collectAsStateWithLifecycle()
//    val myCompany = sharedViewModel.company
//    val type = sharedViewModel.accountType
    val lazyListState = rememberLazyListState()
LaunchedEffect(key1 = messageViewModel.myAllMessages) {
    lazyListState.scrollToItem(index = messageViewModel.myAllMessages.size)
}
    Row {
        LazyColumn(
            state = lazyListState
        ) {
           items(message) {
               Column {

               it.content?.let { it1 ->
                   if (me.id != null) {
                       MessageText(
                           value = it1,
                           aligne = if (
                               it.createdBy == me.id
                           )
                               TextAlign.End else TextAlign.Start,
                           color = if(
                               it.createdBy == me.id
                           )
                               Color.Blue else Color.Black
                       )
                   }
               }
              Text(text =  it.createdDate)
               }
           }
        }
    }
}

@Composable
fun OrderShow(order: PurchaseOrderLine) {
    val shoppingViewModel: ShoppingViewModel = viewModel()
    var showDialog by remember { mutableStateOf(false) }
    Row {
        Column(
            modifier = Modifier
                .clickable {
                    showDialog = true
                }
                .weight(0.9f)
        ) {
            order.article?.let { Text(text = it.article!!.libelle) }
            Text(text = order.quantity.toString())
            Text(text = order.comment)
            if (showDialog) {
                shoppingViewModel.randomArtilce = order.article!!
                shoppingViewModel.qte = order.quantity
                shoppingViewModel.comment = order.comment
                shoppingViewModel.delivery = order.delivery
                ShoppingDialog(
                    article = order.article!!,
                    label = "",
                    isOpen = true,
                    shoppingViewModel = shoppingViewModel
                )
            }
        }
    }
}

@Composable
fun InvetationCard(invetation : Invetation,onClicked: (Status) -> Unit) {
    val companyViewModel : CompanyViewModel = viewModel()
    val appViewModel : AppViewModel = viewModel()
    val sharedViewModel : SharedViewModel = viewModel()
    var companyId by remember {
        mutableLongStateOf(0)
    }
    var role = sharedViewModel.accountType

    LaunchedEffect(Unit) {
        companyViewModel.getMyCompany {
            if (it != null) {
                companyId = it.id!!
            }
        }
    }
    Row (
        modifier = Modifier.fillMaxWidth()
    )
    {
        when (invetation.type) {
            Type.USER_SEND_CLIENT_COMPANY.toString() ->
                InvitationTypeClient(invetation, companyId){
                    onClicked(it)
                }
            Type.COMPANY_SEND_CLIENT_COMPANY.toString() ->
                InvitationTypeClient(invetation, companyId){
                    onClicked(it)
                }
            Type.COMPANY_SEND_PROVIDER_USER.toString() ->
            InvitationTypeProvider(invetation, companyId,role){
                onClicked(it)
            }
            Type.COMPANY_SEND_PROVIDER_COMPANY.toString() ->
                InvitationTypeProvider(invetation, companyId,role){
                    onClicked(it)
                }
            Type.COMPANY_SEND_PARENT_COMPANY.toString() ->
           InvitationTypeParent(invetation = invetation, companyId){
               onClicked(it)
           }
            Type.COMPANY_SEND_WORKER_USER.toString() ->
            InvitationTypeWorker(invetation = invetation,role){
                onClicked(it)
            }
            Type.USER_SEND_WORKER_COMPANY.toString() ->
                InvitationTypeWorker(invetation = invetation,role){
                    onClicked(it)
                }
        }
    }

}

@Composable
fun InvitationTypeWorker(invetation: Invetation, role : AccountType,onClicked: (Status) -> Unit) {
        when (invetation.status){
            Status.INWAITING.toString() ->
            InWaitingTypeWorker(invetation = invetation, role = role){
                onClicked(it)
            }
            Status.ACCEPTED.toString() ->
            AcceptTypeWorker(invetation = invetation, role = role)
            Status.REFUSED.toString() ->
            RefuseTypeWorker(invetation = invetation, role = role)
            Status.CANCELLED.toString() ->
            CancelTypeWorker(invetation = invetation, role = role)
        }
    }
@Composable
fun CancelTypeWorker(invetation: Invetation, role : AccountType) {
    when (role) {
        AccountType.COMPANY -> {
            Row{
                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
            Row(
                modifier = Modifier.weight(3f)
            ) {
                if(invetation.type == Type.USER_SEND_WORKER_COMPANY.toString()){
                Text(text = "${invetation.client?.username} has canceled a worker invitation")
                }else{
                Text(text = "you have canceled a worker invitation to ${invetation.client?.username}")
                }
            }
            }
        }
        AccountType.USER ->{
            Row{
                Row (
                    modifier = Modifier.weight(1f)
                ){
                    if(invetation.type == Type.USER_SEND_WORKER_COMPANY.toString()){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                    }else{
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                    }
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
if(invetation.type == Type.USER_SEND_WORKER_COMPANY.toString()){
                    Text(text = "you have canceled a worker invitation to ${invetation.companyReciver?.name}")
}
                    else{
                    Text(text = "${invetation.companySender?.name} has cnceled a worker invitation")
                    }
                }
            }
        }
        else -> {

        }
    }

    }
@Composable
fun RefuseTypeWorker(invetation: Invetation, role : AccountType) {
    when (role){
        AccountType.COMPANY -> {
            Row {
                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    if(invetation.type == Type.USER_SEND_WORKER_COMPANY.toString()){
                    Text(text = "you have refused a worker invitation from ${invetation.client?.username}")
                    }else{
                    Text(text = "${invetation.client?.username} has refused your worker invitation")
                    }
                }
            }
        }
        AccountType.USER -> {
            Row {
                Row (
                    modifier = Modifier.weight(1f)
                ){
                    if(invetation.type == Type.USER_SEND_WORKER_COMPANY.toString()){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                    }else{
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")

                    }
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    if(invetation.type == Type.USER_SEND_WORKER_COMPANY.toString()){
                    Text(text = "${invetation.companyReciver?.name} has refused your worker invitation")
                    }
                    else{
                    Text(text = "you have refused a worker invitation from ${invetation.companySender?.name}")

                    }
                }
            }
        }
        else ->{

        }
    }
    }
@Composable
fun InWaitingTypeWorker(invetation: Invetation, role: AccountType,onClicked: (Status) -> Unit) {
    when (role) {
        AccountType.COMPANY -> {
            Row {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Column {
                        if (invetation.type == Type.USER_SEND_WORKER_COMPANY.toString()) {
                            Text(text = "${invetation.client?.username} has sent a worker invitation")
                            Row {
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ButtonSubmit(
                                        labelValue = "accept",
                                        color = Color.Green,
                                        enabled = true
                                    ) {
                                    }
                                    onClicked(Status.ACCEPTED)
                                }
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ButtonSubmit(
                                        labelValue = "refuse",
                                        color = Color.Red,
                                        enabled = true
                                    ) {
                                        onClicked(Status.REFUSED)
                                    }
                                }
                            }
                        } else {
                            Column {

                                Text(text = "you have sent a worker invitation to ${invetation.client?.username}")
                                ButtonSubmit(
                                    labelValue = "cancel",
                                    color = Color.Red,
                                    enabled = true
                                ) {
                                    onClicked(Status.CANCELLED)
                                }
                            }
                        }
                    }
                }
            }
        }

        AccountType.USER -> {
            Row {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    if (invetation.type == Type.USER_SEND_WORKER_COMPANY.toString()) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                    } else
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Column {
                        if (invetation.type == Type.USER_SEND_WORKER_COMPANY.toString()) {

                            Text(text = " you have sent a worker invitation to ${invetation.companyReciver?.name}")
                            ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                                onClicked(Status.CANCELLED)
                            }
                        } else {

                            Text(text = "  ${invetation.companySender?.name} has sent a worker invitation")
                            Row {
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ButtonSubmit(
                                        labelValue = "accept",
                                        color = Color.Green,
                                        enabled = true
                                    ) {
                                        onClicked(Status.ACCEPTED)
                                    }
                                }
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ButtonSubmit(
                                        labelValue = "refuse",
                                        color = Color.Red,
                                        enabled = true
                                    ) {
                                        onClicked(Status.REFUSED)
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        else -> {
            Text(text = "salem")
        }
    }
}
@Composable
fun AcceptTypeWorker(invetation: Invetation, role: AccountType) {
    when (role) {
        AccountType.COMPANY -> {
            Row {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    if (invetation.type == Type.USER_SEND_WORKER_COMPANY.toString()) {
                        Text(text = "you have accepted a worker invitation from ${invetation.client?.username}")
                    } else {
                        Text(text = "${invetation.client?.username} has accepted your worker invitation")
                    }
                }
            }
        }

        AccountType.USER -> {
            Row {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    if (invetation.type == Type.USER_SEND_WORKER_COMPANY.toString()) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                    } else {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                    }
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    if (invetation.type == Type.USER_SEND_WORKER_COMPANY.toString()) {
                        Text(text = "${invetation.companyReciver?.name} has accepted your worker invitation")
                    } else {
                        Text(text = "you have accepted a worker invitation from ${invetation.companySender?.name}")
                    }
                }
            }
        }

        else -> {

        }
    }
}
@Composable
fun InvitationTypeParent(invetation : Invetation, companyId : Long, onClicked: (Status) -> Unit) {
        when (invetation.status){
            Status.INWAITING.toString() ->
            InWaitingTypeParent(invetation = invetation, companyId = companyId){
                onClicked(it)
            }
            Status.ACCEPTED.toString() ->
            AcceptTypeParent(invetation = invetation, companyId = companyId)
            Status.REFUSED.toString() ->
            RefuseTypeParent(invetation = invetation, companyId = companyId)
            Status.CANCELLED.toString() ->
            CancelTypeParent(invetation = invetation, companyId = companyId)
        }
    }
@Composable
fun CancelTypeParent(invetation: Invetation, companyId: Long) {
    if (invetation.companyReciver?.id == companyId) {

        Row {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
            }
            Row(
                modifier = Modifier.weight(3f)
            ) {
                Text(text = "${invetation.companySender?.name} has canceled a parent invitation")
            }
        }

    } else {
        Row {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
            }
            Row(
                modifier = Modifier.weight(3f)
            ) {
                Text(text = " you have canceled a parent invitation to ${invetation.companyReciver?.name}")
            }
        }
        }
}
@Composable
fun RefuseTypeParent(invetation: Invetation, companyId: Long) {
    if(invetation.companyReciver?.id == companyId){
        Row {

        Row (
            modifier = Modifier.weight(1f)
        ){
            ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
        }
        Row (
            modifier = Modifier.weight(3f)
        ){
            Text(text = " you have refused a parent invitation from ${invetation.companySender?.name}")
        }
        }
    }
    else{
        Row {

        Row (
            modifier = Modifier.weight(1f)
        ){
            ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
        }
        Row (
            modifier = Modifier.weight(3f)
        ){
            Text(text = "${invetation.companyReciver?.name} has refused your parent invitation")
        }
        }
    }
}
@Composable
fun InWaitingTypeParent(invetation: Invetation , companyId: Long, onClicked: (Status) -> Unit) {
    if(invetation.companyReciver?.id == companyId){
        Row {

        Row (
            modifier = Modifier.weight(1f)
        ){
            ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
        }
        Row (
            modifier = Modifier.weight(3f)
        ){

            Column {

            Text(text = "${invetation.companySender?.name} has sent a parent invitation")
                Row {
                 Row (
                     modifier = Modifier.weight(1f)
                 ){

            ButtonSubmit(labelValue = "accept", color = Color.Green, enabled = true) {
                onClicked(Status.ACCEPTED)
                 }
            }
                    Row (
                        modifier = Modifier.weight(1f)
                    ){

            ButtonSubmit(labelValue = "refuse", color = Color.Red, enabled = true) {
                onClicked(Status.REFUSED)
                    }
                }
        }
            }
            }
        }
    }
    else{
        Row {

        Row (
            modifier = Modifier.weight(1f)
        ){
            ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
        }
        Row (
            modifier = Modifier.weight(3f)
        ){

            Column {

            Text(text = " you have sent a parent invitation to ${invetation.companyReciver?.name}")
            ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                onClicked(Status.CANCELLED)
            }
            }
        }
    }
        }
}
@Composable
fun AcceptTypeParent(invetation: Invetation,companyId: Long) {
    if(invetation.companyReciver?.id == companyId){
        Row {

        Row (
            modifier = Modifier.weight(1f)
        ){
            ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
        }
        Row (
            modifier = Modifier.weight(3f)
        ){
            Text(text = " you have accepted a parent invitation from ${invetation.companySender?.name}")
        }
        }
    }
    else{
        Row {

        Row (
            modifier = Modifier.weight(1f)
        ){
            ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
        }
        Row (
            modifier = Modifier.weight(3f)
        ){
            Text(text = "${invetation.companyReciver?.name} has accepted your parent invitation")
        }
        }
    }
}
@Composable
fun InvitationTypeProvider(invetation : Invetation, companyId : Long, role : AccountType,onClicked: (Status) -> Unit){
    Log.e("aymenbbayrole","company id is : ${companyId}")
        when (invetation.status){
            Status.INWAITING.toString() ->
            InWaitingTypeProvider(invetation = invetation, companyId = companyId){
                onClicked(it)
            }
            Status.ACCEPTED.toString() ->
            AcceptTypeProvider(invetation = invetation, companyId = companyId)
            Status.REFUSED.toString() ->
            RefuseTypeProvider(invetation = invetation, companyId = companyId, role = role)
            Status.CANCELLED.toString() ->
            CancelTypeProvider(invetation = invetation, companyId = companyId, role = role)
        }
    }
@Composable
fun CancelTypeProvider(invetation: Invetation, companyId: Long, role: AccountType) {
        if(invetation.type == Type.COMPANY_SEND_PROVIDER_COMPANY.toString()){
            if(invetation.companyReciver?.id == companyId){
                Row {
                Row (
                    modifier = Modifier.weight(1f)
                ){
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text =  invetation.companySender?.name!! + " has canceled a provider invitation")
                }
                }
            }else{
                Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = " you have canceled a provider invitation to " + invetation.companyReciver?.name!!)
                }
                }
            }
        }else{
            if(role == AccountType.COMPANY){
                Row {

            Row (
                modifier = Modifier.weight(1f)
            ){
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
            }
            Row (
                modifier = Modifier.weight(3f)
            ){
                Text(text = " you have canceled a provider invitation to " + invetation.client?.username!!)
                }
            }
            }else{
            Row {

            Row (
                modifier = Modifier.weight(1f)
            ){
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
            }
            Row (
                modifier = Modifier.weight(3f)
            ){
                Text(text = " you have canceled a provider invitation to " + invetation.companyReciver?.name!!)
            }
            }
        }
    }
}
@Composable
fun RefuseTypeProvider(invetation: Invetation, companyId: Long, role : AccountType) {
        if(invetation.type == Type.COMPANY_SEND_PROVIDER_COMPANY.toString()){
            if(invetation.companyReciver?.id == companyId){
                Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = " you have refused a provider invitation from " + invetation.companySender?.name!!)
                }
                }
            }else {
                Row {

                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                    }
                    Row(
                        modifier = Modifier.weight(3f)
                    ) {
                        Text(text = " you have refused a provider invitation from " + invetation.companyReciver?.name!!)
                    }
                }
            }
        }else {
            if (role == AccountType.COMPANY) {
                Row {
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                    }
                    Row(
                        modifier = Modifier.weight(3f)
                    ) {
                        Text(text = invetation.client?.username!! + " has refused your provider invitation")
                    }
                }

            } else {
                Row {

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Text(text = "you have refused a provider invitation from ${invetation.companySender?.name!!}")
                }
            }
                }
        }

}
@Composable
fun InWaitingTypeProvider(invetation: Invetation , companyId: Long, onClicked: (Status) -> Unit) {

        if(invetation.type == Type.COMPANY_SEND_PROVIDER_COMPANY.toString() ){
            if(invetation.companyReciver?.id == companyId){
                Row {

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    Column {

                        Text(text = "${invetation.companySender?.name} has sent a provider invitation")
                        Row {

                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(labelValue = "accept", color = Color.Green, enabled = true) {
                                    onClicked(Status.ACCEPTED)
                                }

                            }
                            Row(
                                modifier = Modifier.weight(1f)
                            ){
                                ButtonSubmit(labelValue = "refuse", color = Color.Red, enabled = true) {
                                    onClicked(Status.REFUSED)
                                }
                            }
                        }
                }
                    }
                }
            }else{
                Row {

                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                    }
                    Row(
                        modifier = Modifier.weight(3f)
                    ) {
                        Column {

                            Text(text = "you have sent a provider invitation to ${invetation.companyReciver?.name}")
                                    ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                                        onClicked(Status.CANCELLED)
                                    }
                        }
                    }
                }
            }
        }else{
            if(invetation.companySender?.id == companyId){
            Row {

            Row (
                modifier = Modifier.weight(1f)
            ){
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
            }
            Row (
                modifier = Modifier.weight(3f)
            ){
Column {

                Text(text = " you have sent a provider invitation to " + invetation.client?.username!!)
                ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                    onClicked(Status.CANCELLED)
}

                }
            }
        }

    }else{
        Row {

            Row (
                modifier = Modifier.weight(1f)
            ){
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
            }
            Row (
                modifier = Modifier.weight(3f)
            ){
                Column {
                Text(text = "${invetation.companySender?.name!!} has sent a provider invitation")
                    Row {
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            ButtonSubmit(labelValue = "accept", color = Color.Green, enabled = true) {
                                onClicked(Status.ACCEPTED)
                            }
                        }
                        Row (
                            modifier = Modifier.weight(1f)
                        ){
                            ButtonSubmit(labelValue = "refuse", color = Color.Red, enabled = true) {
                                onClicked(Status.REFUSED)
                            }
                        }
                    }

                }
            }
        }
        }
    }
}
@Composable
fun AcceptTypeProvider(invetation: Invetation,companyId: Long) {
        if(invetation.type == Type.COMPANY_SEND_PROVIDER_COMPANY.toString()){
            Row {
                Row (
                    modifier = Modifier.weight(1f)
                ){

                    if(invetation.companyReciver?.id == companyId) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                    }else{
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                    }
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
            if(invetation.companyReciver?.id == companyId){
                Text(text = " you have accepted a provider invitation from " + invetation.companySender?.name!!)
            }else{
                Text(text = invetation.companyReciver?.name!! + " has accepted your provider invitation")
            }
                }
            }
        }else {
            Row {
                Row(
                    modifier = Modifier.weight(1f)
                ) {

                        if(invetation.companySender?.id != companyId){
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                    }
                    else {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                        }

                }
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    if (invetation.companySender?.id == companyId) {
                        Text(text = invetation.client?.username!! + " has accepted your provider invitation ")
                    } else {
                        Text(text = "you have accpted a provider invitation from ${invetation.companySender?.name!!}")
                    }
                }
            }
        }
}
@Composable
fun InvitationTypeClient(invetation : Invetation, companyId : Long, onClicked: (Status) -> Unit){
    when (invetation.status){
        Status.INWAITING.toString() ->
        InWaitingTypeClient(invetation = invetation, companyId = companyId){
            onClicked(it)
        }
        Status.ACCEPTED.toString() ->
        AcceptTypeClient(invetation = invetation, companyId = companyId)
        Status.REFUSED.toString() ->
        RefuseTypeClient(invetation = invetation, companyId = companyId)
        Status.CANCELLED.toString() ->
        CancelTypeClient(invetation = invetation, companyId = companyId)
    }
}
@Composable
fun CancelTypeClient(invetation: Invetation, companyId: Long) {
        if(invetation.type == Type.COMPANY_SEND_CLIENT_COMPANY.toString()){
            if(invetation.companyReciver?.id == companyId){
                Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text =  "${invetation.companySender?.name!!} has canceled a client invitation")
                }
                }
            }else{
                Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = " you have canceled a client invitation to  ${invetation.companyReciver?.name!!}")
                }
                }
            }
        }else{
            if(invetation.companyReciver?.id == companyId){

            Row {

            Row (
                modifier = Modifier.weight(1f)
            ){
                ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
            }
            Row (
                modifier = Modifier.weight(3f)
            ){
                Text(text = "${invetation.client?.username!!} has canceled a client invitation")
            }
            }
            }
            else{
                Row {

                    Row (
                        modifier = Modifier.weight(1f)
                    ){
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                    }
                    Row (
                        modifier = Modifier.weight(3f)
                    ){
                        Text(text = " you have canceled a client invitation to ${invetation.companyReciver?.name!!}")
                    }
                }
            }
        }


}
@Composable
fun RefuseTypeClient(invetation: Invetation, companyId: Long) {

        if(invetation.type == Type.COMPANY_SEND_CLIENT_COMPANY.toString()){
            if(invetation.companyReciver?.id == companyId){
                Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = " you have refused a client invitation from ${invetation.companySender?.name!!}")
                }
                }
            }else{
                Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = invetation.companyReciver?.name!! + " has refused your client invitation")
                }
            }
                }
        }else{
            if(invetation.companyReciver?.id == companyId) {
                Row {

                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                    }
                    Row(
                        modifier = Modifier.weight(3f)
                    ) {
                        Text(text = "you have refused a client invitation from ${invetation.client?.username!!}")
                    }
                }
            }
            else{
                Row {

                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                    }
                    Row(
                        modifier = Modifier.weight(3f)
                    ) {
                        Text(text = invetation.companyReciver?.name!! + " has refused your client invitation")
                    }
                }
            }
        }


}
@Composable
fun InWaitingTypeClient(invetation: Invetation , companyId: Long, onClicked: (Status) -> Unit) {
        if(invetation.type == Type.COMPANY_SEND_CLIENT_COMPANY.toString()){
            if(invetation.companyReciver?.id == companyId){
                Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Column {

                    Text(text =  "${invetation.companySender?.name!!} has sent a client invitation")
                        Row {

                        Row (
                            modifier = Modifier.weight(1f)
                        ){
                            ButtonSubmit(labelValue = "accept", color = Color.Green, enabled = true) {
                                onClicked(Status.ACCEPTED)
                            }
                        }
                        Row (
                            modifier = Modifier.weight(1f)
                        ){

                            ButtonSubmit(labelValue = "refuse", color = Color.Red, enabled = true) {
                                onClicked(Status.REFUSED)
                        }
                            }
                        }

                    }
                }
                }
            }else{
                Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Column {

                    Text(text = " you have sent a client invitation to ${invetation.companyReciver?.name!!}")
                    ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                        onClicked(Status.CANCELLED)
                    }
                }
                    }
                }
            }
        }else{
            if(invetation.companyReciver?.id == companyId) {
                Row {

                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                    }
                    Row(
                        modifier = Modifier.weight(3f)
                    ) {
                        Column {

                        Text(text = "${invetation.client?.username!!} has sent a client invitation ")
                        Row {
                         Row (
                             modifier = Modifier.weight(1f)
                         ){
                             ButtonSubmit(labelValue = "accept", color = Color.Green, enabled = true) {

                             }
                         }
                            Row (
                                modifier = Modifier.weight(1f)
                            ){

                        ButtonSubmit(labelValue = "refuse", color = Color.Red, enabled = true) {

                            }
                        }
                        }
                        }
                    }
                }
            }
            else{
                Row {

                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                    }
                    Row(
                        modifier = Modifier.weight(3f)
                    ) {
                        Column {

                        Text(text = " you have sent a client invitation to " + invetation.companyReciver?.name!!)
                        ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                            onClicked(Status.CANCELLED)
                        }

                        }
                    }
                }
        }
    }
}
@Composable
fun AcceptTypeClient(invetation: Invetation,companyId: Long) {
        if(invetation.type == Type.COMPANY_SEND_CLIENT_COMPANY.toString()){
            if(invetation.companyReciver?.id == companyId){
                Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companySender?.logo}/company/${invetation.companySender?.user
                            ?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = " you have accepted a client invitation from ${invetation.companySender?.name!!}")
                }
                }
            }else{
                Row {

                Row (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                }
                Row (
                    modifier = Modifier.weight(3f)
                ){
                    Text(text = invetation.companyReciver?.name!! + " has accepted your client invitation")
                }
            }
                }
        }else {
            if (invetation.companyReciver?.id == companyId) {
                Row {

                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.client?.image}/user/${invetation.client?.id}")
                    }
                    Row(
                        modifier = Modifier.weight(3f)
                    ) {
                        Text(text = invetation.client?.username!! + " has accepted your client invitation ")
                    }
                }
            }
            else{
                Row {

                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        ShowImage(image = "${BASE_URL}werehouse/image/${invetation.companyReciver?.logo}/company/${invetation.companyReciver?.user?.id}")
                    }
                    Row(
                        modifier = Modifier.weight(3f)
                    ) {
                        Text(text = invetation.companyReciver?.name!! + " has accepted your client invitation ")
                    }
                }
        }
    }
}

@Composable
fun CompanyCard(company : Company, companyViewModel: CompanyViewModel,articleViewModel: ArticleViewModel, onClicked: () -> Unit) {
    Text(text = company.name,
        modifier = Modifier
            .padding(5.dp)
            .clickable {
                onClicked()
            }
    )
}
@Composable
fun UserCard(user : User, appViewModel : AppViewModel, onClicked : () -> Unit) {

    Text(text = user.username,
        modifier = Modifier.clickable {
            onClicked()
        }
        )
}

@Composable
fun EmptyImage(modifier: Modifier = Modifier) {
    val painter: Painter = painterResource(id = R.drawable.emptyprofile)
    Image(
        painter = painter,
        contentDescription = "empty photo profil",
        modifier = Modifier
            .size(30.dp)
            .clip(
                RoundedCornerShape(10.dp)
            )
    )
}

@Composable
fun BuyHistoryCard(paymentForProviders: PaymentForProviders){
    Box{
        Text(text = paymentForProviders.giveenespece.toString())
    }
}


@Composable
fun LocationServiceDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { /* Prevent dismissing the dialog */ },
        title = { Text(text = "Enable Location Services") },
        text = { Text("Your GPS seems to be disabled. Do you want to enable it?") },
        confirmButton = {
            Button(onClick = {
                onConfirm()
            }) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text("No")
            }
        }
    )
}

@Composable
fun checkLocation(type : AccountType, user : User? , company : Company, context: Context) {
    var isLocationGranted by remember {
        mutableStateOf(false)
    }
    var isGpsEnabled by remember {
        mutableStateOf(context.isGpsEnabled())
    }
    var dialogGps by remember {
        mutableStateOf(false)
    }
    var dialogLocation by remember {
        mutableStateOf(false)
    }
    val isLocationPermissed by remember {
        mutableStateOf(context.hasLocationPermission())
    }
    if(isGpsEnabled && isLocationPermissed){
        Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            context.startService(this)
        }
    }
    else {
        val launcherLocation = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    if (!isGpsEnabled && (type == AccountType.USER && user?.id != null && user.latitude == 0.0) || (type == AccountType.COMPANY && company.id != null && company.latitude == 0.0)) {
                        dialogLocation = false
                        isLocationGranted = true
                        dialogGps = true
                    }
                }
            })
        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(lifecycleOwner) {
            lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_RESUME) {
                        // Check if location permission has been granted
                        if (context.hasLocationPermission() && !context.isGpsEnabled() && (type == AccountType.COMPANY && company.id != null && company.latitude == 0.0) || (type == AccountType.USER && user != null && user.latitude == 0.0)) {
                            dialogGps = true
                        }
                    }
                }
            })
        }
        val locationSettingsLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {
            isGpsEnabled =
                context.isGpsEnabled() // After returning from the location settings, check if GPS is enabled
            if (isGpsEnabled) {
                dialogGps =
                    false        // GPS is enabled, you can dismiss the dialog or take other actions
            } else {
            }
        }  // GPS is still not enabled, you might want to show a message
        val gpsStatusReceiver = remember {
            GpsStatusReceiver { isEnabled ->
                isGpsEnabled = isEnabled
                if (isEnabled) {
                    // GPS is enabled, you can dismiss the dialog or take other actions
                    dialogGps = false
                }
            }
        }
        if (dialogLocation) {
            if ((type == AccountType.COMPANY && company.id != null && company.latitude == 0.0) || (type == AccountType.USER && user?.id != null && user.latitude == 0.0)) {
                PermissionSettingsDialog {
                    dialogLocation = false
                }
            }
        }
        if (dialogGps && !isGpsEnabled) {
            LocationServiceDialog(onDismiss = { dialogGps = false }) {
                locationSettingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }
        DisposableEffect(Unit) {
            val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            context.registerReceiver(gpsStatusReceiver, intentFilter)

            onDispose {
                context.unregisterReceiver(gpsStatusReceiver)
            }
        }
        //location launche effect begin
        var launchTrigger by remember {
            mutableStateOf(false)
        }
        LaunchedEffect(key1 = isGpsEnabled, key2 = type) {
            launchTrigger = !launchTrigger
        }
        LaunchedEffect(key1 = user, key2 = company, key3 = launchTrigger) {
            if ((type == AccountType.USER && user != null && user.latitude == 0.0) || (type == AccountType.COMPANY && company.id != null && company.latitude == 0.0)) {

                if (isLocationGranted) {
                    if (isGpsEnabled) {
                        Intent(context, LocationService::class.java).apply {
                            action = LocationService.ACTION_START
                            context.startService(this)
                        }
                    } else {
                        dialogGps = true
                    }
                } else {
                    launcherLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    launcherLocation.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    dialogLocation = true
                }
            }
        }
    }
}


