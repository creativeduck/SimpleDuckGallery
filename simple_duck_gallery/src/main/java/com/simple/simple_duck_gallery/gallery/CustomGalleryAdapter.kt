package com.simple.simple_duck_gallery.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simple.simple_duck_gallery.OnItemClickListener
import com.simple.simple_duck_gallery.databinding.ItemCustomGalleryBinding

class CustomGalleryAdapter(val context: Context) : RecyclerView.Adapter<CustomGalleryAdapter.Holder>()  {
    var listener : OnItemClickListener? = null
    var imageList = ArrayList<GalleryItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemCustomGalleryBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setImage(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size

    }

    override fun getItemId(position: Int): Long {
        return imageList[position].url.hashCode().toLong()
    }
    inner class Holder(val binding: ItemCustomGalleryBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (listener != null) {
                    listener!!.onItemClicked(binding.root, adapterPosition)
                }
            }
        }

        fun setImage(item : GalleryItem) {
            binding.customGalleryUnselected.visibility = View.GONE
            if (item.selected) {
                binding.customGalleryItemSelectedBorder.visibility = View.VISIBLE
                binding.customGallerySelectedFrame.visibility = View.VISIBLE
            } else {
                binding.customGalleryItemSelectedBorder.visibility = View.INVISIBLE
                binding.customGallerySelectedFrame.visibility = View.INVISIBLE
            }
            Glide.with(context)
                .load(item.url)
                .centerCrop()
                .into(binding.customGalleryItemImage)
            binding.customGallerySelectedText.text = "${item.selectedNum}"
        }
    }
}