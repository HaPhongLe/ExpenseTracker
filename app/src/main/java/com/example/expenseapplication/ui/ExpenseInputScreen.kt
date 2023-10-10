package com.example.expenseapplication.ui

import android.os.Build
import android.util.EventLogTags.Description
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expenseapplication.ui.theme.TextWhite
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview(showBackground = true)
fun ExpenseInputScreen(
    userName: String = "Phong"
){
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(vertical = 20.dp),
        ) {

        Greeting(
            modifier = Modifier
                .align(Alignment.TopStart),
            userName = userName,)
//        DateDisplay(
//            modifier = Modifier
//                .padding(vertical = 7.5.dp, horizontal = 15.dp)
//        )

        ExpenseInputSection(
            modifier = Modifier
                .align(Alignment.Center)
        )
    }


}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    userName: String = "Phong"
){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 7.5.dp, horizontal = 15.dp)
    ) {
        Text(
            text = "Hello,",
            fontSize = 22.sp,
            fontWeight = FontWeight.Normal,)
        Text(
            text = userName,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,)
    }
}

enum class ExpenseCategory(val description: String){
    Essential("Essential"),
    NiceToHave("Nice to have"),
    Wasted("Wasted")
}

@Composable
fun ExpenseInputSection(
    modifier: Modifier = Modifier,
){
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.5.dp, horizontal = 15.dp),
            text = "Got something you want to spend money on today? :)",
            fontSize = 24.sp)
        CategoryDropdown(
            modifier = Modifier
                .padding(vertical = 7.5.dp, horizontal = 15.dp)
        )
        ExpenseInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.5.dp, horizontal = 15.dp)
        )
        DescriptionInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.5.dp, horizontal = 15.dp)
        )
        Button(
            modifier = Modifier
                .padding(vertical = 7.5.dp, horizontal = 15.dp)
                .align(Alignment.CenterHorizontally),
            onClick = { /*TODO*/ }) {
            Text(
                text = "Submit",
                color = TextWhite)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    modifier: Modifier = Modifier

) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var expenseCategory by remember {
        mutableStateOf(ExpenseCategory.Essential)
    }

    Box (
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
        ,
        contentAlignment = Alignment.Center
    ) {

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = it}
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                value = expenseCategory.description ,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),

                )
            DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                enumValues<ExpenseCategory>().forEach { category ->
                    DropdownMenuItem(
                        text = { Text(text = category.description) },
                        onClick = {
                            expenseCategory = category
                            isExpanded = false
                        })
                }
            }


        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateDisplay(
    modifier: Modifier = Modifier,
){
    val date = remember {
        mutableStateOf(LocalDate.now())
    }

    Column(
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth()){
            Text(
                text = "${date.value.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp

            )
            Text(text = " ${date.value.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}" +
                    " ${date.value.dayOfMonth}, ${date.value.year}" )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseInput(
    modifier: Modifier = Modifier,
    expenseAmount: Float = 0f
){
    var amount by remember {
        mutableStateOf(0f)
    }

    LaunchedEffect(key1 = expenseAmount){
        amount = expenseAmount
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = amount.toString(),
            onValueChange = {
                amount = it.toFloat()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            trailingIcon = { Text(text = "AUD")},
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun DescriptionInput(
    modifier: Modifier = Modifier,
    description: String = ""
){
    var description by remember {
        mutableStateOf("")
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(10.dp))

    ){
        TextField(
            modifier = Modifier
                .fillMaxSize(),
            placeholder = {
                Text(text = "Description")
            },
            value = description ,
            maxLines = 5,
            onValueChange = {
                description = it
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ))
    }

}