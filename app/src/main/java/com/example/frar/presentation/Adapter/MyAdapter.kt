package com.example.frar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.frar.databinding.RcvRecommendItemsBinding

class MyAdapter() : ListAdapter<ItemRecommendation, MyAdapter.ViewHolder>(
 COMPARATOR
) {

    inner class ViewHolder(val binding: RcvRecommendItemsBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RcvRecommendItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvTitle.text = item.title
        holder.binding.tvDescription.text = item.desc
//        holder.binding

    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<ItemRecommendation>() {
            override fun areItemsTheSame(
                oldItem: ItemRecommendation,
                newItem: ItemRecommendation,
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: ItemRecommendation,
                newItem: ItemRecommendation,
            ): Boolean {
                return oldItem == newItem
            }
        }

    }
}

