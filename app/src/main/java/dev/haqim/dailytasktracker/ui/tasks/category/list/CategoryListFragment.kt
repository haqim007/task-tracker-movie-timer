package dev.haqim.dailytasktracker.ui.tasks.category.list

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.dailytasktracker.R
import dev.haqim.dailytasktracker.data.mechanism.ResourceHandler
import dev.haqim.dailytasktracker.data.mechanism.handle
import dev.haqim.dailytasktracker.databinding.FragmentCategoryListBinding
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.ui.BaseFragment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class CategoryListFragment : BaseFragment() {

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(layoutInflater, container, false)
        binding.setupAdapter()
        viewModel.getCategories()

        binding.apply {
            btnAddCategory.setOnClickListener {
                findNavController().navigate(
                    CategoryListFragmentDirections.actionCategoryListFragmentToAddTaskCategoryFragment()
                )
            }
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        return binding.root
    }

    private fun FragmentCategoryListBinding.setupAdapter() {
        val adapter = CategoryListAdapter(
            object : CategoryListAdapter.Callback {

                override fun onEdit(category: TaskCategory) {
                    findNavController().navigate(
                        CategoryListFragmentDirections
                            .actionCategoryListFragmentToUpdateTaskCategoryFragment(category)
                    )
                }

                override fun onDelete(category: TaskCategory) {
                   viewModel.deleteCategory(category)
                }
            })
        rvTaskCategories.apply {
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
        observeCategories(adapter)
        viewModel.state.map { it.deleteResult }.distinctUntilChanged()
            .launchOnResumeCollectLatest {
                it.handle(
                    object : ResourceHandler<TaskCategory> {
                        override fun onSuccess(data: TaskCategory?) {
                            viewModel.getCategories()
                        }

                        override fun onError(message: String?, data: TaskCategory?, code: Int?) {
                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                        }

                    }
                )
            }
    }

    private fun FragmentCategoryListBinding.observeCategories(adapter: CategoryListAdapter) {
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

}