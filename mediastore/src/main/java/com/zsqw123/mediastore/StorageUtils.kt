package com.zsqw123.mediastore

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.core.content.FileProvider
import com.zsqw123.mediastore.read.ImageRead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLConnection

/**
 * Author zsqw123
 * Create by damyjy
 * Date 2021/7/4 12:39
 */
suspend fun getPicUris(): List<Uri> = withContext(Dispatchers.IO) {
    val list = arrayListOf<Uri>()
    ImageRead.read().onEach {
        list += it.uri
    }
    list
}

val Uri.fileDescriptor: ParcelFileDescriptor?
    get() = storageContext.contentResolver.openFileDescriptor(this, "w")

fun Uri.delete() = storageContext.contentResolver.delete(this, null, null)

fun Uri.share(activity: Activity, type: String = "image/*") =
    activity.startActivity(Intent(Intent.ACTION_SEND).apply {
        setType(type)
        putExtra(Intent.EXTRA_STREAM, this@share)
    })

fun File.getProviderUri(provider: String = "${storageContext.packageName}.provider"): Uri =
    FileProvider.getUriForFile(storageContext, provider, this)

object typeMap {
    operator fun get(file: File): String = URLConnection.getFileNameMap().getContentTypeFor(file.name) ?: ""
    operator fun get(fileName: String): String = get(File(fileName))
}

lateinit var storageContext: Application
fun storageInit(application: Application) {
    storageContext = application
}