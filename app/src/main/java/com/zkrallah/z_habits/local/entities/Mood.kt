package com.zkrallah.z_habits.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_table")
data class Mood(
    var value: Int,
    var message: String,
    var date: String
) {
    @PrimaryKey(autoGenerate = true)
    var moodId: Long = 0L
}
