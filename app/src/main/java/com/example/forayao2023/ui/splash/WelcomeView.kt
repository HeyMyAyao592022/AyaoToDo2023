package com.example.forayao2023.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.forayao2023.logic.model.TaskListModel
import com.example.forayao2023.logic.model.TimeModel
import com.example.forayao2023.ui.activity.MainActivity
import com.example.forayao2023.ui.theme.ForAyao2023Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.concurrent.locks.ReentrantLock

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val swipeHomeView = {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            ForAyao2023Theme {
//                WelcomeView(
//                    gotoHomeScreen = {
//                        swipeHomeView()
//                        finish()
//                    }, iniHome = { title ->
//                        val taskModel = TaskListModel.instance
//                        taskModel.loadTasks()
//                    })
            }
        }
    }
}

@Composable
fun WelcomeView(
    gotoHomeScreen: () -> Unit, iniHome: () -> Unit
) {
    val timeModel = TimeModel.instance

    var visible by remember {
        mutableStateOf(false)
    }
    val title by remember {
        mutableStateOf("第${timeModel.dateGap}天")
    }

    val enter1 = fadeIn(
        animationSpec = tween(durationMillis = 1800)
    )
    val enter2 = enter1 + slideInVertically(
        animationSpec = tween(durationMillis = 1000),
        initialOffsetY = { full: Int ->
            full + 80
        }
    )

    Splash(enter1 = enter1, enter2 = enter2, visible = visible, title = title)

    // 3
    LaunchedEffect(key1 = Unit) {
        val lock = ReentrantLock()
        delay(500)
        launch(Dispatchers.Default) {
            lock.lock()
            Log.d("lc","in wv: ini home")
            //delay(1000)
            visible = true
            iniHome()
            Log.d("lc","in wv: ini home")
            lock.unlock()
        }
        launch(Dispatchers.Main){
            Log.d("lc","in wv: delay 2s")
            delay(2000)
            lock.lock()
            Log.d("lc","in wv: go to home")
            gotoHomeScreen()
            lock.unlock()
        }
    }
}

@Composable
fun Splash(
    enter1: EnterTransition,
    enter2: EnterTransition,
    visible: Boolean,
    title: String
) {
    // 1
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 2
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 3
            AnimatedVisibility(visible = visible, enter = enter1) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            AnimatedVisibility(visible = visible, enter = enter2) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "遇见阿瑶",
                        color = MaterialTheme.colorScheme.outline,
                        fontStyle = MaterialTheme.typography.headlineSmall.fontStyle,
                        fontWeight = FontWeight.W600,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize
                    )
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.outline,
                        fontStyle = MaterialTheme.typography.titleMedium.fontStyle,
                        fontWeight = FontWeight.W500,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                }
            }
        }
    }
}

@Composable
fun Splash(offsetState: Dp, alphaState: Float) {
    val beginDate = LocalDate.of(2020, 10, 1)
    val dateDifference = LocalDate.now().toEpochDay() - beginDate.toEpochDay()
    val greetText = "第${dateDifference}天"

    val styleL = MaterialTheme.typography.headlineSmall
    val styleS = MaterialTheme.typography.titleMedium
    //val colorL = MaterialTheme.typography.displaySmall.color
    val colorS = MaterialTheme.colorScheme.outline
    // 1
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        // 2
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Favorite, contentDescription = null, modifier = Modifier
                    .size(80.dp)
                    .alpha(alpha = alphaState),
                tint = MaterialTheme.colorScheme.primary
            )
            // 4
            Text(
                modifier = Modifier
                    .offset(y = offsetState)
                    .alpha(alpha = alphaState),
                text = "遇见阿瑶",
                color = colorS,
                fontStyle = styleL.fontStyle,
                fontWeight = FontWeight.W600,
                maxLines = 1,
                fontSize = styleL.fontSize
            )
            Text(
                modifier = Modifier
                    .offset(y = offsetState)
                    .alpha(alpha = alphaState),
                text = greetText,
                color = colorS,
                fontStyle = styleS.fontStyle,
                fontWeight = FontWeight.W500,
                maxLines = 1,
                fontSize = styleS.fontSize
            )
        }
    }
}