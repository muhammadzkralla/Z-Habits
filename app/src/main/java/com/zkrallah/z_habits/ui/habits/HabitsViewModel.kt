package com.zkrallah.z_habits.ui.habits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zkrallah.z_habits.local.HabitsDatabase
import com.zkrallah.z_habits.local.entities.Habits
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HabitsViewModel : ViewModel() {

    private val database = HabitsDatabase.getInstance()

    private val _habits = MutableLiveData<List<Habits>>()
    val habits: LiveData<List<Habits>> = _habits

    fun getHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _habits.postValue(database.habitsDAO().getHabits())
        }
    }

    fun insertHabit(habit: Habits) {
        viewModelScope.launch(Dispatchers.IO) {
            database.habitsDAO().insertHabit(habit)
        }
    }

}