package com.zkrallah.z_habits.ui.mood

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zkrallah.z_habits.adapter.MoodAdapter
import com.zkrallah.z_habits.databinding.ActivityMoodBinding
import com.zkrallah.z_habits.local.entities.Mood
import java.text.SimpleDateFormat
import java.util.*

class MoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoodBinding
    private lateinit var viewModel: MoodViewModel
    private val calendar: Calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
    val date = formatter.format(calendar.time).toString()
    private var selected = -1
    private val map = mapOf(
        1 to "Very Bad",
        2 to "Bad",
        3 to "Neutral",
        4 to "Good",
        5 to "Very Good"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MoodViewModel::class.java]

        updateUI()
        getSelected()

        binding.btn.setOnClickListener {
            if (selected != -1) {
                viewModel.checkTodayMood(date)
                viewModel.state.observe(this@MoodActivity, object : Observer<Boolean> {
                    override fun onChanged(value: Boolean) {
                        if (value) {
                            val mood = viewModel.mood.value
                            if (mood != null) {
                                mood.value = selected
                                mood.message = binding.edtMessage.text.toString()
                                viewModel.updateMood(mood)
                                Toast.makeText(this@MoodActivity, "Updated !", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                viewModel.insertMood(
                                    Mood(
                                        selected, binding.edtMessage.text.toString(), date
                                    )
                                )
                                Toast.makeText(this@MoodActivity, "Inserted !", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            viewModel.state.removeObserver(this)
                            viewModel.clearTodayMood()
                        }
                    }

                })
            } else
                Toast.makeText(this@MoodActivity, "No Status Selected !", Toast.LENGTH_SHORT).show()

        }
    }

    @SuppressLint("SetTextI18n")
    private fun getSelected() {
        binding.veryBad.setOnClickListener {
            selected = 1
            binding.selected.text = "Selected : ${map[1]}"
        }

        binding.bad.setOnClickListener {
            selected = 2
            binding.selected.text = "Selected : ${map[2]}"
        }

        binding.neutral.setOnClickListener {
            selected = 3
            binding.selected.text = "Selected : ${map[3]}"
        }

        binding.good.setOnClickListener {
            selected = 4
            binding.selected.text = "Selected : ${map[4]}"
        }

        binding.veryGood.setOnClickListener {
            selected = 5
            binding.selected.text = "Selected : ${map[5]}"
        }
    }

    private fun updateUI() {
        viewModel.getMoodHistory()
        viewModel.moodHistory.observe(this) {
            it?.let {
                val adapter = MoodAdapter(it as MutableList<Mood>, this@MoodActivity)
                binding.recyclerMood.adapter = adapter
                val layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
                layoutManager.stackFromEnd = true
                layoutManager.reverseLayout = true
                binding.recyclerMood.layoutManager = layoutManager
            }
        }
    }
}