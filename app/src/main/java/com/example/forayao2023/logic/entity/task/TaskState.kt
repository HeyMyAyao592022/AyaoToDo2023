package com.example.forayao2023.logic.entity.task

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.forayao2023.logic.common.datetime.DateTimeTool
import com.example.forayao2023.logic.common.utility.TaskEnum
import com.example.forayao2023.logic.common.utility.TaskMark
import java.time.LocalDate
import java.time.LocalDateTime

class TaskState(task: AyaoTask) : TaskBase(task = task) {
    private var _beginTime by mutableStateOf(DateTimeTool.toDateTime(task.timeB))
    var beginTime: LocalDateTime
        get() = _beginTime
        set(value) {
            task.timeB = DateTimeTool.toLong(value).apply {
                Log.d("done","in ts: time b ->$this")
            }
            _beginTime = value
        }
    val beginTimeStr by lazy {
        setBeginTimeStr(_beginTime)
    }

    private var _deadline by mutableStateOf(DateTimeTool.toDateTime(task.timeE))
    var deadline: LocalDateTime
        get() = _deadline
        set(value) {
            task.timeE = DateTimeTool.toLong(value)
            _deadline = value
        }
    val deadlineStr by lazy {
        setDeadlineStr(_deadline)
    }

    private var timeStateType: Int
        get() = task.timeStateType
        set(value) {
            task.timeStateType = value
        }
    private var _timeState by mutableStateOf(setTimeState(task.timeState))
    var timeStateStr: String
        get() = _timeState
        set(value) {
            _timeState = setTimeState(task.timeState)
        }

    private fun setTimeState(str: String): String {
        return when (str.length) {
            1 -> {
                "每日"
            }
            2 -> {
                "每月${str}日"
            }
            4 -> {
                if (timeStateType != TaskEnum.TIME_STATE_TYPE_LUNAR) "${str.substring(0, 1)}月${str.substring(2, 3)}日"
                else "农历${str.substring(0, 2)}月${str.substring(2, 4)}日"
            }
            7 -> {
                var repeatStr = "每周"
                val chineseStr = arrayOf("一", "二", "三", "四", "五", "六", "日")
                for (i in (0..6)) {
                    if (str[i] != '0') {
                        repeatStr += if (repeatStr.length == 3)
                            "周${chineseStr[i]}"
                        else
                            "、周${chineseStr[i]}"
                    }
                }
                if (repeatStr.length == 23) {
                    repeatStr = "一整周"
                }
                repeatStr
            }
            else -> {
                ""
            }
        }
    }

    private fun setBeginTimeStr(time: LocalDateTime): String {
        return when {
            (time.hour == 0 && time.minute == 0) -> {
                ""
            }
            else -> String.format("%02d:", time.hour) + String.format("%02d", time.minute)
        }
    }

    private fun setDeadlineStr(time: LocalDateTime): String {
        val isToday = time.toLocalDate() == LocalDate.now()
        return when {
            (time.hour == 0 && time.minute == 0) -> {
                if (isToday) "" else "${time.monthValue}月${time.dayOfMonth}"
            }
            else -> String.format("%02d:", time.hour) + String.format("%02d", time.minute)
        }
    }
}