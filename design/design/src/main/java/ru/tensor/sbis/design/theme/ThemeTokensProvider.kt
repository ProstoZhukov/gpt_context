package ru.tensor.sbis.design.theme

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources.NotFoundException
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.IntegerRes
import androidx.annotation.Px
import ru.tensor.sbis.design.theme.utils.getDimen
import ru.tensor.sbis.design.theme.utils.getDimenPx
import ru.tensor.sbis.design.theme.utils.getFontFromTheme
import ru.tensor.sbis.design.theme.utils.getThemeColorInt
import ru.tensor.sbis.design.theme.utils.getThemeDrawable
import ru.tensor.sbis.design.theme.utils.getThemeInteger

/**
 * Провайдер значений токенов design system с возможностью кэширования.
 *
 * @author ps.smirnyh
 */
object ThemeTokensProvider {
    private val themeCache = hashMapOf<Int, GlobalAttributesCache>()
    private var fontScale = 0f

    /*
       TODO Включение кэша повлекло за собой серию ошибок связанных с некорректными цветами в некоторых компонентах
        на некоторых устройствах и эмуляторах. В некоторых местах помогло изменение алгоритма вычисления hashCode для
        конкретного набора тем (context.theme.hashCode() -> context.theme.toString().hashCode()) в методе getCache().
        Однако остались случаи где эта модификация не помогла:
           https://online.sbis.ru/opendoc.html?guid=5f2a0043-17d3-408c-b8ba-19d3f9d63bea&client=3
           https://dev.sbis.ru/opendoc.html?guid=db4694fd-852d-456a-a8ae-401ec05d0f97&client=3
        Для избежания возникновения дополнительных возможных ошибок, принято решение отключить кэширование до момента,
        когда разработчик системы локализует и устранит проблему.
     */
    /**
     * Получить цвет по атрибуту из текущей темы.
     */
    @ColorInt
    fun getColorInt(context: Context, @AttrRes attr: Int): Int = context.getThemeColorInt(attr)
//        getCache(context).getColorInt(context, attr)

    /**
     * Получить размер из темы по идентификатору атрибута [attr] в пикселях.
     *
     * @throws NotFoundException если [attr] не найден в теме.
     */
    @Px
    fun getDimenPx(context: Context, @AttrRes attr: Int): Int = context.getDimenPx(attr)
//        getCache(context).getDimenPx(context, attr)

    /**
     * Получить размер из темы по идентификатору атрибута [attr].
     *
     * @throws NotFoundException если [attr] не найден в теме.
     */
    @Dimension
    fun getDimen(context: Context, @AttrRes attr: Int): Float = context.getDimen(attr)
//        getCache(context).getDimen(context, attr)

    /**
     * Получить идентификатор шрифта из атрибута из текущей темы.
     */
    @FontRes
    fun getFontRes(context: Context, @AttrRes attr: Int): Int = context.getFontFromTheme(attr)
//        getCache(context).getFontRes(context, attr)

    /**
     * Получить целочисленное значение по атрибуту из текущей темы.
     */
    @IntegerRes
    fun getInteger(context: Context, @AttrRes attr: Int): Int = context.getThemeInteger(attr)
//        getCache(context).getInteger(context, attr)

    /**
     * Получить Drawable Id из темы по идентификатору атрибута [attr].
     */
    @DrawableRes
    fun getDrawableRes(context: Context, @AttrRes attr: Int): Int = context.getThemeDrawable(attr)
//        getCache(context).getDrawableRes(context, attr)

    /** Задать начальную конфигурацию для отслеживания изменений. */
    fun init(configuration: Configuration) {
        fontScale = configuration.fontScale
    }

    /** Очистить кэш размеров при изменении размера шрифта в системе. */
    fun onConfigurationChanged(configuration: Configuration) {
        if (fontScale == configuration.fontScale) {
            return
        }
        fontScale = configuration.fontScale
        themeCache.values.forEach {
            it.onConfigurationChanged()
        }
    }

    /** Сбросить кэш значений токенов темы. */
    fun invalidateCache() {
        themeCache.clear()
    }

    private fun getCache(context: Context) =
        themeCache.getOrPut(context.theme.toString().hashCode()) {
            GlobalAttributesCache()
        }

}

/**
 * Класс для получения значений из токенов design system и их кэширования.
 *
 * @author ps.smirnyh
 */
private class GlobalAttributesCache {

    private val colorAttsCache = hashMapOf<Int, Int>()

    private val sizePxAttsCache = hashMapOf<Int, Int>()

    private val sizeDimenAttsCache = hashMapOf<Int, Float>()

    private val fontAttsCache = hashMapOf<Int, Int>()

    private val integerAttsCache = hashMapOf<Int, Int>()

    private val drawableAttsCache = hashMapOf<Int, Int>()

    /** @SelfDocumented */
    @ColorInt
    fun getColorInt(context: Context, @AttrRes attr: Int): Int =
        colorAttsCache.getOrPut(attr) {
            context.getThemeColorInt(attr)
        }

    /** @SelfDocumented */
    @Px
    fun getDimenPx(context: Context, @AttrRes attr: Int): Int =
        sizePxAttsCache.getOrPut(attr) {
            context.getDimenPx(attr)
        }

    /** @SelfDocumented */
    @Dimension
    fun getDimen(context: Context, @AttrRes attr: Int): Float =
        sizeDimenAttsCache.getOrPut(attr) {
            context.getDimen(attr)
        }

    /** @SelfDocumented */
    @FontRes
    fun getFontRes(context: Context, @AttrRes attr: Int): Int =
        fontAttsCache.getOrPut(attr) {
            context.getFontFromTheme(attr)
        }

    /** @SelfDocumented */
    @IntegerRes
    fun getInteger(context: Context, @AttrRes attr: Int): Int =
        integerAttsCache.getOrPut(attr) {
            context.getThemeInteger(attr)
        }

    /** @SelfDocumented */
    @DrawableRes
    fun getDrawableRes(context: Context, @AttrRes attr: Int): Int =
        drawableAttsCache.getOrPut(attr) {
            context.getThemeDrawable(attr)
        }

    /** Очистить кэш размеров при изменении размера шрифта в системе. */
    fun onConfigurationChanged() {
        sizePxAttsCache.clear()
        sizeDimenAttsCache.clear()
    }

}