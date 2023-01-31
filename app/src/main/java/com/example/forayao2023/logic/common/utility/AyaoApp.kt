package com.example.forayao2023.logic.common.utility

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.forayao2023.logic.dao.TaskListDataBase

class AyaoApp: Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var sContext: Context

        fun context(): Context {
            return sContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        sContext = applicationContext
    }
}