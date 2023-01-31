@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)

package com.example.forayao2023.ui.common.bottom

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forayao2023.logic.common.utility.TaskEnum
import com.example.forayao2023.logic.common.utility.TaskMark
import com.example.forayao2023.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskListBottom(modifier: Modifier, state: BottomSheetScaffoldState) {
    // model
    val vm: MainViewModel =
        viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
    val model = remember {
        TaskListBottomModal()
    }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // picker
    val dateRangePicker = DatePickerDialog(
        ctx, { _, y, m, d ->
            scope.launch {
                model.onDateChanged(y, m, d)
            }
        },
        model.selectedDate.get(Calendar.YEAR),
        model.selectedDate.get(Calendar.MONTH),
        model.selectedDate.get(Calendar.DAY_OF_MONTH)
    )
    val timeBPicker = TimePickerDialog(
        ctx,
        { _, h, m ->
            scope.launch {
                model.onTimeChanged(0, h, m)
            }
        },
        model.beginTime.get(Calendar.HOUR),
        model.beginTime.get(Calendar.HOUR),
        true
    )
    val timeEPicker = TimePickerDialog(
        ctx,
        { _, h, m ->
            scope.launch {
                model.onTimeChanged(1, h, m)
            }
        },
        model.deadline.get(Calendar.HOUR),
        model.deadline.get(Calendar.HOUR),
        true
    )

    // rep
    if (model.openRepeatDialog) {
        RepeatDialog(onDisReq = {
            model.openRepeatDialog = false
        }, model = model)
    }

    // menu
    DropdownMenu(
        expanded = model.markMenuShow,
        onDismissRequest = { model.markMenuShow = false },
        offset = DpOffset(x = 20.dp, y = (-10).dp)
    ) {
        DropdownMenuItem(text = {
            Row {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 5.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "不归类",
                    fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }, onClick = {
            scope.launch {
                model.markMenuShow = false
                model.currMark = TaskEnum.MARK_NONE
            }
        })
        TaskMark.values().forEach { item ->
            DropdownMenuItem(text = {
                Row {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 5.dp),
                        tint = item.color
                    )
                    Text(
                        text = item.describe,
                        fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }, onClick = {
                scope.launch {
                    model.markMenuShow = false
                    model.currMark = item.index
                }
            })
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            // title && info
            TextValEditor(
                text = model.title,
                onValChanged = {
                    model.onTitleChanged(it)
                },
                modifier = Modifier.fillMaxWidth(),
                icons = Icons.Default.Send,
                tint = "今天要从哪里开始呢?",
                leading = {
                    IconButton(onClick = { model.markMenuShow = true },
                        modifier = Modifier.clip(IconButtonDefaults.outlinedShape),
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        content = {
                            if (model.currMark == TaskEnum.MARK_NONE)
                                Icon(imageVector = Icons.Default.Star, contentDescription = null)
                            else
                                Icon(
                                    imageVector = TaskMark.values()[model.currMark].icon,
                                    contentDescription = null,
                                    tint = TaskMark.values()[model.currMark].color
                                )
                        })
                },
                onDone = {
                    scope.launch {
                        launch(Dispatchers.Main) { state.bottomSheetState.collapse() }
                        launch(Dispatchers.Default) {
                            model.onDone(callback = {
                                vm.insertTask(it)
                            })
                        }
                    }
                }
            )
            TextValEditor(
                text = model.info,
                onValChanged = {
                    model.onInfoChanged(it)
                },
                modifier = Modifier.fillMaxWidth(),
                icons = null,
                tint = "添加一些细节",
                leading = {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null)
                },
                onDone = { }
            )
            // date
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    DateOptionButton(
                        label = "今天",
                        isSelected = model.selectedDateIndex == 0,
                        onSelected = { it ->
                            if (!it) {
                                scope.launch {
                                    model.selectedDateIndex = 0
                                    model.onDateOptChanged(0)
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                item {
                    DateOptionButton(
                        label = "明天",
                        isSelected = model.selectedDateIndex == 1,
                        onSelected = { it ->
                            if (!it) {
                                scope.launch {
                                    model.selectedDateIndex = 1
                                    model.onDateOptChanged(1)
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                item {
                    DateOptionButton(
                        label = "代办箱",
                        isSelected = model.selectedDateIndex == 2,
                        onSelected = { it ->
                            if (!it) {
                                scope.launch {
                                    model.selectedDateIndex = 2
                                    model.onDateOptChanged(2)
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                item {
                    DateOptionButton(
                        label = model.selectedDateStr,
                        isSelected = model.selectedDateIndex == 3,
                        onSelected = {
                            scope.launch {
                                dateRangePicker.show()
                            }
                        }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .clickable {
                        model.openRepeatDialog = true
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = 12.dp, end = 15.dp)
                )
                Text(
                    text = model.repeatStr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            AnimatedVisibility(visible = model.currType == TaskEnum.TASK_TYPE_NORMAL) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(start = 12.dp, end = 15.dp)
                    )
                    ToolOptionButton(
                        isActive = model.timeToolActive[0],
                        label = model.beginTimeStr,
                        icon = Icons.Default.DateRange,
                        onSelected = {
                            scope.launch {
                                timeBPicker.show()
                            }
                        })
                    Spacer(modifier = Modifier.width(30.dp))
                    ToolOptionButton(
                        isActive = model.timeToolActive[1],
                        label = model.deadlineStr,
                        icon = Icons.Default.DateRange,
                        onSelected = {
                            scope.launch {
                                timeEPicker.show()
                            }
                        })
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TextValEditor(
    text: String,
    tint: String,
    onValChanged: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier,
    icons: ImageVector?,
    leading: @Composable (() -> Unit)
) {
    val style1 = MaterialTheme.typography.bodyMedium
    val color1 = MaterialTheme.colorScheme.outline
    val color2 = style1.color

    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        modifier = modifier,
        value = text,
        onValueChange = onValChanged,
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.surface,
            focusedLabelColor = color2,
            unfocusedLabelColor = color1,
            focusedIndicatorColor = MaterialTheme.colorScheme.surface,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = color1,
            focusedTrailingIconColor = color2,
            unfocusedTrailingIconColor = color1
        ),
        leadingIcon = {
            leading()
        },
        trailingIcon = {
            icons?.let {
                IconButton(onClick = onDone) {
                    Icon(imageVector = it, contentDescription = null)
                }
            }
        },
        label = { Text(tint, fontStyle = style1.fontStyle) },
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        }),
    )
}

@Composable
private fun DateOptionButton(
    label: String,
    isSelected: Boolean,
    onSelected: (Boolean) -> Unit,
) {
    FilterChip(
        selected = isSelected,
        onClick = { onSelected(isSelected) },
        label = { Text(text = label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.outline,
            iconColor = MaterialTheme.colorScheme.outline
        ),
    )
}

@Composable
private fun ToolOptionButton(
    isActive: MutableState<Boolean>,
    label: String,
    icon: ImageVector,
    onSelected: (Boolean) -> Unit,
) {
    FilterChip(
        selected = isActive.value,
        onClick = { onSelected(isActive.value) },
        label = {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.outline,
            iconColor = MaterialTheme.colorScheme.outline
        ),
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = null)
        }
    )
}

@Composable
fun RepeatDialog(onDisReq: () -> Unit, model: TaskListBottomModal) {
    val style1 = MaterialTheme.typography.bodyMedium
    val boxShape = RoundedCornerShape(20.dp)

    val labels = model.repLabels
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Dialog(onDismissRequest = { onDisReq() }) {
        Box(
            modifier = Modifier
                .clip(boxShape)
                .background(color = MaterialTheme.colorScheme.surface, shape = boxShape)
                .padding(vertical = 20.dp, horizontal = 0.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ScrollableTabRow(
                    selectedTabIndex = model.currRepTp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    for ((i, item) in labels.withIndex()) {
                        Tab(
                            selected = model.currRepTp == i,
                            onClick = {
                                scope.launch(Dispatchers.IO) {
                                    model.onCurrRepTpChanged(i)
                                }
                            },
                            text = {
                                Text(
                                    text = item,
                                    color = if (model.currRepTp == i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                            })
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(165.dp)
                        .padding(top = 10.dp, bottom = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (model.currRepTp) {
                        4 -> {
                            Text(
                                text = "没有重复事件",
                                color = MaterialTheme.colorScheme.outline,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        0 -> {
                            Text(
                                text = "每日重复事件",
                                color = MaterialTheme.colorScheme.outline,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        1 -> {
                            val weekItemShape = CircleShape
                            val chineseArr = arrayOf("一", "二", "三", "四", "五", "六", "日")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(7) { i ->
                                    Box(
                                        modifier = Modifier
                                            .clip(weekItemShape)
                                            .background(
                                                shape = weekItemShape,
                                                color = if (model.weekReps[i].value) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                                            )
                                            .clickable {
                                                model.weekReps[i].value = !model.weekReps[i].value
                                                scope.launch(Dispatchers.IO) {
                                                    model.setWeekReps(i)
                                                }
                                            }
                                    ) {
                                        Text(
                                            text = chineseArr[i],
                                            style = style1,
                                            color = MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.padding(
                                                vertical = 5.dp,
                                                horizontal = 10.dp
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(5.dp))
                                }
                            }
                        }
                        2 -> {
                            TextField(
                                modifier = Modifier.width(170.dp),
                                value = "${model.monRep}",
                                onValueChange = {
                                    scope.launch(Dispatchers.IO) {
                                        model.onMonRepChanged(it)
                                    }
                                },
                                textStyle = MaterialTheme.typography.titleMedium,
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                                ),
                                leadingIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            model.setMonRepData(false)
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null
                                        )
                                    }
                                },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            model.setMonRepData(true)
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                label = {
                                    Text(
                                        "每月${model.monRep}日",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                keyboardActions = KeyboardActions(onDone = {
                                    keyboardController?.hide()
                                })
                            )
                        }
                        3 -> {
                            LazyColumn(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                item {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (model.yearRepLunar) "农历" else "公历",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Switch(checked = model.yearRepLunar, onCheckedChange = {
                                            scope.launch {
                                                model.setLunar(it)
                                            }
                                        })
                                    }
                                }
                                item {
                                    TextField(
                                        modifier = Modifier
                                            .height(50.dp)
                                            .width(170.dp),
                                        value = "${model.monthOfYearRep}月",
                                        onValueChange = {
                                            scope.launch(Dispatchers.IO) {
                                                model.onMonOfYearChanged(it)
                                            }
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        leadingIcon = {
                                            IconButton(onClick = {
                                                scope.launch {
                                                    model.setMonOfYearRep(false)
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null
                                                )
                                            }
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = {
                                                scope.launch {
                                                    model.setMonOfYearRep(true)
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null
                                                )
                                            }
                                        },
                                        textStyle = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                item {
                                    TextField(
                                        modifier = Modifier
                                            .height(50.dp)
                                            .width(170.dp),
                                        value = "${model.dayOfYearRep}日",
                                        onValueChange = {
                                            scope.launch(Dispatchers.IO) {
                                                model.onDayOfYearChanged(it)
                                            }
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        leadingIcon = {
                                            IconButton(onClick = {
                                                scope.launch {
                                                    model.setDayOfYearRep(false)
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null
                                                )
                                            }
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = {
                                                scope.launch {
                                                    model.setDayOfYearRep(true)
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null
                                                )
                                            }
                                        },
                                        textStyle = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = {
                        Thread {
                            model.onRepSetDone()
                        }.start()
                        model.openRepeatDialog = false
                    }) {
                        Text(text = "确定")
                    }
                }
            }
        }
    }
}
