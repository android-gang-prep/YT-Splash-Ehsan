package com.ehsannarmani.ytsplash

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable as FloatAnimatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.animation.doOnEnd
import androidx.core.graphics.toColor
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ehsannarmani.ytsplash.ui.theme.YTSplashTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            YTSplashTheme {
                Box(modifier= Modifier
                    .fillMaxSize()
                    .background(Color(0xff313131)), contentAlignment = Alignment.Center){
                    Text(text = "Hello World", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
        }


        splashScreen.setOnExitAnimationListener { view ->

            // splash fade animation on exit
            val fade = ObjectAnimator.ofFloat(
                view,
                View.ALPHA,
                1f,
                0f
            )
            fade.interpolator = AnticipateInterpolator()
            fade.duration = 500L
            fade.doOnEnd { view.remove() }

            val composeView = ComposeView(view.context)
            view.addView(composeView)

            composeView.setContent {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xff313131)), contentAlignment = Alignment.Center
                ) {
                    val iconSize = 40
                    val expandedProgressWidth = 230
                    val duration = 800


                    val progressColor = remember {
                        Animatable(Color(0xFFE91E63))
                    }
                    val contentWidth = remember {
                        FloatAnimatable(80f)
                    }
                    val contentHeight = remember {
                        FloatAnimatable(60f)
                    }
                    val playIconXOffset = remember {
                        FloatAnimatable((maxWidth.value/2)-(iconSize/2f))
                    }
                    val progressParentRadius = remember {
                        FloatAnimatable(22f)
                    }
                    val progressXOffset = remember {
                        FloatAnimatable(0f)
                    }
                    val progress = remember {
                        FloatAnimatable(0f)
                    }

                    LaunchedEffect(Unit) {
                        delay(400)
                        launch {
                            progressColor.animateTo(Color.White, animationSpec = tween(duration))
                        }
                        launch {
                            contentHeight.animateTo(5f, animationSpec = tween(duration))
                        }
                        launch {
                            contentWidth.animateTo(expandedProgressWidth.toFloat(), animationSpec = tween(duration, easing = LinearEasing))
                            contentWidth.animateTo(expandedProgressWidth+45f, animationSpec = tween(duration+(duration/1.5).toInt(), easing = LinearEasing))
//                            contentWidth.animateTo(expandedProgressWidth+iconSize.toFloat(), animationSpec = spring(
//                                dampingRatio = Spring.DampingRatioMediumBouncy,
//                                stiffness = duration+(duration/1.5).toFloat(),
//                            ))
                        }
                        launch {
                            playIconXOffset.animateTo(maxWidth.value-(400-iconSize.toFloat()+5), animationSpec = tween(duration, easing = LinearEasing))
                        }
                        launch { progressParentRadius.animateTo(0f, tween(duration)) }
                        launch {
                            progressXOffset.animateTo(5f, animationSpec = tween(duration))
                        }
                        launch {
                            delay((duration/1.5).toLong())
                            launch {
                                progress.animateTo(1f, animationSpec = tween(duration))
                                contentWidth.animateTo(0f, animationSpec = tween(duration))
                            }

                        }
                        launch {
                            delay((duration+(duration/1.5)).toLong())
                            playIconXOffset.animateTo((maxWidth.value/2)-(iconSize/2f), animationSpec = tween(duration-100))
                            fade.start()
                        }
                    }
                    Box(
                        modifier = Modifier
                            .width(contentWidth.value.dp)
                            .height(contentHeight.value.dp)
                            .offset(x = progressXOffset.value.dp)
                            .clip(RoundedCornerShape(progressParentRadius.value))
                            .background(progressColor.value)
                            ,
                        contentAlignment = Alignment.CenterStart
                    ){
                        val width = (contentWidth.value*progress.value).dp
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(width)
                                .background(Color(0xFFE91E63))
                        )
                    }
                    Icon(
                        modifier= Modifier
                            .align(Alignment.TopStart)
                            .absoluteOffset(
                                x = playIconXOffset.value.dp,
                                y = ((maxHeight.value / 2) - (iconSize / 2)).dp
                            )
                            .size(iconSize.dp),
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}