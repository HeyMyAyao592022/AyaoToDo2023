package com.example.forayao2023.logic.common.datetime

import java.time.*

class DateTimeTool {
    companion object {
        // 当前时间
        private val currDate = LocalDate.now()

        // long 转时间
        fun toDate(l: Long): LocalDate {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault())
                .toLocalDate()
        }
        fun toDateTime(l: Long): LocalDateTime {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault())
        }
        fun toTime(l: Long): LocalTime {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault())
                .toLocalTime()
        }

        // 时间转 long
        // 日期转 long
        fun toLong(date: LocalDate = currDate, time: LocalTime = LocalTime.of(0,0)): Long {
            return toLong(LocalDateTime.of(date, time))
        }
        // date time 转 long
        fun toLong(time: LocalDateTime = LocalDateTime.now()): Long {
            return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    }
}