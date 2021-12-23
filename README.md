SimpleDuckGallery
========
[![](https://jitpack.io/v/creativeduck/SimpleDuckGallery.svg)](https://jitpack.io/#creativeduck/SimpleDuckGallery)  
This is an android library for selecting picture in your phone   
<img src="https://user-images.githubusercontent.com/89892954/147030607-4004a342-e0f9-4402-9bec-f1b6df7b8b5a.jpg" width="400" height="800"/>   
<img src="https://user-images.githubusercontent.com/89892954/147030830-cba9288c-edc1-4739-99d5-63db5ec75307.jpg" width="400" height="800"/>   
<img src="https://user-images.githubusercontent.com/89892954/147169007-854ad00b-2a05-4602-ba14-a4b10a77d1bc.jpg" width="400" height="800"/>     
        
Installation
--------

In `settings.gradle` file, add JitPack maven like below:
```
dependencyResolutionManagement {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the following dependency in app build.gradle:
```
dependencies {
    implementation 'com.github.creativeduck:SimpleDuckGallery:1.0.0'   
}
```
Usage
--------
use ListAdapter   
이 listAdapter를 통해서 더 쉽게 이미지를 리스트 형식으로 가져오고, 처리할 수 있습니다.   
또한 x버튼을 눌러서 제거하는 이벤트와, 클릭 시 발생하는 이벤트도 쉽게 처리할 수 있습니다.   
```
private val uriListAdapter = UriListAdapter(
    itemDelete ={
        val newList = ArrayList<UriItem>()
        newList.addAll(uriListAdapter.currentList)
        newList.remove(it)
        uriListAdapter.submitList(newList.toList())
    },
    itemClick ={
        Glide.with(this@MainActivity)
            .load(it.url)
            .into(binding.image)
    }
)
binding.recyclerView.apply {
    adapter = uriListAdapter
    layoutManager =  LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
}
```
make launcher   
```
private val photoLauncher = registerSimpleGallery { images ->
    if (images.isNotEmpty()) {
        val imageList = ArrayList<UriItem>()
        imageList.addAll(uriListAdapter.currentList)
        imageList.addAll(images)
        uriListAdapter.submitList(imageList.toList())
    }
}
```
launch   
```
photoLauncher.launch()
```
Custom SnackBar
--------   
    you can use custom snackBar      
view : The view to find a parent from   
message : message that you want to show   
action : action message that you want to show   
clickListener : click event when you touch action message   
```
SimpleGalleryUtil.showSnackBar(context = this,
                                view = binding.root,
                                message = "This is SnackBar",
                                action = null,
                                clickListener = null)
```
<img src="https://user-images.githubusercontent.com/89892954/147043239-f535aee0-02b3-4063-afb1-e7798fbc2170.jpg" width="400" height="800"/>   

License
--------
   
```
[ColorPicker]   
Copyright 2021 creativeduck
   
Licensed under the Apache License, Version 2.0 (the "License");   
you may not use this file except in compliance with the License.   
You may obtain a copy of the License at   
   
   http://www.apache.org/licenses/LICENSE-2.0   
   
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
