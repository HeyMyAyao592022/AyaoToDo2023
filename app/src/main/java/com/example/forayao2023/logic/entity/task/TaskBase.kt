package com.example.forayao2023.logic.entity.task

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.forayao2023.logic.common.utility.TaskMark

open class TaskBase(val task: AyaoTask) {
    // state
    private var _title by mutableStateOf(task.title)
    var title: String
        get() = _title
        set(value) {
            task.title = value
            _title = value
        }

    private var _info by mutableStateOf(task.info)
    var info: String
        get() = _info
        set(value) {
            task.info = value
            _info = value
        }

    // 表示 normal/regular/course 等类型
    private var _type by mutableStateOf(task.type)
    var type: Int
        get() = _type
        set(value) {
            task.type = value
            _type = value
        }

    // 是否完成/过期
    private var _state by mutableStateOf(task.state)
    var state: Int
        get() = _state
        set(value) {
            task.state = value
            _state = value
        }

    // 标记
    private var _mark by mutableStateOf(task.mark)
    val mark: TaskMark
        get() = when(_mark){
            0-> TaskMark.Study
            1-> TaskMark.Stuffs
            2-> TaskMark.ToBuy
            3-> TaskMark.Holiday
            4-> TaskMark.RecentPlan
            5-> TaskMark.OnlyYao
            else-> TaskMark.OnlyYao
        }
}