package com.example.forayao2023.logic.dao

import androidx.room.*
import com.example.forayao2023.logic.entity.task.AyaoTask

@Dao
interface TaskListDao {
    // 插入
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTask(task: AyaoTask): Long

    @Insert
    fun insertAllTask(tasks: List<AyaoTask>)

    // 更新
    @Update
    fun updateTask(task: AyaoTask)

    @Update
    fun updateTask(tasks: List<AyaoTask>)

    // 删除
    @Delete
    fun deleteTask(task: AyaoTask)

    // 查询
    // 全部
    @Query("SELECT * FROM taskListTable")
    fun loadTasks(): Array<AyaoTask>

    // 时间段
    // 公历/农历：未完成 and 是普通任务 and ((公历/农历 and 在时间段内) or (重复 and 每日重复))
    @Query(
        "SELECT * FROM taskListTable " +
                "WHERE (state == 0 AND type == 0) AND(" +
                "((timeType == 0 OR timeType == 1) AND (timeB >=:start AND timeB <=:end))" +
                "OR (timeType == 2 AND timeStateType == 1))"
    )
    fun loadTasks(start: Long, end: Long): Array<AyaoTask>

    // 周内重复
    // 未完成 and 是普通任务 and 是重复任务 and 是周内重复 and 匹配到 week day
    @Query(
        "SELECT * FROM taskListTable " +
                "WHERE (state == 0 AND type == 0) " +
                "AND timeType == 2 AND timeStateType == 2 " +
                "AND timeState LIKE '%' || :weekDay || '%'"
    )
    fun loadTasksByWeek(weekDay: Int): Array<AyaoTask>

    // 每月
    // 公历/农历：未完成 and 是普通任务 and 是月重复 and 匹配日期
    @Query(
        "SELECT * FROM taskListTable " +
                "WHERE (state == 0 AND type == 0) " +
                "AND timeType == 2 AND timeStateType == 3 " +
                "AND timeState == :monthDay"
    )
    fun loadTasksByMonth(monthDay: String): Array<AyaoTask>

    // 每年
    // 公历/农历：未完成 and 是普通任务 and 是年重复 and 匹配日期
    @Query(
        "SELECT * FROM taskListTable " +
                "WHERE (state == 0 AND type == 0) " +
                "AND timeType == 2 AND (timeStateType == 4 OR timeStateType == 5) " +
                "AND timeState == :date"
    )
    fun loadTasksByYear(date: String): Array<AyaoTask>

    // 备忘录
    // 未完成 and 是备忘录
    @Query("SELECT * FROM taskListTable WHERE (state == 0 AND type == 1)")
    fun loadMemoTasks(): Array<AyaoTask>

    // 已完成
    @Query(
        "SELECT * FROM taskListTable " +
                "WHERE (state == 1 AND type == 0) AND(" +
                "(timeType == 0 OR timeType == 1 OR timeStateType == 1) AND (timeB >=:start AND timeB <=:end))"
    )
    fun loadFinishedTasksByDate(start: Long, end: Long): Array<AyaoTask>

    @Query(
        "SELECT * FROM taskListTable " +
                "WHERE (state == 1 AND type == 0) " +
                "AND timeType == 2 AND timeStateType == 2 " +
                "AND timeState LIKE '%' || :weekDay || '%' AND timeB > :currLong"
    )
    fun loadFinishedTasksByWeek(weekDay: Int, currLong: Long): Array<AyaoTask>

    @Query(
        "SELECT * FROM taskListTable " +
                "WHERE (state == 1 AND type == 0) " +
                "AND timeType == 2 AND timeStateType == 3 " +
                "AND timeState == :monthDay AND timeB > :currLong"
    )
    fun loadFinishedTasksByMonth(monthDay: String, currLong: Long): Array<AyaoTask>

    @Query(
        "SELECT * FROM taskListTable " +
                "WHERE (state == 1 AND type == 0) " +
                "AND timeType == 2 AND (timeStateType == 4 OR timeStateType == 5) " +
                "AND timeState == :date AND timeB > :currLong"
    )
    fun loadFinishedTasksByYear(date: String, currLong: Long): Array<AyaoTask>

    @Query("SELECT * FROM taskListTable WHERE state == 1 AND type == 1 AND (timeB >=:start AND timeB <=:end)")
    fun loadFinishedMemoTasks(start: Long, end: Long): Array<AyaoTask>

    @Query("SELECT * FROM taskListTable WHERE state == 0 AND (timeType == 0 OR timeType == 1) AND timeE < :curr")
    fun loadTimeoutTasks(curr: Long): Array<AyaoTask>

    @Query(
        "SELECT * FROM taskListTable " +
                "WHERE (state == 0 AND type == 0) " +
                "AND timeType == 2 AND timeStateType == 2 AND timeB < :currLong " +
                "AND NOT (timeState LIKE '%' || :weekDay || '%')"
    )
    fun loadTimeoutTasksByWeek(weekDay: Int, currLong: Long): Array<AyaoTask>

    @Query(
        "SELECT * FROM taskListTable " +
                "WHERE (state == 0 AND type == 0) " +
                "AND timeType == 2 AND timeStateType == 3 AND timeB <:currLong " +
                "AND NOT timeState == :monthDay"
    )
    fun loadTimeoutTasksByMonth(monthDay: String, currLong: Long): Array<AyaoTask>

    @Query(
        "SELECT * FROM taskListTable " +
                "WHERE (state == 0 AND type == 0) " +
                "AND timeType == 2 AND (timeStateType == 4 OR timeStateType == 5) AND timeB <:currLong " +
                "AND NOT timeState == :date"
    )
    fun loadTimeoutTasksByYear(date: String, currLong: Long): Array<AyaoTask>
}