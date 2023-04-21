package com.zkrallah.z_habits.ui.habits

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zkrallah.z_habits.databinding.ActivityHabitsBinding

class HabitsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHabitsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}