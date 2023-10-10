package com.example.expenseapplication.ui

import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expenseapplication.ui.theme.gray
import com.example.expenseapplication.ui.theme.white
import java.lang.Math.pow
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt


enum class Duration(val value: String ,val description: String){
    LastSevenDays(value = "7 days" , description = "Last 7 days"),
    LastFourteenDays(value = "14 days" , description = "Last 14 days"),
    LastThirtyDays(value = "30 days" , description = "Last 30 days"),
    LastNinetyDays(value = "90 days" , description = "Last 90 days"),
    LastYear(value = "365 days" , description = "Last 365 days"),

}

@Composable
@Preview(showBackground = true)
fun ExpenseSummaryScreen( ){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gray)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier,
                text = "Spending",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold)
            DurationDropdown(
                modifier = Modifier
                    .padding(vertical = 7.5.dp, horizontal = 15.dp)
            )


        }
        ExpenseSummaryPiechart(
            modifier = Modifier,
            input = listOf(
                PiechartInput(
                    color = Color.Green,
                    value = 50.0,
                    description = "Essential"
                ),
                PiechartInput(
                    color = Color.Yellow,
                    value = 30.0,
                    description = "Nice To Have"
                ),
                PiechartInput(
                    color = Color.Red,
                    value = 40.0,
                    description = "Wasted"
                ),
            )
        )

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationDropdown(
    modifier: Modifier = Modifier

) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var duration by remember {
        mutableStateOf(Duration.LastSevenDays)
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
                    .menuAnchor()
                    .widthIn(min = 50.dp),
                value = duration.description ,
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
                enumValues<Duration>().forEach { durationOption ->
                    DropdownMenuItem(
                        text = { Text(text = durationOption.description) },
                        onClick = {
                            duration = durationOption
                            isExpanded = false
                        })
                }
            }


        }


    }
}

@Composable
@Preview(showBackground = true)
fun ExpenseSummaryPiechart(
    modifier: Modifier = Modifier,
    radius: Float = 500f,
    innerRadius: Float = 250f,
    input: List<PiechartInput> = emptyList(),
    centerText: String = ""
){
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var inputList by remember {
        mutableStateOf(input)
    }

    var isCenterTapped by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center){
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(true) {
                    detectTapGestures(
                        onTap = { offset ->

                            val distanceFromCircleCenterToTapPoint =
                                sqrt((offset.x - circleCenter.x) *(offset.x - circleCenter.x) +
                                    (offset.y - circleCenter.y) * (offset.y - circleCenter.y))
                            //angle of the tappped point to the circle center
                            val tapAngleInDegrees = (-atan2(
                                x = circleCenter.y - offset.y,
                                y = circleCenter.x - offset.x
                            ) * (180f / PI).toFloat() - 90f).mod(360f)

                            val centerClicked = distanceFromCircleCenterToTapPoint <= innerRadius

                            if (centerClicked) {
                                inputList = inputList.map {
                                    it.copy(isTapped = !isCenterTapped)
                                }
                                isCenterTapped = !isCenterTapped
                            } else {
                                val anglePerValue = 360f / input.sumOf {
                                    it.value
                                }
                                var currAngle = 0f
                                inputList.forEach { pieChartInput ->

                                    currAngle += (pieChartInput.value * anglePerValue).toFloat()
                                    if (tapAngleInDegrees < currAngle) {
                                        val description = pieChartInput.description
                                        inputList = inputList.map {
                                            if (description == it.description) {
                                                it.copy(isTapped = !it.isTapped)
                                            } else {
                                                it.copy(isTapped = false)
                                            }
                                        }
                                        return@detectTapGestures
                                    }
                                }
                            }
                        }
                    )
                },
            ){
            val width = size.width
            val height = size.height
            circleCenter = Offset(x = width/2f, y = height/2f)

            val totalValue = input.sumOf {
                it.value
            }

            val anglePerValue = 360f/totalValue
            var currentStartAngle = 0f

            inputList.forEach {  piechartInput ->
                val scale = if (piechartInput.isTapped) 1.1f else 1.0f
                val angleToDraw = piechartInput.value* anglePerValue
                scale(scale){
                    drawArc(
                        color = piechartInput.color,
                        startAngle = currentStartAngle,
                        sweepAngle = angleToDraw.toFloat(),
                        useCenter = true,
                        size = Size(
                            width = radius*2f,
                            height = radius *2f
                        ),
                        topLeft = Offset(
                            x = (width-radius*2f)/2f,
                            y = (height-radius*2f)/2f
                        )
                    )
                    currentStartAngle += angleToDraw.toFloat()
                }

                var rotateAngle = currentStartAngle - angleToDraw/2f - 90f
                var factor = 1f
                //for rotating the text, preventing difficulties reading it upside down
                if(rotateAngle>90f){
                    rotateAngle = (rotateAngle + 180f).mod(360f)
                    factor = -1.0f
                }

                val percentage = (piechartInput.value/totalValue*100).toInt()
                drawContext.canvas.nativeCanvas.apply {
//                    if(percentage>3){
//
//                    }
                    rotate(rotateAngle.toFloat()){
                        drawText(
                            "$percentage% $rotateAngle $angleToDraw $currentStartAngle",
                            circleCenter.x,
                            circleCenter.y+(radius-(radius-innerRadius)/2f)*factor,
                            Paint().apply {
                                textSize = 13.sp.toPx()
                                textAlign = Paint.Align.CENTER
                                color = Color.Black.toArgb()
                            }
                        )
                    }
                }

                if(piechartInput.isTapped){
                    val tabRotation = currentStartAngle - angleToDraw - 90f
                    rotate(tabRotation.toFloat()){
                        drawRoundRect(
                            topLeft = circleCenter,
                            size = Size(12f, radius*1.2f),
                            color = gray,
                            cornerRadius = CornerRadius(15f, 15f)
                        )
                    }
                    rotate((tabRotation + angleToDraw).toFloat()){
                        drawRoundRect(
                            topLeft = circleCenter,
                            size = Size(12f, radius*1.2f),
                            color = gray,
                            cornerRadius = CornerRadius(15f, 15f)
                        )
                    }
                    rotate(rotateAngle.toFloat()){
                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                "${piechartInput.description}: ${piechartInput.value}",
                                circleCenter.x,
                                circleCenter.y + radius*1.3f*factor,
                                Paint().apply {
                                    textSize = 22.sp.toPx()
                                    textAlign = Paint.Align.CENTER
                                    color = white.toArgb()
                                    isFakeBoldText = true
                                }
                            )
                        }
                    }
                }

            }
            //this is needed because the last untapped element will be drawn ontop of the
            //rect used to seperate them
            if(inputList.first().isTapped){
                rotate(-90f){
                    drawRoundRect(
                        topLeft = circleCenter,
                        size = Size(12f,radius*1.2f),
                        color = gray,
                        cornerRadius = CornerRadius(15f,15f)
                    )
                }
            }
            drawCircle(
              color = white,
                radius = innerRadius,
                center = Offset(
                    circleCenter.x,
                    circleCenter.y
                )
            )
        }

        Text(
            text = "Total Expense $250",
            modifier = Modifier
                .width(Dp(innerRadius / 1.5f))
                .padding(25.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )
    }
}

data class PiechartInput(
    val color: Color,
    val value: Double,
    val description: String,
    val isTapped: Boolean = false
)