package com.zkrallah.z_habits.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zkrallah.z_habits.R
import com.zkrallah.z_habits.local.entities.Habits

class HabitsAdapter(private val list: MutableList<Habits>) :
    RecyclerView.Adapter<HabitsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.habit_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.habitName.text = list[position].name
        holder.habitCount.text = list[position].countPerDay.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitName: TextView = itemView.findViewById(R.id.habit_name)
        val habitCount: TextView = itemView.findViewById(R.id.habit_count)
    }
}
