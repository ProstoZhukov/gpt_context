package ru.tensor.sbis.design.utils

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

/**
 * [ViewOutlineProvider], устанавливающий в качестве [Outline] нижнюю половину view. Применимо в случае, когда
 * требуется чтобы тень view не отображалась сверху, а перекрывалась его верхней половиной
 *
 * @author us.bessonov
 */
class HalfHeightViewOutlineProvider : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) = with(view) {
        outline.setRect(0, height / 2, width, height)
    }
}