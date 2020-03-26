package com.tantawy.eiad.photoweather

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item.view.*

class ImageListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {
    private val cont = context
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var images = emptyList<CapturedImage>() // Cached copy of words

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageItemView: ImageView = itemView.findViewById(R.id.imageView)
        var uri:String = ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return ImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val current = images[position]
        Log.d("sooo",current.image)
        holder.imageItemView.setImageURI(Uri.parse(current.image))
        holder.uri = current.image

        Log.d("being sent", holder.uri)
       // Glide.with(cont).load(Uri.parse(current.image)).into(holder.imageItemView)

        holder.imageItemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(cont, ViewCapturedActivity::class.java)
            val bd = holder.imageItemView.drawable as BitmapDrawable
            intent.putExtra("data", bd.bitmap)
            intent.putExtra("firstTime",false)
            intent.putExtra("uri",holder.uri)
            cont.startActivity(intent)
        })
    }

    internal fun setImages(images: List<CapturedImage>) {
        this.images = images
        Log.d("pls",images.size.toString())
        notifyDataSetChanged()
    }

    override fun getItemCount() = images.size
}