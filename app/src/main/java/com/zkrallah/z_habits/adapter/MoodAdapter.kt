package com.zkrallah.z_habits.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zkrallah.z_habits.R
import com.zkrallah.z_habits.local.entities.Mood

class MoodAdapter(private val list: MutableList<Mood>) : RecyclerView.Adapter<MoodAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.mood_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.date.text = list[position].date
        holder.value.text = list[position].value.toString()
        holder.message.text = list[position].message
        holder.id.text = list[position].moodId.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val date: TextView = itemView.findViewById(R.id.date)
        val value: TextView = itemView.findViewById(R.id.value)
        val message: TextView = itemView.findViewById(R.id.message)
        val id: TextView = itemView.findViewById(R.id.mood_id)
    }
}