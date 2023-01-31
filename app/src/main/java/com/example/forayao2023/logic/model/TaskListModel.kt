package com.example.forayao2023.logic.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.forayao2023.logic.common.datetime.DateTimeTool
import com.example.forayao2023.logic.common.utility.AyaoApp
import com.example.forayao2023.logic.common.utility.TaskEnum
import com.example.forayao2023.logic.dao.TaskListDataBase
import com.example.forayao2023.logic.entity.task.AyaoTask
import com.example.forayao2023.logic.entity.task.TaskState
import java.time.LocalDate
import java.time.LocalDateTime

class TaskListModel {
    // 展示在界面的 tasks
    val normalTasks = ArrayList<AyaoTask>()
    val memoTasks = ArrayList<AyaoTask>()
    val finishedTasks = ArrayList<AyaoTask>()
    val timeoutTasks = ArrayList<AyaoTask>()

    fun sortTaskList(tp: Int) {
        when (tp) {
            0 -> {
                normalTasks.sortWith(compareBy({
                    if (it.timeB != 0L) it.timeB
                    else -10
                }, { it.timeE }))
            }
            1 -> {
                memoTasks.sortBy { it.timeE }
            }
            2 -> {
                finishedTasks.sortBy { it.timeB }
            }
            else -> {}
        }
    }

    // 插入
    @Synchronized
    fun insertTask(task: AyaoTask) {
        // insert db
        taskDateBase.todoDao().insertTask(task)
    }

    // 更新
    @Synchronized
    fun updateTasks(task: AyaoTask) {
        taskDateBase.todoDao().updateTask(task)
    }

    // 加载 tasks
    private fun loadNorTasks(
        date: LocalDate = LocalDate.now(),
        currDateLong: Long = DateTimeTool.toLong(LocalDateTime.now()),
        tomorrowLong: Long = DateTimeTool.toLong(LocalDateTime.now().plusDays(1))
    ) {
        synchronized(taskDateBase.todoDao()) {
            taskDateBase.todoDao()
                .let { it ->
                    // 普通非重复任务
                    normalTasks.addAll(it.loadTasks(currDateLong, tomorrowLong))
                    // 普通重复: 每周
                    val weekDay = date.dayOfWeek.value
                    normalTasks.addAll(it.loadTasksByWeek(weekDay))
                    // 普通重复：每月
                    val dayStr: String =
                        if (date.dayOfMonth < 10) "0" + date.dayOfMonth.toString() else date.dayOfMonth.toString()
                    normalTasks.addAll(it.loadTasksByMonth(dayStr))
                    // 普通重复: 公历每年
                    val monthStr: String =
                        if (date.monthValue < 10) "0" + date.monthValue.toString() else date.monthValue.toString()
                    normalTasks.addAll(it.loadTasksByYear(monthStr + dayStr))
                    // todo 重复农历
                }
        }
    }

    private fun loadMemoTasks(date: LocalDate = LocalDate.now()) {
        synchronized(taskDateBase.todoDao()) {
            taskDateBase.todoDao()
                .let {
                    // 备忘录任务
                    memoTasks.addAll(it.loadMemoTasks())
                }
        }
    }

    private fun loadFinishedTasks(
        date: LocalDate = LocalDate.now(),
        currDateLong: Long = DateTimeTool.toLong(LocalDateTime.now()),
        tomorrowLong: Long = DateTimeTool.toLong(LocalDateTime.now().plusDays(1))
    ) {
        synchronized(taskDateBase.todoDao()) {
            val currLong = DateTimeTool.toLong(LocalDate.now())
            Log.d("done", "in tskM: curr l->$currLong")
            taskDateBase.todoDao().let { it ->
                finishedTasks.let { finished ->
                    // 普通非重复任务
                    finished.addAll(it.loadFinishedTasksByDate(currDateLong, tomorrowLong))
                    // 普通重复: 每周
                    val weekDay = date.dayOfWeek.value
                    finished.addAll(
                        it.loadFinishedTasksByWeek(
                            weekDay = weekDay,
                            currLong = currLong
                        )
                    )
                    // 普通重复：每月
                    val dayStr: String =
                        if (date.dayOfMonth < 10) "0" + date.dayOfMonth.toString() else date.dayOfMonth.toString()
                    finished.addAll(it.loadFinishedTasksByYear(dayStr, currLong))
                    // 普通重复: 公历每年
                    val monthStr: String =
                        if (date.monthValue < 10) "0" + date.monthValue.toString() else date.monthValue.toString()
                    finished.addAll(it.loadFinishedTasksByYear(monthStr + dayStr, currLong))
                    // todo 重复农历
                    // memo 任务
                    finished.addAll(
                        it.loadFinishedMemoTasks(
                            start = currDateLong,
                            end = tomorrowLong
                        )
                    )
                }
            }
        }
    }

    private fun loadTimeoutTasks(currDateLong: Long) {
        synchronized(taskDateBase.todoDao()) {
            taskDateBase.todoDao()
                .let {
                    // 备忘录任务
                    timeoutTasks.addAll(it.loadTimeoutTasks(currDateLong))
                }
        }
    }

    private fun updateRepFinished() {
        val currLong = DateTimeTool.toLong(LocalDate.now())
        finishedTasks.filter { it.timeType == TaskEnum.TIME_TYPE_REPEAT }.forEach { tsk ->
            if (tsk.timeB < currLong) {
                tsk.state = TaskEnum.UNDONE
                updateTasks(tsk)
            }
        }
    }

    @Synchronized
    fun iniTasks() {
        // 日期
        val date = LocalDate.now()
        val tomorrow = date.plusDays(1)
        // 查询公历 (今天~明天）
        val currDateLong = DateTimeTool.toLong(date)
        val tomorrowLong = DateTimeTool.toLong(tomorrow)
        // 加载 task
        loadFinishedTasks(date = date, currDateLong = currDateLong, tomorrowLong = tomorrowLong)
        updateRepFinished()
        loadNorTasks(date = date, currDateLong = currDateLong, tomorrowLong = tomorrowLong)
        loadMemoTasks(date = date)
        loadTimeoutTasks(currDateLong = currDateLong)
        // 排序
        sortTaskList(0)
        sortTaskList(1)
        sortTaskList(2)
    }

    @Synchronized
    fun loadTasks(date: LocalDate = LocalDate.now()) {
        normalTasks.clear()
        memoTasks.clear()
        finishedTasks.clear()
        timeoutTasks.clear()
        // 日期
        val tomorrow = date.plusDays(1)
        // 查询公历 (今天~明天）
        val currDateLong = DateTimeTool.toLong(date)
        val tomorrowLong = DateTimeTool.toLong(tomorrow)
        // 加载 task
        loadNorTasks(date = date, currDateLong = currDateLong, tomorrowLong = tomorrowLong)
        loadMemoTasks(date = date)
        loadFinishedTasks(date = date, currDateLong = currDateLong, tomorrowLong = tomorrowLong)
        if (date == LocalDate.now())
            loadTimeoutTasks(currDateLong)
        // 排序
        sortTaskList(0)
        sortTaskList(1)
        sortTaskList(2)
    }

    // date base
    private val taskDateBase: TaskListDataBase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Log.d("ls", "in tskM: ini db~~~~")
        TaskListDataBase.getInstance(AyaoApp.context())
    }

    // test
    private val testData1 = arrayListOf(
        AyaoTask(title = "3D建模", timeType = TaskEnum.TIME_TYPE_NORMAL),
        AyaoTask(title = "数学建模", timeType = TaskEnum.TIME_TYPE_LUNAR),
        AyaoTask(
            title = "socket",
            timeType = TaskEnum.TIME_TYPE_REPEAT,
            timeStateType = TaskEnum.TIME_STATE_TYPE_DAILY,
        ),
        AyaoTask(
            title = "servlet",
            timeType = TaskEnum.TIME_TYPE_REPEAT,
            timeStateType = TaskEnum.TIME_STATE_TYPE_WEEKLY,
            timeState = "1234567"
        ),
        AyaoTask(
            title = "MyBaits",
            timeType = TaskEnum.TIME_TYPE_REPEAT,
            timeStateType = TaskEnum.TIME_STATE_TYPE_MONTHLY,
            timeState = "16"
        ),
        AyaoTask(
            title = "SpringMCV",
            timeType = TaskEnum.TIME_TYPE_REPEAT,
            timeStateType = TaskEnum.TIME_STATE_TYPE_LUNAR,
            timeState = "0125"
        ),
        AyaoTask(
            title = "Spring",
            timeType = TaskEnum.TIME_TYPE_REPEAT,
            timeStateType = TaskEnum.TIME_STATE_TYPE_YEARLY,
            timeState = "0116"
        ),
        AyaoTask(title = "SpringBoot", timeType = TaskEnum.TIME_TYPE_NORMAL),
        AyaoTask(title = "vue", timeType = TaskEnum.TIME_TYPE_NORMAL),
        AyaoTask(title = "three.js", timeType = TaskEnum.TIME_TYPE_NORMAL),
    )

    private val testData2 = arrayListOf<AyaoTask>(
        AyaoTask(title = "模电", type = TaskEnum.TASK_TYPE_MEMO, timeType = TaskEnum.TIME_TYPE_MEMO),
        AyaoTask(title = "线代", type = TaskEnum.TASK_TYPE_MEMO, timeType = TaskEnum.TIME_TYPE_MEMO),
        AyaoTask(title = "六级", type = TaskEnum.TASK_TYPE_MEMO, timeType = TaskEnum.TIME_TYPE_MEMO),
        AyaoTask(
            title = "数据结构",
            type = TaskEnum.TASK_TYPE_MEMO,
            timeType = TaskEnum.TIME_TYPE_MEMO
        ),
        AyaoTask(
            title = "大物网课",
            type = TaskEnum.TASK_TYPE_MEMO,
            timeType = TaskEnum.TIME_TYPE_MEMO
        ),
    )

    fun testResetDB() {
        val arr = taskDateBase.todoDao().loadTasks()
        arr.forEach {
            taskDateBase.todoDao().deleteTask(it)
        }

        testData1.forEach {
            taskDateBase.todoDao().insertTask(it)
        }
        testData2.forEach {
            taskDateBase.todoDao().insertTask(it)
        }
    }
}