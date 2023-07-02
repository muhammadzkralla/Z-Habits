package com.zkrallah.z_habits.ui.mood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MoodViewModel::class.java]

        binding.btn.setOnClickListener {
            viewModel.checkTodayMood(date)

            viewModel.state.observe(this@MoodActivity, object : Observer<Boolean>{
                override fun onChanged(value: Boolean) {
                    if (value){
                        val mood = viewModel.mood.value
                        if (mood != null){
                            mood.value = binding.edtValue.text.toString().toInt()
                            mood.message = binding.edtMessage.text.toString()
                            viewModel.updateMood(mood)
                        } else
                            viewModel.
                            insertMood(Mood(binding.edtValue.text.toString().toInt()
                                , binding.edtMessage.text.toString()
                                , date))

                        viewModel.state.removeObserver(this)
                        viewModel.clearTodayMood()
                    }
                }

            })

        }

        updateUI()
    }

    private fun updateUI() {
        viewModel.getMoodHistory()
        viewModel.moodHistory.observe(this){
            it?.let {
                val adapter = MoodAdapter(it as MutableList<Mood>)
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