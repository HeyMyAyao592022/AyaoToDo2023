package com.example.forayao2023.logic.common.utility

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class TaskMark(val index: Int, val color: Color, val describe: String, val icon: ImageVector) {
    //None(index = 0, color = Color(255, 255, 255), describe = "none", icon = Icons.Default.Star),
    Study(index = 0, color = Color(239, 101, 91), describe = "学习", icon = Icons.Default.Star),
    Stuffs(index = 1, color = Color(46, 122, 184), describe = "杂项", icon = Icons.Default.Star),
    ToBuy(index = 2, color = Color(63, 171, 200), describe = "购物清单", icon = Icons.Default.Star),
    Holiday(index = 3, color = Color(17, 171, 171), describe = "假期计划", icon = Icons.Default.Star),
    RecentPlan(
        index = 4,
        color = Color(255, 139, 43),
        describe = "近期目标",
        icon = Icons.Default.Star
    ),
    OnlyYao(index = 5, color = Color(250, 111, 150), describe = "For瑶", icon = Icons.Default.Star),
}
