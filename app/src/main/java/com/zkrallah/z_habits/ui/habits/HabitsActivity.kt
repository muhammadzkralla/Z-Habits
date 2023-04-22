package com.zkrallah.z_habits.ui.habits

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zkrallah.z_habits.R
import com.zkrallah.z_habits.adapter.HabitsAdapter
import com.zkrallah.z_habits.databinding.ActivityHabitsBinding
import com.zkrallah.z_habits.local.entities.Habits
import com.zkrallah.z_habits.local.entities.History
import java.text.SimpleDateFormat
import java.util.*

class HabitsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHabitsBinding
    private lateinit var viewModel: HabitsViewModel
    private lateinit var dialog: AlertDialog
    private val calendar: Calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HabitsViewModel::class.java]
        updateUI()

        binding.addFab.setOnClickListener {
            buildAddHabitAlertDialog()
            dialog.show()
        }
    }

    private fun buildAddHabitAlertDialog() {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.add_habit_dialog, null)
        val edtName = dialogView.findViewById<EditText>(R.id.edt_habit_name)
        val edtCount = dialogView.findViewById<EditText>(R.id.edt_habit_count)

        val builder = AlertDialog.Builder(this@HabitsActivity)
        builder.setView(dialogView)
        builder.setCancelable(true)
        builder.setTitle("ADD A HABIT")
        builder.setMessage(
            "Fill in the data :"
        )
        builder.setPositiveButton("ADD") { _, _ ->
            if (edtName.text.isNotEmpty() && edtCount.text.isNotEmpty()) {
                val habit = Habits(
                    edtName.text.toString(),
                    edtCount.text.toString().toInt()
                )
                viewModel.insertHabit(habit)
            } else
                Toast
                    .makeText(
                        this@HabitsActivity,
                        "Discarded",
                        Toast.LENGTH_LONG
                    )
                    .show()
        }
        dialog = builder.create()
    }

    private fun updateUI() {
        viewModel.getHistory()
        viewModel.habits.observe(this) {
            it?.let { habits ->
                val adapter = HabitsAdapter(habits as MutableList<Habits>)
                binding.recyclerHabits.adapter = adapter
                val layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.recyclerHabits.layoutManager = layoutManager

                adapter.setItemClickListener(object : HabitsAdapter.OnItemClickListener {
                    override fun onShowHistoryClicked(habits: Habits) {
                        TODO("Not yet implemented")
                    }

                    override fun onAddCountClicked(habits: Habits) {
                        val date = formatter.format(calendar.time).toString()
                        viewModel.checkTodayHistory(habits.habitId, date)
                        Handler(Looper.getMainLooper()).postDelayed({
                            var history: History? = viewModel.history.value
                            if (history == null) {
                                history = History(
                                    habits.habitId,
                                    habits.name,
                                    1,
                                    habits.countPerDay,
                                    date
                                )
                                viewModel.insertHistory(history)
                            } else {
                                if (history.countDone == history.countPerDay) history.countDone = 0
                                else history.countDone++
                                viewModel.updateHistory(history)
                            }
                            Snackbar.make(
                                binding.root,
                                "${history.habitName} is now ${history.countDone} from ${history.countPerDay}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }, 500)
                    }

                })
            }
        }
    }
}