package dev.haqim.dailytasktracker.ui.tasks.category.picker

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.dailytasktracker.R
import dev.haqim.dailytasktracker.data.mechanism.ResourceHandler
import dev.haqim.dailytasktracker.data.mechanism.handle
import dev.haqim.dailytasktracker.databinding.FragmentCategoryListBinding
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.ui.BaseFragment
import dev.haqim.dailytasktracker.ui.tasks.todo.list.TaskListViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class CategoryPickerListFragment : BaseFragment() {

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(layoutInflater, container, false)


        binding.apply {
            setupAdapter()
            topAppBar.title = getString(R.string.choose_category)
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            viewModel.getCategories()
            btnAddCategory.setOnClickListener {
                findNavController().navigate(
                    CategoryPickerListFragmentDirections.actionCategoryPickerListFragmentToAddTaskCategoryFragment()
                )
            }
        }

        return binding.root
    }

    private fun FragmentCategoryListBinding.setupAdapter() {
        val adapter = CategoryPickerListAdapter(
            object : CategoryPickerListAdapter.Callback {
                override fun onClick(category: TaskCategory) {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set("category", category)
                    findNavController().navigateUp()
                }
            })
        rvTaskCategories.apply {
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
        observeCategories(adapter)

    }

    private fun FragmentCategoryListBinding.observeCategories(adapter: CategoryPickerListAdapter) {
        viewModel.state.map { it.categories }.distinctUntilChanged()
            .launchOnResumeCollectLatest {

                llError.isVisible = false
                tvCategoryMessage.text = null
                btnTryAgain.isVisible = false
                adapter.submitList(null)

                it.handle(
                    object : ResourceHandler<List<TaskCategory>> {
                        override fun onLoading() {
                            llError.isVisible = true
                            tvCategoryMessage.text = getString(R.string.loading_please_wait)
                        }

                        override fun onSuccess(data: List<TaskCategory>?) {
                            if (!data.isNullOrEmpty()) {
                                adapter.submitList(data)
                            } else {
                                llError.isVisible = true
                                tvCategoryMessage.text =
                                    getString(R.string.there_is_no_category_currently)
                            }
                        }

                        override fun onError(
                            message: String?,
                            data: List<TaskCategory>?,
                            code: Int?
                        ) {
                            llError.isVisible = true
                            tvCategoryMessage.text = message
                            btnTryAgain.isVisible = true
                        }

                    }
                )


                btnTryAgain.setOnClickListener {
                    viewModel.getCategories()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCategories()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}