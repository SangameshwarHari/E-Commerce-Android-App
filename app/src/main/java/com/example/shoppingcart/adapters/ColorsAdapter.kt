package com.example.shoppingcart.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppingcart.databinding.ColorRvItemsBinding

class ColorsAdapter :RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>() {

    private var selectedPosition = -1
    inner class ColorsViewHolder(private val binding: ColorRvItemsBinding) : ViewHolder(binding.root){
    fun bind(color: Int,position: Int){
        val imageDrawable = ColorDrawable(color)
        binding.imageColor.setImageDrawable(imageDrawable)
        if (position == selectedPosition){//color is selected
            binding.apply {
                imageShadow.visibility = View.VISIBLE
                imagePicked.visibility = View.VISIBLE
            }
        }
        else {//color is not
            binding.apply {
                imageShadow.visibility = View.INVISIBLE
                imagePicked.visibility = View.INVISIBLE
            }
        }
    }
}


    private val diffCallback = object : DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {
        return ColorsViewHolder(
            ColorRvItemsBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color ,position)
        holder.itemView.setOnClickListener{
            if (selectedPosition >= 0)
                notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            OnItemClick?.invoke(color)

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
     }

    var OnItemClick: ((Int)-> Unit) ?= null
}