package com.zsqw123.mediastore.save

import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.zsqw123.mediastore.MediaType
import com.zsqw123.mediastore.mediaUris
import com.zsqw123.mediastore.storageContext
import kotlinx.coroutines.*
import java.io.File
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

/**
 * @property type Int 类型, 详见 TypeInt
 * @property mainPath String 文件夹路径,
 * 如下载文件夹路径是 Dowload, 参见 Environment.STANDARD_DIRECTORIES
 * @property mimeType String 不必要, 事实上 mimeType 只会作为参考,
 * 仅在不能通过保存文件名识别出文件类型的时候才会用到
 *
 * @see MediaType
 * @see Environment
 *
 * Author zsqw123
 * Create by damyjy
 * Date 2021/7/5 21:08
 */
open class FileSave(
    var inputStream: InputStream? = null,
    @MediaType var type: Int = MediaType.TYPE_DOWNLOAD,
    var mainPath: String = Environment.DIRECTORY_DOWNLOADS,
    var mimeType: String = "",
) : MediaSave {
    var suspendFile: Deferred<File>? = null

    companion object {
        operator fun invoke(file: File) = FileSave(file.inputStream())

        /**
         * 涉及到了新建文件, 强烈建议此方法在子线程中执行, 这里没有强制
         * @see WorkerThread
         */
        operator fun invoke(string: String, charset: Charset = Charsets.UTF_8) = FileSave(string.toByteArray(charset))

        /**
         * 涉及到了新建文件, 强烈建议此方法在子线程中执行, 这里没有强制
         * @see WorkerThread
         */
        operator fun invoke(bytes: ByteArray): FileSave {
            val save = FileSave()
            save.suspendFile = GlobalScope.async(Dispatchers.IO) {
                MediaSave.dupCreateFile(storageContext.cacheDir, Date().time.toString()).apply { writeBytes(bytes) }
            }
            return save
        }
    }

    override suspend fun save(name: String, subPath: String, contentValues: ContentValues): Boolean = withContext(Dispatchers.IO) {
        try {
            inputStream = inputStream ?: suspendFile?.await()?.inputStream() ?: return@withContext false
            MediaSave.commonMediaSave(
                name, mainPath, subPath, mediaUris[type],
                inputStream!!, ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    putAll(contentValues)
                })
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}