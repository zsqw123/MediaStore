package com.zsqw123.mediastore.save

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.zsqw123.mediastore.MediaParams
import com.zsqw123.mediastore.storageContext
import com.zsqw123.mediastore.typeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

/**
 * Author zsqw123
 * Create by damyjy
 * Date 2021/7/5 9:39
 */
interface MediaSave {
    /**
     * 假设图片要保存到 /Pictures/App/Pic/1.jpg, 那么:
     * @param name 1.jpg
     * @param subPath App/Pic
     *
     * 主路径则由实现接口的类型提供
     *
     * @return Boolean
     */
    suspend fun save(name: String = Date().time.toString(), subPath: String = "", contentValues: ContentValues = ContentValues()): Boolean

    companion object {
        suspend fun commonMediaSave(
            name: String, mainPath: String, subPath: String, contentUri: Uri,
            inputStream: InputStream, contentValues: ContentValues
        ): Boolean = withContext(Dispatchers.IO) {
            val autoType = typeMap[name]
            if (autoType.isNotBlank()) contentValues.put(MediaParams.MIME_TYPE, autoType)
            @Suppress("DEPRECATION", "BlockingMethodInNonBlockingContext")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val parent = File(Environment.getExternalStorageDirectory().absolutePath + "/$mainPath/$subPath")
                val file = dupCreateFile(parent, name)
                val fos = FileOutputStream(file)
                inputStream.copyTo(fos)
                contentValues.put(MediaStore.MediaColumns.DATA, file.absolutePath)
                val uri = storageContext.contentResolver.insert(contentUri, contentValues)
                val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                intent.data = uri
                storageContext.sendBroadcast(intent)
            } else {
                contentValues.apply {
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "$mainPath/$subPath")
                }
                val item = storageContext.contentResolver.insert(contentUri, contentValues) ?: return@withContext false
                storageContext.contentResolver.openFileDescriptor(item, "w", null).use { pfd ->
                    if (pfd == null) return@withContext false
                    val out = FileOutputStream(pfd.fileDescriptor)
                    inputStream.copyTo(out)
                }
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                storageContext.contentResolver.update(item, contentValues, null, null)
            }
            return@withContext true
        }

        fun dupCreateFile(parent: File, child: String): File {
            if (!parent.exists() && !parent.mkdirs()) throw NoSuchFileException(parent, reason = "parent file couldn't be found/created.")
            var cFile = File(parent, child)
            val main = cFile.nameWithoutExtension
            val ex = "." + cFile.extension
            var i = 1
            while (cFile.exists())
                cFile = File(parent, main + i++ + ex)
            println(cFile.absolutePath)
            if (!cFile.createNewFile()) throw NoSuchFileException(cFile, reason = "child file couldn't be created.")
            return cFile
        }
    }
}
