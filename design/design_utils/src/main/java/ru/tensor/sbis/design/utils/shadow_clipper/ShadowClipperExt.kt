package ru.tensor.sbis.design.utils.shadow_clipper

import android.view.View
import android.graphics.Outline
import ru.tensor.sbis.design.utils.extentions.doOnAttachToWindow
import ru.tensor.sbis.design.utils.extentions.doOnDetachedFromWindow
import ru.tensor.sbis.design.utils.shadow_clipper.utils.attachShadow
import ru.tensor.sbis.design.utils.shadow_clipper.utils.detachShadow

/**
 * Метод обрезает тень по контору, вычисляемому из [Outline].
 * Позволяет использовать тень для view с полупрозрачным или прозрачным фоном без отображения "теневых" артефактов.
 *
 * @author ra.geraskin
 */
@Suppress("unused")
fun View.clipOutlineShadow() {
    if (isAttachedToWindow) attachShadow(this)
    else doOnAttachToWindow(::attachShadow)
    doOnDetachedFromWindow(::detachShadow)
}