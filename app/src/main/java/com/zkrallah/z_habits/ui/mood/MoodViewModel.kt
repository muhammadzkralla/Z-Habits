package com.zkrallah.z_habits.ui.mood

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zkrallah.z_habits.local.HabitsDatabase
import com.zkrallah.z_habits.local.entities.Mood
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MoodViewModel : ViewModel() {

    private val database = HabitsDatabase.getInstance()

    private val _moodHistory = MutableLiveData<List<Mood>>()
    val moodHistory: LiveData<List<Mood>> = _moodHistory
    private val _mood = MutableLiveData<Mood?>()
    val mood: LiveData<Mood?> = _mood
    private val _state = MutableLiveData(false)
    val state = _state

    fun insertMood(mood: Mood) {
        viewModelScope.launch(Dispatchers.IO) {
            database.moodDAO().insertMode(mood)
        }
    }

    fun updateMood(mood: Mood) {
        viewModelScope.launch(Dispatchers.IO) {
            database.moodDAO().updateMood(mood)
        }
    }

    fun checkTodayMood(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val job = async { database.moodDAO().getTodayMood(date) }
            _mood.postValue(job.await())
            _state.postValue(true)
        }
    }

    fun getMoodHistory(){
        viewModelScope.launch (Dispatchers.IO){
            _moodHistory.postValue(database.moodDAO().getMoodHistory())
        }
    }

    fun clearTodayMood() {
        _mood.value = null
        _state.value = false
    }
}