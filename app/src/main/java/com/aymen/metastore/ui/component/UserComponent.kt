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
import android.widget.Space
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
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
import com.aymen.metastore.model.repository.ViewModel.SignInViewModel
import com.aymen.metastore.model.repository.ViewModel.StateHandlerViewModel
import com.aymen.metastore.ui.screen.user.CustomSpacer
import com.aymen.metastore.ui.screen.user.FreeDeliveryCard
import com.aymen.metastore.ui.screen.user.ShowBalance
import com.aymen.metastore.util.BASE_URL
import com.aymen.metastore.util.IMAGE_URL_ARTICLE
import com.aymen.metastore.util.IMAGE_URL_COMPANY
import com.aymen.store.model.Enum.UnitArticle
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.store.model.repository.ViewModel.ShoppingViewModel
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale


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
fun ShowImage(image: String, height: Dp? = null, width : Dp? = null, shape : RoundedCornerShape? = null){
    val imageHeight = height ?: 20.dp
    val imageWidth = width ?: 20.dp
    val imageShape = shape ?: CircleShape
    AsyncImage(
        model = image,
        contentDescription = "article image",
        onLoading = {Log.e("showimage","loading")},
        onSuccess = {Log.e("showimage","success")},
        onError = {Log.e("showimage","error")},
        modifier = Modifier
            .padding(2.dp)
            .size(height = imageHeight, width = imageWidth)
            .clip(imageShape)
        ,
        contentScale = ContentScale.Crop,

    )
}
@Composable
fun ShowImageForHome(image: String, shape : RoundedCornerShape? = null, width: Dp){
    val imageShape = shape ?: CircleShape
    AsyncImage(
        model = image,
        contentDescription = "article image",
        onLoading = {Log.e("showimage","loading")},
        onSuccess = {Log.e("showimage","success")},
        onError = {Log.e("showimage","error")},
        modifier = Modifier
            .padding(2.dp)
            .fillMaxHeight()
            .width(width)
            .clip(imageShape)
        ,
        contentScale = ContentScale.Crop,

    )
}


@SuppressLint("DefaultLocale")
@Composable
fun ShowPrice( priceHt : Double, tva : Double){

    val price = BigDecimal(priceHt).multiply(BigDecimal(1).add(BigDecimal(tva).divide(BigDecimal(100.0))))
    val formattedPrice = String.format("%.2f", price)
    Text( text = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
            append(formattedPrice)
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
            append(stringResource(id = R.string.dt))
        }
    },
        fontSize = 16.sp)
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
            leadingIcon = if (label == "Type a comment") {
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
            trailingIcon = if ( label == "Type a comment") {
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
fun ButtonSubmit(labelValue: String, color: Color,enabled:Boolean, tintColor: Color? = Color.White, clickAction: () -> Unit) {
    Row {
    Button(
        onClick =  clickAction ,
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(color),
        enabled = enabled
    ) {
        Box (
            contentAlignment = Alignment.Center
        ){
            Text(text = labelValue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = tintColor?:Color.White
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
    val signInViewModel: SignInViewModel = hiltViewModel()
    val categoryViewModel: CategoryViewModel = hiltViewModel()
    val appViewModel: AppViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val stateViewModel : StateHandlerViewModel = hiltViewModel()
    val scrollState = rememberLazyListState()
    val enabledGps by appViewModel.enbaledGps.collectAsStateWithLifecycle()
    val triggerLocationCheck by signInViewModel.showCheckLocationDialog.collectAsStateWithLifecycle()
    val triggerLocationCheck2 by appViewModel.showCheckLocationDialog.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        scrollState.scrollToItem(stateViewModel.getScrollPosition())
    }
    DisposableEffect(Unit) {
        onDispose {
            stateViewModel.saveScrollPosition(scrollState.firstVisibleItemIndex)
        }
    }
    Row {
        LazyColumn(state = scrollState) {
            item {
                Row (
                    modifier = Modifier.padding(8.dp),
                    Arrangement.SpaceEvenly
                ){
                    Row (
                        modifier = Modifier.weight(1f)
                    ){
                     FreeDeliveryCard(firstLine = R.string.free_delivery, secondLine = R.string.over, thirdLine = R.string.dt,
                        value = 30, Color(0xFFDFF7E5),Color(0xFF01BC59) ,painterResource(id = R.drawable.freedelivery) )
                    }
                    Row (
                        modifier = Modifier.weight(1f)
                    ) {

                        FreeDeliveryCard(firstLine = R.string.all_year, secondLine = R.string.upto,R.string.percent,
                        value = 15, Color(0xFF01BC59),Color.White,painterResource(id = R.drawable.discount_tag_02))
                    }
                }
            }
            items(
                count = article.itemCount,
                key = article.itemKey { article -> article.id!! },
                ) { index : Int ->
                val art: ArticleCompany? = article[index]
                art?.let {
                    Card(
                        elevation = CardDefaults.cardElevation(6.dp),
                        modifier = Modifier
                            .padding(8.dp)
                            .height(160.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .height(100.dp)
                                    .fillMaxWidth()
                                    .weight(8f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(4f)
                                        .clickable {
                                            sharedViewModel.setHisCompany(art.company!!)
                                            articleViewModel.assignArticleCompany(art)
                                            RouteController.navigateTo(Screen.ArticleDetailScreen)
                                        }
                                ) {
                                    // Article Image or Default Icon
                                    if (art.article?.image.isNullOrEmpty()) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Article,
                                            contentDescription = "article photo",
                                            modifier = Modifier
                                                .size(100.dp) // Adjust size as needed
                                                .align(Alignment.Center) // Center alignment if no image
                                        )
                                    } else {
                                        ShowImageForHome(
                                            image = String.format(IMAGE_URL_ARTICLE, art.article?.image, art.article?.category?.ordinal),
                                            shape = RoundedCornerShape(10.dp),
                                            width = 125.dp
                                        )
                                    }
                                    Row (
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    ) {
                                        Icon(
                                            imageVector = if (it.isFav == true) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                            contentDescription = "favorite",
                                            modifier = Modifier
                                                .size(26.dp)
                                                .background(
                                                    Color.White,
                                                    shape = CircleShape
                                                )
                                                .padding(4.dp)
                                                .clickable {
                                                    articleViewModel.makeItAsFav(it)
                                                }
                                        )
                                        if (it.likeNumber != null) {
                                            Text(
                                                text = "${if (it.likeNumber >= 1_000_000L) it.likeNumber / 1_000_000L else if (it.likeNumber >= 1_000L) it.likeNumber / 1_000L else it.likeNumber} ${if (it.likeNumber >= 1_000_000L) "M" else if (it.likeNumber >= 1_000L) "K" else ""}",
                                                modifier = Modifier
                                                    .padding(4.dp)
                                            )
                                        }
                                    }
                                }

                                Row (
                                    modifier = Modifier.weight(6f)
                                ){
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Row (
                                            modifier = Modifier
                                                .clickable {
                                                    categoryViewModel.setFilter(art.company?.id!!)
                                                    sharedViewModel.setHisCompany(art.company!!)
                                                    articleViewModel.companyId = it.company?.id!!
                                                    RouteController.navigateTo(Screen.CompanyScreen)
                                                },
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ){
                                            if (art.company?.logo != null ) {
                                                ShowImage(
                                                    image = String.format(IMAGE_URL_COMPANY,art.company?.logo,art.company?.user?.id)
                                                )
                                            } else {
                                                NotImage()
                                            }
                                            Row {

                                            art.company?.let { it1 -> Text(text = it1.name,fontSize = 14.sp) }
                                            Icon(
                                                imageVector = Icons.Default.Verified,
                                                contentDescription = "verification account",
                                                tint = if(art.company?.metaSeller == true) Color.Green else Color.Cyan,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = art.article?.libelle!!,
                                                modifier = Modifier
                                                    .padding(2.dp)
                                                    .fillMaxWidth()
                                                ,
                                                style = TextStyle(
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    fontFamily = FontFamily.Serif
                                                )
                                                , color = Color.Black,
                                            )

                                        Spacer(modifier = Modifier.height(8.dp))
                                        ShowPrice(
                                            priceHt = it.sellingPrice!!,
                                            tva = art.article?.tva ?: 0.0
                                        )
                                    }

                                }
                            }
                            if(art.company?.metaSeller==true) {
                                Row(
                                    modifier = Modifier
                                        .weight(2f)
                                        .fillMaxWidth()
                                ) {
                                    ShoppingDialog(
                                        it,
                                        label = "add",
                                        isOpen = false,
                                        shoppingViewModel = shoppingViewModel,
                                        null
                                    ){}
                                }
                            }
                        }
                    }
                }
            }
            item {
                if(article.itemCount == 0) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                        if (triggerLocationCheck || triggerLocationCheck2)
                            Text(text = "please enable GPS to see all our products")
                        if (article.loadState.refresh == LoadState.Loading || enabledGps)
                                LodingShape()
                        }
                }
            }
        }
    }
}

@Composable
fun NotImage(modifier: Modifier = Modifier) {
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
        ShowImage(image = String.format(IMAGE_URL_ARTICLE,article.article?.image,article.article?.category?.ordinal))
        NormalText(value = article.article?.libelle!!, aligne = TextAlign.Start)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
                ShowPrice(
                    priceHt = article.sellingPrice!!,
                    tva = article.article?.tva?:0.0
                )

            ArticleDetails(value = article.quantity.toString(), aligne = TextAlign.Start)
        }
    }
}

@Composable
fun AddTypeDialog(isOpen : Boolean,id : Long,isCompany : Boolean,hisClient : Boolean, hisProvider : Boolean,hisParent : Boolean , hisWorker : Boolean ,  onSelected :(Type, Boolean) -> Unit) {
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
    var isDeleted by remember {
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

        onSelected(type, isDeleted)
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
                            labelValue = if(!hisProvider)"add as provider" else "delete as provider",
                            color = Color.Green,
                            enabled = true
                        ) {
                            isDeleted = hisProvider
                            subType = SubType.CLIENT
                            openDialog = false
                            selected = true
                        }
                        ButtonSubmit(
                            labelValue = if(!hisWorker)"add as worker" else "delete as worker",
                            color = Color.Green,
                            enabled = true
                        ) {
                            isDeleted = hisWorker
                            subType = SubType.WORKER
                            openDialog = false
                            selected = true
                        }
                    } else {
                        if (isCompany) {
                                ButtonSubmit(
                                    labelValue = if (!hisProvider)"add as provider" else "delete as provider",
                                    color = Color.Green,
                                    enabled = true
                                ) {
                                    isDeleted = hisProvider
                                    subType = SubType.PROVIDER
                                    openDialog = false
                                    selected = true
                                }
                            ButtonSubmit(
                                labelValue = if(!hisParent)"add as parent" else "delete as parent",
                                color = Color.Green,
                                enabled = true
                            ) {
                                isDeleted = hisParent
                                subType = SubType.PARENT
                                openDialog = false
                                selected = true
                            }
                        } else {
                            ButtonSubmit(
                                labelValue = if(!hisWorker)"add as worker" else "delete as worker",
                                color = Color.Green,
                                enabled = true
                            ) {
                                isDeleted = hisWorker
                                subType = SubType.WORKER
                                openDialog = false
                                selected = true
                            }
                        }
                            ButtonSubmit(
                                labelValue = if(!hisClient)"add as client" else "delete as client",
                                color = Color.Green,
                                enabled = true
                            ) {
                                isDeleted = hisClient
                                subType = SubType.CLIENT
                                openDialog = false
                                selected = true
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
fun  ShoppingDialog(article : ArticleCompany, label: String, isOpen : Boolean,shoppingViewModel : ShoppingViewModel, quantity : Double?,
                    commentaire : String ? = "", onClose: (Boolean) -> Unit) {
    var openDialog by remember {
        mutableStateOf(isOpen)
    }

    var rawInput by remember { mutableStateOf(quantity?:1) }
    var qte by remember { mutableDoubleStateOf(quantity?:1.0) }
    val oldqte by remember { mutableDoubleStateOf(quantity?:0.0) }
    var comment by remember { mutableStateOf(commentaire) }
    if(label != "")
         Button(
            onClick = {
                shoppingViewModel.resetPurchaseOrderLine()
                openDialog = true
                      },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 1.dp)
                .clip(shape = RoundedCornerShape(8.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF01BC59),
                contentColor = Color.White
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.shopping_basket_add_01),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.add_to_cart),
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            )

        }


    var showFeesDialog by remember { mutableStateOf(false) }
     var restBalance by remember { mutableStateOf(BigDecimal.ZERO) }
    if(openDialog) {
        val sharedViewModel: SharedViewModel = hiltViewModel()
        val accountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
        val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()
        val myUser by sharedViewModel.user.collectAsStateWithLifecycle()
        var isChaked by remember {
            mutableStateOf(true)
        }
        var isCompany by remember {
            mutableStateOf(false)
        }
        var enableButtons by remember {
            mutableStateOf(true)
        }
        val gson = Gson()

        var balance by remember { mutableDoubleStateOf(0.0) }

        var cost by remember {
            mutableStateOf(BigDecimal(0.0))
        }
        LaunchedEffect(key1 = shoppingViewModel.delivery) {
            shoppingViewModel.calculateCost()
        }
        LaunchedEffect(key1 = qte, key2 = shoppingViewModel.delivery) {
            val articelPriceTtc = BigDecimal(article.sellingPrice!!).multiply(
                BigDecimal(1).add(
                    BigDecimal(
                        article.article?.tva ?: 0.0
                    ).divide(BigDecimal(100))
                )
            ).setScale(2, RoundingMode.HALF_UP)
            cost = BigDecimal(qte).multiply(articelPriceTtc)
                .setScale(2, RoundingMode.HALF_UP)
            val oldcost = BigDecimal(oldqte).multiply(articelPriceTtc)
                .setScale(2, RoundingMode.HALF_UP)
            isCompany = accountType == AccountType.COMPANY

            balance = if (isCompany) {
                myCompany.balance!!
            } else {
                myUser.balance!!
            }
             shoppingViewModel.deliveryFee =
                 if (!shoppingViewModel.delivery ||
                     cost.add(shoppingViewModel.cost) <= BigDecimal(0.00) ||
                     cost.add(shoppingViewModel.cost).subtract(oldcost) >= BigDecimal(30)) BigDecimal.ZERO
                 else BigDecimal(3)
            restBalance = BigDecimal(balance).add(oldcost).subtract(cost).subtract(shoppingViewModel.cost)
                .setScale(2, RoundingMode.HALF_UP)

            enableButtons = restBalance.subtract(shoppingViewModel.deliveryFee) >= BigDecimal.ZERO
        }

        Dialog(
            onDismissRequest = {
                openDialog = false
                shoppingViewModel.remiseAZero()
                onClose(false)
            }
        ) {
            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                Column {
                    Row {
                        Button(onClick = {
                                openDialog = false
                                onClose(false)
                                shoppingViewModel.remiseAZero()
                        },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color(0xFF01BC59)
                            )                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_turn_backward),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(id = R.string.back), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = stringResource(id = R.string.your_current_balance), fontSize = 14.sp , fontWeight = FontWeight.Medium, modifier = Modifier.padding(5.dp))
                            }
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ShowBalance(balance = restBalance)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row {

                            Row(
                                modifier = Modifier.height(70.dp)
                            ) {
                                if (article.article?.image.isNullOrEmpty()) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Article,
                                        contentDescription = "article photo",
                                        modifier = Modifier
                                            .size(100.dp) // Adjust size as needed
                                    )
                                } else {
                                    ShowImageForHome(
                                        image = String.format(
                                            IMAGE_URL_ARTICLE,
                                            article.article?.image,
                                            article.article?.category?.ordinal
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        width = 125.dp
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    if (article.company?.logo != null) {
                                        ShowImage(
                                            image = String.format(
                                                IMAGE_URL_COMPANY,
                                                article.company?.logo,
                                                article.company?.user?.id
                                            )
                                        )
                                    } else {
                                        NotImage()
                                    }
                                    Row {

                                        article.company?.let { it1 ->
                                            Text(
                                                text = it1.name,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "verification account",
                                            tint = if (article.company?.metaSeller == true) Color.Green else Color.Cyan,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = article.article?.libelle!!,
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .fillMaxWidth(),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = FontFamily.Serif
                                    ),
                                    color = Color.Black,
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                ShowPrice(
                                    priceHt = article.sellingPrice!!,
                                    tva = article.article?.tva ?: 0.0
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = stringResource(id = R.string.quantity_unit), fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp) ,
                                fontWeight = FontWeight.Light)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    IconButton(onClick = {
                                        if(qte >0)
                                            qte -= 1
                                        rawInput = qte.toString()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Outlined.RemoveCircleOutline,
                                            contentDescription = null
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.weight(4f)
                                    , verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Row {
                                    Text(text = qte.toString(), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                    }
                                    Row {

                                    if (article.unit == UnitArticle.U)
                                        Text(text = stringResource(id = R.string.item), fontSize = 14.sp , fontWeight = FontWeight.Light)
                                    else
                                        Text(text = article.unit?.name?.lowercase(Locale.ROOT)!!, fontSize = 14.sp , fontWeight = FontWeight.Light)
                                    }
                                }
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    IconButton(onClick = {
                                        qte += 1
                                        rawInput = qte.toString()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Outlined.AddCircleOutline,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Column {
                        Text(text = stringResource(id = R.string.comment), fontSize = 14.sp , fontWeight = FontWeight.ExtraLight, modifier = Modifier.padding(horizontal = 8.dp))
                        OutlinedTextField(value = comment!!, placeholder = {
                            stringResource(
                                id = R.string.type_a_comment
                            )
                        }, onValueChange = {
                            comment = it
                        },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(checked = !shoppingViewModel.delivery, onCheckedChange = {
                                    isChaked = !it
                                    shoppingViewModel.delivery = !shoppingViewModel.delivery
                                })
                            }
                            Row(
                                modifier = Modifier.weight(9f)
                            ) {
                                Column {
                                    Text(text = stringResource(id = R.string.self_pickup), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 5.dp))
                                    Text(text = stringResource(id = R.string.self_pickup_phrase), fontSize = 12.sp, fontWeight = FontWeight.ExtraLight, modifier = Modifier.padding(horizontal = 5.dp))
                                }
                            }
                        }
                        Row (

                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ){
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(checked = shoppingViewModel.delivery, onCheckedChange = {
                                    isChaked = it
                                    shoppingViewModel.delivery = !shoppingViewModel.delivery
                                })
                            }
                            Row(
                                modifier = Modifier.weight(9f)
                            ) {
                                Column {
                                    Text(text = stringResource(id = R.string.delivery), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 5.dp))
                                    Text(text = stringResource(id = R.string.delivery_phrase), fontSize = 12.sp, fontWeight = FontWeight.ExtraLight, modifier = Modifier.padding(horizontal = 5.dp))
                                }
                            }
                        }
                    }
                    CustomSpacer()
                    Column (
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ){
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.prix_tot))
                                append(" ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append(cost.toString())
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append(stringResource(id = R.string.dt))
                                }
                            },
                            fontSize = 14.sp
                        )
                        CustomSpacer()
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(id = R.string.delivery_fees))
                                append(" ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append(shoppingViewModel.deliveryFee.toString())
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append(stringResource(id = R.string.dt))
                                }
                            },
                            fontSize = 14.sp
                        )
                    }
                    CustomSpacer()

                    Row (
                        modifier = Modifier.padding(5.dp)
                    ){
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                        ) {
                            ButtonSubmit(
                                labelValue = stringResource(id = R.string.buy_now),
                                color = Color(0xFF01BC59),
                                enabled = enableButtons
                            ) {
                                    shoppingViewModel.randomArticle = article
                                    shoppingViewModel.submitShopping(qte , comment!!)
                                    openDialog = false
                                    if (shoppingViewModel.delivery && shoppingViewModel.cost.add(
                                            cost
                                        ) <= BigDecimal(30)
                                    ) {

                                        showFeesDialog = true
                                    } else shoppingViewModel.sendOrder()

                            }
                        }
                        Row (
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                        ){
                            ButtonSubmit(labelValue = stringResource(id = R.string.keep_shopping), color = Color.White, enabled = enableButtons, Color.Black) {
                                    openDialog = false
                                    onClose(false)
                                    shoppingViewModel.randomArticle = article
                                    shoppingViewModel.submitShopping(qte , comment!!)
                                    gson.toJson(shoppingViewModel.orderArray)
                            }
                        }
                    }
                }
//                Column(
//                    modifier = Modifier.padding(2.dp)
//                ) {
//                    Row(
//                        modifier = Modifier.align(Alignment.CenterHorizontally)
//                    ) {
//                        ShowImage(
//                            image = String.format(
//                                IMAGE_URL_ARTICLE,
//                                article.article?.image,
//                                article.article?.category!!.ordinal
//                            )
//                        )
//                        Column {
//
//                            Text(text = if (restBalance!! < BigDecimal.ZERO || (shoppingViewModel.qte == 0.0 && restBalance!! <= 1.toBigDecimal())) "sold insufficient $restBalance DT" else "$restBalance DT")
//                            Text(text = article.article?.libelle!!)
//                        }
//                    }
//                    Row {
//                        InputTextField(
//                            labelValue = shoppingViewModel.rawInput,
//                            label = stringResource(id = R.string.quantity),
//                            singleLine = true,
//                            maxLine = 1,
//                            keyboardOptions = KeyboardOptions(
//                                keyboardType = KeyboardType.Decimal,
//                                imeAction = ImeAction.Next
//                            ),
//                            onValueChange = {
//                                if (article.unit == UnitArticle.U) {
//                                    if (it.matches(Regex("^[0-9]*$"))) {
//                                        shoppingViewModel.rawInput = it
//                                        shoppingViewModel.qte = it.toDoubleOrNull() ?: 0.0
//                                    }
//                                } else {
//                                    if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
//                                        val normalizedInput = it.replace(',', '.')
//                                        shoppingViewModel.rawInput = normalizedInput
//                                        if (normalizedInput.startsWith(".")) {
//                                            shoppingViewModel.rawInput = normalizedInput
//                                            shoppingViewModel.qte = 0.0
//                                        } else
//                                            if (normalizedInput.endsWith(".")) {
//                                                shoppingViewModel.qte = normalizedInput.let { inp ->
//                                                    if (inp.toDouble() % 1.0 == 0.0) inp.toDouble() else 0.0
//                                                }
//                                            } else {
//                                                shoppingViewModel.qte =
//                                                    normalizedInput.toDoubleOrNull() ?: 0.0
//                                            }
//                                    }
//                                }
//                            }, onImage = {}, true
//                        ) {
//
//                        }
//                    }
//                    Row {
//                        InputTextField(
//                            labelValue = shoppingViewModel.comment,
//                            label = stringResource(id = R.string.type_a_comment),
//                            singleLine = false,
//                            maxLine = 3,
//                            keyboardOptions = KeyboardOptions(
//                                keyboardType = KeyboardType.Text,
//                                imeAction = ImeAction.Done
//                            ),
//                            onValueChange = {
//                                shoppingViewModel.comment = it
//                            }, onImage = {}, true
//                        ) {
//                        }
//                    }
//                    Row {
//                        if ((restBalance!! >= BigDecimal.ZERO && shoppingViewModel.qte != 0.0)) {
//                            Row(
//                                modifier = Modifier.weight(1f)
//                            ) {
//                                ButtonSubmit(
//                                    labelValue = stringResource(id = R.string.add_more),
//                                    color = Color.Blue,
//                                    enabled = true
//                                ) {
//                                    openDialog = false
//                                    onClose(false)
//                                    shoppingViewModel.randomArticle = article
//                                    shoppingViewModel.submitShopping()
//                                    gson.toJson(shoppingViewModel.orderArray)
//                                }
//                            }
//                            Row(
//                                modifier = Modifier.weight(1f)
//                            ) {
//                                ButtonSubmit(
//                                    labelValue = "send",
//                                    color = Color.Green,
//                                    enabled = true
//                                ) {
//                                    shoppingViewModel.randomArticle = article
//                                    shoppingViewModel.submitShopping()
//                                    openDialog = false
//                                    if (shoppingViewModel.delivery && shoppingViewModel.cost.add(
//                                            cost
//                                        ) <= BigDecimal(30)
//                                    ) {
//
//                                        showFeesDialog = true
//                                    } else shoppingViewModel.sendOrder(-1, restBalance!!)
//
//                                }
//
//                            }
//                        }
//                        Row(
//                            modifier = Modifier.weight(1f)
//                        ) {
//
//                            ButtonSubmit(
//                                labelValue = stringResource(id = R.string.cancel),
//                                color = Color.Red,
//                                enabled = true
//                            ) {
//                                openDialog = false
//                                onClose(false)
//                                shoppingViewModel.remiseAZero()
//                            }
//                        }
//
//                    }
//                    Row {
//                        CheckBoxComp(
//                            value = "Delivery",
//                            free = if (cost.add(shoppingViewModel.cost) >= BigDecimal(30)) "free" else null,
//                            pay = if (cost.add(shoppingViewModel.cost) < BigDecimal(30) && shoppingViewModel.delivery && shoppingViewModel.qte != 0.0) " 3dt" else null,
//                            shoppingViewModel.delivery
//                        ) { isChecked ->
//                            shoppingViewModel.delivery = isChecked
//                        }
//                    }
//                }
            }
        }
    }
    if(showFeesDialog){
        ShowFeesDialog(isOpen = true) {submitfees ->
            if(submitfees) {
                shoppingViewModel.sendOrder()
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
                        ShowImage(image = "$BASE_URL/${company.logo}/company/${company.user?.id}",35.dp)
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
fun OrderShow(order: PurchaseOrderLine, shoppingViewModel: ShoppingViewModel, myCompanyId : Long , onDelete : (Boolean, Long) -> Unit) {
    val price = BigDecimal(order.quantity!!)
        .multiply(BigDecimal(order.article?.sellingPrice!!))
        .setScale(2, RoundingMode.HALF_UP)

    val tvaRate = BigDecimal(order.article.article?.tva ?: 0.0)
    val tvaAmount = price.multiply(tvaRate).divide(BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)
    val totalPrice = price.add(tvaAmount)
    Column {
        Spacer(modifier = Modifier.height(32.dp))
        Row {

            Row(
                modifier = Modifier.height(70.dp)
            ) {
                if (order.article.article?.image.isNullOrEmpty()) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Article,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                    )
                } else {
                    ShowImageForHome(
                        image = String.format(
                            IMAGE_URL_ARTICLE,
                            order.article.article?.image,
                            order.article.article?.category?.ordinal
                        ),
                        shape = RoundedCornerShape(10.dp),
                        width = 125.dp
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (order.article.company?.logo != null) {
                        ShowImage(
                            image = String.format(
                                IMAGE_URL_COMPANY,
                                order.article.company?.logo,
                                order.article.company?.user?.id
                            )
                        )
                    } else {
                        NotImage()
                    }
                    Row {

                        order.article.company?.let { it1 ->
                            Text(
                                text = it1.name,
                                fontSize = 14.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "verification account",
                            tint = if (order.article.company?.metaSeller == true) Color.Green else Color.Cyan,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
                CustomSpacer()
                Text(
                    text = order.article.article?.libelle!!,
                    modifier = Modifier
                        .padding(2.dp)
                        .fillMaxWidth(),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif
                    ),
                    color = Color.Black,
                )
                CustomSpacer()
            }
        }
        Row {
            Column(
                modifier = Modifier.weight(2f)
            ) {
                Text( text = buildAnnotatedString {
                append(stringResource(id = R.string.quantity_unit))
                append(" ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(order.quantity.toString())
                }
                append(" ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(order.article.unit?.name!!)
                }
            },
                fontSize = 14.sp)
                CustomSpacer()
                Text( text = buildAnnotatedString {
                    append(stringResource(id = R.string.total_price))
                    append(" ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(totalPrice.toString())
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(stringResource(id = R.string.dt))
                    }
                },
                    fontSize = 14.sp)
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                if(order.id == null || order.article.company?.id != myCompanyId)
                    IconButton(onClick = {
                        onDelete(false, order.article.id!!)
                    }) {
                        Icon(painter = painterResource(id = R.drawable.edit), contentDescription = null )
                    }

                IconButton(onClick = {
                    onDelete(true, order.article.id!!)
                }) {
                    Icon(painter = painterResource(id = R.drawable.delete), contentDescription = null, tint = Color(0xFFFE2A2A))
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        DividerComponent()
    }
//    Row {
//        Column(
//            modifier = Modifier
//                .clickable {
//                    showDialog = true
//                }
//                .weight(0.9f)
//        ) {
//            order.article?.article?.let { Text(text = it.libelle?:"") }
//            Text(text = order.quantity.toString())
//            order.comment?.let { Text(text = it) }
//            if (showDialog) {
//                shoppingViewModel.randomArticle = order.article!!
//                shoppingViewModel.delivery = order.delivery!!
//                ShoppingDialog(
//                    article = order.article,
//                    label = "",
//                    isOpen = showDialog,
//                    shoppingViewModel = shoppingViewModel,
//                    order.quantity,
//                    order.comment
//                ){
//                    showDialog = it
//                }
//            }
//        }
//    }
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
                Text(text = paymentForProviders.purchaseOrder?.orderNumber.toString())
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
fun CheckLocation(type: AccountType, user: User?, company: Company, context: Context, appViewModel: AppViewModel) {
    var isOk by remember {
        mutableStateOf(false)
    }

    if ((type == AccountType.USER && (user?.latitude == 0.0 || user?.longitude == 0.0)) ||
        (type == AccountType.COMPANY && (company.latitude == 0.0 || company.longitude == 0.0))
    ) {
        isOk = true
    }
    if (isOk) {
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
                appViewModel.setEnabledGps()
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
//            PermissionSettingsDialog {
//                dialogLocation = false
//            }
        }

        if (dialogGps && !isGpsEnabled) {
            LocationServiceDialog(onDismiss = { dialogGps = false }) {
                locationSettingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }

        // Initial check for location permissions and GPS
        LaunchedEffect(isGpsEnabled, isLocationGranted) {
            if ((type == AccountType.USER && (user?.latitude == 0.0 || user?.longitude == 0.0)) ||
                (type == AccountType.COMPANY && (company.latitude == 0.0 || company.longitude == 0.0))
            ) {
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


