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
 * Date 2021/7/7 0:46
 */
/**
 * 事实上 mimeType 只会作为参考,
 * 仅在不能通过保存文件名识别出文件类型的时候才会用到
 */
class VideoSave(
    inputStream: InputStream? = null,
    @MediaType type: Int = MediaType.TYPE_VIDEO,
    mainPath: String = Environment.DIRECTORY_MOVIES,
    mimeType: String = "",
) : FileSave(inputStream, type, mainPath, mimeType) {
    companion object {
        operator fun invoke(file: File) = VideoSave(file.inputStream())

        /**
         * 涉及到了新建文件, 强烈建议此方法在子线程中执行, 这里没有强制
         * @see WorkerThread
         */
        operator fun invoke(bytes: ByteArray): VideoSave {
            val save = VideoSave()
            save.suspendFile = GlobalScope.async(Dispatchers.IO) {
                MediaSave.dupCreateFile(storageContext.cacheDir, Date().time.toString() + ".mp4").apply { writeBytes(bytes) }
            }
            return save
        }
    }
}