package com.example.day5app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.day5app.ui.theme.Day5AppTheme

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Day5AppTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) {
                    UnitConverter()
                }
            }
        }
    }
}
private val padding = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp)

private const val cel = "Celsius"
private const val kel = "Kelvin"
private const val fah = "Fahrenheit"
private const val kelToCelTerm = 273.15
private const val farDivider = 1.8
private const val celToFarTerm = 32
private const val kelToFarTerm = 459.67

private var lastFrom = ""
private var lastTo = ""
private var lastValue = ""

fun changeResultValue(result: MutableState<String>, from: String = "", to: String = "", value: String = "") {
    val isTo = to != ""

    if(from != "") {
        lastFrom = from
        if(isTo) lastValue = value
    }
    if(isTo) lastTo = to

    if(lastFrom == lastTo || lastValue == "") result.value = lastValue
    else if(lastFrom == cel) {
        if(lastTo == kel) result.value = (lastValue.toDouble() + kelToCelTerm).toString()
        else if(lastTo == fah) result.value = (lastValue.toDouble() * farDivider + celToFarTerm).toString()
    }
    else if(lastFrom == kel) {
        if(lastTo == cel) result.value = (lastValue.toDouble() - kelToCelTerm).toString()
        else if(lastTo == fah) result.value = (lastValue.toDouble() * farDivider - kelToFarTerm).toString()
    }
    else if(lastFrom == fah) {
        if(lastTo == cel) result.value = ((lastValue.toDouble() - celToFarTerm) / farDivider).toString()
        else if(lastTo == kel) result.value = ((lastValue.toDouble() + kelToFarTerm) / farDivider).toString()
    }
}

@Composable
fun DrawMenu(expanded: MutableState<Boolean>, buttonText: MutableState<String>, result: MutableState<String>, isFrom: Boolean) {
    val funcOnItemClick = { value: String -> {
        expanded.value = false
        buttonText.value = value
        changeResultValue(result, if(isFrom) value else "", if(isFrom) "" else value)
    } }
    DropdownMenu(expanded.value, onDismissRequest = {}) {
        DropdownMenuItem(text = {Text(cel)}, onClick = funcOnItemClick(cel))
        DropdownMenuItem(text = {Text(kel)}, onClick = funcOnItemClick(kel))
        DropdownMenuItem(text = {Text(fah)}, onClick = funcOnItemClick(fah))
    }
}

@Composable
fun DrawBox(buttonText: MutableState<String>, result: MutableState<String>, isFrom: Boolean, modifier: Modifier = Modifier) {
    val isComboExpanded = remember { mutableStateOf(false) }

    Box(modifier) {
        Button(onClick = {isComboExpanded.value = true} ) {
            Text(buttonText.value)
            Icon(Icons.Default.ArrowDropDown, "")
        }
        DrawMenu(isComboExpanded, buttonText, result, isFrom)
    }
}

@Composable
fun UnitConverter() {
    val fieldValue = remember { mutableStateOf("") }
    val resultValue = remember { mutableStateOf("") }
    val buttonFromText = remember { mutableStateOf("Convert from") }
    val buttonToText = remember { mutableStateOf("Convert to") }

    Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Converter", padding)
        OutlinedTextField(value = fieldValue.value,
            onValueChange = {
                entered: String ->
                run {
                    var dotFound = false
                    val enteredResult = entered.toCharArray().filter {
                        if(it == '.') {
                            if(dotFound) return@filter false
                            else {
                                dotFound = true
                                return@filter  true
                            }
                        }
                        return@filter it.isDigit()
                     }.joinToString(separator = "")
                    fieldValue.value = enteredResult
                    changeResultValue(resultValue, buttonFromText.value, buttonToText.value, enteredResult)
                }
            }, placeholder = { Text("Enter value")}, modifier = padding)
        Row(modifier = padding) {
            DrawBox(buttonFromText, resultValue, true, modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp))
            DrawBox(buttonToText, resultValue, false)
        }
        Text("Result: ${resultValue.value}")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Day5AppTheme {
        UnitConverter()
    }
}