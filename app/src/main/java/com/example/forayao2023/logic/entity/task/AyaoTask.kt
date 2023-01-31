package com.example.forayao2023.logic.entity.task

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.forayao2023.logic.common.datetime.DateTimeTool
import com.example.forayao2023.logic.common.utility.TaskEnum
import java.time.LocalDate

@Entity(tableName = "taskListTable")
data class AyaoTask(
    var title: String = "title",
    var info: String = "details",
    // 表示 normal/regular/course 等类型
    var type: Int = TaskEnum.TASK_TYPE_NORMAL,
    // 时间类型: 公历无时间/公历有时间/农历/重复事件/备忘录
    var timeType: Int = TaskEnum.TIME_TYPE_NORMAL,
    // 公历开始时间/农历开始时间/备忘录创建时间
    var timeB: Long = DateTimeTool.toLong(LocalDate.now()),
    // 公历截止时间/农历截止时间/备忘录截止时间
    var timeE: Long = DateTimeTool.toLong(LocalDate.now().plusDays(1)),
    // 重复状态
    var timeStateType: Int = TaskEnum.TIME_STATE_TYPE_NONE,
    var timeState: String = "0",
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
