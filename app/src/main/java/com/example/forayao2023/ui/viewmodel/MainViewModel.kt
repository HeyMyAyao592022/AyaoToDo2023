package com.example.forayao2023.ui.viewmodel

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forayao2023.logic.common.utility.TaskEnum
import com.example.forayao2023.logic.entity.task.AyaoTask
import com.example.forayao2023.logic.entity.task.TaskState
import com.example.forayao2023.logic.model.TaskListModel
import com.example.forayao2023.logic.model.TimeModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MainViewModel : ViewModel() {
    // task model
    private val taskListModel by lazy {
        TaskListModel()
    }

    var currMark by mutableStateOf(TaskEnum.MARK_NONE)

    lateinit var normalTasks: MutableStateFlow<ArrayList<TaskState>>
        private set
    lateinit var memoTasks: MutableStateFlow<ArrayList<TaskState>>
        private set
    lateinit var finishedTasks: MutableStateFlow<ArrayList<TaskState>>
        private set
    lateinit var timeoutTasks: MutableStateFlow<ArrayList<TaskState>>
        private set

    fun tsLoad() {
        loadTasks(LocalDate.now())
    }

    fun tsSet() {
        viewModelScope.launch(Dispatchers.IO) {
            taskListModel.testResetDB()
        }
    }

    // 插入
    fun insertTask(task: AyaoTask) {
        // model
        viewModelScope.launch {
            launch(Dispatchers.IO) { taskListModel.insertTask(task = task) }
            launch(Dispatchers.Default) {
                when (task.type) {
                    TaskEnum.TASK_TYPE_NORMAL, TaskEnum.TASK_TYPE_COURSE -> {
                        taskListModel.let {
                            it.normalTasks.add(task)
                            it.sortTaskList(0)
                        }
                    }
                    TaskEnum.TASK_TYPE_MEMO -> {
                        taskListModel.let {
                            it.memoTasks.add(task)
                            it.sortTaskList(1)
                        }
                    }
                }
            }
        }
    }

    // 更新
    fun onTaskChecked(tsk: TaskState) {
        viewModelScope.launch(Dispatchers.IO) {
            // 修改实例
            tsk.state = if (tsk.state == TaskEnum.UNDONE) TaskEnum.DONE else TaskEnum.UNDONE
            if (tsk.state == TaskEnum.UNDONE)
                when (tsk.type) {
                    TaskEnum.TIME_TYPE_NORMAL, TaskEnum.TIME_TYPE_LUNAR -> {}
                    TaskEnum.TIME_TYPE_MEMO -> {
                        tsk.beginTime = LocalDateTime.now()
                    }
                    TaskEnum.TIME_TYPE_REPEAT -> {
                        tsk.deadline = tsk.beginTime
                        tsk.beginTime = LocalDateTime.now()
                    }
                    else -> {}
                }
            else
                when (tsk.type) {
                    TaskEnum.TIME_TYPE_REPEAT -> {
                        tsk.beginTime = tsk.deadline
                    }
                    else -> {}
                }
            // todo 更新数据库
            taskListModel.updateTasks(tsk.task)
        }
    }

    fun delayTimeout(tsk: TaskState) {
        if (tsk.state == TaskEnum.UNDONE)
            viewModelScope.launch(Dispatchers.IO) {
                // 修改实例
                tsk.let {
                    when (it.task.timeType) {
                        TaskEnum.TIME_TYPE_NORMAL, TaskEnum.TIME_TYPE_LUNAR -> {
                            it.beginTime = it.beginTime.plusDays(1)
                            it.deadline = it.deadline.plusDays(1)
                        }
                        TaskEnum.TIME_TYPE_REPEAT -> {
                            when (it.task.timeStateType) {
                                TaskEnum.TIME_STATE_TYPE_WEEKLY -> {
                                    it.beginTime = it.beginTime.plusWeeks(1)
                                }
                                TaskEnum.TIME_STATE_TYPE_MONTHLY -> {
                                    it.beginTime = it.beginTime.plusMonths(1)
                                }
                                TaskEnum.TIME_STATE_TYPE_LUNAR, TaskEnum.TIME_STATE_TYPE_YEARLY -> {
                                    it.beginTime = it.beginTime.plusYears(1)
                                }
                            }
                        }
                        else -> {}
                    }
                }
                // 修改数据库
                taskListModel.updateTasks(tsk.task)
                // 刷新
                loadTasks(LocalDate.now())
            }
    }

    // 加载
    fun onCurrDateChanged(index: Int) {
        // time
        timeModel.setTimeData(index)
        // state
        topBarTitle.value = setTopTitle()
        currTime.value = timeModel.selectedTime
        currTime.value.toLocalDate().let {
            // ls model
            taskListModel.loadTasks(date = it)
            // states
            loadTasks(date = it)
        }
    }

    fun onCurrDateChanged(begin: LocalDate, end: LocalDate) {

    }

    fun loadTasks(date: LocalDate) {
        // model 加载
        taskListModel.loadTasks(date)
        // state list
        normalTasks.value = ArrayList<TaskState>().apply {
            taskListModel.normalTasks
                .filter { (currMark != TaskEnum.MARK_NONE && it.mark == currMark) || currMark == TaskEnum.MARK_NONE }
                .forEach { tsk ->
                    this.add(TaskState(tsk))
                }
        }
        memoTasks.value = ArrayList<TaskState>().apply {
            taskListModel.memoTasks
                .filter { (currMark != TaskEnum.MARK_NONE && it.mark == currMark) || currMark == TaskEnum.MARK_NONE }
                .forEach { tsk ->
                    this.add(TaskState(tsk))
                }
        }
        finishedTasks.value = ArrayList<TaskState>().apply {
            taskListModel.finishedTasks
                .filter { (currMark != TaskEnum.MARK_NONE && it.mark == currMark) || currMark == TaskEnum.MARK_NONE }
                .forEach { tsk ->
                    this.add(TaskState(tsk))
                }
        }
        timeoutTasks.value = ArrayList<TaskState>().apply {
            taskListModel.timeoutTasks
                .filter { (currMark != TaskEnum.MARK_NONE && it.mark == currMark) || currMark == TaskEnum.MARK_NONE }
                .forEach { tsk ->
                    this.add(TaskState(tsk))
                }
        }
    }

    fun iniTasks() {
        // model 加载
        // taskListModel.loadTasks(LocalDate.now())
        taskListModel.iniTasks()
        // state list
        normalTasks = MutableStateFlow(ArrayList<TaskState>().apply {
            taskListModel.normalTasks
                .filter { (currMark != TaskEnum.MARK_NONE && it.mark == currMark) || currMark == TaskEnum.MARK_NONE }
                .forEach { tsk ->
                    this.add(TaskState(tsk))
                }
        })
        memoTasks = MutableStateFlow(ArrayList<TaskState>().apply {
            taskListModel.memoTasks
                .filter { (currMark != TaskEnum.MARK_NONE && it.mark == currMark) || currMark == TaskEnum.MARK_NONE }
                .forEach { tsk ->
                    this.add(TaskState(tsk))
                }
        })
        finishedTasks = MutableStateFlow(ArrayList<TaskState>().apply {
            taskListModel.finishedTasks
                .filter { (currMark != TaskEnum.MARK_NONE && it.mark == currMark) || currMark == TaskEnum.MARK_NONE }
                .forEach { tsk ->
                    this.add(TaskState(tsk))
                }
        })
        timeoutTasks = MutableStateFlow(ArrayList<TaskState>().apply {
            taskListModel.timeoutTasks
                .filter { (currMark != TaskEnum.MARK_NONE && it.mark == currMark) || currMark == TaskEnum.MARK_NONE }
                .forEach { tsk ->
                    this.add(TaskState(tsk))
                }
        })
    }

    // time
    val timeModel by lazy {
        TimeModel.instance
    }
    val topBarTitle by lazy {
        mutableStateOf(setTopTitle())
    }
    val currTime by lazy {
        mutableStateOf(timeModel.selectedTime)
    }
    val dayItemStates by lazy {
        Array(7) {
            mutableStateOf(timeModel.dateItemModels[it].dateStr)
        }
    }

    private fun setTopTitle(): String {
        val festival = timeModel.lunarFestival
        return if (festival != "") {
            "${timeModel.lunarYear}年${festival}"
        } else {
            "${timeModel.lunarMonthStr}月${timeModel.lunarDayStr}"
        }
    }

    // bottom
    //导航label数组
    val viewLabels by lazy {
        arrayOf("代办", "课表", "我的")
    }
    val viewIcons by lazy {
        arrayOf(Icons.Default.List, Icons.Default.AccountCircle, Icons.Default.Favorite)
    }
    val viewIconsUnselected by lazy {
        arrayOf(Icons.Outlined.List, Icons.Outlined.AccountCircle, Icons.Outlined.Favorite)
    }
    val viewSelectedIndex by lazy {
        mutableStateOf(0)
    }

    fun onViewChanged(index: Int) {
        viewSelectedIndex.value = index
    }

    init {
        Log.d("cd", "in vm: ini vm")
    }
}