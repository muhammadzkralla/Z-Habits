package com.zkrallah.z_habits.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zkrallah.z_habits.local.HabitsDatabase
import com.zkrallah.z_habits.local.entities.History
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val database = HabitsDatabase.getInstance()

    private val _history = MutableLiveData<List<History>?>()
    val history = _history
    private val _state = MutableLiveData(false)
    val state = _state

    fun getHistory(list: Array<String?>){
        viewModelScope.launch (Dispatchers.IO){
            val result = mutableListOf<History>()
            val job = async {
                for (day in list){
                    val fetch = async { database.historyDAO().getAllTodayHistory(day!!) }
                    val response = fetch.await()
                    for (item in response!!) result.add(item)
                }
            }
            job.await()
            _history.postValue(result)
            _state.postValue(true)
        }
    }

    fun clear() {
        _history.value = null
        _state.value = false
    }

}