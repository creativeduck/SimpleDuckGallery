package com.simple.simple_duck_gallery.gallery

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GalleryItem(
    var url : String,
    var selected : Boolean,
    var selectedNum : Int,
    var bucketName : String,
    var bucketId : Long
) : Parcelable
