package com.zsqw123.mediastore.save

import android.os.Environment
import androidx.annotation.WorkerThread
import com.zsqw123.mediastore.MediaType
import com.zsqw123.mediastore.storageContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File
import java.io.InputStream
import java.util.*

/**
 * Author zsqw123
 * Create by damyjy
 * Date 2021/7/7 0:42
 */
/**
 * 事实上 mimeType 只会作为参考,
 * 仅在不能通过保存文件名识别出文件类型的时候才会用到
 */
class AudioSave(
    inputStream: InputStream? = null,
    @MediaType type: Int = MediaType.TYPE_AUDIO,
    mainPath: String = Environment.DIRECTORY_MUSIC,
    mimeType: String = "",
) : FileSave(inputStream, type, mainPath, mimeType) {
    companion object {
        operator fun invoke(file: File) = AudioSave(file.inputStream())

        /**
         * 涉及到了新建文件, 强烈建议此方法在子线程中执行, 这里没有强制
         * @see WorkerThread
         */
        operator fun invoke(bytes: ByteArray): AudioSave {
            val save = AudioSave()
            save.suspendFile = GlobalScope.async(Dispatchers.IO) {
                MediaSave.dupCreateFile(storageContext.cacheDir, Date().time.toString() + ".mp3").apply { writeBytes(bytes) }
            }
            return save
        }
    }
}