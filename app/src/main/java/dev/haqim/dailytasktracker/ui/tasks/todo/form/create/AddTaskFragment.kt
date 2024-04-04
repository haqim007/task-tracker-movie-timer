package dev.haqim.dailytasktracker.ui.tasks.todo.form.create

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.dailytasktracker.R
import dev.haqim.dailytasktracker.data.mechanism.Resource
import dev.haqim.dailytasktracker.databinding.FragmentAddTaskBinding
import dev.haqim.dailytasktracker.databinding.FragmentAddTaskCategoryBinding
import dev.haqim.dailytasktracker.domain.model.TaskCategory
import dev.haqim.dailytasktracker.ui.BaseFragment
import dev.haqim.dailytasktracker.util.FileUtils
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class AddTaskFragment : BaseFragment() {
    private val viewModel: AddTaskViewModel by viewModels()
    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(layoutInflater, container, false)

        binding.apply {
            observeCategories()
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId){
                    R.id.save_task -> {
                        viewModel.save(etTitle.text.toString(), etNote.text.toString())
                        true
                    }
                    else -> false
                }
            }

            btnFile.setOnClickListener {
                Log.d("btnFile", "clicked")
                Log.d("btnFile", "${viewModel.state.value.fileURI}")

                if (viewModel.state.value.fileURI == null)
                    browseFile()
                else{
                    val intent = FileUtils.openFile(requireActivity(), viewModel.state.value.fileURI!!)
                    if (intent != null) {
                        startActivity(intent)
                    }
                }


            }

            btnAddCategory.setOnClickListener {
                findNavController().navigate(
                    AddTaskFragmentDirections.actionAddTaskFragmentToCategoryPickerListFragment()
                )
            }

            viewModel.state.map { it.title }.distinctUntilChanged().launchOnResumeCollectLatest {
                if (it is Resource.Error){
                    tilTitle.error = it.message
                }else{
                    tilTitle.error = null
                }
            }

            viewModel.state.map { it.fileURI }.distinctUntilChanged().launchOnResumeCollectLatest {
                if (it != null){
                    btnFile.text = FileUtils.getFileName(requireContext(), it)

                }else{
                    btnFile.text = getString(R.string.choose_file)
                }
                btnDeleteFile.isVisible = it != null
            }

            btnDeleteFile.setOnClickListener {
                viewModel.removeFile()
            }

            viewModel.state.map { it.submitResult }
                .distinctUntilChanged()
                .launchOnResumeCollectLatest {
                    if (it is Resource.Error){
                        createDialog(
                            requireContext(),
                            "Failed",
                            message = it.message ?: "Failed to save task"
                        ).show()
                    }
                    else if(it is Resource.Success){
                        findNavController().previousBackStackEntry?.savedStateHandle?.set("refreshTask", it.data)
                        findNavController().navigateUp()
                    }
                }

        }

        return binding.root
    }

    override fun onSelectedMedia(uri: Uri) {
        val newUri = FileUtils.moveToInternal(requireContext(), uri)
        if (newUri != null) {
            viewModel.setFile(newUri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow<TaskCategory?>("category", null)
            ?.launchCollect {
                if (it != null) {
                    viewModel.addCategory(it)
                }
            }
    }


    private fun FragmentAddTaskBinding.observeCategories() {
        viewModel.state.map { it.categories }
            .distinctUntilChanged()
            .launchOnResumeCollectLatest {
                it.forEach { category ->
                    val chip = LayoutInflater.from(requireContext()).inflate(
                        R.layout.input_chip,
                        cgCategories,
                        false
                    ) as Chip
                    chip.id = category.id.toInt()
                    chip.text = category.title
                    chip.setOnCloseIconClickListener {
                        cgCategories.removeView(chip)
                        viewModel.removeCategory(category)
                    }
                    var isChipExist = false
                    cgCategories.forEach {
                        isChipExist = it.id == chip.id
                    }
                    if (!isChipExist){
                        cgCategories.addView(chip)
                    }
                }
            }
    }

    override fun onPause() {
        super.onPause()
        binding.cgCategories.removeAllViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}