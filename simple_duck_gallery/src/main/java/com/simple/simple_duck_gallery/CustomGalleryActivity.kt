package com.simple.simple_duck_gallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.simple.simple_duck_gallery.databinding.ActivityCustomGalleryBinding
import com.simple.simple_duck_gallery.gallery.CustomGalleryAdapter
import com.simple.simple_duck_gallery.gallery.GalleryItem
import java.util.*
import kotlin.collections.ArrayList

class CustomGalleryActivity : BaseActivity<ActivityCustomGalleryBinding>(ActivityCustomGalleryBinding::inflate) {
    lateinit var galleryAdapter: CustomGalleryAdapter
    lateinit var gallerySpinnerAdapter : ArrayAdapter<String>
    lateinit var gallerySpinnerList : List<String>
    private var totalImageList = ArrayList<GalleryItem>()
    private var imageList = ArrayList<GalleryItem>()
    private var selectedNum = 0
    private var selectedIndex = 0
    private var completeActive = false
    private var multipleImageList = ArrayList<String>()
    private var multiSelectedNum = 1
    private var currentSelectedNum = 0
//    private var alreadyImageIndex = 0
    private var onlyOne = false
    private var bucketHashMap = HashMap<String, Long>()
    private var bucketList = mutableListOf<String>()
    private var currentSelectedIndex = 1 // 현재 선택된 폴더
    private var selectedPhotoList = ArrayList<GalleryItem>()
    private var contentObserver: ContentObserver? = null
    private var isFirst = true

    private var maxImageCount = 105

    // 갤러리 권한
    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                isGranted: Boolean ->
            if (isGranted) {
                loadMultipleImage()
            }
            else {
                showSnackBar(binding.root, getString(R.string.need_permission), null, null)
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(binding.customGalleryToolbar, R.drawable.ic_back)

        binding.customGalleryMultiNum.visibility = View.VISIBLE
        galleryAdapter = CustomGalleryAdapter(this)
        setRecycler()

//        currentSelectedNum = intent.getIntExtra(CURRENT_IMAGE_NUM, 0)
//        alreadyImageIndex = currentSelectedNum
        binding.customGalleryTitle.setOnClickListener {
            binding.customGallerySpinner.performClick()
        }
        val handlerThread = HandlerThread("content_observer")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        contentObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                val regex =
                    Regex(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/[0-9]+")
                if (uri.toString().matches(regex)) {
                    val index = binding.customGallerySpinner.selectedItemPosition
                    totalImageList = listOfImages(true, index)
                }
                super.onChange(selfChange, uri)
            }
        }
        contentObserver?.let {
            contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, it)
        }
    }
    override fun onStart() {
        super.onStart()
        if (isFirst) {
            isFirst = false
            doPermissionCheck(true)
        }
        else {
            doPermissionCheck(false)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        contentObserver?.let {
            contentResolver.unregisterContentObserver(it)
        }
    }
    fun doPermissionCheck(first: Boolean) {
        // 권한이 없다면
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 권한을 거부했고 설명이 필요하다면
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                val clickListener = View.OnClickListener { requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) }
                showSnackBar(binding.root, getString(R.string.need_permission), getString(R.string.agreed), clickListener)
            }
            // 사용자가 승인 거절과 동시에 다시 표시하지 않기 옵션을 선택한 경우
            else {
                val clickListener = View.OnClickListener { // 설정창으로 가기
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                showSnackBar(binding.root, getString(R.string.need_permission_go_to_setting), getString(R.string.okay), clickListener)
                // 아직 승인 요청을 한 적이 없는 경우
            }

        } else { // 권한이 있다면
            if (first) {
                loadMultipleImage()
            }
            else {

            }
        }
    }
    fun showLimitFive() {
        showSnackBar(binding.root, "이미지는 최대 5장까지 선택할 수 있어요.", null, null)
    }
    private fun loadMultipleImage() {
        imageList = listOfImages(false, currentSelectedIndex)
        galleryAdapter.imageList = imageList
        galleryAdapter.listener = object : OnItemClickListener {
            override fun onItemClicked(view: View, pos: Int) {
                val selectBorder : ImageView = view.findViewById(R.id.custom_gallery_item_selected_border)
                val selectFrame : FrameLayout = view.findViewById(R.id.custom_gallery_selected_frame)
                val selectText : TextView = view.findViewById(R.id.custom_gallery_selected_text)
                val isSelected = imageList[pos].selected
                if (isSelected) {
                    selectBorder.visibility = View.GONE
                    selectFrame.visibility = View.GONE
                    multiSelectedNum -= 1
                    val item = imageList[pos]
                    with(item) {
                        selected = false
                        // 마지막으로 선택된 아이템이었다면
                        if (selectedNum < selectedPhotoList.size) {
                            selectedPhotoList.remove(this)
                            for (i in 0 until selectedPhotoList.size) {
                                val item = selectedPhotoList[i]
                                item.selectedNum = i +1
                            }
                        }
                        else {
                            selectedPhotoList.remove(this)
                        }
                        selectedNum = 0
                    }
                    selectedNum -= 1
                    // 여기서 이전에 선택된 나머지 숫자도 조정되도록 해야 한다.
                    multipleImageList.remove(item.url)
                    if (multiSelectedNum == 0) {
                        binding.customGalleryComplete.setTextColor(getColor(R.color.main_gray))
                        completeActive = false
                    }
                    if (selectedNum > 0) {
                        binding.customGalleryMultiNum.text = "$selectedNum"
                    } else {
                        binding.customGalleryMultiNum.text = ""
                    }
                    galleryAdapter.notifyDataSetChanged()
                } else {
                    if (multiSelectedNum == maxImageCount+1|| (multiSelectedNum + currentSelectedNum) >= maxImageCount+1) {
                        showLimitFive()
                    } else {
                        selectBorder.visibility = View.VISIBLE
                        selectFrame.visibility = View.VISIBLE
                        val item = imageList[pos]
                        with(item) {
                            selected = true
                            selectedNum = multiSelectedNum
                            selectedPhotoList.add(this)
                        }
                        binding.customGalleryComplete.setTextColor(getColor(R.color.mackduck_yellow))
                        selectText.text = "${multiSelectedNum}"
                        multiSelectedNum += 1
                        selectedNum += 1
                        completeActive = true
                        if (selectedNum > 0) {
                            binding.customGalleryMultiNum.text = "$selectedNum"
                        } else {
                            binding.customGalleryMultiNum.text = ""
                        }
                        multipleImageList.add(item.url)
                    }
                }
            }
        }
    }
    private fun setRecycler() {
        galleryAdapter = CustomGalleryAdapter(this)
        galleryAdapter.setHasStableIds(true)
        binding.customGalleryRecycler.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 3)
            adapter = galleryAdapter
            addItemDecoration(GridSpacingItemDecoration(3, 5, false))
            itemAnimator = null
        }
        binding.customGalleryComplete.setOnClickListener {
            if (onlyOne) {
                val image = imageList[selectedIndex].url
                val intent = Intent()
                intent.putExtra(JUST_IMAGE, image)
                setResult(RESULT_CUSTOM_GALLERY, intent)
                finish()
            }
            else {
                val intent = Intent()
                intent.putExtra(IMAGE_LIST, multipleImageList)
                setResult(RESULT_CUSTOM_GALLERY, intent)
                finish()
            }
        }
    }
    fun listOfImages(changed: Boolean, index: Int) : ArrayList<GalleryItem> {
        // 여기선 사용자가 선택하고 있었던 거를 유지하고 있어야 한다.
        val listOfAllImages = ArrayList<GalleryItem>()
        var apsolutePathOfImage: String = ""
        var uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        var projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID
        )
        var orderBy = MediaStore.Video.Media.DATE_ADDED
        var cursor = contentResolver.query(
            uri, projection, null,
            null, "$orderBy DESC"
        )
        var columnIndexData = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        var buckets = HashSet<String>()
        while (cursor != null && cursor.moveToNext()) {
            if (columnIndexData != null) {
                apsolutePathOfImage = cursor.getString(columnIndexData!!)
                val bucketName = cursor.getString(cursor.getColumnIndexOrThrow(projection[1]))
                val bucketId = cursor.getLong(cursor.getColumnIndexOrThrow(projection[2]))
                listOfAllImages.add(
                    GalleryItem(
                        apsolutePathOfImage,
                        false,
                        1,
                        bucketName,
                        bucketId
                    )
                )
                bucketHashMap[bucketName] = bucketId
            }
        }
        cursor?.close()
        for ((key, value) in bucketHashMap) {
            buckets.add(key)
        }
        bucketList = buckets.toMutableList()
        bucketList.sort()
        bucketList.add(0, getString(R.string.all_picture))

        gallerySpinnerList = bucketList.toList()
        gallerySpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, gallerySpinnerList)
        gallerySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        Handler(Looper.getMainLooper()).post {
            binding.customGallerySpinner.adapter = gallerySpinnerAdapter
            binding.customGallerySpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val title = gallerySpinnerList[position]
                        selectedIndex = position
                        binding.customGalleryTitle.text = title
                        if (title == getString(R.string.all_picture)) {
                            galleryAdapter.imageList = totalImageList
                            imageList = totalImageList
                        } else {
                            val id = bucketHashMap[title]
                            val tmpList = ArrayList<GalleryItem>()
                            for (i in 0 until totalImageList.size) {
                                val item = totalImageList[i]
                                if (item.bucketId == id) {
                                    tmpList.add(item)
                                }
                            }
                            galleryAdapter.imageList = tmpList
                            imageList = tmpList
                        }
                        galleryAdapter.notifyDataSetChanged()
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }
            totalImageList.addAll(listOfAllImages)
            if (changed) {
                binding.customGallerySpinner.setSelection(index)
            }
        }
        return listOfAllImages
    }

    companion object {
        const val RESULT_CUSTOM_GALLERY = 1113
        val CURRENT_IMAGE_NUM = "currentImageNum"
        val JUST_IMAGE = "justImage"
        val IMAGE_LIST = "imageList"

    }
}