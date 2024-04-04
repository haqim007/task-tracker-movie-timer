package dev.haqim.dailytasktracker.ui.tasks.category.update

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.dailytasktracker.R
import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.databinding.FragmentAddTaskCategoryBinding
import dev.haqim.dailytasktracker.ui.BaseFragment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.lang.Exception

@AndroidEntryPoint
class UpdateTaskCategoryFragment : BaseFragment() {
    private val viewModel: UpdateTaskCategoryViewModel by viewModels()
    private var _binding: FragmentAddTaskCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskCategoryBinding.inflate(layoutInflater, container, false)

        val args: UpdateTaskCategoryFragmentArgs by navArgs()
        viewModel.setCategory(args.category)

        binding.apply {
            setDefaultTitle()
            topAppBar.title = getString(R.string.update_category)
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

            viewModel.state.map { it.category }
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

    private fun FragmentAddTaskCategoryBinding.setDefaultTitle() {
        try {
            viewModel.state.value.category.data?.let {
                etName.setText(it.title)
            }
            tilName.error = null
        }catch (e: Exception){
            tilName.error = "Failed to show previous category"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}