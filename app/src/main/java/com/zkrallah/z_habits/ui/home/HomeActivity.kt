package com.zkrallah.z_habits.ui.home

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.zkrallah.z_habits.databinding.ActivityHomeBinding
import com.zkrallah.z_habits.ui.habits.HabitsActivity
import com.zkrallah.z_habits.ui.history.HistoryActivity
import com.zkrallah.z_habits.ui.mood.MoodActivity
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

        binding.modeCard.setOnClickListener {
            startActivity(Intent(this@HomeActivity, MoodActivity::class.java))
        }

        binding.weeksCalendarBtn.setOnClickListener {
            Toast.makeText(this@HomeActivity, "Choose the starting day", Toast.LENGTH_SHORT).show()
            showSelectWeekDialog()
        }

        binding.monthsCalendarBtn.setOnClickListener {
            Toast.makeText(this@HomeActivity, "Choose the starting day", Toast.LENGTH_SHORT).show()
            showSelectMonthDialog()
        }

        binding.moodCalendarBtn.setOnClickListener {
            Toast.makeText(this@HomeActivity, "Choose the starting day", Toast.LENGTH_SHORT).show()
            showSelectMoodMonthDialog()
        }

        binding.analyzeBtn.setOnClickListener {
            val prevWeeks = getPreviousWeek()
            val prevWeeksInDays = getPreviousWeekNames()
            updateWeeksGraph(prevWeeks, prevWeeksInDays)
            val prevMonths = getPreviousMonth()
            val prevMonthsInDays = getPreviousMonthNames()
            updateMonthsGraph(prevMonths, prevMonthsInDays)
            updateMoodGraph(prevMonths, prevMonthsInDays)
        }
    }

    private fun showSelectWeekDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, yearSelected, monthOfYear, dayOfMonth ->

                val newCalendar = Calendar.getInstance()
                val nameFormatter = SimpleDateFormat("EE", Locale.ROOT)
                newCalendar.set(yearSelected, monthOfYear, dayOfMonth)

                val days = arrayOfNulls<String>(7)
                val daysNames = arrayOfNulls<String>(7)
                for (i in 0..6) {
                    days[i] = formatter.format(newCalendar.time)
                    daysNames[i] = nameFormatter.format(newCalendar.time)
                    newCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                updateWeeksGraph(days, daysNames)

            },

            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showSelectMonthDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, yearSelected, monthOfYear, dayOfMonth ->

                val newCalendar = Calendar.getInstance()
                val nameFormatter = SimpleDateFormat("dd/MM", Locale.ROOT)
                newCalendar.set(yearSelected, monthOfYear, dayOfMonth)

                val days = arrayOfNulls<String>(31)
                val daysNames = arrayOfNulls<String>(31)
                for (i in 0..30) {
                    days[i] = formatter.format(newCalendar.time)
                    daysNames[i] = nameFormatter.format(newCalendar.time)
                    newCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                updateMonthsGraph(days, daysNames)

            },

            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showSelectMoodMonthDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, yearSelected, monthOfYear, dayOfMonth ->

                val newCalendar = Calendar.getInstance()
                val nameFormatter = SimpleDateFormat("dd/MM", Locale.ROOT)
                newCalendar.set(yearSelected, monthOfYear, dayOfMonth)

                val days = arrayOfNulls<String>(31)
                val daysNames = arrayOfNulls<String>(31)
                for (i in 0..30) {
                    days[i] = formatter.format(newCalendar.time)
                    daysNames[i] = nameFormatter.format(newCalendar.time)
                    newCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                updateMoodGraph(days, daysNames)

            },

            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun updateWeeksGraph(prev: Array<String?>, prevInDays: Array<String?>) {
        viewModel.getWeekHistory(prev)
        viewModel.weekState.observe(this@HomeActivity, object : Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                if (value) {
                    val result = viewModel.weekHistory.value

                    val barArrayList = mutableListOf<BarEntry>()
                    var count = 0f

                    if (result != null) {
                        Log.d("HabitsApp", "updateGraph: result : $result")
                        for (day in prev) {
                            var countDone = 0.0
                            var countPerDay = 0.0
                            for (res in result) {
                                if (res.date == day) {
                                    countDone += res.countDone
                                    countPerDay += res.countPerDay
                                }
                            }
                            if (countPerDay != 0.0) {
                                val percentage = (countDone / countPerDay) * 100
                                val number2digits: Double =
                                    String.format("%.2f", percentage).toDouble()
                                barArrayList.add(BarEntry(count, number2digits.toFloat()))
                                count++
                            } else {
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
                            binding.barChart.xAxis.valueFormatter =
                                IndexAxisValueFormatter(prevInDays)
                            binding.barChart.axisLeft.textColor = Color.GRAY
                            binding.barChart.axisRight.textColor = Color.GRAY
                            binding.barChart.xAxis.textColor = Color.GRAY
                            binding.barChart.description.textColor = Color.GRAY
                            binding.barChart.description.textColor = Color.GRAY
                            val colors = listOf(Color.GRAY)
                            binding.barChart.data.setValueTextColors(colors)

                            binding.barChart.animateXY(1500, 1500)
                            binding.barChart.invalidate()
                        }
                    }
                    Log.d("HabitsApp", "updateGraph: habits done : $barArrayList")
                    viewModel.clearWeek()
                    viewModel.weekState.removeObserver(this)
                }
            }

        })

    }

    private fun updateMonthsGraph(prev: Array<String?>, prevInDays: Array<String?>) {
        viewModel.getMonthHistory(prev)
        viewModel.monthState.observe(this@HomeActivity, object : Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                if (value) {
                    val result = viewModel.monthHistory.value

                    val barArrayList = mutableListOf<BarEntry>()
                    var count = 0f

                    if (result != null) {
                        Log.d("HabitsApp", "updateGraph: result : ${result.size}")
                        for (day in prev) {
                            var countDone = 0.0
                            var countPerDay = 0.0
                            for (res in result) {
                                if (res.date == day) {
                                    countDone += res.countDone
                                    countPerDay += res.countPerDay
                                }
                            }
                            if (countPerDay != 0.0) {
                                val percentage = (countDone / countPerDay) * 100
                                val number2digits: Double =
                                    String.format("%.2f", percentage).toDouble()
                                barArrayList.add(BarEntry(count, number2digits.toFloat()))
                                count++
                            } else {
                                barArrayList.add(BarEntry(count, 0.0f))
                                count++
                            }
                            val barDataSet = BarDataSet(barArrayList, "Days")
                            barDataSet.colors = ColorTemplate.COLORFUL_COLORS.asList()
                            barDataSet.setDrawValues(false)
                            barDataSet.stackLabels = prev
                            val barData = BarData(barDataSet)
                            binding.monthsBarChart.data = barData
                            binding.monthsBarChart.description.isEnabled = true
                            binding.monthsBarChart.description.text = "Progress percentage per day."
                            binding.monthsBarChart.xAxis.valueFormatter =
                                IndexAxisValueFormatter(prevInDays)

                            binding.monthsBarChart.axisRight.setLabelCount(10, true)
                            binding.monthsBarChart.axisLeft.setLabelCount(10, true)
                            binding.monthsBarChart.axisRight.axisMaximum = 100f
                            binding.monthsBarChart.axisLeft.axisMaximum = 100f
                            binding.monthsBarChart.axisRight.axisMinimum = 0f
                            binding.monthsBarChart.axisLeft.axisMinimum = 0f
                            binding.monthsBarChart.xAxis.granularity = 1f

                            binding.monthsBarChart.axisLeft.textColor = Color.GRAY
                            binding.monthsBarChart.axisRight.textColor = Color.GRAY
                            binding.monthsBarChart.xAxis.textColor = Color.GRAY
                            binding.monthsBarChart.description.textColor = Color.GRAY
                            binding.monthsBarChart.description.textColor = Color.GRAY
                            val colors = listOf(Color.GRAY)
                            binding.monthsBarChart.data.setValueTextColors(colors)

                            binding.monthsBarChart.animateXY(1500, 1500)
                            binding.monthsBarChart.invalidate()
                        }
                    }
                    Log.d("HabitsApp", "updateGraph: habits done : $barArrayList")
                    viewModel.clearMonth()
                    viewModel.monthState.removeObserver(this)
                }
            }

        })
    }

    private fun updateMoodGraph(prev: Array<String?>, prevInDays: Array<String?>) {
        viewModel.getMonthMoodHistory(prev)
        viewModel.moodState.observe(this, object : Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                if (value) {
                    val result = viewModel.moodHistory.value
                    val entryArrayList = mutableListOf<Entry>()
                    var count = 0f
                    if (result != null) {
                        for (day in prev) {
                            var found = false
                            for (res in result) {
                                if (res.date == day) {
                                    found = true
                                    entryArrayList.add(Entry(count, res.value.toFloat()))
                                    count++
                                    break
                                }
                            }
                            if (!found) {
                                entryArrayList.add(Entry(count, 0f))
                                count++
                            }
                        }
                        val lineDataSet = LineDataSet(entryArrayList, "Days")
                        lineDataSet.colors = ColorTemplate.COLORFUL_COLORS.asList()
                        lineDataSet.setDrawValues(false)
                        val data = LineData(lineDataSet)

                        binding.moodLineChart.data = data
                        binding.moodLineChart.xAxis.valueFormatter =
                            IndexAxisValueFormatter(prevInDays)
                        binding.moodLineChart.description.isEnabled = true
                        binding.moodLineChart.description.text = "Mood status per day."

                        binding.moodLineChart.axisRight.setLabelCount(6, true)
                        binding.moodLineChart.axisLeft.setLabelCount(6, true)
                        binding.moodLineChart.axisRight.axisMaximum = 5f
                        binding.moodLineChart.axisLeft.axisMaximum = 5f
                        binding.moodLineChart.axisRight.axisMinimum = 0f
                        binding.moodLineChart.axisLeft.axisMinimum = 0f
                        binding.moodLineChart.xAxis.granularity = 1f

                        binding.moodLineChart.axisLeft.setDrawLabels(false)
                        binding.moodLineChart.axisRight.setDrawLabels(false)
                        binding.moodLineChart.xAxis.textColor = Color.GRAY
                        binding.moodLineChart.description.textColor = Color.GRAY
                        binding.moodLineChart.description.textColor = Color.GRAY
                        val colors = listOf(Color.GRAY)
                        binding.moodLineChart.data.setValueTextColors(colors)
                        binding.moodLineChart.animateXY(1500, 1500)
                        binding.moodLineChart.invalidate()

                        viewModel.clearMood()
                        viewModel.moodState.removeObserver(this)
                    }
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

    private fun getNextMonth(): Array<String?> {
        val days = arrayOfNulls<String>(31)
        for (i in 0..30) {
            days[i] = formatter.format(this.calendar.time)
            this.calendar.add(Calendar.DATE, 1)
        }
        this.calendar.add(Calendar.DATE, -1)
        return days
    }

    private fun getPreviousMonth(): Array<String?> {
        this.calendar.add(Calendar.DATE, -30)
        return getNextMonth()
    }

    private fun getNextMonthNames(): Array<String?> {
        val formatter = SimpleDateFormat("dd/MM", Locale.ROOT)
        val days = arrayOfNulls<String>(31)
        for (i in 0..30) {
            days[i] = formatter.format(this.calendar.time)
            this.calendar.add(Calendar.DATE, 1)
        }
        this.calendar.add(Calendar.DATE, -1)
        return days
    }

    private fun getPreviousMonthNames(): Array<String?> {
        this.calendar.add(Calendar.DATE, -30)
        return getNextMonthNames()
    }

}
