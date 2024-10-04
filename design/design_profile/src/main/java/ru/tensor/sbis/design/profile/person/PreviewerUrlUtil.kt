/**
 * Упрощённая реализация инструментов для работы со ссылками для сервиса previewer. Полная поддержка реализована в
 * классе PreviewerUrlUtil.
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.profile.person

import androidx.annotation.Px
import ru.tensor.sbis.design.utils.requireSafe
import timber.log.Timber

/**
 * Результат подстановки размеров в url изображения.
 *
 * @property url ссылка на изображение, возможно с подставленными размерами
 * @property isSizeSet `true` если подстановка успешна
 *
 * @author us.bessonov
 */
internal data class PreviewerUrlSizeSetResult(val url: String, val isSizeSet: Boolean)

/**
 * Замена/установка размеров изображения в ссылке, содержащей [PREVIEWER_SERVICE_NAME].
 * Предварительно проверяется содержание в ссылке подстроки [PREVIEWER_SERVICE_NAME].
 *
 * @param url       ссылка на изображение
 * @param width     ширина
 * @param height    высота
 *
 * @return ссылка на изображение указанных размеров и флаг успешности применения размеров.
 */
internal fun replacePreviewerUrlPartWithCheck(
    url: String,
    @Px width: Int,
    @Px height: Int
): PreviewerUrlSizeSetResult {
    val isSizeSet = PREVIEWER_SUPPORTED_MODE_REGEX.containsMatchIn(url)
    requireSafe(!isSizeSet || width > 0 && height > 0) {
        "Image dimensions should be greater than 0 (w: $width, h: $height)"
    }
    val resultUrl = when {
        isSizeSet -> insertSizeInImageUrl(url, width, height)
        PREVIEWER_UNSUPPORTED_MODE_REGEX.containsMatchIn(url) -> {
            Timber.w(IllegalArgumentException("Unsupported scale mode in $url"))
            url
        }

        else -> url
    }
    return PreviewerUrlSizeSetResult(resultUrl, isSizeSet)
}

/**
 * Проверяет, содержит ли ссылка шаблон для подстановки размеров изображения.
 */
internal fun hasPreviewSizeTemplate(url: String) = PREVIEWER_SUPPORTED_MODE_REGEX.containsMatchIn(url)

/**
 * Установка новых размеров изображения.
 *
 * @param imageUrl ссылка, содержащая [PREVIEWER_SERVICE_NAME]
 * @param width    ширина
 * @param height   высота
 *
 * @return ссылка на изображение с указанными размерами.
 */
private fun insertSizeInImageUrl(imageUrl: String, @Px width: Int, @Px height: Int): String =
    imageUrl.replaceFirst(PREVIEWER_UNKNOWN_SIZES, "$width/$height")

private const val PREVIEWER_SERVICE_NAME = "previewer"
private const val SUPPORTED_SCALE_MODES = "[rcm]"

/** @SelfDocumented */
internal const val PREVIEWER_UNKNOWN_SIZES = "%d/%d"
private val PREVIEWER_SUPPORTED_MODE_REGEX =
    "/$PREVIEWER_SERVICE_NAME/$SUPPORTED_SCALE_MODES/$PREVIEWER_UNKNOWN_SIZES".toRegex()
private val PREVIEWER_UNSUPPORTED_MODE_REGEX = "/$PREVIEWER_SERVICE_NAME/./$PREVIEWER_UNKNOWN_SIZES".toRegex()
