package ru.tensor.sbis.viewer.decl.slider

import android.view.View

/**
 * Параметры вью контрола слайдера просмотрщиков (тулбар, барабан превью и т.д.
 */
data class ViewerSliderControlViewParams(
    val viewHeight: Float,
    val viewTranslationY: Float
)

/**
 * Рассчитать [View.getBottom] позицию вью контрола относительно верха экрана
 * Например, тулбар разположен вверху экрана, метод вернет его [View.getBottom] с учетом [ViewerSliderControlViewParams.viewTranslationY]
 */
fun ViewerSliderControlViewParams.calculateBottomFromTop(): Float = viewTranslationY + viewHeight

/**
 * Рассчитать [View.getTop] позицию вью контрола относительно низа экрана
 * Например, барабан превью разположен снизу экрана, метод вернет его [View.getTop] с учетом [ViewerSliderControlViewParams.viewTranslationY]
 */
fun ViewerSliderControlViewParams.calculateTopFromBottom(): Float = viewTranslationY - viewHeight
