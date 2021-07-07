package com.zsqw123.mediastore

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.SparseArray
import androidx.annotation.IntDef
import com.zsqw123.mediastore.MediaType.Companion.TYPE_AUDIO
import com.zsqw123.mediastore.MediaType.Companion.TYPE_DOWNLOAD
import com.zsqw123.mediastore.MediaType.Companion.TYPE_FILE
import com.zsqw123.mediastore.MediaType.Companion.TYPE_IMAGE
import com.zsqw123.mediastore.MediaType.Companion.TYPE_VIDEO

/**
 * Author zsqw123
 * Create by damyjy
 * Date 2021/7/5 9:40
 */
object MediaParams {
    const val ID = "_id"
    const val DATE_ADDED = "date_added"
    const val DATE_MODIFIED = "date_modified"
    const val DISPLAY_NAME = "_display_name"
    const val DURATION = "duration"
    const val HEIGHT = "height"
    const val MIME_TYPE = "mime_type"
    const val ORIENTATION = "orientation"
    const val RELATIVE_PATH = "relative_path"
    const val SIZE = "_size"
    const val WIDTH = "width"
}

@IntDef(TYPE_AUDIO, TYPE_VIDEO, TYPE_FILE, TYPE_DOWNLOAD, TYPE_IMAGE)
@Retention(AnnotationRetention.SOURCE)
annotation class MediaType {
    companion object {
        const val TYPE_VIDEO = 0
        const val TYPE_AUDIO = 1
        const val TYPE_FILE = 2
        const val TYPE_DOWNLOAD = 3
        const val TYPE_IMAGE = 4
    }
}

val mediaUris = SparseArray<Uri>().apply {
    put(TYPE_AUDIO, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
    put(TYPE_VIDEO, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
    put(TYPE_IMAGE, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    val downloadUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Downloads.EXTERNAL_CONTENT_URI else MediaStore.Files.getContentUri("external")
    put(TYPE_FILE, downloadUri)
    put(TYPE_DOWNLOAD, downloadUri)
}