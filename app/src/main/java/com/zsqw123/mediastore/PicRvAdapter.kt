package com.zsqw123.mediastore

import android.content.res.Resources
import android.net.Uri
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * Author zsqw123
 * Create by damyjy
 * Date 2021/7/4 12:45
 */
class PicRvAdapter(private val pics: List<Uri>) : RecyclerView.Adapter<PicRvAdapter.PicRvHolder>() {
    override fun getItemCount(): Int = pics.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicRvHolder = PicRvHolder(ImageView(parent.context))
    override fun onBindViewHolder(holder: PicRvHolder, position: Int) {
        holder.itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Resources.getSystem().displayMetrics.widthPixels / 3)
        (holder.itemView as ImageView).scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(holder.itemView).load(pics[position]).into(holder.itemView)
//        事实上应该用下面这个方法, 但是下面方法 api 29 告辞
//        Glide.with(holder.itemView).load(
//            contentResolver.loadThumbnail(pics[position], Size(Resources.getSystem().displayMetrics.widthPixels / 3, 100.dp.toInt()), null)
//        ).into(holder.itemView)
    }

    class PicRvHolder(itemView: ImageView) : RecyclerView.ViewHolder(itemView)

}