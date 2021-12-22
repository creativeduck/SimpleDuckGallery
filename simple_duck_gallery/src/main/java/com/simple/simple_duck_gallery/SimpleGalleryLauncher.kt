package com.simple.simple_duck_gallery

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.simple.simple_duck_gallery.gallery.UriItem

typealias SimpleGalleryCallback = (ArrayList<UriItem>) -> Unit

class SimpleGalleryLauncher(
    private val context: () -> Context,
    private val resultLauncher : ActivityResultLauncher<Intent>
) {
    fun launch(currentImageNum : Int) {
        val intent = Intent(context(), CustomGalleryActivity::class.java)
        intent.putExtra(CustomGalleryActivity.CURRENT_IMAGE_NUM, currentImageNum)
        resultLauncher.launch(intent)
    }
}

@SuppressLint("WrongConstant")
fun getImages(data: Intent?, contentResolver: ContentResolver) : ArrayList<UriItem> {
    val images = ArrayList<UriItem>()
    if (data != null) {
        val multipleList = data.getStringArrayListExtra(CustomGalleryActivity.IMAGE_LIST)
        for (i in 0 until multipleList!!.size) {
            // 여기서 처리하고 url 다 받기
            val tmpStr = multipleList[i]
            val currentImageUri = SimpleGalleryUtil.getUriFromPath(tmpStr!!, contentResolver)
            val takeFlags = data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            try {
                contentResolver.takePersistableUriPermission(currentImageUri, takeFlags)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            images.add(UriItem(currentImageUri))
        }
    }
    return images
}

fun ComponentActivity.registerSimpleGallery(
    context: () -> Context = {this},
    callback: SimpleGalleryCallback
) : SimpleGalleryLauncher {
    return SimpleGalleryLauncher(context, createLauncher(callback))
}

fun Fragment.registerSimpleGallery(
    context: () -> Context = { requireContext() },
    callback: SimpleGalleryCallback
) : SimpleGalleryLauncher {
    return SimpleGalleryLauncher(context, createLauncher(callback))
}

private fun ComponentActivity.createLauncher(callback: SimpleGalleryCallback) : ActivityResultLauncher<Intent> {
    return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val images = getImages(it.data, contentResolver)
        callback(images)
    }
}

private fun Fragment.createLauncher(callback: SimpleGalleryCallback) : ActivityResultLauncher<Intent> {
    return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val images = getImages(it.data, activity!!.contentResolver)
        callback(images)
    }
}