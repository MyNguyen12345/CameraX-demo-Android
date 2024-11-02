package com.example.cameraxdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cameraxdemo.adapter.ImageAdapter
import com.example.cameraxdemo.databinding.ActivityListImageBinding

class ListImageActivity : AppCompatActivity() {

    private val listImageBinding: ActivityListImageBinding by lazy {
        ActivityListImageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(listImageBinding.root)
    }

    override fun onResume() {
        super.onResume()
        listImageBinding.recyclerPhotos.adapter = ImageAdapter(getAllImages(this), this)
        listImageBinding.imgBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

    }


}