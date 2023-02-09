package com.example.forayao2023.logic.entity.task

import com.example.forayao2023.logic.common.datetime.DateTimeTool
import com.example.forayao2023.logic.common.utility.TaskEnum
import java.time.LocalDate
import java.time.LocalDateTime

class TaskTimeRepeat(
    var taskID: Long,
    var begin: Long = DateTimeTool.toLong(LocalDate.now()),
    var deadline: Long = DateTimeTool.toLong(LocalDate.now().plusDays(1)),
    var created: Long = DateTimeTool.toLong(LocalDate.now()),
    var checked: Long = -10,
    var lastChecked: Long = -10,
    var stateType: Int = TaskEnum.TIME_STATE_TYPE_NONE,
    var timeState: String = "0"
) : TaskTime {
    override var startTime: LocalDateTime?
        get() = DateTimeTool.toDateTime(begin)
        set(value) {
            if (value != null) {
                deadline = DateTimeTool.toLong(value)
            }
        }

    override var endTime: LocalDateTime?
        get() = DateTimeTool.toDateTime(deadline)
        set(value) {
            if (value != null) {
                deadline = DateTimeTool.toLong(value)
            }
        }
}