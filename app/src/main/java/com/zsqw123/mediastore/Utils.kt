package com.zsqw123.mediastore

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

fun toast(string: String, context: Context = app) = Toast.makeText(context, string, Toast.LENGTH_SHORT).show()

open class ButtonRvAdapter(val count: Int, val event: (Int, MaterialButton) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    override fun getItemCount(): Int = count
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = MainHolder(MaterialButton(parent.context))
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as MaterialButton).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            event(position, this)
        }
    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
fun getBitmap(resources: Resources, size: Int, @DrawableRes id: Int): Bitmap {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeResource(resources, id, options)
    options.apply {
        inJustDecodeBounds = false
        inDensity = minOf(outWidth, outHeight)
        inTargetDensity = size
    }
    return BitmapFactory.decodeResource(resources, id, options)
}

fun getSquareBitmap(resources: Resources, size: Int) = getBitmap(resources, size, R.drawable.icon)
