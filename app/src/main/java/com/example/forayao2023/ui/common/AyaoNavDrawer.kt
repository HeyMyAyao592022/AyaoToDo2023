package com.example.forayao2023.ui.common

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forayao2023.logic.common.utility.TaskEnum
import com.example.forayao2023.logic.common.utility.TaskMark
import com.example.forayao2023.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TaskListDrawer(state: BottomSheetScaffoldState) {
    val styleLabel = MaterialTheme.typography.bodyMedium
    val marginTitle = 25.dp
    val styleTitle = MaterialTheme.typography.titleMedium
    val spacing = 20.dp

    val vm: MainViewModel =
        viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
    val items = listOf(Icons.Default.Favorite, Icons.Default.Face)
    val labels = listOf("今日任务", "已完成任务")
    val selectedItem = remember { mutableStateOf(items[0]) }
    val scope = rememberCoroutineScope()

    ModalDrawerSheet(drawerShape = DrawerDefaults.shape) {
        Spacer(Modifier.height(spacing))
        Text(
            text = "为瑶准备",
            style = styleTitle,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(start = marginTitle)
        )
        Spacer(Modifier.height(spacing))
        for ((index, item) in items.withIndex()) {
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = item,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                label = {
                    Text(
                        text = labels[index],
                        style = styleLabel,
                        color = MaterialTheme.colorScheme.outline
                    )
                },
                selected = item == selectedItem.value,
                onClick = {
                    scope.launch {
                        state.drawerState.close()
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(start = marginTitle), thickness = 1.dp
        )
        Text(
            text = "分类",
            style = styleTitle,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(start = marginTitle, top = 10.dp, bottom = 10.dp)
        )
        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            },
            label = {
                Text(
                    text = "全部",
                    style = styleLabel,
                    color = MaterialTheme.colorScheme.outline
                )
            },
            selected = vm.currMark == TaskEnum.MARK_NONE,
            onClick = {
                scope.launch {
                    state.drawerState.close()
                    vm.currMark = TaskEnum.MARK_NONE
                    launch(Dispatchers.IO) { vm.loadTasks(vm.currTime.value.toLocalDate()) }
                }
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        TaskMark.values().forEach { item ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.color
                    )
                },
                label = {
                    Text(
                        text = item.describe,
                        style = styleLabel,
                        color = MaterialTheme.colorScheme.outline
                    )
                },
                selected = item.index == vm.currMark,
                onClick = {
                    scope.launch {
                        state.drawerState.close()
                        vm.currMark = item.index
                        launch(Dispatchers.IO) { vm.loadTasks(vm.currTime.value.toLocalDate()) }
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}