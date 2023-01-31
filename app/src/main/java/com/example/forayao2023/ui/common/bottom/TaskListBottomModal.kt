package com.example.forayao2023.ui.common.bottom

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.forayao2023.logic.common.datetime.DateTimeTool
import com.example.forayao2023.logic.common.utility.TaskEnum
import com.example.forayao2023.logic.entity.task.AyaoTask
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class TaskListBottomModal {
    var currMark by mutableStateOf(TaskEnum.MARK_NONE)
    var markMenuShow by mutableStateOf(false)

    var currType by mutableStateOf(TaskEnum.TASK_TYPE_NORMAL)

    var title by mutableStateOf("")
    var info by mutableStateOf("")
    private var timeType = TaskEnum.TIME_TYPE_NORMAL

    private val today = LocalDateTime.now()
    private val tomorrow: LocalDateTime = today.plusDays(1)

    var selectedDateIndex by mutableStateOf(0)
    var selectedDate: Calendar by mutableStateOf(
        Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).apply {
            this.set(today.year, today.monthValue - 1, today.dayOfMonth)
        })
    var selectedDateStr by mutableStateOf("选一天")

    private var bTimeIni = false
    var beginTime: Calendar by mutableStateOf(
        Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).apply {
            this.set(today.year, today.monthValue - 1, today.dayOfMonth, today.hour, today.minute)
        })
    var beginTimeStr by mutableStateOf(
        "开始时间"
    )
    private var deadlineIni = false
    var deadline: Calendar by mutableStateOf(
        Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).apply {
            this.set(today.year, today.monthValue - 1, today.dayOfMonth, today.hour, today.minute)
        })
    var deadlineStr by mutableStateOf(
        "截止时间"
    )
    val timeToolActive = arrayOf(mutableStateOf(false), mutableStateOf(false))

    var repeatStr by mutableStateOf("设置重复事件")
    var repeatData = ""
    private var timeStateType = TaskEnum.TIME_STATE_TYPE_NONE

    val repLabels = arrayOf("每日", "每周", "每月", "每年", "无重复")
    var openRepeatDialog by mutableStateOf(false)
    var currRepTp by mutableStateOf(4)

    // 周重复
    val weekReps = Array(7) {
        mutableStateOf(false)
    }

    // 月重复
    var monRep by mutableStateOf(1)

    // 年重复
    var yearRepLunar by mutableStateOf(false)
    var monthOfYearRep by mutableStateOf(today.monthValue)
    var dayOfYearRep by mutableStateOf(today.dayOfMonth)

    fun onDone(callback: (AyaoTask) -> Unit) {
        if (title == "")
            return
        Log.d("bm", "in bm: on done")
        val currDate = LocalDate.of(
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH) + 1,
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        val timeBegin: LocalDateTime = when {
            (bTimeIni && (timeType == TaskEnum.TIME_TYPE_NORMAL || timeType == TaskEnum.TIME_TYPE_LUNAR)) -> {
                LocalDateTime.of(
                    currDate,
                    LocalTime.of(beginTime.get(Calendar.HOUR), beginTime.get(Calendar.MINUTE))
                )
            }
            timeType == TaskEnum.TIME_TYPE_REPEAT -> {
                setRepBeginTime(rep = repeatData)
            }
            else -> {
                LocalDateTime.of(
                    currDate,
                    LocalTime.of(0, 0)
                )
            }
        }
        val timeEnd: LocalDateTime = when {
            (deadlineIni && (timeType == TaskEnum.TIME_TYPE_NORMAL || timeType == TaskEnum.TIME_TYPE_LUNAR)) -> {
                LocalDateTime.of(
                    currDate,
                    LocalTime.of(0, 0)
                )
            }
            else -> {
                LocalDateTime.of(
                    currDate,
                    LocalTime.of(deadline.get(0), deadline.get(0))
                )
            }
        }
        AyaoTask(
            title = title,
            info = info,
            type = currType,
            timeB = DateTimeTool.toLong(timeBegin),
            timeE = DateTimeTool.toLong(timeEnd),
            timeStateType = timeStateType,
            timeState = repeatData,
            timeType = timeType,
            mark = currMark
        ).let {
            callback(it)
            Log.d("bm", "in bm: new task->$it/ bt->$timeBegin/ ddl->$timeEnd/ td->$today")
        }
    }

    private fun setRepBeginTime(rep: String): LocalDateTime {
        val curr = LocalDate.now()
        val timeBase = LocalTime.of(0, 0)
        return when (rep.length) {
            2 -> {
                val day = rep.toInt()
                val time = LocalDate.of(curr.year, curr.monthValue, day)
                if (time < curr) LocalDateTime.of(
                    time.plusMonths(1),
                    timeBase
                ) else LocalDateTime.of(time, timeBase)
            }
            4 -> {
                val m = rep.substring(0, 2).toInt()
                val d = rep.substring(2, 4).toInt()
                val time = LocalDate.of(curr.year, m, d)
                if (time < curr) LocalDateTime.of(
                    time.plusYears(1),
                    timeBase
                ) else LocalDateTime.of(time, timeBase)
            }
            7 -> {
                var currIndex = curr.dayOfWeek.value
                var begin = LocalDate.now()
                var flag = false
                while (currIndex <= 7) {
                    if (rep[currIndex - 1] != '0') {
                        begin = begin.plusDays((currIndex - curr.dayOfWeek.value).toLong())
                        flag = true
                        break
                    }
                    currIndex += 1
                }
                if (!flag) {
                    currIndex = 1
                    while (currIndex < curr.dayOfWeek.value) {
                        if (rep[currIndex - 1] != '0') {
                            begin = begin.minusDays((curr.dayOfWeek.value - currIndex).toLong())
                                .plusWeeks(1)
                            break
                        }
                        currIndex += 1
                    }
                }
                LocalDateTime.of(begin, timeBase)
            }
            else -> return LocalDateTime.now()
        }
    }

    fun onTitleChanged(str: String) {
        title = str
    }

    fun onInfoChanged(str: String) {
        info = str
    }

    fun onDateChanged(year: Int, mon: Int, day: Int) {
        val dateEq: (Int, Int, Int, LocalDateTime) -> Boolean =
            { y: Int, m: Int, d: Int, compare: LocalDateTime ->
                (y == compare.year && (m + 1) == compare.monthValue && d == compare.dayOfMonth)
            }
        selectedDate.set(year, mon, day)
        when {
            dateEq(year, mon, day, today) -> {
                selectedDateIndex = 0
                selectedDateStr = "选一天"
            }
            dateEq(year, mon, day, tomorrow) -> {
                selectedDateIndex = 1
                selectedDateStr = "选一天"
            }
            else -> {
                selectedDateIndex = 3
                selectedDateStr = "${mon + 1}.$day"
            }
        }
    }

    fun onDateOptChanged(tp: Int) {
        when (tp) {
            0 -> {
                currType = TaskEnum.TASK_TYPE_NORMAL
                selectedDate.set(today.year, today.monthValue, today.dayOfMonth)
                timeType = TaskEnum.TIME_TYPE_NORMAL
            }
            1 -> {
                currType = TaskEnum.TASK_TYPE_NORMAL
                selectedDate.set(tomorrow.year, tomorrow.monthValue, tomorrow.dayOfMonth)
                timeType = TaskEnum.TIME_TYPE_NORMAL
            }
            2 -> {
                currType = TaskEnum.TASK_TYPE_MEMO
                timeType = TaskEnum.TIME_TYPE_MEMO
            }
            3 -> {
                currType = TaskEnum.TASK_TYPE_NORMAL
                timeType = TaskEnum.TIME_TYPE_NORMAL
            }
        }
    }

    fun onTimeChanged(tp: Int, h: Int, m: Int) {
        when (tp) {
            0 -> {
                timeToolActive[0].value = true
                bTimeIni = true
                beginTime.set(2002, 10, 1, h, m)
                beginTimeStr = "$h:$m"
                // 判断开始截止时间是否合理
                if (deadlineIni && !beginTime.before(deadline)) {
                    timeToolActive[1].value = false
                    deadlineIni = false
                    deadlineStr = "截止时间"
                }
            }
            1 -> {
                timeToolActive[1].value = true
                deadlineIni = true
                deadline.set(2002, 10, 1, h, m)
                deadlineStr = "$h:$m"
                // 判断开始截止时间是否合理
                if (bTimeIni && !beginTime.before(deadline)) {
                    timeToolActive[0].value = false
                    bTimeIni = false
                    beginTimeStr = "开始时间"
                }
            }
            else -> {}
        }
    }

    fun onRepSetDone() {
        when (currRepTp) {
            0 -> {
                repeatData = "0"
                timeStateType = TaskEnum.TIME_STATE_TYPE_DAILY
            }
            1 -> {
                val charArr = CharArray(7) { it ->
                    if (weekReps[it].value) (it + 1).toString()[0] else '0'
                }
                repeatData = String(charArr)
                timeStateType = TaskEnum.TIME_STATE_TYPE_WEEKLY
            }
            2 -> {
                repeatData = if (monRep < 10) "0$monRep" else monRep.toString()
                timeStateType = TaskEnum.TIME_STATE_TYPE_DAILY
            }
            3 -> {
                repeatData =
                    if (monthOfYearRep < 10) "0${monthOfYearRep}" else monthOfYearRep.toString()
                repeatData += if (dayOfYearRep < 10) "0${dayOfYearRep}" else dayOfYearRep.toString()
                timeStateType =
                    if (!yearRepLunar) TaskEnum.TIME_STATE_TYPE_DAILY else TaskEnum.TIME_STATE_TYPE_LUNAR
            }
            4 -> {
                repeatData = ""
                timeStateType = TaskEnum.TIME_STATE_TYPE_NONE
            }
        }
        Log.d("rp", "in bm: rp ->$repeatData")
        when (repeatData.length) {
            0 -> {
                repeatStr = "设置重复事件"
                timeStateType = TaskEnum.TIME_STATE_TYPE_NONE
            }
            1 -> {
                repeatStr = "每日重复事件"
            }
            2 -> {
                repeatStr = "每月${monRep}日重复事件"
            }
            4 -> {
                repeatStr =
                    if (!yearRepLunar) "每年${monthOfYearRep}月${dayOfYearRep}日重复事件" else "每年农历${monthOfYearRep}月${dayOfYearRep}日重复事件"
            }
            7 -> {
                repeatStr = "每周的"
                val chineseStr = arrayOf("一", "二", "三", "四", "五", "六", "日")
                for (i in (0..6)) {
                    if (weekReps[i].value) {
                        repeatStr += if (repeatStr.length == 3)
                            "周${chineseStr[i]}"
                        else
                            "、周${chineseStr[i]}"
                    }
                }
                if (repeatStr.length == 23) {
                    repeatStr = "每周的每天都重复"
                }
            }
        }
        timeType = if (repeatData.isNotEmpty()) {
            TaskEnum.TIME_TYPE_REPEAT
        } else {
            when (currType) {
                TaskEnum.TASK_TYPE_NORMAL -> TaskEnum.TIME_TYPE_NORMAL
                TaskEnum.TASK_TYPE_MEMO -> TaskEnum.TIME_TYPE_MEMO
                else -> TaskEnum.TIME_TYPE_NORMAL
            }
        }
    }

    fun setWeekReps(index: Int) {
        val tempArr = CharArray(7) {
            if (weekReps[it].value) (it + 1).toString()[0] else '0'
        }
        repeatData = String(tempArr)
    }

    fun onMonRepChanged(str: String) {
        val n = str.toInt()
        if (n <= 0 || n >= 32) {
            monRep = 1
        }
        monRep = n
        repeatData = if (monRep < 10) "0$monRep" else monRep.toString()
    }

    fun setMonRepData(increase: Boolean) {
        if (increase && monRep < 31) {
            monRep += 1
        } else if (!increase && monRep > 1) {
            monRep -= 1
        }
        repeatData = if (monRep < 10) "0${monRep}" else monRep.toString()
    }

    fun setLunar(flg: Boolean) {
        yearRepLunar = flg
        timeStateType = if (flg) TaskEnum.TIME_STATE_TYPE_LUNAR else TaskEnum.TIME_STATE_TYPE_YEARLY
    }

    fun setMonOfYearRep(increase: Boolean) {
        if (increase && monthOfYearRep < 12) {
            monthOfYearRep += 1
        } else if (!increase && monthOfYearRep > 1) {
            monthOfYearRep -= 1
        }
        repeatData =
            if (monthOfYearRep < 10) "0${monthOfYearRep}" else monthOfYearRep.toString()
        repeatData += if (dayOfYearRep < 10) "0${dayOfYearRep}" else dayOfYearRep.toString()
    }

    fun onMonOfYearChanged(str: String) {
        val n = str.toInt()
        if (n <= 0 || n >= 13) {
            monRep = 1
        }
        monRep = n
        repeatData = if (monRep < 10) "0$monRep" else monRep.toString()
    }

    fun setDayOfYearRep(increase: Boolean) {
        if (increase && dayOfYearRep < 31) {
            dayOfYearRep += 1
        } else if (!increase && dayOfYearRep > 1) {
            dayOfYearRep -= 1
        }
        repeatData =
            if (monthOfYearRep < 10) "0${monthOfYearRep}" else monthOfYearRep.toString()
        repeatData += if (dayOfYearRep < 10) "0${dayOfYearRep}" else dayOfYearRep.toString()
    }

    fun onDayOfYearChanged(str: String) {
        val n = str.toInt()
        if (n <= 0 || n >= 32) {
            monRep = 1
        }
        monRep = n
        repeatData = if (monRep < 10) "0$monRep" else monRep.toString()
    }

    fun onCurrRepTpChanged(i: Int) {
        currRepTp = i
//        if (i != currRepTp) {
//            repeatData = ""
//        }
//        when (i) {
//            0 -> {
//                repeatData = "0"
//                timeStateType = TaskEnum.TIME_STATE_TYPE_DAILY
//            }
//            1 -> {
//                val charArr = CharArray(7) { it ->
//                    if (weekReps[it].value) (it + 1).toString()[0] else '0'
//                }
//                repeatData = String(charArr)
//                timeStateType = TaskEnum.TIME_STATE_TYPE_WEEKLY
//            }
//            2 -> {
//                repeatData = if (monRep < 10) "0$monRep" else monRep.toString()
//                timeStateType = TaskEnum.TIME_STATE_TYPE_DAILY
//            }
//            3 -> {
//                repeatData =
//                    if (monthOfYearRep < 10) "0${monthOfYearRep}" else monthOfYearRep.toString()
//                repeatData += if (dayOfYearRep < 10) "0${dayOfYearRep}" else dayOfYearRep.toString()
//                timeStateType =
//                    if (!yearRepLunar) TaskEnum.TIME_STATE_TYPE_DAILY else TaskEnum.TIME_STATE_TYPE_LUNAR
//            }
//            4 -> {
//                repeatData = ""
//                timeStateType = TaskEnum.TIME_STATE_TYPE_NONE
//            }
//        }
    }
}