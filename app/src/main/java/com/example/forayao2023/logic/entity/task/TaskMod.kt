package com.example.forayao2023.logic.entity.task

import androidx.room.PrimaryKey
import com.example.forayao2023.logic.common.datetime.DateTimeTool
import com.example.forayao2023.logic.common.utility.TaskEnum
import java.time.LocalDate

data class TaskMod(
    var title: String = "title",
    var info: String = "details",
    // 表示 normal/memo/course 等类型
    var type: Int = TaskEnum.TASK_TYPE_NORMAL,
    // 时间类型: 公历无时间/公历有时间/农历/重复事件/备忘录
    var timeType: Int = TaskEnum.TIME_TYPE_NORMAL,
    // 是否完成/过期
    var state: Int = TaskEnum.UNDONE,
    // 优先级
    var priority: Int = TaskEnum.PRI_NONE,
    // 标记
    var mark: Int = TaskEnum.MARK_NONE,
    // id
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
)