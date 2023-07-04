package com.zkrallah.z_habits.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zkrallah.z_habits.local.HabitsDatabase
import com.zkrallah.z_habits.local.entities.History
import com.zkrallah.z_habits.local.entities.Mood
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val database = HabitsDatabase.getInstance()

    private val _history = MutableLiveData<List<History>>()
    val history: LiveData<List<History>> = _history
    private val _dayHistory = MutableLiveData<List<History>?>()
    val dayHistory: LiveData<List<History>?> = _dayHistory
    private val _moodStatus = MutableLiveData<Mood?>()
    val moodStatus: LiveData<Mood?> = _moodStatus
    private val _state = MutableLiveData(false)
    val state = _state

    fun getHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _history.postValue(database.historyDAO().getHistory())
        }
    }

    fun deleteHistory(historyId: Long) {
        viewModelScope.launch (Dispatchers.IO){
            database.historyDAO().deleteHistory(historyId)
        }
    }

    fun getTodayDetails(date: String){
        viewModelScope.launch (Dispatchers.IO){
            val historyJob = async{ database.historyDAO().getAllTodayHistory(date) }
            val moodJob = async { database.moodDAO().getTodayMood(date) }
            _dayHistory.postValue(historyJob.await())
            _moodStatus.postValue(moodJob.await())
            _state.postValue(true)
        }
    }

    fun clearDetails(){
        _dayHistory.value = null
        _state.value = false
    }
}