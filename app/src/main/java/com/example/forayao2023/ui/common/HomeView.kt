package com.example.forayao2023.ui.common

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.forayao2023.ui.activity.Greeting
import com.example.forayao2023.ui.common.bottom.TaskListBottom
import com.example.forayao2023.ui.listview.TasksListView
import com.example.forayao2023.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeView() {
    val mainViewModel: MainViewModel =
        viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
    // nav
    val navCtrl = rememberNavController()
    val labels = mainViewModel.viewLabels

    Scaffold(bottomBar = {
        AyaoBottomBar(navCtrl = navCtrl)
    }, content = { padding ->
        NavHost(
            navController = navCtrl,
            startDestination = labels[0],
            builder = {
                composable(labels[0]) {
                    TaskScreen(padding.calculateBottomPadding())
                }
                composable(labels[1]) {
                    Greeting(name = "耀瑶")
                }
                composable(labels[2]) {
                    Greeting(name = "阿瑶")
                }
            })
    })
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreen(padding: Dp) {
    val scope = rememberCoroutineScope()
    val state = rememberBottomSheetScaffoldState()
    val bottomShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    BottomSheetScaffold(
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        sheetShape = bottomShape,
        sheetElevation = 25.dp,
        sheetGesturesEnabled = true,
        scaffoldState = state, sheetContent = {
            TaskListBottom(
                modifier = Modifier
                    .padding(bottom = padding + 20.dp, top = 20.dp),
                state = state
            )
        }, contentColor = MaterialTheme.colorScheme.surface,
        content = {
            TasksListView(
                modifier = Modifier
                    .padding(bottom = padding)
                    .clickable(onClick = {
                        if (state.bottomSheetState.isExpanded) {
                            scope.launch(Dispatchers.Main) { state.bottomSheetState.collapse() }
                        }
                    }, indication = null, interactionSource = remember {
                        MutableInteractionSource()
                    })
            )
        }, topBar = {
            TaskListTopBar(onDrawerSet = {
                scope.launch {
                    state.drawerState.open()
                }
            })
        }, drawerShape = DrawerDefaults.shape, drawerContent = {
            TaskListDrawer(state = state)
        }, floatingActionButton = {
            AnimatedVisibility(
                visible = state.bottomSheetState.isCollapsed,
                enter = fadeIn() + slideInVertically { h -> h + 5 },
                exit = fadeOut() + slideOutVertically { h -> h / 2 }) {
                FloatingActionButton(modifier = Modifier.padding(bottom = padding + 90.dp),
                    onClick = {
                        scope.launch(Dispatchers.Main) {
                            state.bottomSheetState.let {
                                if (it.isCollapsed) it.expand()
                                else it.collapse()
                            }
                        }
                    }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun Example() {

}