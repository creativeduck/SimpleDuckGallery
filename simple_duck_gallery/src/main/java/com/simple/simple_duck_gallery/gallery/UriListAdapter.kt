package com.simple.simple_duck_gallery.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simple.simple_duck_gallery.OnItemClickListener
import com.simple.simple_duck_gallery.databinding.ItemMultiImageBinding

class UriListAdapter(
    private val itemDelete : (UriItem) -> Unit,
    private val itemClick : (UriItem) -> Unit
) : ListAdapter<UriItem, UriListAdapter.Holder>(UriCallback()){
    var deleteListener: OnItemClickListener? = null
    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemMultiImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setImage(currentList[position])
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    inner class Holder(val binding: ItemMultiImageBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.itemMultiImageDelete.setOnClickListener {
                itemDelete(getItem(adapterPosition))
            }

            binding.root.setOnClickListener {
                itemClick(getItem(adapterPosition))
            }
        }

        fun setImage(item: UriItem) {
            Glide.with(binding.root.context)
                .load(item.url)
                .centerCrop()
                .into(binding.itemMultiImageView)
        }
    }
}

