package dev.haqim.dailytasktracker.ui.tasks.category.create

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.dailytasktracker.R
import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.databinding.FragmentAddTaskCategoryBinding
import dev.haqim.dailytasktracker.databinding.FragmentTaskListBinding
import dev.haqim.dailytasktracker.ui.BaseFragment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class AddTaskCategoryFragment : BaseFragment() {

    private val viewModel: AddTaskCategoryViewModel by viewModels()
    private var _binding: FragmentAddTaskCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskCategoryBinding.inflate(layoutInflater, container, false)

        binding.apply {
            topAppBar.apply {
                setOnMenuItemClickListener { menuItem ->
                    when(menuItem.itemId){
                        R.id.save_category -> {
                            viewModel.save(etName.text.toString())
                            true
                        }
                        else -> false
                    }
                }

                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
            }

            viewModel.state.map { it.title }
                .distinctUntilChanged()
                .launchOnResumeCollectLatest {
                    if(it is Resource.Error){
                        tilName.error = it.message
                    }
                    else{
                        tilName.error = null
                    }

                    if(it is Resource.Success){
                        findNavController().navigateUp()
                    }
                }
        }


        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}