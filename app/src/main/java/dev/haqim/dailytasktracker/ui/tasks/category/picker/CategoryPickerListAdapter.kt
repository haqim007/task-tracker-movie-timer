package dev.haqim.dailytasktracker.ui.tasks.category.picker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.haqim.dailytasktracker.databinding.CategoryItemBinding
import dev.haqim.dailytasktracker.databinding.CategoryItemPickerBinding
import dev.haqim.dailytasktracker.domain.model.TaskCategory

class CategoryPickerListAdapter(
    private val callback: Callback,
): ListAdapter<TaskCategory, CategoryPickerListAdapter.ViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.onCreate(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(
            position, getItem(position), callback
        )
    }

    class ViewHolder(
        private val binding: CategoryItemPickerBinding,
    ): RecyclerView.ViewHolder(binding.root){

        fun onBind(
            position: Int,
            data: TaskCategory,
            callback: Callback,
        ){
            binding.apply {
                tvCategoryTitle.text = data.title
                root.setOnClickListener {
                    callback.onClick(data)
                }
            }
        }

        companion object{
            fun onCreate(parent: ViewGroup): ViewHolder {
                val itemView = CategoryItemPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(itemView)
            }
        }
    }

    interface Callback{
        fun onClick(category: TaskCategory)
    }


    private class ItemDiffCallback: DiffUtil.ItemCallback<TaskCategory>(){
        override fun areItemsTheSame(
            oldItem: TaskCategory,
            newItem: TaskCategory,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TaskCategory,
            newItem: TaskCategory,
        ): Boolean {
            return oldItem == newItem
        }
    }

}