package com.simple.simple_duck_gallery

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

object SimpleGalleryUtil {
    fun rotateBitmap(bitmap: Bitmap, orientation: Int) : Bitmap? {
        var matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> {
                return bitmap
            }
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                matrix.postRotate(180f)
            }
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.postRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                matrix.postRotate(90f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.postRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                matrix.postRotate(-90f)
            }
            else -> {
                return bitmap
            }
        }
        return try {
            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            rotatedBitmap
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
    }
    fun showSnackBar(context: Context, view: View, message: String, action: String?, clickListener : View.OnClickListener?) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.white)))
            .setActionTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.mackduck_yellow)))
        snackbar.setBackgroundTintList(
            ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.box_gray)))
        if (action != null) {
            snackbar.setAction(action, clickListener)
        }
        snackbar.show()
    }

}