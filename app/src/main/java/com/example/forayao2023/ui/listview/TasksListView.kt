package com.example.forayao2023.ui.listview

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forayao2023.logic.common.utility.TaskEnum
import com.example.forayao2023.logic.common.utility.TaskMark
import com.example.forayao2023.logic.entity.task.TaskState
import com.example.forayao2023.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TasksListView(modifier: Modifier) {
    val vm: MainViewModel =
        viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
    val norTasksList by vm.normalTasks.collectAsState()
    val memoTasksList by vm.memoTasks.collectAsState()
    val finishedTasksList by vm.finishedTasks.collectAsState()
    val timeoutTasksList by vm.timeoutTasks.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        contentColor = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {
                timeoutTasksList.forEach { tsk ->
                    item {
                        TimeoutTasksListItem(task = tsk, modifier = Modifier, vm = vm)
                    }
                }
                if (timeoutTasksList.isNotEmpty() && (norTasksList.isNotEmpty() || memoTasksList.isNotEmpty() || finishedTasksList.isNotEmpty()))
                    item {
                        Divider(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                for (tsk in norTasksList) {
                    item {
                        NorTasksListItem(task = tsk, modifier = Modifier, vm = vm)
                    }
                }
                // divider
                if (norTasksList.isNotEmpty() && (memoTasksList.isNotEmpty() || finishedTasksList.isNotEmpty()))
                    item {
                        Divider(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                for (tsk in memoTasksList) {
                    item {
                        MemoTasksListItem(task = tsk, modifier = Modifier, vm = vm)
                    }
                }
                // divider
                if (memoTasksList.isNotEmpty() && finishedTasksList.isNotEmpty())
                    item {
                        Divider(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                for (tsk in finishedTasksList) {
                    item {
                        when (tsk.type) {
                            TaskEnum.TASK_TYPE_NORMAL -> {
                                NorTasksListItem(task = tsk, modifier = Modifier, vm = vm)
                            }
                            TaskEnum.TASK_TYPE_MEMO -> {
                                MemoTasksListItem(task = tsk, modifier = Modifier, vm = vm)
                            }
                            else -> {}
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NorTasksListItem(task: TaskState, modifier: Modifier, vm: MainViewModel) {
    var taskChecked = (task.state == TaskEnum.DONE)
    val scope = rememberCoroutineScope()

    ListItem(
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
        headlineText = {
            if (!(task.info == "" && task.task.mark == TaskEnum.MARK_NONE))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    task.let {
                        if (it.task.mark != TaskEnum.MARK_NONE)
                            Icon(
                                imageVector = it.mark.icon,
                                contentDescription = null,
                                tint = it.mark.color,
                                modifier = Modifier.size(15.dp)
                            )
                        Text(
                            text = if (it.info != "") task.info else if (it.task.mark != TaskEnum.MARK_NONE) it.mark.describe else "",
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = if (taskChecked) TextDecoration.LineThrough else TextDecoration.None,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
        },
        overlineText = {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (taskChecked) TextDecoration.LineThrough else TextDecoration.None,
            )
        },
        supportingText = {
            Row() {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(15.dp),
                )
                Text(
                    text = task.deadlineStr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(15.dp),
                )
                Text(
                    text = task.timeStateStr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(end = 5.dp)
                )
            }
        },
        leadingContent = {
            Checkbox(
                checked = taskChecked,
                onCheckedChange = {
                    scope.launch {
                        taskChecked = it
                        vm.onTaskChecked(task)
                    }
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.outline
                )
            )
        },
        trailingContent = {
            Row {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = task.beginTimeStr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemoTasksListItem(task: TaskState, modifier: Modifier, vm: MainViewModel) {
    var taskChecked = (task.state == TaskEnum.DONE)
    val scope = rememberCoroutineScope()

    ListItem(
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
        headlineText = {
            if (!(task.info == "" && task.task.mark == TaskEnum.MARK_NONE))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    task.let {
                        if (it.task.mark != TaskEnum.MARK_NONE)
                            Icon(
                                imageVector = it.mark.icon,
                                contentDescription = null,
                                tint = it.mark.color,
                                modifier = Modifier.size(15.dp)
                            )
                        Text(
                            text = if (it.info != "") task.info else if (it.task.mark != TaskEnum.MARK_NONE) it.mark.describe else "",
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = if (taskChecked) TextDecoration.LineThrough else TextDecoration.None,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
        },
        overlineText = {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (taskChecked) TextDecoration.LineThrough else TextDecoration.None,
            )
        },
        leadingContent = {
            Checkbox(
                checked = taskChecked,
                onCheckedChange = {
                    scope.launch {
                        taskChecked = it
                        vm.onTaskChecked(task)
                    }
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeoutTasksListItem(task: TaskState, modifier: Modifier, vm: MainViewModel) {
    var taskChecked = (task.state == TaskEnum.DONE)
    val scope = rememberCoroutineScope()

    ListItem(
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
        headlineText = {
            if (!(task.info == "" && task.task.mark == TaskEnum.MARK_NONE))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    task.let {
                        if (it.task.mark != TaskEnum.MARK_NONE)
                            Icon(
                                imageVector = it.mark.icon,
                                contentDescription = null,
                                tint = it.mark.color,
                                modifier = Modifier.size(15.dp)
                            )
                        Text(
                            text = if (it.info != "") task.info else if (it.task.mark != TaskEnum.MARK_NONE) it.mark.describe else "",
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = if (taskChecked) TextDecoration.LineThrough else TextDecoration.None,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
        },
        overlineText = {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (taskChecked) TextDecoration.LineThrough else TextDecoration.None,
            )
        },
        leadingContent = {
            Checkbox(
                checked = taskChecked,
                onCheckedChange = {
                    scope.launch {
                        taskChecked = it
                        vm.onTaskChecked(task)
                    }
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.outline
                )
            )
        },
        supportingText = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(15.dp),
                )
                Text(
                    text = "忽略",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .clickable { vm.onTaskChecked(task) }
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(15.dp),
                )
                Text(
                    text = "顺延",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .clickable { vm.delayTimeout(task) }
                )
            }
        },
    )
}