package com.aymen.metastore.ui.component

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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Image
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.aymen.metastore.R
import com.aymen.metastore.model.Enum.SubType
import com.aymen.metastore.model.Location.GpsStatusReceiver
import com.aymen.metastore.model.Location.LocationService
import com.aymen.metastore.model.Location.hasLocationPermission
import com.aymen.metastore.model.Location.isGpsEnabled
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.Conversation
import com.aymen.metastore.model.entity.model.Message
import com.aymen.metastore.model.entity.model.PaymentForProviders
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.store.model.Enum.Type
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.ArticleViewModel
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.metastore.model.repository.ViewModel.PointsPaymentViewModel
import com.aymen.metastore.util.BASE_URL
import com.aymen.store.model.Enum.UnitArticle
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.store.model.repository.ViewModel.ShoppingViewModel
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.google.gson.Gson
import kotlinx.coroutines.flow.map
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
        onLoading = {Log.e("showimage","loading")},
        onSuccess = {Log.e("showimage","success")},
        onError = {Log.e("showimage","error")},
        modifier = Modifier
            .padding(5.dp)
            .size(imageSize)
            .clip(CircleShape)
        ,
        contentScale = ContentScale.Crop,

    )
    Log.e("showimage","inside show image $image")
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
fun InputTextField(labelValue: String, label:String, singleLine: Boolean, maxLine : Int, keyboardOptions: KeyboardOptions, onValueChange: (String) -> Unit,
                   onImage: (Uri?)-> Unit,
                  enabled  : Boolean? = true, onImeAction: (File?) -> Unit ) {
    var image by remember {
        mutableStateOf<Uri?>(null)
    }
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val photo = resolveUriToFile(image, context)
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> image = uri }
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
            enabled = enabled ?: true,
            shape = RoundedCornerShape(20.dp),
            value = labelValue,
            label = { Text(label) },
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(
                onSend = {
                    onImeAction(photo)
                }),
            colors = TextFieldDefaults.colors(
                focusedLabelColor = Color.Black,
                cursorColor = Color.Magenta
            ),
            onValueChange = onValueChange,
            singleLine = singleLine,
            maxLines = maxLine,
            leadingIcon = if (label == "Type a message" || label == "Type a comment") {
                {
                    IconButton(onClick = {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) {
                        Icon(imageVector = Icons.Outlined.Image, contentDescription = "")
                    }
                }
            } else {
                null
            },
            trailingIcon = if (label == "Type a message" || label == "Type a comment") {
                {
                    IconButton(onClick = {
                        onImeAction(photo)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = "description"
                        )
                    }
                }
            } else {
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
fun ArticleCardForUser(article : LazyPagingItems<ArticleCompany>) {
    val shoppingViewModel: ShoppingViewModel = hiltViewModel()
    val articleViewModel: ArticleViewModel = hiltViewModel()
    val companyViewModel: CompanyViewModel = hiltViewModel()
    val categoryViewModel: CategoryViewModel = hiltViewModel()
    val appViewModel: AppViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    Row {
        LazyColumn {
            items(
                count = article.itemCount,
                key = article.itemKey { article -> article.id!! },
                ) { index : Int ->
                val art: ArticleCompany? = article[index]
                art?.let {
                    Card(
                        elevation = CardDefaults.cardElevation(6.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Row {
                            Row(
                                modifier = Modifier
                                    .weight(0.8f)
                                    .clickable {
                                        categoryViewModel.setFilter(art.company?.id!!)
//                                        companyViewModel.myCompany = art.company!!
                                        sharedViewModel.setHisCompany(art.company!!)
                                        articleViewModel.companyId = it.company?.id!!
                                        RouteController.navigateTo(Screen.CompanyScreen)
                                    }
                            ) {
                                if (art.company?.logo != null ) {
                                    ShowImage(
                                        image = "${BASE_URL}werehouse/image/${art.company?.logo}/company/${art.company?.user?.id}",
                                        30.dp
                                    )
                                } else {
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
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "verification account",
                                    tint = if(art.company?.metaSeller == true) Color.Green else Color.Cyan
                                )
                                art.company?.let { it1 -> Text(text = it1.name) }
                            }
                            Row(
                                modifier = Modifier.weight(0.2f)
                            ) {
                                Icon(imageVector = if (it.isFav == true) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "favorite",
                                    Modifier.clickable {
                                        articleViewModel.makeItAsFav(it)
                                    }
                                )
                                if (it.likeNumber != null) {
                                    Text(text = "${if (it.likeNumber >= 1000000L) it.likeNumber.div(1000000L) else if (it.likeNumber >= 1000L) it.likeNumber.div(1000L) else it.likeNumber} ${if (it.likeNumber >= 1000000L) "M" else if (it.likeNumber >= 1000L) "K" else ""}")
                                }
                            }
                        }
                        Column(
                            modifier = Modifier.clickable {
                                sharedViewModel.setHisCompany(art.company!!)
                                articleViewModel.assignArticleCompany(art)
                                RouteController.navigateTo(Screen.ArticleDetailScreen)
                            }
                        ) {
                            if ( art.article?.image?.isBlank() == true) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Article,
                                    contentDescription = "article photo"
                                )
                            } else {
                                ShowImage(image = "${BASE_URL}werehouse/image/${art.article?.image}/article/${art.article?.category?.ordinal}")
                            }
                                NormalText(value = art.article?.libelle!!, aligne = TextAlign.Start)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ShowPrice(
                                    cost = it.cost ?: 0.0,
                                    margin = it.sellingPrice!!,
                                    tva = art.article?.tva ?: 0.0
                                )
                                ArticleDetails(
                                    value = it.quantity.toString(),
                                    aligne = TextAlign.Start
                                )
                            }
                        }
                        if(art.company?.metaSeller==true) {
                            Row {
                                ShoppingDialog(
                                    it,
                                    "add to cart",
                                    isOpen = false,
                                    shoppingViewModel = shoppingViewModel
                                ){}
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleCardForSearch(article: ArticleCompany, onClicked: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                onClicked()
            }
    ) {
        ShowImage(image = "${BASE_URL}werehouse/image/${article.article?.image}/article/${article.company?.category?.ordinal}")
        NormalText(value = article.article?.libelle!!, aligne = TextAlign.Start)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
                ShowPrice(
                    cost = article.cost?:0.0,
                    margin = article.sellingPrice!!,
                    tva = article.article?.tva?:0.0
                )

            ArticleDetails(value = article.quantity.toString(), aligne = TextAlign.Start)
        }
    }
}

@Composable
fun AddTypeDialog(isOpen : Boolean,id : Long,isCompany : Boolean, onSelected :(Type) -> Unit) {
    val sharedViewModel: SharedViewModel = hiltViewModel()
    var openDialog by remember {
        mutableStateOf(isOpen)
    }
    var subType by remember {
        mutableStateOf(SubType.OTHER)
    }
    var type by remember {
        mutableStateOf(Type.OTHER)
    }
    var selected by remember {
        mutableStateOf(false)
    }
    val accountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = selected) {
        if (selected){
            if (accountType == AccountType.USER) {
                if (isCompany) {
                    when (subType) {
                        SubType.CLIENT -> {
                            type = Type.USER_SEND_CLIENT_COMPANY
                        }
                        SubType.WORKER -> {
                            type = Type.USER_SEND_WORKER_COMPANY
                        }
                        else -> {
                        }
                    }
                }
            } else {
                if (isCompany) {
                    when (subType) {
                        SubType.CLIENT -> {
                            type = Type.COMPANY_SEND_CLIENT_COMPANY
                        }
                        SubType.PROVIDER -> {
                            type = Type.COMPANY_SEND_PROVIDER_COMPANY
                        }
                        SubType.PARENT -> {
                            type = Type.COMPANY_SEND_PARENT_COMPANY
                        }
                        else ->
                            Type.OTHER
                    }
                } else {
                    when (subType) {
                        SubType.CLIENT -> {
                            type = Type.COMPANY_SEND_PROVIDER_USER
                        }
                        SubType.WORKER -> {
                            type = Type.COMPANY_SEND_WORKER_USER
                        }
                        else ->
                            Type.OTHER
                    }
                }
                if (type != Type.OTHER) {
                    Log.e("type", "type $type")
                }
            }

        onSelected(type)
    }
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
                    if (accountType == AccountType.USER) {
                        ButtonSubmit(
                            labelValue = "add as provider",
                            color = Color.Green,
                            enabled = true
                        ) {
                            selected = true
                            openDialog = false
                            subType = SubType.PROVIDER
                        }
                    } else {
                        if (isCompany) {
                            ButtonSubmit(
                                labelValue = "add as provider",
                                color = Color.Green,
                                enabled = true
                            ) {
                                selected = true
                                openDialog = false
                                subType = SubType.PROVIDER
                            }
                            ButtonSubmit(
                                labelValue = "add as parent",
                                color = Color.Green,
                                enabled = true
                            ) {
                                selected = true
                                openDialog = false
                                subType = SubType.PARENT
                            }
                        } else {
                            ButtonSubmit(
                                labelValue = "add as worker",
                                color = Color.Green,
                                enabled = true
                            ) {
                                selected = true
                                openDialog = false
                                subType = SubType.WORKER
                            }
                        }
                        ButtonSubmit(
                            labelValue = "add as client",
                            color = Color.Green,
                            enabled = true
                        ) {
                            selected = true
                            openDialog = false
                            subType = SubType.CLIENT
                        }
                    }
                }
                }
            }
        }

}


@Composable
fun SendPointDialog(isOpen : Boolean, user: User, client : Company) {
    val pointsPaymentViewModel : PointsPaymentViewModel = hiltViewModel()
    var openDialog by remember {
        mutableStateOf(isOpen)
    }
    var dinars by remember {
        mutableDoubleStateOf(0.0)
    }
    var points by remember {
        mutableDoubleStateOf(0.0)
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
            val pt = BigDecimal.valueOf(dinars).divide(BigDecimal(3),2, RoundingMode.HALF_UP)
            points = pt.toDouble()
            val eq = BigDecimal.valueOf(dinars).multiply(BigDecimal(1.1))
            equalsDinars = eq.setScale(2,RoundingMode.HALF_UP).toDouble()
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
                    .multiply(BigDecimal(10))
                    .divide(BigDecimal("11"), 2, RoundingMode.HALF_UP)

                // Calculate points and round to 2 decimal places
                val point = dinar.divide(BigDecimal(3), 2, RoundingMode.HALF_UP)

                dinars = dinar.toDouble()
                 points = point.toDouble()
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
                        labelValue = if(dinars == 0.0) "" else dinars.toString(),
                        label = "how many do you have money?",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            dinars = it.toDouble()
                            launchDinar = true
                            launchPoints = false
                            launchEquals = false
                        }
                        , onImage = {}
                        ,true
                    ) {

                    }
                    InputTextField(
                        labelValue = if(points == 0.0) "" else points.toString(),
                        label = "How many points do you want to buy?",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            points = it.toDouble()
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
fun  ShoppingDialog(article : ArticleCompany, label: String, isOpen : Boolean,shoppingViewModel : ShoppingViewModel, onClose: (Boolean) -> Unit) {
    var openDialog by remember {
        mutableStateOf(isOpen)
    }
    if(label != ""){
    ButtonSubmit(labelValue = label, color = Color.Blue, enabled = true) {
        openDialog = true
    }
    }

    var showFeesDialog by remember { mutableStateOf(false) }
     var restBalance by remember { mutableStateOf(BigDecimal.ZERO) }
    if(openDialog){
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val accountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
    val myCompanyBalance by sharedViewModel.company.map { it.balance }.collectAsStateWithLifecycle(0.0)
    val myUserBalance by sharedViewModel.user.map { it.balance }.collectAsStateWithLifecycle(0.0)

    var isCompany by remember {
        mutableStateOf(false)
    }

        LaunchedEffect(key1 = myUserBalance, key2 = myCompanyBalance) {
            restBalance = if (isCompany) BigDecimal(myCompanyBalance!!).setScale(2, RoundingMode.HALF_UP)
            else BigDecimal(myUserBalance!!).setScale(2, RoundingMode.HALF_UP)
        }
    val gson = Gson()

    var balance by remember { mutableDoubleStateOf(0.0) }

        var cost by remember {
            mutableStateOf(BigDecimal(0.0))
        }
        LaunchedEffect(key1 = shoppingViewModel.qte) {
            cost = BigDecimal(shoppingViewModel.qte).multiply(BigDecimal(article.sellingPrice!!)).setScale(2, RoundingMode.HALF_UP)
            isCompany = accountType == AccountType.COMPANY
                restBalance =
                    if (isCompany) {
                        balance = myCompanyBalance!!
                        BigDecimal(myCompanyBalance!!).subtract(cost).subtract(shoppingViewModel.cost)
                            .setScale(2, RoundingMode.HALF_UP)
                    } else {
                        balance = myUserBalance!!
                        BigDecimal(myUserBalance!!).subtract(cost).subtract(shoppingViewModel.cost).setScale(2, RoundingMode.HALF_UP)

                    }
        }

        Dialog(
            onDismissRequest = {openDialog = false
                shoppingViewModel.remiseAZero()
                onClose(false)
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
                        ShowImage(image = "${BASE_URL}werehouse/image/${article.article?.image}/article/${article.company?.category!!.ordinal}")
                        Column {

                        Text(text = if( restBalance!! <BigDecimal.ZERO || (shoppingViewModel.qte == 0.0 && restBalance!! <= 1.toBigDecimal())) "sold insufficient $restBalance DT" else "$restBalance DT")
                            Text(text = article.article?.libelle!!)
                        }
                    }
                    Row {
                        InputTextField(
                            labelValue = shoppingViewModel.rawInput,
                            label = "Quantity",
                            singleLine = true,
                            maxLine = 1,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            onValueChange = {
                                if (article.unit == UnitArticle.U) {
                                    if (it.matches(Regex("^[0-9]*$"))) {
                                        shoppingViewModel.rawInput = it
                                        shoppingViewModel.qte = it.toDoubleOrNull() ?: 0.0
                                    }
                                } else {
                                    if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                                        val normalizedInput = it.replace(',', '.')
                                        shoppingViewModel.rawInput = normalizedInput
                                        if (normalizedInput.startsWith(".")) {
                                            shoppingViewModel.rawInput = normalizedInput
                                            shoppingViewModel.qte = 0.0
                                        } else
                                        if (normalizedInput.endsWith(".")) {
                                            shoppingViewModel.qte = normalizedInput.let { inp ->
                                                if (inp.toDouble() % 1.0 == 0.0) inp.toDouble() else 0.0
                                            }
                                        } else {
                                            shoppingViewModel.qte = normalizedInput.toDoubleOrNull() ?: 0.0
                                        }
                                    }
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
                        if((restBalance!! >=BigDecimal.ZERO && shoppingViewModel.qte != 0.0) ) {
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = "add more",
                                    color = Color.Blue,
                                    enabled = true
                                ) {
                                    openDialog = false
                                    onClose(false)
                                    shoppingViewModel.randomArticle = article
                                    shoppingViewModel.submitShopping()
                                     gson.toJson(shoppingViewModel.orderArray)
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
                                    shoppingViewModel.randomArticle = article
                                    shoppingViewModel.submitShopping()
                                        openDialog = false
                                    if(shoppingViewModel.delivery && shoppingViewModel.cost.add(cost) <= BigDecimal(30)) {

                                        showFeesDialog = true
                                    }
                                    else shoppingViewModel.sendOrder(-1,restBalance!!)

                                }

                            }
                        }
                        Row (
                            modifier = Modifier.weight(1f)
                        ){

                            ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                                openDialog = false
                                onClose(false)
                                shoppingViewModel.remiseAZero()
                            }
                        }

                        }
                        Row {
                            CheckBoxComp(
                                value = "Delivery",
                                free = if(cost.add(shoppingViewModel.cost)>=BigDecimal(30))"free" else null,
                                pay = if(cost.add(shoppingViewModel.cost)<BigDecimal(30) && shoppingViewModel.delivery && shoppingViewModel.qte != 0.0)" 3dt" else null,
                                shoppingViewModel.delivery
                            ) { isChecked ->
                                shoppingViewModel.delivery = isChecked
                            }
                        }
                    }
                }
            }
        }
    if(showFeesDialog){
        ShowFeesDialog(isOpen = true) {submitfees ->
            if(submitfees) {
                val balance = restBalance.subtract(BigDecimal((3)))
                shoppingViewModel.sendOrder(-1, balance)
            }
            
            showFeesDialog = false
        }
    }
    }

@Composable
fun ShowFeesDialog(isOpen: Boolean, onClose: (Boolean) -> Unit) {
    if(isOpen){
        Dialog(
            onDismissRequest = { onClose(false) }
        ){
            Column (  modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally){
                Row (modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center){
                 Text(text = "3dt for delivery"
                     , modifier = Modifier.background(Color.White),
                     style = TextStyle(Color.Red, fontSize = 30.sp)
                 )
                }
                Row {
                  Row(modifier = Modifier.weight(1f)) {

                ButtonSubmit(labelValue = "Ok" , color = Color.Green, enabled = true) {
                    onClose(true)
                  }
                }
                    Row(modifier = Modifier.weight(1f)) {

                ButtonSubmit(labelValue = "CANCEL" , color = Color.Red , enabled = true) {
                    onClose(false)
                    }
                }
                }
            }
        }
    }
}

@Composable
fun UpdateImageDialog(isOpen: Boolean, onClose: () -> Unit) {
    val appViewModel : AppViewModel = hiltViewModel()
    val sViewModel : SharedViewModel = hiltViewModel()
    val company by sViewModel.company.collectAsStateWithLifecycle()
    val user by sViewModel.user.collectAsStateWithLifecycle()
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
                        ShowImage(image = "$BASE_URL/${company.logo}/company/${user.id}",35.dp)
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
fun OrderShow(order: PurchaseOrderLine) {
    val shoppingViewModel: ShoppingViewModel = hiltViewModel()
    var showDialog by remember { mutableStateOf(false) }
    Row {
        Column(
            modifier = Modifier
                .clickable {
                    showDialog = true
                }
                .weight(0.9f)
        ) {
            order.article?.article?.let { Text(text = it.libelle?:"") }
            Text(text = order.quantity.toString())
            order.comment?.let { Text(text = it) }
            if (showDialog) {
                shoppingViewModel.randomArticle = order.article!!
                shoppingViewModel.qte = order.quantity!!
                shoppingViewModel.rawInput = order.quantity.toString()
                shoppingViewModel.comment = order.comment?:""
                shoppingViewModel.delivery = order.delivery!!
                ShoppingDialog(
                    article = order.article,
                    label = "",
                    isOpen = showDialog,
                    shoppingViewModel = shoppingViewModel
                ){
                    showDialog = it
                }
            }
        }
    }
}

@Composable
fun CompanyCard(company : Company, onClicked: () -> Unit) {
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

    Text(text = user.username?:"",
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
        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize()
        ) {
            Column {
                Text(text = paymentForProviders.giveenespece.toString())
                Text(text = paymentForProviders.lastModifiedDate)
                Text(text = paymentForProviders.purchaseOrderLine?.purchaseorder?.orderNumber.toString())
            }
        }
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
fun CheckLocation(type: AccountType, user: User?, company: Company, context: Context) {
    var isGpsEnabled by remember { mutableStateOf(context.isGpsEnabled()) }
    var dialogGps by remember { mutableStateOf(false) }
    var dialogLocation by remember { mutableStateOf(false) }
    var isLocationGranted by remember { mutableStateOf(context.hasLocationPermission()) }

    // Handle location permission launcher
    val launcherLocation = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isLocationGranted = isGranted
        if (isGranted && !isGpsEnabled) {
            dialogGps = true
        }
    }

    // Handle GPS settings launcher
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        isGpsEnabled = context.isGpsEnabled()
        if (isGpsEnabled) {
            dialogGps = false
        }
    }

    // Register GPS status changes dynamically
    val gpsStatusReceiver = remember {
        GpsStatusReceiver { isEnabled ->
            isGpsEnabled = isEnabled
            if (isEnabled) {
                dialogGps = false
                // Start location service if necessary
                if (isLocationGranted) {
                    Intent(context, LocationService::class.java).apply {
                        action = LocationService.ACTION_START
                        context.startService(this)
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(gpsStatusReceiver, intentFilter)

        onDispose {
            context.unregisterReceiver(gpsStatusReceiver)
        }
    }

    // Handle location permission and GPS dialogs
    if (dialogLocation) {
        PermissionSettingsDialog {
            dialogLocation = false
        }
    }

    if (dialogGps && !isGpsEnabled) {
        LocationServiceDialog(onDismiss = { dialogGps = false }) {
            locationSettingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    // Initial check for location permissions and GPS
    LaunchedEffect(isGpsEnabled, isLocationGranted) {
        if ((type == AccountType.USER && (user?.latitude == 0.0 || user?.longitude == 0.0)) ||
            (type == AccountType.COMPANY && (company.latitude == 0.0 || company.longitude == 0.0))) {
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
                dialogLocation = true
            }
        }
    }
}


//@Composable
//fun CheckLocation(type : AccountType, user : User?, company : Company, context: Context) {
//    var isGpsEnabled by remember { mutableStateOf(context.isGpsEnabled()) }
//    var dialogGps by remember { mutableStateOf(false) }
//    var dialogLocation by remember { mutableStateOf(false) }
//    var isLocationGranted by remember { mutableStateOf(false) }
//
//    val isLocationPermissed by remember { mutableStateOf(context.hasLocationPermission()) }
//    if(isGpsEnabled && isLocationPermissed){
//        Intent(context, LocationService::class.java).apply {
//            action = LocationService.ACTION_START
//            context.startService(this)
//        }
//    }
//    else {
//        val launcherLocation = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.RequestPermission(),
//            onResult = { isGranted ->
//                if (isGranted) {
//                    if (!isGpsEnabled && (type == AccountType.USER && user?.id != null && user.latitude == 0.0) || (type == AccountType.COMPANY && company.id != null && company.latitude == 0.0)) {
//                        dialogLocation = false
//                        isLocationGranted = true
//                        dialogGps = true
//                    }
//                }
//            })
//        val lifecycleOwner = LocalLifecycleOwner.current
//        LaunchedEffect(lifecycleOwner) {
//            lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
//                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
//                    if (event == Lifecycle.Event.ON_RESUME) {
//                        // Check if location permission has been granted
//                        if (context.hasLocationPermission() && !context.isGpsEnabled() && (type == AccountType.COMPANY && company.id != null && company.latitude == 0.0) || (type == AccountType.USER && user != null && user.latitude == 0.0)) {
//                            dialogGps = true
//                        }
//                    }
//                }
//            })
//        }
//        val locationSettingsLauncher = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.StartActivityForResult()
//        ) {
//            isGpsEnabled =
//                context.isGpsEnabled() // After returning from the location settings, check if GPS is enabled
//            if (isGpsEnabled) {
//                dialogGps =
//                    false        // GPS is enabled, you can dismiss the dialog or take other actions
//            } else {
//            }
//        }  // GPS is still not enabled, you might want to show a message
//        val gpsStatusReceiver = remember {
//            GpsStatusReceiver { isEnabled ->
//                isGpsEnabled = isEnabled
//                if (isEnabled) {
//                    // GPS is enabled, you can dismiss the dialog or take other actions
//                    dialogGps = false
//                }
//            }
//        }
//        if (dialogLocation) {
//            if ((type == AccountType.COMPANY && company.id != null && company.latitude == 0.0) || (type == AccountType.USER && user?.id != null && user.latitude == 0.0)) {
//                PermissionSettingsDialog {
//                    dialogLocation = false
//                }
//            }
//        }
//        if (dialogGps && !isGpsEnabled) {
//            LocationServiceDialog(onDismiss = { dialogGps = false }) {
//                locationSettingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//            }
//        }
//        DisposableEffect(Unit) {
//            val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
//            context.registerReceiver(gpsStatusReceiver, intentFilter)
//
//            onDispose {
//                context.unregisterReceiver(gpsStatusReceiver)
//            }
//        }
//        //location launche effect begin
//        var launchTrigger by remember {
//            mutableStateOf(false)
//        }
//        LaunchedEffect(key1 = isGpsEnabled, key2 = type) {
//            launchTrigger = !launchTrigger
//        }
//        LaunchedEffect(key1 = user, key2 = company, key3 = launchTrigger) {
//            if ((type == AccountType.USER && user != null && user.latitude == 0.0) || (type == AccountType.COMPANY && company.id != null && company.latitude == 0.0)) {
//
//                if (isLocationGranted) {
//                    if (isGpsEnabled) {
//                        Intent(context, LocationService::class.java).apply {
//                            action = LocationService.ACTION_START
//                            context.startService(this)
//                        }
//                    } else {
//                        dialogGps = true
//                    }
//                } else {
//                    launcherLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//                    launcherLocation.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
//                    dialogLocation = true
//                }
//            }
//        }
//    }
//}


