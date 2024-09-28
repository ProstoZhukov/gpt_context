package ru.tensor.sbis.design.utils.shadow_clipper.providers

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

/**
 * Клас-обёртка над [ViewOutlineProvider] оригинального view.
 * Обёртка делает невидимой собственную тень view-компонента, а так же передаёт обратный вызов функции
 * getOutline(view, outline) наружу.
 *
 * @property wrapped  - [ViewOutlineProvider] оригинального view.
 * @property callback - внешний метод для перехвата вызова getOutline.
 *
 * @author ra.geraskin
 */
internal class ProviderWrapper(
    private val wrapped: ViewOutlineProvider,
    private val callback: (Outline) -> Unit
) : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        wrapped.getOutline(view, outline)
        callback(outline)
        outline.alpha = 0.0F
    }
}