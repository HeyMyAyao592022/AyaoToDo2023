package com.example.forayao2023.logic.entity.task

import java.time.LocalDateTime

interface TaskTime {
    open var startTime: LocalDateTime?
    open var endTime: LocalDateTime?
}