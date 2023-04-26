package com.zkrallah.z_habits.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
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

        binding.weekHistory.setOnClickListener {
            val prev = getPreviousWeek()
            val prevInDays = getPreviousWeekNames()
            updateGraph(prev, prevInDays)
        }
    }

    private fun updateGraph(prev: Array<String?>, prevInDays: Array<String?>) {
        viewModel.getHistory(prev)
        viewModel.state.observe(this@HomeActivity, object : Observer<Boolean>{
            override fun onChanged(value: Boolean) {
                if (value){
                    val result = viewModel.history.value

                    val barArrayList = mutableListOf<BarEntry>()
                    var count = 0f

                    if (result != null) {
                        Log.d("HabitsApp", "updateGraph: result : $result")
                        for (day in prev){
                            var countDone = 0.0
                            var countPerDay = 0.0
                            for (res in result){
                                if (res.date == day){
                                    countDone += res.countDone
                                    countPerDay += res.countPerDay
                                }
                            }
                            if (countPerDay != 0.0) {
                                val percentage = (countDone / countPerDay) * 100
                                val number2digits:Double = String.format("%.2f", percentage).toDouble()
                                barArrayList.add(BarEntry(count, number2digits.toFloat()))
                                count++
                            }else {
                                barArrayList.add(BarEntry(count, 0.0f))
                                count++
                            }
                            val barDataSet = BarDataSet(barArrayList, "Days")
                            barDataSet.colors = ColorTemplate.COLORFUL_COLORS.asList()
                            barDataSet.valueTextColor = Color.BLACK
                            barDataSet.valueTextSize = 16f
                            barDataSet.stackLabels = prev
                            val barData = BarData(barDataSet)
                            binding.barChart.data = barData
                            binding.barChart.description.isEnabled = true
                            binding.barChart.description.text = "Progress percentage per day."
                            binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(prevInDays)
                        }
                    }
                    Log.d("HabitsApp", "updateGraph: habits done : $barArrayList")
                    viewModel.clear()
                    viewModel.state.removeObserver(this)
                }
            }

        })

    }

    private fun getNextWeek(): Array<String?> {
        val days = arrayOfNulls<String>(7)
        for (i in 0..6) {
            days[i] = formatter.format(this.calendar.time)
            this.calendar.add(Calendar.DATE, 1)
        }
        this.calendar.add(Calendar.DATE, -1)
        return days
    }

    private fun getPreviousWeek(): Array<String?> {
        this.calendar.add(Calendar.DATE, -6)
        return getNextWeek()
    }

    private fun getNextWeekNames(): Array<String?> {
        val formatter = SimpleDateFormat("EE", Locale.ROOT)
        val days = arrayOfNulls<String>(7)
        for (i in 0..6) {
            days[i] = formatter.format(this.calendar.time)
            this.calendar.add(Calendar.DATE, 1)
        }
        this.calendar.add(Calendar.DATE, -1)
        return days
    }

    private fun getPreviousWeekNames(): Array<String?> {
        this.calendar.add(Calendar.DATE, -6)
        return getNextWeekNames()
    }

}
