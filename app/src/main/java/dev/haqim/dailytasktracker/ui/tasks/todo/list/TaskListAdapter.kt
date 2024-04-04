package dev.haqim.dailytasktracker.ui.tasks.todo.list

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import dev.haqim.dailytasktracker.R
import dev.haqim.dailytasktracker.databinding.TaskItemBinding
import dev.haqim.dailytasktracker.domain.model.Task
import dev.haqim.dailytasktracker.util.FileUtils

class TaskListAdapter(
    private val callback: Callback,
): PagingDataAdapter<Task, RecyclerView.ViewHolder>(ItemDiffCallback()){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.onCreate(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ViewHolder -> {
                holder.onBind(
                    position, getItem(position), callback
                )
            }
        }

    }

    class ViewHolder(
        private val binding: TaskItemBinding,
    ): RecyclerView.ViewHolder(binding.root){

        fun onBind(
            position: Int,
            data: Task?,
            callback: Callback,
        ){
            var initialized = false
            data?.let {
                binding.apply {
                    cbTaskTitle.text = data.title
                    cbTaskTitle.isChecked = data.hasDone
                    tvTaskNote.text = data.note
                    data.attachmentPath?.let { uri ->
                        btnFile.text = FileUtils.getFileName(root.context, uri)
                        btnFile.setOnClickListener {
                            callback.onClickFile(uri)
                        }
                    }
                    btnFile.isVisible = data.attachmentPath != null

                    cgCategories.removeAllViews()
                    data.categories.forEach { category ->
                        val chip = LayoutInflater.from(root.context).inflate(
                            R.layout.chip,
                            cgCategories,
                            false
                        ) as Chip
                        chip.id = category.id.toInt()
                        chip.text = category.title

                        cgCategories.addView(chip)
                    }
                    root.setOnClickListener {
                        callback.onClick(data)
                    }
                    cbTaskTitle.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (initialized) callback.onCheck(data, isChecked)
                    }
                }
            }
            initialized = true
        }

        companion object{
            fun onCreate(parent: ViewGroup): ViewHolder {
                val itemView = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(itemView)
            }
        }
    }

    interface Callback{
        fun onCheck(task: Task, isChecked: Boolean)
        fun onClick(task: Task)
        fun onClickFile(uri: Uri)
    }


    private class ItemDiffCallback: DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(
            oldItem: Task,
            newItem: Task,
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Task,
            newItem: Task,
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

}