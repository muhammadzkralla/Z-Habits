package com.zkrallah.z_habits.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class History(
    var habitId: Long,
    var habitName: String,
    var countDone: Int,
    var countPerDay: Int,
    var date: String
) {
    @PrimaryKey(autoGenerate = true)
    var historyId: Long = 0L
}