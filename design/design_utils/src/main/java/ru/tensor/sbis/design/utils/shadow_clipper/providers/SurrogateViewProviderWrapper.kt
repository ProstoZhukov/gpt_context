package ru.tensor.sbis.design.utils.shadow_clipper.providers

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

/**
 * Клас [ViewOutlineProvider] для "сурогатной"-view тени, который имитирует [ViewOutlineProvider] оригинальной view.
 *
 * @property provider  - [ViewOutlineProvider] оригинальной view.
 * @property surrogate - экземпляр оригинальной view.
 *
 * @author ra.geraskin
 */
internal class SurrogateViewProviderWrapper(
    private val provider: ViewOutlineProvider,
    private val surrogate: View
) : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        provider.getOutline(surrogate, outline)
    }
}
