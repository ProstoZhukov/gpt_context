package ru.tensor.sbis.mediaplayer

import android.net.Uri
import android.os.Parcelable
import androidx.media3.common.C
import kotlinx.parcelize.Parcelize
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * Информация о медиафайле
 *
 * @property uri        URI, ссылающийся на источник медифайла
 * @property extension  Расширение медиафайла без точки. Используется для определения типа медиафайла.
 *                      Следует передавать, если uri не содержит расширения
 * @property isHls      true если точно известо что тип потока hls
 *
 * @author sa.nikitin
 */
@Parcelize
data class MediaInfo(
    val uri: Uri,
    val extension: String = "",
    val isHls: Boolean = false,
    val startPositionMs: Long = C.TIME_UNSET
) : Parcelable {

    companion object {

        fun create(uri: Uri, name: String): MediaInfo = MediaInfo(uri, getFileExtension(name, false))

        /*
        копия метода из ru.tensor.sbis.core.util.FileUtil,
        т.к. FileUtil был перемещен в ru.tensor.sbis.common.util.FileUtil
        подключать тяжелый модуль common ради одного метода нерационально.
        */
        private fun getFileExtension(fileName: String, withDot: Boolean): String {
            val lastDotIndex = fileName.lastIndexOf(".")
            return if (lastDotIndex == -1) {
                StringUtils.EMPTY
            } else {
                val substringBeginIndex = if (withDot) lastDotIndex else lastDotIndex + 1
                if (substringBeginIndex > fileName.length)
                    StringUtils.EMPTY
                else
                    fileName.substring(substringBeginIndex, fileName.length).lowercase(Locale.getDefault())
            }
        }
    }
}