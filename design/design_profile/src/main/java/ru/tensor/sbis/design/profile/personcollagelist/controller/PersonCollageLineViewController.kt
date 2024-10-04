package ru.tensor.sbis.design.profile.personcollagelist.controller

import android.graphics.Canvas
import android.view.View
import androidx.annotation.Px
import ru.tensor.sbis.design.profile.personcollagelist.PersonCollageLineView
import ru.tensor.sbis.design.profile.personcollagelist.collagechildrenmanager.PersonCollageLineViewChildrenManager

/** @SelfDocumented */
internal data class MeasuredDimension(@Px val measuredWidth: Int, @Px val measuredHeight: Int)

/**
 * Контракт контроллера [PersonCollageLineView].
 *
 * @author us.bessonov
 */
internal interface PersonCollageLineViewController :
    PersonCollageLineViewApi,
    PersonCollageLineViewChildrenManager {

    /**
     * Возвращает [MeasuredDimension], значения которого должны быть переданы в [View.setMeasuredDimension].
     */
    fun performMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int, @Px minWidth: Int): MeasuredDimension

    /** @SelfDocumented */
    fun performLayout()

    /** @SelfDocumented */
    fun performDraw(canvas: Canvas)

    /** @SelfDocumented */
    fun performInvalidate()

    /** @SelfDocumented */
    fun onVisibilityAggregated(isVisible: Boolean)
}