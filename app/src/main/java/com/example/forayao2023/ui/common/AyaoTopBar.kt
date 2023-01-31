package com.example.forayao2023.ui.common

import androidx.activity.ComponentActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forayao2023.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

private val chineseArr = arrayOf("一", "二", "三", "四", "五", "六", "日")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListTopBar(onDrawerSet: () -> Unit) {
    val vm: MainViewModel =
        viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
    val scope = rememberCoroutineScope()
    val currTime = vm.currTime
    var currIndex by remember {
        mutableStateOf(LocalDate.now().dayOfWeek.value - 1)
    }
    Column(modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.surface)) {
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${currTime.value.year}.${currTime.value.monthValue}.${currTime.value.dayOfMonth}",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = vm.topBarTitle.value,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    onDrawerSet()
                }) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            },
            actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            repeat(7) { index ->
                DayListItem(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 5.dp),
                    selected = index == currIndex,
                    index = index,
                    dayStr = vm.dayItemStates[index].value
                ) {
                    scope.launch {
                        currIndex = index
                        launch(Dispatchers.Default) { vm.onCurrDateChanged(index) }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayListItem(
    modifier: Modifier,
    selected: Boolean,
    index: Int,
    dayStr: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(onClick = {
            onClick()
        }, indication = null, interactionSource = remember {
            MutableInteractionSource()
        })
    ) {
        Text(
            text = chineseArr[index],
            fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
            fontSize = MaterialTheme.typography.labelSmall.fontSize,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(CircleShape)
                .background(
                    color = if (selected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                )
        ) {
            Text(
                text = dayStr,
                //fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.Center),
                softWrap = false
            )
        }
    }
}