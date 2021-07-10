package com.zsqw123.mediastore.read

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.util.ArrayMap
import com.zsqw123.mediastore.MediaParams
import com.zsqw123.mediastore.MediaType
import com.zsqw123.mediastore.mediaUris

/**
 * Author zsqw123
 * Create by damyjy
 * Date 2021/7/5 22:25
 */
class AudioRead @JvmOverloads constructor(
    var name: String = "",
    var mimeType: String = "audio/*",
    var size: Int = 0, // 文件尺寸 bytes
    var dateAdded: Int = 0, // seconds
    var dateModified: Int = 0, // seconds
    var duration: Int = 0, // seconds
    var others: Map<String, String> = ArrayMap()
) : MediaRead {
    lateinit var uri: Uri

    override fun readFromCursor(cursor: Cursor, params: Array<String>, paramIndices: IntArray): MediaRead = apply {
        val othersMap = ArrayMap<String, String>()
        val id = cursor.getColumnIndexOrThrow(MediaParams.ID)
        for (i in paramIndices.indices) when (params[i]) {
            MediaParams.ID -> uri = ContentUris.withAppendedId(mediaUris[MediaType.TYPE_AUDIO], cursor.getLong(id))
            MediaParams.DISPLAY_NAME -> name = cursor.getString(paramIndices[i])
            MediaParams.DATE_ADDED -> dateAdded = cursor.getInt(paramIndices[i])
            MediaParams.DATE_MODIFIED -> dateModified = cursor.getInt(paramIndices[i])
            MediaParams.DURATION -> duration = cursor.getInt(paramIndices[i])
            MediaParams.MIME_TYPE -> mimeType = cursor.getString(paramIndices[i])
            MediaParams.SIZE -> size = cursor.getInt(paramIndices[i])
            else -> othersMap[params[i]] = cursor.getString(paramIndices[i])
        }
        others = othersMap
    }

    companion object {
        suspend fun read(
            filter: String? = null, sortBy: String = MediaParams.DATE_MODIFIED, isAscend: Boolean = false, otherParams: Array<String> = arrayOf()
        ): List<AudioRead> = MediaRead.read(defParams + otherParams, filter, sortBy, isAscend)

        suspend fun read(uri: Uri, otherParams: Array<String> = arrayOf()): AudioRead = MediaRead.read(uri, defParams + otherParams)

        private val defParams = arrayOf(
            MediaParams.ID,
            MediaParams.DATE_ADDED,
            MediaParams.DATE_MODIFIED,
            MediaParams.DISPLAY_NAME,
            MediaParams.DURATION,
            MediaParams.MIME_TYPE,
            MediaParams.SIZE,
        )
    }
}