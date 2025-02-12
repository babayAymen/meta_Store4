package com.aymen.metastore.ui.component

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
import androidx.compose.material3.HorizontalDivider
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
            .fillMaxWidth()
        ,
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontStyle = FontStyle.Normal
        )
        , color = Color.Black,
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
fun textField(label : String ,labelValue : String, icon: ImageVector, enable : Boolean? = true, value : (String) -> Unit){

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
        enabled = enable?:true,
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
fun PasswordTextField(labelValue : String, icon: ImageVector, keyboardOptions: KeyboardOptions, onSubmit : (String) -> Unit){
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
        onValueChange = {
            password = it
            onSubmit(password)
                        },
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
fun CheckBoxComp(value :String, free : String?, pay : String?,check : Boolean, state : (Boolean) -> Unit){
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
        Row {
        Text(text = value)
            Text(text = free?:"", color = if(free == "free")Color.Green else Color.Red)
            Text(text = pay?:"", color = if(pay == "NOT_FREE")Color.Red else Color.Red)
        }

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
fun DividerComponent(){
    Row (modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .weight(1f),
            thickness = 1.dp,
            color = Color.Gray
        )
    }
}

@Composable
fun ClickableLoginTextComponent(value: String, contentAlignment : Alignment, onTextSelected : (String) -> Unit){
    val annoutatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Green,textDecoration = TextDecoration.Underline, fontSize = 18.sp)){
            pushStringAnnotation(tag = value, annotation = value)
            append(value)
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = contentAlignment,
    ) {
        ClickableText(
            text = annoutatedString,
            onClick = { offset ->
                annoutatedString.getStringAnnotations(offset, offset)
                    .firstOrNull()?.also { span ->
                        if (span.item == value) {
                            onTextSelected(span.item)
                        }
                    }
            }
        )
    }
}