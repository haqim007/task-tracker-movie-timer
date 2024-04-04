package dev.haqim.dailytasktracker.util

import android.annotation.SuppressLint
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


object FileUtils {

    const val DOC_TYPE = "application/msword"
    const val DOCX_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    const val XLS_TYPE = "application/vnd.ms-excel"
    const val XLSX_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    const val PPT_TYPE = "application/vnd.ms-powerpoint"
    const val PPTX_TYPE = "application/vnd.openxmlformats-officedocument.presentationml.presentation"
    const val PDF_TYPE = "application/pdf"
    const val IMAGE_TYPE = "image/*"
    const val ZIP_TYPE = "application/zip"
    const val ZIP_COMPRESSED_TYPE = "application/x-zip-compressed"
    const val RAR_TYPE = "application/vnd.rar"
    const val RAR2_TYPE = "application/rar"
    const val RAR_COMPRESSED_TYPE = "application/x-rar-compressed"
    const val TGZ_TYPE = "application/x-compressed"
    const val x7ZIP_TYPE = "application/x-7z-compressed"
    const val TAR_TYPE = "application/x-tar"
    const val AUDIO_TYPE = "audio/*"
    const val TEXT_TYPE = "text/*"



    // TODO: Replace deprecated function getExternalStorageDirectory()
    val PUBLIC_DOWNLOAD_FOLDER: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val PUBLIC_DOWNLOAD: File = Environment.getExternalStorageDirectory()

    fun getPath(context: Context, uri: Uri): String? {
        //return RealPathUtil.getRealPath(context, uri)
        val fileChooserUtil = FileChooserUtil(context)
        return fileChooserUtil.getPath(uri)
    }

    private fun getMediaType(context: Context, uri: Uri) : MediaType? {
        var type: MediaType? = "image/jpeg".toMediaTypeOrNull()
        try {
            val getType = context.contentResolver.getType(uri)
            getType?.let {
                type = it.toMediaTypeOrNull()
            }?: run{
                type = getMimeType(uri.toString())?.toMediaTypeOrNull()
            }
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        return type
    }

    fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun getFilePath(context: Context, uri: Uri?): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, null, null,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun createFileMultiPart(context: Context, inputName: String, file: File) =
        MultipartBody.Part.createFormData(
            inputName,
            file.name,
            file.asRequestBody(getMediaType(context, file.toUri()))
        )

    /**
     * Delete the files in the "Temp" folder at the root of the project.
     *
     */
    fun deleteTemporaryFiles(context: Context) {
        context.getExternalFilesDir("Temp")?.let { folder ->
            folder.listFiles()?.let { files ->
                files.forEach {
                    if (it.deleteRecursively()) {
                        Log.d(this.javaClass.simpleName, "${it.absoluteFile} delete file was called")
                    } else {
                        Log.e(this.javaClass.simpleName, "${it.absoluteFile} there is no file")
                    }
                }
            }
        }
    }

    fun copyFileToDownloads(context: Context, downloadedFile: File): Uri? {
        val resolver = context.contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, getFileName(context, downloadedFile.toUri()))
                put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(downloadedFile.toString()))
                put(MediaStore.MediaColumns.SIZE, downloadedFile.length())
            }
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
//          No need to copy because of same file path to destination path
            null
        }?.also { downloadedUri ->
            resolver.openOutputStream(downloadedUri).use { outputStream ->
                val brr = ByteArray(1024)
                var len: Int
                val bufferedInputStream = BufferedInputStream(FileInputStream(downloadedFile.absoluteFile))
                while ((bufferedInputStream.read(brr, 0, brr.size).also { len = it }) != -1) {
                    outputStream?.write(brr, 0, len)
                }
                outputStream?.flush()
                bufferedInputStream.close()
            }
        }
    }

    fun openFile(context: Context, file: File, title: String = "Open file with"): Intent? {
        val path = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName.toString() + ".provider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW)
        val type = getMimeType(file.toUri().toString())
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        val chooser =
            Intent.createChooser(intent, title)
        intent.setDataAndType(path, type)
        return chooser
    }

    fun openFile(context: Context, fileUri: Uri): Intent? {
        val file = getPath(context, fileUri)?.let { File(it) }
        return file?.let { openFile(context, it) }
    }

    @SuppressLint("Range")
    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    if (it.getColumnIndex(OpenableColumns.DISPLAY_NAME)>= 0){
                        result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }

                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }

    fun getFileSizeInString(file: File): String {
        val fileSizeInBytes = file.length().toDouble()
        val fileSizeInKB = fileSizeInBytes / 1000
        val fileSizeInMB = fileSizeInKB / 1000
        return if (fileSizeInMB < 1 && fileSizeInKB > 1){
            "${fileSizeInKB.toInt()} KB"
        }else if(fileSizeInMB > 1){
            "%.2f".format(fileSizeInMB)+" MB"
        } else if (fileSizeInKB < 1) {
            "$fileSizeInBytes bytes"
        } else {
            "0 bytes"
        }
    }

    fun getFileSize(
        context: Context,
        uri: Uri
    ): Double {

        var fileSize = -1.0
        val cursor = context.contentResolver
            .query(uri, null, null, null, null, null)
        cursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {

                // get file size
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (!cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getDouble(sizeIndex)
                }
            }
        }
        return fileSize
    }

    fun isExceedMaxFileSize(
        context: Context,
        uri: Uri,
        maxSize: Double = DEFAULT_MAX_FILE_SIZE
    ): Boolean{
        return getFileSize(context, uri) > maxSize
    }

    fun getFileSizeInString(fileSize: Double): String {
        val fileSizeInKB = fileSize / 1000
        val fileSizeInMB = fileSizeInKB / 1000
        return if (fileSizeInMB < 1 && fileSizeInKB > 1){
            "${fileSizeInKB.toInt()} KB"
        }else if(fileSizeInMB > 1){
            if(fileSizeInMB % 1 != 0.00) "%.2f".format(fileSizeInMB)+" MB"
            else "${fileSizeInMB.toInt()} MB"
        } else if (fileSizeInKB < 1) {
            "$fileSize bytes"
        } else {
            "0 bytes"
        }
    }

    fun getTmpFile(context: Context): File {
        val tmpFile = File.createTempFile("tmp_image_file", ".jpg", context.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return tmpFile
    }

    fun getTmpFileUri(context: Context, file: File, application_id: String): Uri {
        return FileProvider.getUriForFile(context, "$application_id.provider", file)
    }

    /**
     * Picked attachment to be uploaded
     *
     * @param context
     * @param item
     * @param fieldName
     * @return
     */
    fun addAttachmentToUpload(
        context: Context,
        item: Uri,
        fieldName: String
    ): MultipartBody.Part? {
        val file = getPath(context, item)?.let { File(it) }
        return file?.let { createFileMultiPart(context, fieldName, it) }
    }

    /**
     * Move to internal app folder
     *
     * @param context
     * @param uri
     * @return
     */
    fun moveToInternal(context: Context, uri: Uri): Uri? {
        // Create a new file in the internal app directory
        val fileName = getFileName(context, uri)
        val newFile = File(context.filesDir, fileName ?: "Unknown.file")

        try {
            // Open an input stream for the content URI
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                // Create an output stream for the new file
                FileOutputStream(newFile).use { outputStream ->
                    // Copy the content from the input stream to the output stream
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        // Return the URI of the newly created file
        return FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            newFile
        )
    }


    const val DEFAULT_MAX_FILE_SIZE = 15000000.0

}