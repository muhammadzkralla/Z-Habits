package com.zkrallah.z_habits.local

import androidx.room.*
import com.zkrallah.z_habits.local.entities.History

@Dao
interface HistoryDAO {

    @Query("select * from history_table")
    fun getHistory(): List<History>

    @Query("select * from history_table WHERE date = :date and habitId = :habitId")
    fun getTodayHistory(date: String, habitId: Long): History?

    @Insert
    suspend fun insertHistory(history: History?)

    @Query("DELETE from history_table WHERE historyId = :historyId")
    suspend fun deleteHistory(historyId: Long): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateHistory(history: History)

}