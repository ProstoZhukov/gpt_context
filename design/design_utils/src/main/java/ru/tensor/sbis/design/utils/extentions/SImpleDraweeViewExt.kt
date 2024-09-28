/**
 * Расширения SimpleDraweeView
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.utils.extentions

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder.DEFAULT_FADE_DURATION
import com.facebook.drawee.view.SimpleDraweeView

/**
 * Отключает анимацию появления изображения
 */
fun SimpleDraweeView.disableFadeAnimation() {
    hierarchy.fadeDuration = 0
}

fun SimpleDraweeView.enableFadeAnimation() {
    hierarchy.fadeDuration = DEFAULT_FADE_DURATION
}