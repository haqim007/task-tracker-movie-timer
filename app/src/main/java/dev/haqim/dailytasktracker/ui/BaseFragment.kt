package dev.haqim.dailytasktracker.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.SpannableStringBuilder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import dev.haqim.dailytasktracker.R
import dev.haqim.dailytasktracker.databinding.LayoutLoaderAndErrorBinding
import dev.haqim.dailytasktracker.hideKeyboard
import dev.haqim.dailytasktracker.util.FileUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

abstract class BaseFragment : Fragment(){


    private val selectMedia = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            onSelectedMedia(uri)
        }
    }

    private val selectDocument = registerForActivityResult(GetContentWithMultiFilter()) { uri: Uri? ->
        uri?.let {
            onSelectedMedia(uri)
        }
    }

    protected open fun onSelectedMedia(uri: Uri){}


    @SuppressLint("CheckResult")
    protected fun browseFile() {
        val list = listOf("File", "Image")
        val dialog = MaterialDialog(requireContext())
        val title = getString(
            R.string.attach_file_with_max_size,
            FileUtils.getFileSizeInString(FileUtils.DEFAULT_MAX_FILE_SIZE)
        )
        dialog.title(text = title)
            .cancelable(true)
            .listItems(items = list) { _, index, _ -> // dialog, index, text
                when (index) {
                    0 -> {
                        selectDocument.launch(
                        "${FileUtils.DOC_TYPE};" +
                                "${FileUtils.DOCX_TYPE};" +
                                "${FileUtils.XLS_TYPE};" +
                                "${FileUtils.XLSX_TYPE};" +
                                "${FileUtils.PPT_TYPE};" +
                                "${FileUtils.PPTX_TYPE};" +
                                "${FileUtils.PDF_TYPE};" +
                                "${FileUtils.IMAGE_TYPE};" +
                                "${FileUtils.ZIP_TYPE};" +
                                "${FileUtils.RAR_TYPE};" +
                                "${FileUtils.RAR2_TYPE};" +
                                "${FileUtils.ZIP_COMPRESSED_TYPE};" +
                                "${FileUtils.RAR_COMPRESSED_TYPE};" +
                                "${FileUtils.TGZ_TYPE};" +
                                "${FileUtils.x7ZIP_TYPE};" +
                                "${FileUtils.TAR_TYPE};"
                        )
                    }

                    1 -> {
                        selectMedia.launch(FileUtils.IMAGE_TYPE)
                    }
                }
            }
        dialog.show()
    }

    fun <T> Flow<T>.launchCollectLatest(callback: suspend (value: T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            this@launchCollectLatest.distinctUntilChanged().collectLatest {
                callback(it)
            }
        }
    }

    fun <T> Flow<T>.launchOnResumeCollectLatest(callback: suspend (value: T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                this@launchOnResumeCollectLatest.distinctUntilChanged().collectLatest {
                    callback(it)
                }
            }
        }
    }

    fun <T> Flow<T>.launchCollect(callback: suspend (value: T) -> Unit){
        viewLifecycleOwner.lifecycleScope.launch {
            this@launchCollect.distinctUntilChanged().collect {
                callback(it)
            }
        }
    }
    
    fun hideKeyboard(){
        view?.let { 
            requireActivity().hideKeyboard()
        }
    }

    protected fun isPermissionGranted(permission: String) =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED

    fun createDialog(
        context: Context,
        title: String,
        message: String,
        cancelable: Boolean = false
    ): MaterialDialog {
        val dialog = MaterialDialog(context)

        dialog.message(text = message)
            .title(text = title)
            .cancelable(cancelable)
            .positiveButton(text = context.getString(R.string.ok))
        return dialog
    }

    fun handleLoadStates(
        loadStates: CombinedLoadStates,
        layoutLoader: LayoutLoaderAndErrorBinding,
        recyclerView: RecyclerView,
        adapter: PagingDataAdapter<*, *>,
        emptyMessage: String,
        errorMessage: String
    ) {
        when  {
            loadStates.refresh is LoadState.Loading -> {
                layoutLoader.root.isVisible = true
                layoutLoader.lavAnimation.setAnimation(R.raw.loading)
                layoutLoader.lavAnimation.playAnimation()
                layoutLoader.lavAnimation.isVisible = true
                recyclerView.isVisible = false
                layoutLoader.tvErrorMessage.text = null
            }

            loadStates.refresh is LoadState.Error -> {
                if (adapter.itemCount == 0) {
                    layoutLoader.root.isVisible = true
                    layoutLoader.lavAnimation.setAnimation(R.raw.error_box)
                    layoutLoader.lavAnimation.playAnimation()
                    layoutLoader.lavAnimation.isVisible = true
                    recyclerView.isVisible = false
                    layoutLoader.tvErrorMessage.text = errorMessage
                    layoutLoader.tvErrorMessage.isVisible = true
                } else {
                    layoutLoader.lavAnimation.isVisible = false
                    recyclerView.isVisible = true
                    layoutLoader.tvErrorMessage.isVisible = false
                }
            }
            loadStates.append.endOfPaginationReached && adapter.itemCount == 0 -> {
                layoutLoader.root.isVisible = true
                recyclerView.isVisible = false
                layoutLoader.lavAnimation.setAnimation(R.raw.empty_box)
                layoutLoader.lavAnimation.playAnimation()
                layoutLoader.tvErrorMessage.text = emptyMessage
                layoutLoader.tvErrorMessage.isVisible = true
            }
            else -> {
                recyclerView.isVisible = true
                layoutLoader.tvErrorMessage.text = null
                layoutLoader.tvErrorMessage.isVisible = false
                layoutLoader.lavAnimation.cancelAnimation()
                layoutLoader.root.isVisible = false
            }
        }
    }
}

class GetContentWithMultiFilter: ActivityResultContracts.GetContent() {
    override fun createIntent(context: Context, input:String): Intent {
        super.createIntent(context, input)
        val inputArray = input.split(";").toTypedArray()
//        val myIntent = super.createIntent(context, FileUtils.IMAGE_TYPE)
        val myIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = FileUtils.IMAGE_TYPE
            putExtra(Intent.EXTRA_MIME_TYPES, inputArray)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

        }
        return myIntent
    }
}