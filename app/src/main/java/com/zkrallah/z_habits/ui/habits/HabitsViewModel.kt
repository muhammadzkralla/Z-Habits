package com.zkrallah.z_habits.ui.habits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zkrallah.z_habits.local.HabitsDatabase
import com.zkrallah.z_habits.local.entities.Habits
import com.zkrallah.z_habits.local.entities.History
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HabitsViewModel : ViewModel() {

    private val database = HabitsDatabase.getInstance()

    private val _habits = MutableLiveData<List<Habits>>()
    val habits: LiveData<List<Habits>> = _habits
    private val _history = MutableLiveData<History>()
    val history: LiveData<History> = _history

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

    fun insertHistory(history: History) {
        viewModelScope.launch(Dispatchers.IO) {
            database.historyDAO().insertHistory(history)
        }
    }

    fun updateHistory(history: History) {
        viewModelScope.launch(Dispatchers.IO) {
            database.historyDAO().updateHistory(history)
        }
    }

    fun checkTodayHistory(habitId: Long, date: String){
        viewModelScope.launch (Dispatchers.IO){
            _history.postValue(database.historyDAO().getTodayHistory(date, habitId))
        }
    }


}