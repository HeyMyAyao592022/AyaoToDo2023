package com.example.forayao2023.logic.common.utility

import androidx.compose.ui.graphics.Color

class TaskEnum {
    companion object {
        // type
        const val TASK_TYPE_NORMAL = 0
        const val TASK_TYPE_MEMO = 1
        const val TASK_TYPE_COURSE = 2

        // time type
        const val TIME_TYPE_NORMAL = 0
        const val TIME_TYPE_LUNAR = 1
        const val TIME_TYPE_REPEAT = 2
        const val TIME_TYPE_MEMO = 3

        // time state type
        const val TIME_STATE_TYPE_NONE = 0
        const val TIME_STATE_TYPE_DAILY = 1
        const val TIME_STATE_TYPE_WEEKLY = 2
        const val TIME_STATE_TYPE_MONTHLY = 3
        const val TIME_STATE_TYPE_LUNAR = 4
        const val TIME_STATE_TYPE_YEARLY = 5

        // state
        const val UNDONE = 0
        const val DONE = 1
        const val TIMEOUT = 2

        // priority
        const val PRI_NONE = 0
        const val PRI_LEVEL1 = 1

        // mark
        const val MARK_NONE = -1
    }
}