package com.zkrallah.z_habits.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zkrallah.z_habits.HabitsApp.Companion.ctx
import com.zkrallah.z_habits.local.entities.Habits
import com.zkrallah.z_habits.local.entities.History

@Database(entities = [Habits::class, History::class], version = 2)
abstract class HabitsDatabase : RoomDatabase(){
    abstract fun habitsDAO(): HabitsDAO
    abstract fun historyDAO(): HistoryDAO

    companion object {
        private var instance: HabitsDatabase? = null
        private val context = ctx

        @Synchronized
        fun getInstance(): HabitsDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(context.applicationContext, HabitsDatabase::class.java,
                    "habits_database")
                    .fallbackToDestructiveMigration()
                    .build()

            return instance!!
        }
    }
}