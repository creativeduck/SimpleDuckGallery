package com.simple.simple_duck_gallery

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.simple.simple_duck_gallery.gallery.GalleryItem
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GalleryUtil {

    companion object {
        // return rotateBitmap
        @SuppressLint("Range")
        fun getRotateBitmap(cursor : Cursor, contentResolver: ContentResolver, uri: Uri) : Bitmap? {
            // get orientation
            var path = ""
            cursor.moveToFirst()
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            cursor.close()
            var exif : ExifInterface? = null
            try {
                exif = ExifInterface(path)
            } catch (e : IOException) {
                e.printStackTrace()
            }
            val orientation = exif!!.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED)
//            Log.d("PHOTO", "orientation = $orientation")
            // to bitmap
            var bitmap : Bitmap? = null
            try {
                bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return SimpleGalleryUtil.rotateBitmap(bitmap!!, orientation)
        }

        // path to uri
        fun getUriFromPath(filePath : String, contentResolver: ContentResolver) : Uri {
            val photoId: Long
            val photoUri = MediaStore.Images.Media.getContentUri("external")
            val projection = arrayOf(MediaStore.Images.ImageColumns._ID)
            val cursor: Cursor? = contentResolver.query(
                photoUri,
                projection,
                MediaStore.Images.ImageColumns.DATA + " LIKE ?",
                arrayOf(filePath),
                null
            )
            cursor!!.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(projection[0])
            photoId = cursor.getLong(columnIndex)
            cursor.close()
            return Uri.parse("$photoUri/$photoId")
        }
        // 기기에서 사진 가져오기
        fun listOfImages(context: Context) : ArrayList<GalleryItem> {
            val listOfAllImages = ArrayList<GalleryItem>()
            var apsolutePathOfImage : String = ""
            var uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            var projection = arrayOf(MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID)
            var orderBy = MediaStore.Video.Media.DATE_ADDED
            var cursor = context.contentResolver.query(uri, projection, null,
                null, orderBy+" DESC")
            var columnIndexData = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            while (cursor != null && cursor.moveToNext()) {
                if (columnIndexData != null) {
                    apsolutePathOfImage = cursor.getString(columnIndexData!!)
                    val bucketName = cursor.getString(cursor.getColumnIndexOrThrow(projection[1]))
                    val bucketId = cursor.getLong(cursor.getColumnIndexOrThrow(projection[2]))
                    listOfAllImages.add(GalleryItem(apsolutePathOfImage, false, 1, bucketName, bucketId))
                }
            }
            return listOfAllImages
        }
        // 사진 파일 저장
        fun saveFile(imageUri : Uri, contentResolver: ContentResolver) {
            val values = ContentValues()
            val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
            val fileName = "mackduck$timeStamp.jpg"
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            try {
                val pdf = contentResolver.openFileDescriptor(uri!!, "w", null)
                if (pdf == null) {

                } else {
                    val inputData = getBytes(imageUri, contentResolver)
                    val fos = FileOutputStream(pdf.fileDescriptor)
                    fos.write(inputData)
                    fos.flush()
                    fos.close()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(uri, values, null, null)
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        // 이미지 Uri 를 byteArray 로 변환해주는 메서드
        fun getBytes(imageUri : Uri, contentResolver: ContentResolver) : ByteArray {
            val isStream = contentResolver.openInputStream(imageUri)
            val byteBuffer = ByteArrayOutputStream()
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            var len = 0
            len = isStream!!.read(buffer)
            while (len != -1) {
                byteBuffer.write(buffer, 0, len)
                len = isStream.read(buffer)
            }
            return byteBuffer.toByteArray()
        }

        // 사진 회전
        fun rotateImage(source: Bitmap, angle : Float) : Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }
    }
}
