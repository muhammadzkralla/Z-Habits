package com.zkrallah.z_habits.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.zkrallah.z_habits.databinding.ActivityHomeBinding
import com.zkrallah.z_habits.ui.habits.HabitsActivity
import com.zkrallah.z_habits.ui.history.HistoryActivity

import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val calendar: Calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        binding.habitsCard.setOnClickListener {
            startActivity(Intent(this@HomeActivity, HabitsActivity::class.java))
        }

        binding.historyCard.setOnClickListener {
            startActivity(Intent(this@HomeActivity, HistoryActivity::class.java))
        }

        updateDailyProgress()
        val prev = getPreviousWeek()

    }

    @SuppressLint("SetTextI18n")
    private fun updateDailyProgress() {
        val date = formatter.format(calendar.time).toString()
        viewModel.getAllTodayHistory(date)
        var totalCountDone = 0.0
        var totalCountPerDay = 0.0
        viewModel.history.observe(this){
            it?.let { list ->
                for (history in list){
                    totalCountDone += history.countDone
                    totalCountPerDay += history.countPerDay
                }
                if (totalCountPerDay != 0.0) {
                    val percentage = (totalCountDone / totalCountPerDay) * 100
                    val number2digits:Double = String.format("%.2f", percentage).toDouble()
                    binding.progress.text = "Progress : $number2digits %"
                }

            }
        }
    }

    private fun getNextWeek(): Array<String?> {
        val days = arrayOfNulls<String>(7)
        for (i in 0..6) {
            days[i] = formatter.format(this.calendar.time)
            this.calendar.add(Calendar.DATE, 1)
        }
        return days
    }

    private fun getPreviousWeek(): Array<String?> {
        this.calendar.add(Calendar.DATE, -6)
        return getNextWeek()
    }

}
