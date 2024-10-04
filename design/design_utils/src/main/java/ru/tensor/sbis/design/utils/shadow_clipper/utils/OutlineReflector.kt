package ru.tensor.sbis.design.utils.shadow_clipper.utils

import android.annotation.SuppressLint
import android.graphics.Outline
import android.graphics.Rect
import java.lang.reflect.Field

/**
 * Объект, предоставляющий доступ к полям mRect и mRadius класса [Outline] с использованием рефлексии.
 *
 * Использование рефлексии обусловлено тем, что параметры mRect и mRadius являются приватными для API версии 23 и ниже.
 * В более поздних версиях эти поля являются публичными и использование рефлексии не требуется.
 * Перед каждым использованием [OutlineReflector] , производится проверка текущей версии android.
 *
 * @author ra.geraskin
 */
@SuppressLint("DiscouragedPrivateApi", "SoonBlockedPrivateApi")
internal object OutlineReflector {

    private val mRectField: Field by lazy { Outline::class.java.getDeclaredField("mRect") }

    private val mRadiusField: Field by lazy { Outline::class.java.getDeclaredField("mRadius") }

    /** @SelfDocumented */
    val isValid = try {
        mRectField
        mRadiusField
        true
    } catch (e: Error) {
        false
    }

    /** @SelfDocumented */
    fun getRect(outline: Outline, outRect: Rect): Boolean {
        val rect = mRectField.get(outline) as Rect?
        return rect?.let {
            outRect.set(rect)
            true
        } ?: false
    }

    /** @SelfDocumented */
    fun getRadius(outline: Outline) = mRadiusField.getFloat(outline)
}