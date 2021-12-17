package com.simple.simpleduckgallery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.simple.simple_duck_gallery.CustomGalleryActivity
import com.simple.simple_duck_gallery.CustomGalleryActivity.Companion.RESULT_CUSTOM_GALLERY
import com.simple.simple_duck_gallery.GalleryUtil.Companion.getUriFromPath
import com.simple.simple_duck_gallery.OnItemClickListener
import com.simple.simple_duck_gallery.SimpleGalleryUtil
import com.simple.simple_duck_gallery.gallery.UriItem
import com.simple.simple_duck_gallery.gallery.UriListAdapter
import com.simple.simpleduckgallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var uriListAdapter: UriListAdapter
    private var alreadyImageIndex = 0


    private val binding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val photoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_CUSTOM_GALLERY) {
            val data = result.data
            val multipleList = data!!.getStringArrayListExtra("imageList")
            val tmpList = ArrayList<UriItem>()
            for (i in 0 until multipleList!!.size) {
                // 여기서 처리하고 url 다 받기
                val tmpStr = multipleList[i]
                val currentImageUri = getUriFromPath(tmpStr!!, contentResolver)
                val takeFlags = data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                try {
                    contentResolver.takePersistableUriPermission(currentImageUri, takeFlags)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
                tmpList.add(UriItem(currentImageUri))
            }
            val realList = ArrayList<UriItem>()
            realList.addAll(uriListAdapter.currentList)
            realList.addAll(tmpList)
            uriListAdapter.submitList(realList.toList())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        uriListAdapter = UriListAdapter()
        uriListAdapter.deleteListener = object : OnItemClickListener {
            override fun onItemClicked(view: View, pos: Int) {
                val item = uriListAdapter.currentList[pos]
                // 만약 원래 있던 사진이라면
                if (pos < alreadyImageIndex) {
                    alreadyImageIndex -= 1
                }
                val newList = ArrayList<UriItem>()
                newList.addAll(uriListAdapter.currentList)
                newList.remove(item)
                uriListAdapter.submitList(newList.toList())
            }
        }
        uriListAdapter.listener = object : OnItemClickListener {
            override fun onItemClicked(view: View, pos: Int) {
                val item = uriListAdapter.currentList[pos]
                Glide.with(this@MainActivity)
                    .load(item.url)
                    .into(binding.image)
            }
        }

        binding.recyclerView.apply {
            adapter = uriListAdapter
            layoutManager =  LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
//            addItemDecoration(HorizontalSpaceDecoration(7))
        }

        binding.btnGetImage.setOnClickListener {
            val intent = Intent(this, CustomGalleryActivity::class.java)
            intent.putExtra(CURRENT_IMAGE_NUM, uriListAdapter.currentList.size)
            photoLauncher.launch(intent)
        }

        binding.btnShowSnack.setOnClickListener {
            SimpleGalleryUtil.showSnackBar(this, binding.root, "nop", null, null)
        }
    }

    companion object {
        val CURRENT_IMAGE_NUM = "current_image_num"
    }
}