package com.zkrallah.z_habits.local

import androidx.room.*
import com.zkrallah.z_habits.local.entities.History
import com.zkrallah.z_habits.local.entities.Mood

@Dao
interface MoodDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMode(mood: Mood?)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMood(mood: Mood?)

    @Query("select * from mood_table WHERE date = :date")
    fun getTodayMood(date: String): Mood?

    @Query("select * from mood_table")
    fun getMoodHistory(): List<Mood>
}