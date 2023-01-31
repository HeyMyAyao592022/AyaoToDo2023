package com.example.forayao2023.ui.common

import androidx.activity.ComponentActivity
import androidx.compose.material.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.forayao2023.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AyaoBottomBar(navCtrl: NavHostController) {
    val vm: MainViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
    val scope = rememberCoroutineScope()

    val labels = vm.viewLabels
    val iconsSelected = vm.viewIcons
    val iconsUnselected = vm.viewIconsUnselected
    val selectedIndex = vm.viewSelectedIndex

    NavigationBar {
        for ((index, label) in labels.withIndex()) {
            NavigationBarItem(selected = selectedIndex.value == index,
                onClick = {
                    scope.launch {
                        launch(Dispatchers.IO) { vm.onViewChanged(index) }
                        launch(Dispatchers.Main) { navCtrl.navigate(label) }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selectedIndex.value == index) iconsSelected[index] else iconsUnselected[index],
                        contentDescription = label,
                        tint = if (selectedIndex.value == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = MaterialTheme.colorScheme.outline,
                        fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    )
                })
        }
    }
}