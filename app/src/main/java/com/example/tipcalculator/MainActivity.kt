package com.example.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tipcalculator.ui.theme.TipCalculatorTheme
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipCalculatorTheme {
                Surface(){
                    TipTimeScreen()
                }

            }
        }
    }
}

@Composable
fun TipTimeScreen(modifier : Modifier = Modifier){
    //Focus manager to change focus as per keyboard's action button
    val focusManager = LocalFocusManager.current
    //Variables used for getting value from first text field
    var amountInput by remember {
        mutableStateOf("")
    }

    val amount = amountInput.toDoubleOrNull() ?: 0.0
    //Variables used for getting value from second text field
    var tipPercentInput by remember {
        mutableStateOf("")
    }
    val tipPercent = tipPercentInput.toDoubleOrNull() ?: 0.0
    //variables used for rounding up the tip
    var roundUp by remember {
        mutableStateOf(false)
    }
    //calculating tip
    val tip = calculateTip(amount, tipPercent, roundUp)

    //Column layout
    Column(modifier = Modifier
        .padding(32.dp)
        .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Title
        Text(
            text = stringResource(id = R.string.calculate_tip),
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        //Space between title and first text field
        Spacer(modifier = Modifier.height(16.dp))
        //First text field to enter number with keyboard type number and action button as next
        EditNumberField(labelId = R.string.bill_amount,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            value = amountInput,
            onValueChange = { amountInput = it})
        //Space between two text fields
        Spacer(modifier = Modifier.height(16.dp))
        //Second text field to enter number with keyboard type number and action button as done
        EditNumberField(labelId = R.string.how_was_the_service,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            value = tipPercentInput,
            onValueChange = { tipPercentInput = it}
        )
        //Space between second text field and row function
        Spacer(modifier = Modifier.height(16.dp))
        RoundTheTipRow(roundUp = roundUp, onRoundUpChanged = {roundUp = it} )
        //Space between round up row switch and result
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = stringResource(id = R.string.tip_amount,tip),
        modifier = Modifier.align(Alignment.CenterHorizontally),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
        )
        
    }

}

@Composable
fun EditNumberField(@StringRes labelId : Int,
                    keyboardOptions: KeyboardOptions,
                    keyboardActions: KeyboardActions,
                    value : String,
                    onValueChange : (String) -> Unit){
    TextField(
        label = {Text(text = stringResource(labelId))},
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = Modifier.fillMaxWidth()
    )
}
@Composable
fun RoundTheTipRow(roundUp : Boolean, onRoundUpChanged : (Boolean) -> Unit,modifier: Modifier = Modifier){
    //Row layout for text and switch
    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = stringResource(id = R.string.round_up_tip))
        Switch(checked = roundUp,
            onCheckedChange = onRoundUpChanged,
            modifier = modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = Color.DarkGray
            )
        ) }

}

@VisibleForTesting
internal fun calculateTip(amount : Double, tipPercent : Double, roundUp: Boolean): String {
    var tip =tipPercent/100 * amount
    if(roundUp){
        tip = kotlin.math.ceil(tip)
    }
    return NumberFormat.getCurrencyInstance().format(tip)
}
@Preview(showBackground = true)

@Composable
fun DefaultPreview(){
    TipCalculatorTheme() {
        TipTimeScreen()
    }
}