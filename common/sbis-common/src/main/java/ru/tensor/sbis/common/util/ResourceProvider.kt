package ru.tensor.sbis.common.util

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.plugin_struct.feature.Feature
import javax.inject.Inject

/**
 * Класс-обертка над ApplicationContext для предоставления ресурсов по id
 */
open class ResourceProvider @Inject constructor(context: Context) : Feature {

    val mContext = context.applicationContext

    fun getString(@StringRes resId: Int): String = mContext.getString(resId)

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String = mContext.getString(resId, *formatArgs)

    fun getStringArray(@ArrayRes resId: Int): Array<out String> = mContext.resources.getStringArray(resId)

    fun <T> runWithTypedArray(@ArrayRes resId: Int, action: TypedArray.() -> T): T {
        val typedArray = mContext.resources.obtainTypedArray(resId)
        return typedArray.action()
            .also { typedArray.recycle() }
    }

    @ColorInt
    fun getColor(@ColorRes resId: Int) = ContextCompat.getColor(mContext, resId)

    fun getDimensionPixelSize(@DimenRes resId: Int) = mContext.resources.getDimensionPixelSize(resId)

    /**
     * Возвращает измерение, объявленное в ресурсах в виде числа с плавающей точкой без ед. измерения. Пример объявления ресурса:
     * <dimen name="my_float_dimension" format="float" type="dimen">1.35</dimen>
     * @param resId идентификатор ресурса
     */
    fun getFloatDimension(@DimenRes resId: Int): Float {
        val outValue = TypedValue()
        mContext.resources.getValue(resId, outValue, true)
        return outValue.float
    }

    fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any) = mContext.resources.getQuantityString(
        resId,
        quantity,
        *formatArgs
    )

    fun getBoolean(@BoolRes resId: Int) = mContext.resources.getBoolean(resId)

    fun getFont(@FontRes resId: Int) = ResourcesCompat.getFont(mContext, resId)

    fun getDrawable(@DrawableRes resId: Int) = ResourcesCompat.getDrawable(mContext.resources, resId, null)

    fun getDisplayMetrics(): DisplayMetrics = mContext.resources.displayMetrics

    @Throws(Resources.NotFoundException::class)
    fun getInteger(@IntegerRes resId: Int): Int = mContext.resources.getInteger(resId)

    fun getOrientation() = mContext.resources.configuration.orientation

    fun getPackageName(): String = mContext.packageName

    /**
     * Расширение для получения цвета из атрибута
     */
    @ColorInt
    fun getColorFromAttr(@AttrRes colorAttr: Int, @IntegerRes theme: Int): Int {
        val themeForResolve = mContext.resources.newTheme()
        themeForResolve.applyStyle(theme, true)

        val typedValue = TypedValue()
        themeForResolve.resolveAttribute(colorAttr, typedValue, true)
        return if (typedValue.resourceId != ID_NULL) {
            mContext.getCompatColor(typedValue.resourceId)
        } else {
            typedValue.data
        }
    }
}