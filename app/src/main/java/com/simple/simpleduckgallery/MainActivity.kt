package com.simple.simpleduckgallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.simple.simple_duck_gallery.SimpleGalleryUtil
import com.simple.simple_duck_gallery.gallery.UriItem
import com.simple.simple_duck_gallery.gallery.UriListAdapter
import com.simple.simple_duck_gallery.registerSimpleGallery
import com.simple.simpleduckgallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var uriListAdapter: UriListAdapter
    private val binding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val photoLauncher = registerSimpleGallery { images ->
        if (images.isNotEmpty()) {
            val imageList = ArrayList<UriItem>()
            imageList.addAll(uriListAdapter.currentList)
            imageList.addAll(images)
            uriListAdapter.submitList(imageList.toList())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        uriListAdapter = UriListAdapter(
            itemDelete = {
                val newList = ArrayList<UriItem>()
                newList.addAll(uriListAdapter.currentList)
                newList.remove(it)
                uriListAdapter.submitList(newList.toList())
            },
            itemClick = {
                Glide.with(this@MainActivity)
                    .load(it.url)
                    .into(binding.image)
            }
        )

        binding.btnGetImage.setOnClickListener {
            photoLauncher.launch(uriListAdapter.currentList.size)
        }

        binding.recyclerView.apply {
            adapter = uriListAdapter
            layoutManager =  LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.btnShowSnack.setOnClickListener {
            SimpleGalleryUtil.showSnackBar(this, binding.root, "nop", null, null)
        }
    }
}