package com.zkrallah.z_habits.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zkrallah.z_habits.R
import com.zkrallah.z_habits.local.entities.Habits

class HabitsAdapter(private val list: MutableList<Habits>) :
    RecyclerView.Adapter<HabitsAdapter.ViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {

        fun onShowHistoryClicked(habits: Habits)
        fun onAddCountClicked(habits: Habits)
        fun onDeleteHabitClicked(habits: Habits, position: Int)
        fun onEditHabitClicked(habits: Habits, position: Int)
        fun onMessageClicked(habits: Habits)

    }

    fun setItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.habit_item, parent, false), mListener
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.habitName.text = list[position].name
        holder.habitCount.text = "Habit count per day : ${list[position].countPerDay}"
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun removeItem(position: Int){
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editItem(habits: Habits, position: Int){
        list[position] = habits
        notifyItemChanged(position)
    }

    inner class ViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val habitName: TextView = itemView.findViewById(R.id.habit_name)
        val habitCount: TextView = itemView.findViewById(R.id.habit_count)
        private val showHistory: ImageButton = itemView.findViewById(R.id.show_history)
        private val addCount: ImageButton = itemView.findViewById(R.id.add_count)
        private val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_habit)
        private val editBtn: ImageButton = itemView.findViewById(R.id.update_count)
        private val msgBtn: ImageButton = itemView.findViewById(R.id.message_btn)

        init {
            showHistory.setOnClickListener {
                listener.onShowHistoryClicked(list[adapterPosition])
            }
            addCount.setOnClickListener {
                listener.onAddCountClicked(list[adapterPosition])
            }
            deleteBtn.setOnClickListener {
                listener.onDeleteHabitClicked(list[adapterPosition], adapterPosition)
            }
            editBtn.setOnClickListener {
                listener.onEditHabitClicked(list[adapterPosition], adapterPosition)
            }
            msgBtn.setOnClickListener {
                listener.onMessageClicked(list[adapterPosition])
            }
        }
    }
}
