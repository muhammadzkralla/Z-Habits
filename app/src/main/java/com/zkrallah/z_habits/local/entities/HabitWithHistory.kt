package com.zkrallah.z_habits.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class HabitWithHistory(
    @Embedded val habits: Habits,
    @Relation(
        parentColumn = "habitId",
        entityColumn = "habitId"
    )
    val history: List<History>
)