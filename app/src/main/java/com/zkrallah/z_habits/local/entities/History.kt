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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as History

        if (habitName == other.habitName && date == other.date) return true

        return false
    }

    override fun hashCode(): Int {
        var result = habitId.hashCode()
        result = 31 * result + habitName.hashCode()
        return result
    }
}