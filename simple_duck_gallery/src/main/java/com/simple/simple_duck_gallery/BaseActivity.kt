package com.simple.simple_duck_gallery

import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

// 액티비티의 기본을 작성, 뷰 바인딩 활용
abstract class BaseActivity<B : ViewBinding>(private val inflate: (LayoutInflater) -> B) :
    AppCompatActivity() {
    protected lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()

            else -> return true
        }
        return super.onOptionsItemSelected(item)
    }

    open fun setToolbar(toolbar: Toolbar, resId: Int) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setHomeAsUpIndicator(resId)
    }

    open fun showSnackBar(view: View, message: String, action: String?, clickListener : View.OnClickListener?) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.white)))
            .setActionTextColor(
                ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.mackduck_yellow)))
        snackbar.setBackgroundTintList(
            ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.box_gray)))
        if (action != null) {
            snackbar.setAction(action, clickListener)
        }
        snackbar.show()
    }
}