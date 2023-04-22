package com.zkrallah.z_habits.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zkrallah.z_habits.databinding.ActivityHomeBinding
import com.zkrallah.z_habits.ui.habits.HabitsActivity
import com.zkrallah.z_habits.ui.history.HistoryActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.habitsCard.setOnClickListener {
            startActivity(Intent(this@HomeActivity, HabitsActivity::class.java))
        }

        binding.historyCard.setOnClickListener {
            startActivity(Intent(this@HomeActivity, HistoryActivity::class.java))
        }
    }
}