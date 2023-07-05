package com.zkrallah.z_habits.ui.history

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zkrallah.z_habits.R
import com.zkrallah.z_habits.adapter.HistoryAdapter
import com.zkrallah.z_habits.databinding.ActivityHistoryBinding
import com.zkrallah.z_habits.local.entities.History
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: HistoryViewModel
    private lateinit var dialog: AlertDialog
    private val calendar: Calendar = Calendar.getInstance()
    private val map = mapOf(
        1 to "Very Bad",
        2 to "Bad",
        3 to "Neutral",
        4 to "Good",
        5 to "Very Good"
    )

    private val imagesMap = mapOf(
        1 to R.drawable.ic_very_bad,
        2 to R.drawable.ic_bad,
        3 to R.drawable.ic_neutral,
        4 to R.drawable.ic_good,
        5 to R.drawable.ic_very_good
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]

        updateUI()
    }

    private fun updateUI() {
        viewModel.getHistory()
        viewModel.history.observe(this) {
            it?.let {
                val adapter = HistoryAdapter(it as MutableList<History>)
                binding.recyclerHistory.adapter = adapter
                val layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
                layoutManager.stackFromEnd = true
                layoutManager.reverseLayout = true
                binding.recyclerHistory.layoutManager = layoutManager

                adapter.setItemClickListener(object : HistoryAdapter.OnItemClickListener {
                    override fun onDeleteClicked(history: History, position: Int) {
                        viewModel.deleteHistory(history.historyId)
                        adapter.removeItem(position)
                    }

                })
            }
        }
    }

    private fun buildDetailsDialog(date: String) {
        viewModel.getTodayDetails(date)
        viewModel.state.observe(this@HistoryActivity, object : Observer<Boolean> {
            @SuppressLint("SetTextI18n")
            override fun onChanged(value: Boolean) {
                if (value) {
                    val historyResponse = viewModel.dayHistory.value
                    val moodResponse = viewModel.moodStatus.value

                    var countPerDay = 0.0
                    var countDone = 0.0

                    if (historyResponse != null) {
                        for (item in historyResponse) {
                            countPerDay += item.countPerDay
                            countDone += item.countDone
                        }
                    }

                    val inflater = layoutInflater
                    val dialogView = inflater.inflate(R.layout.day_details_dialog, null)
                    val builder = AlertDialog.Builder(this@HistoryActivity, R.style.MyDialogTheme)
                    builder.setNegativeButton("OK") { _, _ ->
                        dialog.dismiss()
                    }

                    builder.setCancelable(true)

                    val recycler =
                        dialogView.findViewById<RecyclerView>(R.id.recycler_habit_history)
                    val progress = dialogView.findViewById<TextView>(R.id.progress)
                    val moodCard = dialogView.findViewById<CardView>(R.id.mood_card)
                    val moodDate = dialogView.findViewById<TextView>(R.id.date)
                    val moodValue = dialogView.findViewById<TextView>(R.id.value)
                    val moodMessage = dialogView.findViewById<TextView>(R.id.message)
                    val moodImage = dialogView.findViewById<ImageView>(R.id.mood_image)

                    val layoutManager =
                        LinearLayoutManager(
                            this@HistoryActivity,
                            LinearLayoutManager.VERTICAL,
                            true
                        )
                    layoutManager.stackFromEnd = true
                    layoutManager.reverseLayout = true

                    val adapter = HistoryAdapter(historyResponse as MutableList<History>)
                    adapter.setItemClickListener(object : HistoryAdapter.OnItemClickListener {
                        override fun onDeleteClicked(history: History, position: Int) {
                            viewModel.deleteHistory(history.historyId)
                            adapter.removeItem(position)
                        }

                    })

                    recycler.adapter = adapter
                    recycler.layoutManager = layoutManager
                    if (moodResponse != null) {
                        moodDate.text = moodResponse.date
                        moodValue.text = map[moodResponse.value]
                        moodMessage.text = moodResponse.message
                        Glide.with(this@HistoryActivity).load(imagesMap[moodResponse.value])
                            .into(moodImage)
                    } else moodCard.visibility = View.GONE

                    if (countPerDay != 0.0) {
                        val percentage = (countDone / countPerDay) * 100
                        val roundedUp = percentage.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                        progress.text = "Progress : $roundedUp%"
                    }

                    viewModel.clearDetails()
                    viewModel.state.removeObserver(this)

                    builder.setView(dialogView)
                    dialog = builder.create()
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.show()

                }
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.search) {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, yearSelected, monthOfYear, dayOfMonth ->

                    val newCalendar = Calendar.getInstance()
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
                    newCalendar.set(yearSelected, monthOfYear, dayOfMonth)
                    val date = formatter.format(newCalendar.time)
                    buildDetailsDialog(date)

                },

                year,
                month,
                day
            )
            datePickerDialog.show()
        }
        return super.onOptionsItemSelected(item)
    }
}