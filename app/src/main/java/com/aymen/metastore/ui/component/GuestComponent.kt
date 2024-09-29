package com.aymen.store.ui.component

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NormalText(value : String, aligne : TextAlign){
    Text(
        text = value,
        modifier = Modifier
            .heightIn(min = 20.dp)
            .fillMaxWidth()
        ,
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        )
        , color = Color.Black,
        textAlign = aligne
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationText(value : String, aligne : TextAlign,maxLines: Int,
                     overflow: TextOverflow = TextOverflow.Clip){
    Text(
        text = value,
        modifier = Modifier
            .heightIn(min = 20.dp)
            .fillMaxWidth()
        ,
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        )
        , color = Color.Black,
        textAlign = aligne,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun MessageText(value : String, aligne : TextAlign, color: Color){
    Text(
        text = value,
        modifier = Modifier
            .heightIn(min = 20.dp)
            .fillMaxWidth(),
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        )
        , color = color,
        textAlign = aligne
    )
}


@Composable
fun ArticleDetails(value : String, aligne : TextAlign){
    Text(
        text = "quantity : $value",
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 20.dp)
            .padding(4.dp),
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        )
        , color = Color.Black,
        textAlign = aligne
    )
}


@Composable
fun HeadingText(value : String){
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal
        )
        , color = Color.Black,
        textAlign = TextAlign.Center
    )
}

@Composable
fun textField(label : String ,labelValue : String, icon: ImageVector, value : (String) -> Unit){

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
        ,
        shape = RoundedCornerShape(10.dp),
        label = { Text(text = label) },
        colors =  TextFieldDefaults.colors(
            focusedLabelColor = Color.Black,
            cursorColor = Color.Magenta
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        value = labelValue,
        onValueChange = value
        ,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "" )

        },
        singleLine = true,
        maxLines = 1
    )
}

@Composable
fun emailField(labelValue : String, icon: ImageVector):String{
    var textValue by remember {
        mutableStateOf("")
    }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
        ,
        shape = RoundedCornerShape(10.dp),
        label = { Text(text = labelValue) },
        colors =  TextFieldDefaults.colors(
            focusedLabelColor = Color.Black,
            cursorColor = Color.Magenta
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email ,imeAction = ImeAction.Next),
        value = textValue,
        onValueChange = { textValue = it },
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = "")
        },
        singleLine = true,
        maxLines = 1
    )
    return textValue
}

@Composable
fun PhoneField(labelValue : String, icon: ImageVector):String{
    var textValue by remember {
        mutableStateOf("")
    }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
        ,
        shape = RoundedCornerShape(10.dp),
        label = { Text(text = labelValue) },
        colors =  TextFieldDefaults.colors(
            focusedLabelColor = Color.Black,
            cursorColor = Color.Magenta
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone ,imeAction = ImeAction.Next),
        value = textValue,
        onValueChange = { textValue = it },
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = "")
        },
        singleLine = true,
        maxLines = 1
    )
    return textValue
}
@Composable
fun passwordTextField(labelValue : String, icon: ImageVector, keyboardOptions: KeyboardOptions):String{
    val localFocusManager = LocalFocusManager.current
    var password by remember {
        mutableStateOf("")
    }
    val showPassword = remember {
        mutableStateOf(false)
    }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
        ,
        shape = RoundedCornerShape(10.dp),
        label = { Text(text = labelValue) },
        colors =  TextFieldDefaults.colors(
            focusedLabelColor = Color.Black,
            cursorColor = Color.Magenta
        ),
        keyboardOptions = keyboardOptions,
        singleLine = true,
        keyboardActions = KeyboardActions{
            localFocusManager.clearFocus()
        },
        value = password,
        onValueChange = { password = it },
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = "")
        },
        trailingIcon = {
            val iconImage = if(showPassword.value){
                Icons.Filled.Visibility
            }else{
                Icons.Filled.VisibilityOff
            }
            val description = if(showPassword.value){
                "Hide password"
            }else{
                "Show password"
            }

            IconButton(onClick = {showPassword.value = !showPassword.value}) {
                Icon(imageVector = iconImage, contentDescription = description)
            }
        },
        visualTransformation = if(showPassword.value) VisualTransformation.None else
            PasswordVisualTransformation()
    )
    return password

}

@Composable
fun CheckBoxComponent(value :String, onTextSelected : (String) -> Unit){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    )
    {
        val checkState = remember {
            mutableStateOf(false)
        }
        Checkbox(checked = checkState.value, onCheckedChange = {
            checkState.value = !checkState.value
        }
        )
        ClickableTextComponent( onTextSelected)

    }
}
@Composable
fun CheckBoxComp(value :String,check : Boolean, state : (Boolean) -> Unit){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    )
    {
        var checkState by remember {
            mutableStateOf(check)
        }
        Checkbox(
            checked = checkState,
            onCheckedChange = {
            checkState = it
                state(checkState)

        }
        )
        Text(text = value)

    }
}

@Composable
fun ClickableTextComponent( onTextSelected : (String) -> Unit){
    val initText = "By continuing you accept our "
    val privacyPolicyText = "Privacy Policy "
    val endText = "and "
    val termText = "Term of Use"
    val annoutatedString = buildAnnotatedString {
        append(initText)
        withStyle(style = SpanStyle(color = Color.Cyan)){
            pushStringAnnotation(tag = privacyPolicyText, annotation = privacyPolicyText)
            append(privacyPolicyText)
        }
        append(endText)
        withStyle(style = SpanStyle(color = Color.Cyan)){
            pushStringAnnotation(tag = termText, annotation = termText)
            append(termText)
        }
    }

    ClickableText(text = annoutatedString, onClick = { offset ->
        annoutatedString.getStringAnnotations(offset,offset)
            .firstOrNull()?.also {
                    span ->
                if ((span.item == termText) || (span.item == privacyPolicyText)){
                    onTextSelected(span.item)
                }
            }
    })
}

@Composable
fun ButtonComponent(value: String, isEnabled : Boolean, clickAction: () -> Unit){
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        enabled = isEnabled,
        onClick = clickAction,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        shape = RoundedCornerShape(50.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .background(
                brush = Brush.horizontalGradient(listOf(Color.Cyan, Color.Blue)),
                shape = RoundedCornerShape(50.dp)
            ),
            contentAlignment = Alignment.Center
        ){
            Text(text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DividerTextComponent(){
    Row (modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Divider(modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
            color = Color.Gray,
            thickness = 1.dp
        )

        Text(modifier = Modifier.padding(6.dp) ,
            text = "or",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray)
        Divider(modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
            color = Color.Gray,
            thickness = 1.dp
        )
    }
}

@Composable
fun ClickableLoginTextComponent(value: String, onTextSelected : (String) -> Unit){
    val annoutatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Green,textDecoration = TextDecoration.Underline, fontSize = 18.sp)){
            pushStringAnnotation(tag = value, annotation = value)
            append(value)
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(), // Makes the Box take the full width
        contentAlignment = Alignment.Center,
    ) {
        ClickableText(
            text = annoutatedString,
            onClick = { offset ->
                annoutatedString.getStringAnnotations(offset, offset)
                    .firstOrNull()?.also { span ->
                        Log.e("text clickable", "$span $value")
                        if (span.item == value) {
                            onTextSelected(span.item)
                        }
                    }
            }
        )
    }
}