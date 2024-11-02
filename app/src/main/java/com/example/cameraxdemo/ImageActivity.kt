package com.example.cameraxdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.example.cameraxdemo.databinding.ActivityImageBinding
import com.yalantis.ucrop.UCrop
import java.io.File

class ImageActivity : AppCompatActivity() {
    private var imagePath: String? = null
    private var positionImage: Int = -1
    private var imageUri: Uri? = null
    private var sizeImage = 0
    private lateinit var listImage: List<String>

    private val imageBinding: ActivityImageBinding by lazy {
        ActivityImageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(imageBinding.root)

        listImage = getAllImages(this)
        positionImage = intent.getIntExtra("position", -1)
        sizeImage = intent.getIntExtra("size", 0)
        imagePath = listImage[positionImage]

        if (!imagePath.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagePath)
                .signature(ObjectKey(System.currentTimeMillis()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageBinding.imgViewImage)
        } else {
            Toast.makeText(this, "No image path received", Toast.LENGTH_SHORT).show()
        }

        imageBinding.imgEdit.setOnClickListener {
            val file = imagePath?.let { File(it) }
            imageUri = Uri.fromFile(file)
            if (imageUri != null) {
                UCrop.of(imageUri!!, imageUri!!)
                    .withAspectRatio(0f, 0f)
                    .withMaxResultSize(2000, 2000)
                    .start(this)
            } else {
                Toast.makeText(this, "Failed to create destination URI", Toast.LENGTH_SHORT).show()
            }
        }

        imageBinding.imgDelete.setOnClickListener {
            imagePath?.let { uri ->
                deletePhotoFromMediaStore(uri)
            } ?: run {
                Toast.makeText(this, "No image URI to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deletePhotoFromMediaStore(filePath: String) {
        val file = File(filePath)
        val contentResolver = contentResolver
        val uriToDelete = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Images.Media.DATA} = ?"
        val selectionArgs = arrayOf(file.absolutePath)
        val rowsDeleted = contentResolver.delete(uriToDelete, selection, selectionArgs)

        if (rowsDeleted > 0) {
            listImage = getAllImages(this)
            sizeImage = listImage.size

            if (positionImage >= sizeImage) {
                positionImage = sizeImage - 1
            }

            if (positionImage in listImage.indices) {
                imagePath = listImage[positionImage]
                Glide.with(this)
                    .load(imagePath)
                    .signature(ObjectKey(System.currentTimeMillis()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageBinding.imgViewImage)
            } else {
                Toast.makeText(this, "No more images available", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ListImageActivity::class.java)
                startActivity(intent)
                finish()
            }
            Toast.makeText(this, "Image deleted successfully from MediaStore", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, "Failed to delete image from MediaStore", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val resultUri = data?.let { UCrop.getOutput(it) }
            if (resultUri != null) {
                Glide.with(this)
                    .load(resultUri)
                    .signature(ObjectKey(System.currentTimeMillis()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageBinding.imgViewImage)
            } else {
                Toast.makeText(this, "Cropped image URI is null", Toast.LENGTH_SHORT).show()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = data?.let { UCrop.getError(it) }
            Toast.makeText(this, "Error cropping image: ${cropError?.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
