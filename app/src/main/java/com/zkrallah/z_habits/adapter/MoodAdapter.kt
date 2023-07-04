package com.zkrallah.z_habits.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zkrallah.z_habits.R
import com.zkrallah.z_habits.local.entities.Mood

class MoodAdapter(private val list: MutableList<Mood>,private val context: Context) : RecyclerView.Adapter<MoodAdapter.ViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.mood_item, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.date.text = list[position].date
        holder.value.text = "Status : ${map[list[position].value]}"
        holder.message.text = "Message : ${list[position].message}"
        Glide.with(context).load(imagesMap[list[position].value]).into(holder.image)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(mood: Mood){
        list.add(mood)
        notifyItemInserted(list.size)
    }
    fun editItem(mood: Mood){
        list[list.size - 1] = mood
        notifyItemChanged(list.size - 1)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val date: TextView = itemView.findViewById(R.id.date)
        val value: TextView = itemView.findViewById(R.id.value)
        val message: TextView = itemView.findViewById(R.id.message)
        val image: ImageView = itemView.findViewById(R.id.mood_image)
    }
}