package dev.haqim.dailytasktracker.ui.tasks.category.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.haqim.dailytasktracker.databinding.CategoryItemBinding
import dev.haqim.dailytasktracker.domain.model.TaskCategory

class CategoryListAdapter(
    private val callback: Callback,
): ListAdapter<TaskCategory, CategoryListAdapter.ViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.onCreate(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(
            position, getItem(position), callback
        )
    }

    class ViewHolder(
        private val binding: CategoryItemBinding,
    ): RecyclerView.ViewHolder(binding.root){

        fun onBind(
            position: Int,
            data: TaskCategory,
            callback: Callback,
        ){
            binding.apply {
                tvCategoryTitle.text = data.title
                btnEdit.setOnClickListener {
                    callback.onEdit(data)
                }
                btnDelete.setOnClickListener {
                    callback.onDelete(data)
                }
            }
        }

        companion object{
            fun onCreate(parent: ViewGroup): ViewHolder {
                val itemView = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(itemView)
            }
        }
    }

    interface Callback{
        fun onEdit(category: TaskCategory)
        fun onDelete(category: TaskCategory)
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