package com.example.a7minworkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.a7minworkout.databinding.ItemExerciseStatusBinding

class ExerciseStatusAdapter(val items : ArrayList<ExerciseModel>)
    :RecyclerView.Adapter<ExerciseStatusAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemExerciseStatusBinding)
        :RecyclerView.ViewHolder(binding.root) {
            val tvItem = binding.tvItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemExerciseStatusBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model :ExerciseModel =items[position]
        holder.tvItem.text = model.getId().toString()

        when{
            model.getIsCompleted() -> {
                holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context,R.drawable.item_circular_gray_background)
            }
            model.getIsSelected() -> {
                holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context,R.drawable.item_circular_yellow_background)
            }
            else->{
                holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context,R.drawable.item_circular_red_background)
            }
        }
    }
}