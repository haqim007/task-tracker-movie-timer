package dev.haqim.dailytasktracker.ui.tasks.todo.list

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.dailytasktracker.R
import dev.haqim.dailytasktracker.databinding.FragmentTaskListBinding
import dev.haqim.dailytasktracker.domain.model.Task
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.ui.BaseFragment
import dev.haqim.dailytasktracker.util.FileUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskListFragment : BaseFragment() {

    private val viewModel: TaskListViewModel by activityViewModels()
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TaskListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(layoutInflater, container, false)

        binding.apply {
            observeCategoriesFilter()

            btnAddFilter.setOnClickListener {
                findNavController().navigate(
                    TaskListFragmentDirections.actionTaskListFragmentDestToCategoryFilterListFragment()
                )
            }

            btnCategories.setOnClickListener {
                findNavController().navigate(
                    TaskListFragmentDirections.actionTaskListFragmentDestToCategoryListFragment()
                )
            }

            btnAddTask.setOnClickListener {
                findNavController().navigate(
                    TaskListFragmentDirections.actionTaskListFragmentDestToAddTaskFragment()
                )
            }

            adapter = TaskListAdapter(
                object : TaskListAdapter.Callback {
                    override fun onCheck(task: Task, isChecked: Boolean) {
                        viewModel.check(task, isChecked)
                    }

                    override fun onClick(task: Task) {
                        findNavController().navigate(
                            TaskListFragmentDirections.actionTaskListFragmentDestToUpdateTaskFragment(task)
                        )
                    }

                    override fun onClickFile(uri: Uri) {
                        val intent = FileUtils.openFile(requireActivity(), uri)
                        if (intent != null) {
                            startActivity(intent)
                        }
                    }

                })
            rvTasks.adapter = adapter

            viewLifecycleOwner.lifecycleScope.launch{
                viewModel.pagingFlow.collect{
                    adapter.submitData(it)
                }
            }

            adapter.loadStateFlow.launchCollect {
                handleLoadStates(
                    it,
                    layoutLoader,
                    rvTasks,
                    adapter,
                    getString(R.string.there_is_no_task_at_the_moment),
                    getString(R.string.an_error_occurred)
                )
            }

            handleMainButton()
        }
        return binding.root
    }

//    override fun onStop() {
//        super.onStop()
//        viewLifecycleOwner.lifecycleScope.launch {
//            adapter.submitData(PagingData.empty())
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        //adapter.refresh()
//    }

    private fun FragmentTaskListBinding.observeCategoriesFilter() {
        viewModel.state.map { it.categories }
            .distinctUntilChanged()
            .launchOnResumeCollectLatest {
                adapter.refresh()
                it.forEach { category ->
                    val chip = LayoutInflater.from(requireContext()).inflate(
                        R.layout.input_chip,
                        cgCategoriesFilter,
                        false
                    ) as Chip
                    chip.id = category.id.toInt()
                    chip.text = category.title
                    chip.setOnCloseIconClickListener {
                        viewModel.removeFilterCategories(category)
                        cgCategoriesFilter.removeView(chip)
                        //adapter.refresh()
                    }
                    var isChipExist = false
                    cgCategoriesFilter.forEach {
                        isChipExist = it.id == chip.id
                    }
                    if (!isChipExist){
                        cgCategoriesFilter.addView(chip)
                    }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow<Task?>("refreshTask", null)
            ?.launchCollect {
                //adapter.refresh()
            }
    }

    private fun handleMainButton() {
        binding.btnShowTasks.setOnClickListener {
            binding.apply {
                btnShowTasks.apply {
                    setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorOnPrimary
                        )
                    )
                    setTextColor(requireContext().getColor(R.color.colorPrimary))
                }

                btnShowTasksCompleted.apply {
                    setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        )
                    )
                    setTextColor(requireContext().getColor(R.color.colorOnPrimary))
                }
            }
            viewModel.seeAllTasks()
        }

        binding.btnShowTasksCompleted.setOnClickListener {
            binding.apply {
                btnShowTasksCompleted.apply {
                    setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorOnPrimary
                        )
                    )
                    setTextColor(requireContext().getColor(R.color.colorPrimary))
                }

                btnShowTasks.apply {
                    setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        )
                    )
                    setTextColor(requireContext().getColor(R.color.colorOnPrimary))
                }
            }
            viewModel.seeCompletedTasks()
        }

        viewModel.state.map { it.onlyCompleted }.distinctUntilChanged().launchCollect {
            adapter.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}