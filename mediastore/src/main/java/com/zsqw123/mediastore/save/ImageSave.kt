package com.zsqw123.mediastore.save

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.zsqw123.mediastore.MediaParams
import com.zsqw123.mediastore.storageContext
import kotlinx.coroutines.*
import java.io.File
import java.io.InputStream
import java.util.*

/**
 * Author zsqw123
 * Create by damyjy
 * Date 2021/7/5 12:59
 */
/**
 * 事实上 mimeType 只会作为参考,
 * 仅在不能通过保存文件名识别出文件类型的时候才会用到
 */
class ImageSave(
    var inputStream: InputStream? = null,
    var mimeType: String = "",
    var description: String = "",
) : MediaSave {
    var suspendFile: Deferred<File>? = null

    companion object {
        operator fun invoke(file: File) = ImageSave(file.inputStream())

        /**
         * 涉及到了新建文件, 强烈建议此方法在子线程中执行, 这里没有强制
         * @see WorkerThread
         */
        operator fun invoke(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 100): ImageSave {
            val save = ImageSave()
            save.suspendFile = GlobalScope.async(Dispatchers.IO) {
                MediaSave.dupCreateFile(storageContext.cacheDir, "${Date().time}.jpg").apply {
                    bitmap.compress(format, quality, outputStream())
                }
            }
            return save
        }
    }

    override suspend fun save(name: String, subPath: String, contentValues: ContentValues): Boolean = withContext(Dispatchers.IO) {
        try {
            inputStream = inputStream ?: suspendFile?.await()?.inputStream() ?: return@withContext false
            MediaSave.commonMediaSave(
                name, Environment.DIRECTORY_PICTURES, subPath, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                inputStream!!, ContentValues().apply {
                    put(MediaParams.DISPLAY_NAME, name)
                    put(MediaParams.MIME_TYPE, mimeType)
                    put(MediaStore.Images.Media.DESCRIPTION, description)
                    putAll(contentValues)
                })
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
