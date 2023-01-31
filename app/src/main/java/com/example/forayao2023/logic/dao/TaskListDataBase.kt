package com.example.forayao2023.logic.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.forayao2023.logic.entity.task.AyaoTask

@Database(
    version = 1,
    entities = [AyaoTask::class],
)
abstract class TaskListDataBase : RoomDatabase() {
    abstract fun todoDao(): TaskListDao

    companion object {
        private var instant: TaskListDataBase? = null

        @Synchronized
        fun getInstance(context: Context): TaskListDataBase {
            instant?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                TaskListDataBase::class.java,
                "taskListDataBase"
            )
                .build().apply {
                    instant = this
                }
        }
    }
}