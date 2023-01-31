package com.example.forayao2023.logic.model

import android.util.Log
import com.example.forayao2023.logic.common.datetime.LunarFestival
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DayItemModel(val date: LocalDate) {
    val dateStr = date.dayOfMonth.toString()
    val lunarDate =
        LunarFestival().let {
            it.initLunarCalendarInfo(date)
            LocalDate.of(it.year, it.month, it.day)
        }
}

class TimeModel() {
    companion object {
        val chineseStr = arrayListOf("一", "二", "三", "四", "五", "六", "日", "休息日", "工作日")
        private val storyBeginDate = LocalDate.of(2020, 10, 1)

        val instance: TimeModel by lazy {
            TimeModel()
        }
    }

    // day item
    lateinit var dateItemModels: Array<DayItemModel>
        private set

    var selectedTime: LocalDateTime = LocalDateTime.now()
        private set
    var lunarYear: String = ""
        private set
    var lunarMonthStr: String = ""
        private set
    var lunarDayStr: String = ""
        private set
    var lunarFestival: String = ""
        private set
    var weekDayStr: String = ""
        private set
    val dateGap: Long by lazy {
        LocalDate.now().toEpochDay() - storyBeginDate.toEpochDay()
    }

    fun iniTimeData(date: LocalDateTime = LocalDateTime.now()) {
        selectedTime = date
        LunarFestival().let {
            it.initLunarCalendarInfo(selectedTime.toLocalDate())
            lunarYear = it.ganZhiYear
            lunarMonthStr = it.lunarMonth
            lunarDayStr = it.lunarDay
            lunarFestival = it.lunarFestival
        }
        weekDayStr = "星期${chineseStr[selectedTime.dayOfWeek.value - 1]}"
        // day items
        iniDayItemModels(calculateDates(selectedTime.toLocalDate()))
        Log.d("cd", "in tm: data->${lunarYear}/${lunarMonthStr}/${lunarDayStr}/${lunarFestival}")
    }

    fun setTimeData(index: Int) {
        selectedTime = LocalDateTime.of(dateItemModels[index].date, LocalTime.now())
        LunarFestival().let {
            it.initLunarCalendarInfo(selectedTime.toLocalDate())
            lunarYear = it.ganZhiYear
            lunarMonthStr = it.lunarMonth
            lunarDayStr = it.lunarDay
            lunarFestival = it.lunarFestival
        }
        weekDayStr = "星期${chineseStr[selectedTime.dayOfWeek.value - 1]}"
    }

    private fun calculateDates(curr: LocalDate): Array<LocalDate> {
        val firstDay = curr.let {
            it.plusDays((1 - it.dayOfWeek.value).toLong())
        }
        Log.d("day","in tm: fst day ->$firstDay")
        return Array(7) {
            firstDay.plusDays(it.toLong())
        }
    }

    private fun iniDayItemModels(dates: Array<LocalDate>) {
        dateItemModels = Array(7) {
            DayItemModel(date = dates[it])
        }
    }
}