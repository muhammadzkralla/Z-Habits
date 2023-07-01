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

    private val _weekHistory = MutableLiveData<List<History>?>()
    val weekHistory = _weekHistory
    private val _weekState = MutableLiveData(false)
    val weekState = _weekState
    private val _monthHistory = MutableLiveData<List<History>?>()
    val monthHistory = _monthHistory
    private val _monthState = MutableLiveData(false)
    val monthState = _monthState

    fun getWeekHistory(list: Array<String?>){
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
            _weekHistory.postValue(result)
            _weekState.postValue(true)
        }
    }

    fun clearWeek() {
        _weekHistory.value = null
        _weekState.value = false
    }

    fun getMonthHistory(list: Array<String?>){
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
            _monthHistory.postValue(result)
            _monthState.postValue(true)
        }
    }

    fun clearMonth() {
        _monthHistory.value = null
        _monthState.value = false
    }

}