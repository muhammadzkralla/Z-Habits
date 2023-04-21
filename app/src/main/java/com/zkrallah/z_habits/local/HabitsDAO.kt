package com.zkrallah.z_habits.local

import androidx.room.*
import com.zkrallah.z_habits.local.entities.HabitWithHistory
import com.zkrallah.z_habits.local.entities.Habits


@Dao
interface HabitsDAO {

    @Query("select * from habits_table")
    fun getHabits(): List<Habits>

    @Insert
    suspend fun insertHabit(habits: Habits?)

    @Query("DELETE from habits_table WHERE habitId = :habitId")
    suspend fun deleteHabit(habitId: Long): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateHabit(habits: Habits)

    @Transaction
    @Query("SELECT * FROM habits_table WHERE habitId=:habitId")
    fun getHabitWithHistoryById(habitId: Long): HabitWithHistory?
}