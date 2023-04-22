package com.zkrallah.z_habits.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zkrallah.z_habits.local.HabitsDatabase
import com.zkrallah.z_habits.local.entities.History
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val database = HabitsDatabase.getInstance()

    private val _history = MutableLiveData<List<History>>()
    val history: LiveData<List<History>> = _history

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
}