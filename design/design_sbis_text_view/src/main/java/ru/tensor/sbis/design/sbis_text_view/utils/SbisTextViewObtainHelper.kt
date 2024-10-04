package ru.tensor.sbis.design.sbis_text_view.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Typeface
import android.os.Build
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.sbis_text_view.R
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * Вспомогательная реализация [SbisTextView] для чтения значений атрибутов текста.
 *
 * @author vv.chekurda
 */
object SbisTextViewObtainHelper {

    private var textAppearanceCache = hashMapOf<Int, TextAppearanceData>()
    private var fontScale: Float? = null

    /**
     * Инициализировать.
     */
    fun init(configuration: Configuration) {
        fontScale = configuration.fontScale
    }

    /**
     * Обработать изменение конфигурации приложения.
     */
    fun onConfigurationChanged(config: Configuration) {
        if (fontScale != config.fontScale) {
            fontScale = config.fontScale
            clear()
        }
    }

    /**
     * Очистить ресурсы.
     */
    fun clear() {
        textAppearanceCache.clear()
    }

    /**
     * Получить внешний вид текста в виде [TextAppearanceData] с проверкой на существование стиля в хэше.
     * Если стиль существует в хэше, то значение извлекается из него.
     *
     * @param context контекст.
     * @param currentTypeface текущий шрифт текста.
     * @param style ресурс стиля, который необходимо применить к тексту.
     *
     * @return [TextAppearanceData] внешний вид текста.
     */
    internal fun getTextAppearance(
        context: Context,
        currentTypeface: Typeface?,
        @StyleRes style: Int
    ): TextAppearanceData {
        val cachedStyle = textAppearanceCache[style]
        return if (cachedStyle == null) {
            var data: TextAppearanceData? = null
            context.withStyledAttributes(resourceId = style, attrs = R.styleable.SbisTextView) {
                val textSize = getDimensionPixelSize(R.styleable.SbisTextView_android_textSize, 0)
                    .takeIf { it != 0 }
                val colorStateList = getColorStateList(
                    context,
                    this,
                    R.styleable.SbisTextView_android_textColor
                )
                val color = colorStateList?.defaultColor
                    ?: getColor(R.styleable.SbisTextView_android_textColor, NO_RESOURCE)
                        .takeIf { it != NO_RESOURCE }
                val linkColorStateList = getColorStateList(
                    context,
                    this,
                    R.styleable.SbisTextView_android_textColorLink
                )
                val fontFamily = getResourceId(R.styleable.SbisTextView_android_fontFamily, NO_RESOURCE)
                    .takeIf { it != NO_RESOURCE }
                val textStyle = getInt(R.styleable.SbisTextView_android_textStyle, NO_RESOURCE)
                    .takeIf { it != NO_RESOURCE }
                val typeface = getTypeface(context, currentTypeface, fontFamily, textStyle)
                val allCaps = getBoolean(R.styleable.SbisTextView_android_textAllCaps, false)

                data = TextAppearanceData(
                    textSize = textSize?.toFloat(),
                    typeface = typeface,
                    color = color,
                    colorStateList = colorStateList,
                    linkColorStateList = linkColorStateList,
                    allCaps = allCaps
                ).also {
                    textAppearanceCache[style] = it
                }
            }
            requireNotNull(data)
        } else {
            cachedStyle
        }
    }

    /**
     * Получить шрифт текста [Typeface] или null.
     *
     * @param context контекст.
     * @param currentTypeface текущий шрифт текста.
     * @param fontFamily семейство шрифта текста.
     * @param textStyle стиль текста.
     *
     * @return шрифт текста или null, если fontFamily=null или textStyle=null.
     */
    internal fun getTypeface(
        context: Context,
        currentTypeface: Typeface?,
        fontFamily: Int?,
        textStyle: Int?
    ): Typeface? =
        when {
            fontFamily != null -> {
                try {
                    val typeface = ResourcesCompat.getFont(context, fontFamily)
                    if (textStyle != null) {
                        Typeface.create(typeface, textStyle)
                    } else {
                        typeface
                    }
                } catch (ex: Resources.NotFoundException) {
                    // Expected if it is not a font resource.
                    val familyName = context.resources.getString(fontFamily)
                    Typeface.create(familyName, textStyle ?: Typeface.NORMAL)
                }
            }
            textStyle != null -> {
                Typeface.create(currentTypeface, textStyle)
            }
            else -> null
        }

    /**
     * Получить список состояний для цвета текста [ColorStateList] или null.
     *
     * @param context контекст.
     * @param typedArray список ресурсов.
     * @param attr стилевой ресурс.
     *
     * @return список состояний для цвета текста или null, если attr=null.
     */
    internal fun getColorStateList(
        context: Context,
        typedArray: TypedArray,
        @StyleableRes attr: Int
    ): ColorStateList? = with(typedArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getColorStateList(attr)
        } else {
            getResourceId(attr, NO_RESOURCE)
                .takeIf { it != NO_RESOURCE }
                ?.let { ContextCompat.getColorStateList(context, it) }
                ?: getColorStateList(attr)
        }
    }
}

/**
 * Описывает внешний вид текста.
 *
 * @property textSize размер текста.
 * @property typeface шрифт текста.
 * @property color цвет текста.
 * @property colorStateList список состояний для цвета текста.
 * @property linkColorStateList список состояний для цвета ссылок.
 * @property allCaps все ли символы заглавные.
 */
internal data class TextAppearanceData(
    val textSize: Float?,
    val typeface: Typeface?,
    val color: Int?,
    val colorStateList: ColorStateList?,
    val linkColorStateList: ColorStateList?,
    val allCaps: Boolean?
)

private const val NO_RESOURCE = -1