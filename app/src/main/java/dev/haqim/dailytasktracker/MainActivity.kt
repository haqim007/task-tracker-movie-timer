package dev.haqim.dailytasktracker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import dagger.hilt.android.AndroidEntryPoint
import dev.haqim.dailytasktracker.databinding.ActivityMainBinding
import dev.haqim.dailytasktracker.util.FileUtils
import dev.haqim.dailytasktracker.util.PermissionRequestType

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

        var permission = ALLOW
        var isAllowed = true

        if (permissions[PermissionRequestType.EX_STORAGE_REQ.value] == false){
            isAllowed = false
            permission = EXTERNAL_STORAGE
        }

        else if (
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
            permissions[PermissionRequestType.EX_WRITE_STORAGE_REQ.value] == false
        ){
            isAllowed = false
            permission = EXTERNAL_WRITE_STORAGE
        }

        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            if(permissions[PermissionRequestType.READ_IMAGES.value] == false){
                isAllowed = false
                permission = READ_IMAGE
            }

            else if (permissions[PermissionRequestType.READ_VIDEO.value] == false){
                isAllowed = false
                permission = READ_VIDEO
            }

            else if (permissions[PermissionRequestType.POST_NOTIFICATION.value] == false){
                isAllowed = false
                permission = POST_NOTIFICATION
            }
        }

        if (!isAllowed){
            Toast.makeText(
                this,
                "You need the $permission permission to use this app",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView
            .setupWithNavController(navController)


    }

    override fun onStart() {
        super.onStart()
        setupInitPermissions()
    }

    private fun setupInitPermissions(){
        val permissionRequestTypes = mutableListOf<PermissionRequestType>()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            permissionRequestTypes.addAll(
                listOf(
                    PermissionRequestType.READ_IMAGES,
                    PermissionRequestType.READ_VIDEO,
                    PermissionRequestType.POST_NOTIFICATION
                )
            )
        }else{
            permissionRequestTypes.add(PermissionRequestType.EX_STORAGE_REQ)
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
            permissionRequestTypes.add(PermissionRequestType.EX_WRITE_STORAGE_REQ)
        }

        setupPermissions(permissionRequestTypes)
    }

    private fun setupPermissions(permissionReqTypes: List<PermissionRequestType>){
        val permissionsToRequest = permissionReqTypes
            .filter { it.value != null }
            .filter { it.value?.let { ContextCompat.checkSelfPermission(this, it) } != PackageManager.PERMISSION_GRANTED }
            .mapNotNull { it.value }
            .toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            permissionRequest.launch(permissionsToRequest)
        }
    }

    companion object {
        const val ALLOW = "allow"
        const val EXTERNAL_STORAGE = "external storage"
        const val EXTERNAL_WRITE_STORAGE = "external write Storage"
        const val READ_IMAGE = "read image"
        const val READ_VIDEO = "read video"
        const val POST_NOTIFICATION = "post notification"
    }
}