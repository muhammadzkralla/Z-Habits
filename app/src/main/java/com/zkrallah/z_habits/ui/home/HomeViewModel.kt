package com.zkrallah.z_habits.ui.home

import androidx.lifecycle.ViewModel
import com.zkrallah.z_habits.local.HabitsDatabase

class HomeViewModel : ViewModel() {

    private val database = HabitsDatabase.getInstance()

}