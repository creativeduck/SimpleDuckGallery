package com.simple.simple_duck_gallery.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simple.simple_duck_gallery.OnItemClickListener
import com.simple.simple_duck_gallery.R
import com.simple.simple_duck_gallery.databinding.ItemCustomGalleryBinding

class CustomGalleryListAdapter : ListAdapter<GalleryItem, CustomGalleryListAdapter.Holder>(
    CustomGalleryCallback()
) {
    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemCustomGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setImage(currentList[position])
    }

    inner class Holder(val binding: ItemCustomGalleryBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if(listener != null) {
                    listener?.onItemClicked(binding.root, adapterPosition)
                }
            }
        }


        fun setImage(item : GalleryItem) {
            if (item.url != "cameraButton") {
                if (item.selected) {
                    binding.customGalleryItemSelectedBorder.visibility = View.VISIBLE
                    binding.customGallerySelectedFrame.visibility = View.VISIBLE
                } else {
                    binding.customGalleryItemSelectedBorder.visibility = View.INVISIBLE
                    binding.customGallerySelectedFrame.visibility = View.INVISIBLE
                }
                Glide.with(binding.root.context)
                    .load(item.url)
                    .centerCrop()
                    .into(binding.customGalleryItemImage)
                binding.customGallerySelectedText.text = "${item.selectedNum}"
            } else {
                binding.customGalleryUnselected.visibility = View.GONE
                Glide.with(binding.root.context)
                    .load(R.drawable.ic_photo_upload)
                    .centerCrop()
                    .into(binding.customGalleryItemImage)
            }
        }
    }
}