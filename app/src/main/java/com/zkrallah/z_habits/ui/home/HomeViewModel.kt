package com.zkrallah.z_habits.ui.home

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zkrallah.z_habits.local.HabitsDatabase
import com.zkrallah.z_habits.local.entities.History
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val database = HabitsDatabase.getInstance()

    private val _history = MutableLiveData<List<History>?>()
    val history: LiveData<List<History>?> = _history

    fun getAllTodayHistory(date: String){
        val job = viewModelScope.launch (Dispatchers.IO){
            _history.postValue(database.historyDAO().getAllTodayHistory(date))
        }
        viewModelScope.launch{
            job.join()
            Log.d("HomeActivity", "getAllTodayHistory: finished with ${_history.value?.size}")
        }
    }
}