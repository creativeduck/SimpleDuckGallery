package com.simple.simple_duck_gallery.gallery

import androidx.recyclerview.widget.DiffUtil

class UriCallback : DiffUtil.ItemCallback<UriItem>() {
    override fun areItemsTheSame(oldItem: UriItem, newItem: UriItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UriItem, newItem: UriItem): Boolean {
        return oldItem == newItem
    }
}