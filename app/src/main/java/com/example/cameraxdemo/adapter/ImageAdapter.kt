package com.example.cameraxdemo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.example.cameraxdemo.ImageActivity
import com.example.cameraxdemo.R

class ImageAdapter(private val imageList: List<String>, private val context: Context) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imgView_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageItem = imageList[position]
        Glide.with(holder.itemView.context)
            .load(imageItem)
            .centerCrop()
            .signature(ObjectKey(System.currentTimeMillis()))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("size", imageList.size)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
