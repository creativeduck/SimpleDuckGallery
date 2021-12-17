package  com.simple.simple_duck_gallery

import android.view.View

interface OnItemClickListener {
    fun onItemClicked(view: View, pos: Int)
}
interface OnItemClickListenerWithParent {
    fun onItemClicked(view: View, pos: Int, childPosition: Int)
}

interface OnItemLongClickListener {
    fun onItemLongClicked(view: View, pos: Int)
}

interface OnMinusClickListener {
    fun onMinusClick(view: View, pos: Int)
}

interface onPlusClickListener {
    fun onPlusClick(view: View, pos: Int)
}

interface onDeleteClickListener {
    fun onDeleteClick(view: View, pos: Int)
}