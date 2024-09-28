package ru.tensor.sbis.business.common.ui.utils

import androidx.annotation.Px
import com.google.gson.JsonObject
import ru.tensor.sbis.common.util.UrlUtils

private const val PHOTO_BY_ID_METHOD = "ProfileServiceMobile.PhotoById"
private const val PHOTO_ID_PROPERTY = "Id"
private const val SERVICE_SEGMENT = "service"

object PhotoUrlUtils {

    /**
     * Формирует URL фото
     *
     * @param photoId id фото
     * @param size желаемый размер фото
     * @return URL фото
     */
    @JvmStatic
    fun getPhotoUrlById(photoId: String, @Px size: Int): String {
        val json = JsonObject()
        json.addProperty(PHOTO_ID_PROPERTY, photoId)
        return UrlUtils.buildPreviewUrlByMethod(SERVICE_SEGMENT, PHOTO_BY_ID_METHOD, json, 0, size, size)
                ?: ""
    }
}
