@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.forayao2023.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.forayao2023.ui.common.HomeView
import com.example.forayao2023.ui.splash.WelcomeView
import com.example.forayao2023.ui.theme.ForAyao2023Theme
import com.example.forayao2023.ui.viewmodel.MainViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // nav
            val navCtrl = rememberNavController()
            // vm
            val vm: MainViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
            ForAyao2023Theme {
                // status
                rememberSystemUiController().run {
                    setStatusBarColor(color = MaterialTheme.colorScheme.surface, darkIcons = !isSystemInDarkTheme())
                }
                // 去除状态栏
                WindowCompat.setDecorFitsSystemWindows(window, false)
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(navController = navCtrl, startDestination = "welcome", builder = {
                        composable("welcome") {
                            WelcomeView(gotoHomeScreen = {
                                navCtrl.navigate("main"){
                                    popUpTo(navCtrl.graph.findStartDestination().id){ inclusive = true }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            }, iniHome = {
                                vm.let {
                                    it.timeModel.iniTimeData()
                                    it.iniTasks()
                                }
                            })
                        }
                        composable("main") {
                            Log.d("cd", "in mac: ini home")
                            HomeView()
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val vm: MainViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
    val normalTasks by vm.normalTasks.collectAsState()
    val memoTasks by vm.memoTasks.collectAsState()
    //val tsTasks by vm.tsTask.collectAsState()
    val scope = rememberCoroutineScope()
    Column {
        Text(text = "Loving $name!")
        Button(onClick = {
        })
        {
            Text(text = "重置数据库")
        }
        Button(onClick = {
            scope.launch {
                vm.tsSet()
            }
        }) {
            Text(text = "Reset")
        }
        Button(onClick = {
            scope.launch {
                vm.tsLoad()
            }
        }) {
            Text(text = "Load")
        }

        LazyColumn {
            normalTasks.forEach {
                item {
                    Text(text = "title-> ${it.title}")
                }
            }
            memoTasks.forEach {
                item {
                    Text(text = "title-> ${it.title}")
                }
            }
//            tsTasks.forEach {
//                item {
//                    Text(text = "ts title -> ${it.title}")
//                }
//            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ForAyao2023Theme {
        Greeting("Android")
    }
}