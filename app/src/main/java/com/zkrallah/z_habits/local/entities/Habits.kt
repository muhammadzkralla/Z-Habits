package com.zkrallah.z_habits.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits_table")
data class Habits(
    var name: String,
    var countPerDay: Int
) {
    @PrimaryKey(autoGenerate = true)
    var habitId: Long = 0L
}