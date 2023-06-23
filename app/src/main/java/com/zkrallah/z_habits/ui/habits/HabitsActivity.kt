package com.zkrallah.z_habits.ui.habits

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.zkrallah.z_habits.R
import com.zkrallah.z_habits.adapter.HabitsAdapter
import com.zkrallah.z_habits.adapter.HistoryAdapter
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
    val date = formatter.format(calendar.time).toString()
    private lateinit var adapter: HabitsAdapter
    private var alertCounter = 0

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

    private fun updateUI() {
        viewModel.getHistory()
        viewModel.habits.observe(this) {
            it?.let { habits ->

                adapter = HabitsAdapter(habits as MutableList<Habits>)
                binding.recyclerHabits.adapter = adapter
                val layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.recyclerHabits.layoutManager = layoutManager

                adapter.setItemClickListener(object : HabitsAdapter.OnItemClickListener {
                    override fun onShowHistoryClicked(habits: Habits) {
                        buildHistoryAlertDialog(habits)
                        dialog.show()
                    }

                    override fun onAddCountClicked(habits: Habits) {
                        viewModel.checkTodayHistory(habits.habitId, date)

                        viewModel.state.observe(this@HabitsActivity, object : Observer<Boolean>{
                            override fun onChanged(value: Boolean) {
                                if (value){
                                    val history: History? = viewModel.history.value
                                    if (history != null) {
                                        if (history.countDone == history.countPerDay) history.countDone = 0
                                        else history.countDone++
                                        viewModel.updateHistory(history)
                                        Snackbar.make(
                                            binding.root,
                                            "${history.habitName} is now ${history.countDone} from ${history.countPerDay}",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }else{
                                        if (alertCounter == 0) buildStartNewHabitDialog(habits)
                                        if (alertCounter == 1) dialog.show()
                                    }
                                    viewModel.state.removeObserver(this)
                                    viewModel.clearTodayHistory()
                                    viewModel.state.value = false
                                }
                            }
                        }
                        )
                    }

                    override fun onDeleteHabitClicked(habits: Habits, position: Int) {
                        buildDeleteHabitDialog(habits, position)
                        dialog.show()
                    }

                    override fun onEditHabitClicked(habits: Habits, position: Int) {
                        buildEditHabitDialog(habits, position)
                        dialog.show()
                    }

                })
            }
        }
    }

    private fun buildDeleteHabitDialog(habits: Habits, position: Int) {
        val builder = AlertDialog.Builder(this@HabitsActivity, R.style.MyDialogTheme)
        builder.setTitle("Delete this Habit ?")
        builder.setCancelable(true)
        builder.setMessage("Click DELETE if you want to delete this Habit.")
        builder.setPositiveButton("DELETE"){_, _ ->
            viewModel.deleteHabit(habits.habitId)
            adapter.removeItem(position)
            dialog.dismiss()
        }

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun buildEditHabitDialog(habits: Habits, position: Int) {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.edit_habit_dialog, null)
        val edtCount = dialogView.findViewById<EditText>(R.id.edt_habit_count)

        val builder = AlertDialog.Builder(this@HabitsActivity, R.style.MyDialogTheme)
        builder.setTitle("Edit Habit Count")
        builder.setCancelable(true)
        builder.setView(dialogView)
        builder.setMessage("NOTE THAT TODAY'S HISTORY OF THIS HABIT IS GOING TO BE DELETED !")
        builder.setPositiveButton("EDIT"){_, _ ->
            if (edtCount.text.isNotEmpty()){
                habits.countPerDay = edtCount.text.toString().toInt()
                adapter.editItem(habits, position)
                viewModel.editHabit(habits)

                viewModel.checkTodayHistory(habits.habitId, date)
                viewModel.state.observe(this@HabitsActivity, object : Observer<Boolean>{
                    override fun onChanged(value: Boolean) {
                        if (value){
                            val history: History? = viewModel.history.value
                            if (history != null) viewModel.deleteHistory(history.historyId)
                            viewModel.state.removeObserver(this)
                            viewModel.clearTodayHistory()
                            viewModel.state.value = false
                        }
                    }

                })
                dialog.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun buildStartNewHabitDialog(habits: Habits) {
        val builder = AlertDialog.Builder(this@HabitsActivity, R.style.MyDialogTheme)
        builder.setTitle("Start a new History?")
        builder.setCancelable(false)
        builder.setMessage("Click start if you want to add a new history to this habit.")
        builder.setPositiveButton("START"){_, _ ->
            val history = History(
                habits.habitId,
                habits.name,
                1,
                habits.countPerDay,
                date
            )
            viewModel.insertHistory(history)
            alertCounter--
        }
        builder.setNegativeButton("CANCEL"){_,_ ->
            dialog.dismiss()
            alertCounter--
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertCounter++
    }

    private fun buildAddHabitAlertDialog() {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.add_habit_dialog, null)
        val edtName = dialogView.findViewById<EditText>(R.id.edt_habit_name)
        val edtCount = dialogView.findViewById<EditText>(R.id.edt_habit_count)

        val builder = AlertDialog.Builder(this@HabitsActivity, R.style.MyDialogTheme)
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
                finish()
                startActivity(intent)
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
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun buildHistoryAlertDialog(habits: Habits) {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.habit_history_dialog, null)
        val builder = AlertDialog.Builder(this@HabitsActivity, R.style.MyDialogTheme)

        builder.setCancelable(true)
        builder.setTitle("HABIT HISTORY")
        val recycler = dialogView.findViewById<RecyclerView>(R.id.recycler_habit_history)
        val layoutManager =
            LinearLayoutManager(this@HabitsActivity, LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        recycler.layoutManager = layoutManager
        viewModel.getHabitHistory(habits.habitId)
        viewModel.habitHistory.observe(this@HabitsActivity) {
            it?.let {
                val historyAdapter = HistoryAdapter(it.history as MutableList<History>)
                historyAdapter.setItemClickListener(object : HistoryAdapter.OnItemClickListener{
                    override fun onDeleteClicked(history: History, position: Int) {
                        viewModel.deleteHistory(history.historyId)
                        historyAdapter.removeItem(position)
                    }

                })
                recycler.adapter = historyAdapter
            }
        }
        builder.setView(dialogView)
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}