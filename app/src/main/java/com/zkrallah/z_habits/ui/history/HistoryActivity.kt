package com.zkrallah.z_habits.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zkrallah.z_habits.adapter.HistoryAdapter
import com.zkrallah.z_habits.databinding.ActivityHistoryBinding
import com.zkrallah.z_habits.local.entities.History

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: HistoryViewModel

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
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.recyclerHistory.layoutManager = layoutManager

                adapter.setItemClickListener(object : HistoryAdapter.OnItemClickListener{
                    override fun onDeleteClicked(history: History, position: Int) {
                        viewModel.deleteHistory(history.historyId)
                        adapter.removeItem(position)
                    }

                })
            }
        }
    }
}