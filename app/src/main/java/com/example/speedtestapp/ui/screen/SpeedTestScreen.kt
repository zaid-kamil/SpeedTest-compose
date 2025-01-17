@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.speedtestapp.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.AppTheme
import com.example.compose.Green200
import com.example.compose.Green500
import com.example.compose.GreenGradient
import com.example.compose.LightColor
import com.example.compose.darkGradient
import com.example.speedtestapp.R
import com.example.speedtestapp.UiState
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt


suspend fun startAnimation(animation: Animatable<Float, AnimationVector1D>) {
    animation.animateTo(0.84f, keyframes {
        durationMillis = 10000
        0f at 0 with CubicBezierEasing(0f, 1.5f, 0.8f, 1f)
        0.72f at 1000 with CubicBezierEasing(0.2f, -1.5f, 0f, 1f)
        0.76f at 2000 with CubicBezierEasing(0.2f, -2f, 0f, 1f)
        0.78f at 3000 with CubicBezierEasing(0.2f, -1.5f, 0f, 1f)
        0.82f at 4000 with CubicBezierEasing(0.2f, -2f, 0f, 1f)
        0.85f at 5000 with CubicBezierEasing(0.2f, -2f, 0f, 1f)
        0.89f at 6000 with CubicBezierEasing(0.2f, -1.2f, 0f, 1f)
        0.82f at 7500 with LinearOutSlowInEasing
    })
}

@Composable
fun SpeedTestScreen() {
    val coroutineScope = rememberCoroutineScope()

    val animation = remember { Animatable(0f) }
    val maxSpeed = remember { mutableStateOf(0f) }
    maxSpeed.value = max(maxSpeed.value, animation.value * 100f)

    SpeedTestScreen(animation.toUiState(maxSpeed.value)) {
        coroutineScope.launch {
            maxSpeed.value = 0f
            startAnimation(animation)
        }
    }
}
fun Animatable<Float, AnimationVector1D>.toUiState(maxSpeed: Float) = UiState(
    arcValue = value,
    speed = "%.1f".format(value * 100),
    ping = if (value > 0.2f) "${(value * 15).roundToInt()} ms" else "-",
    maxSpeed = if (maxSpeed > 0f) "%.1f mbps".format(maxSpeed) else "-",
    inProgress = isRunning
)

@Composable
private fun SpeedTestScreen(state: UiState, onClick: () -> Unit) {
    Scaffold(
        bottomBar = { Navigation() },
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(darkGradient)
                .border(
                    border = BorderStroke(1.dp, Color(0xFF414D66)),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Header()
            SpeedIndicator(state, onClick)
            AdditionalInfo(state.ping, state.maxSpeed)
        }
    }
}

@Composable
fun Header() {
    Text(
        text = "SPEEDTEST",
        modifier = Modifier.padding(top = 52.dp, bottom = 16.dp),
        style = MaterialTheme.typography.displaySmall
    )
}

@Composable
fun Navigation() {
    val items = listOf(
        R.drawable.wifi,
        R.drawable.person,
        R.drawable.speed,
        R.drawable.settings,
    )
    val selectedItem by remember { mutableStateOf(2) }
    NavigationBar(
        // background color
        containerColor = Color.Black,
        tonalElevation = 10.dp,
    ) {
        items.mapIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedItem,
                onClick = { /*TODO*/ },
                icon = {
                    Icon(
                        painter = painterResource(id = item), contentDescription = null
                    )
                },
            )
        }
    }
}

@Composable
fun SpeedIndicator(
    state: UiState,
    onClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        CircularSpeedIndicator(state.arcValue, 240f)
        StartButton(!state.inProgress, onClick)
        SpeedValue(state.speed)
    }
}

@Composable
fun SpeedValue(speed: String) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("DOWNLOAD", style = MaterialTheme.typography.labelLarge)
        Text(
            text = speed, fontSize = 45.sp, color = Color.White, fontWeight = FontWeight.Bold
        )
        Text("mbps", style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color(0xFF414D66))
            .width(1.dp)
    )
}

@Composable
fun AdditionalInfo(ping: String, maxSpeed: String) {

    @Composable
    fun RowScope.InfoColumn(title: String, value: String, color: Color) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(title, color = color)
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            )
    ) {
        InfoColumn(title = "PING", value = ping, color = MaterialTheme.colorScheme.primaryContainer)
        VerticalDivider()
        InfoColumn(
            title = "MAX SPEED",
            value = maxSpeed,
            color = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
fun StartButton(isEnabled: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier.padding(bottom = 24.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            width = 2.dp, MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = "START", modifier = Modifier.padding(
                horizontal = 24.dp, vertical = 4.dp
            )
        )
    }
}

@Composable
fun CircularSpeedIndicator(value: Float, angle: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        drawArcs(value, angle)
        drawLines(value, angle)
    }
}


// extension functions
fun DrawScope.drawArcs(progress: Float, maxValue: Float) {
    val startAngle = 270 - maxValue / 2
    val sweepAngle = progress * maxValue
    val topLeft = Offset(50f, 50f)
    val size = Size(size.width - 100f, size.height - 100f)

    fun drawBlur() {
        for (i in 0..20) {
            drawArc(
                color = Green200.copy(alpha = i / 900f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(width = 80f + (20 - i) * 20, cap = StrokeCap.Round)
            )
        }
    }

    fun drawStroke() {
        drawArc(
            color = Green500,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = size,
            style = Stroke(width = 86f, cap = StrokeCap.Round)
        )
    }

    fun drawGradient() {
        drawArc(
            brush = GreenGradient,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = size,
            style = Stroke(width = 80f, cap = StrokeCap.Round)
        )
    }

    drawBlur()
    drawStroke()
    drawGradient()
}

fun DrawScope.drawLines(progress: Float, maxValue: Float, numOfLines: Int = 40) {
    val oneRotation = maxValue / numOfLines
    val startValue = if (progress == 0f) 0 else floor(progress * numOfLines).toInt() + 1

    for (i in startValue..numOfLines) {
        rotate(i * oneRotation + (180 - maxValue) / 2) {
            drawLine(
                LightColor,
                Offset(if (i % 5 == 0) 80f else 30f, size.height / 2),
                Offset(0f, size.height / 2),
                8f,
                StrokeCap.Round
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    AppTheme {
        Surface {
            SpeedTestScreen(UiState(
                speed = "120.4",
                ping = "5 ms",
                maxSpeed = "150.0 mbps",
                arcValue = 0.83f,
            ), onClick = { /*TODO*/ })
        }
    }
}