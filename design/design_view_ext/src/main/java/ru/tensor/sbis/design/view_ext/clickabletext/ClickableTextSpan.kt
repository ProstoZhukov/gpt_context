package ru.tensor.sbis.design.view_ext.clickabletext

import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.ColorInt

/**
 * Реализация ClickableSpan для выделения кликабельного текста и обработки кликов через [clickListener] и [id] текста
 *
 * Отсутствует подчёркивание кликабельного текста. Выделение происходит за счёт цвета,
 * который можно установить через [linkColorInt], android:textColorLink у TextView, либо через [ForegroundColorSpan]
 *
 * Для отслеживания кликов необходимо в качестве [MovementMethod] установить [LinkMovementMethod]
 * у TextView, к тексту которой применяется данный span
 *
 * @property id             Идентификатор кликабельного текста
 * @property clickListener  Слушатель кликов
 * @property linkColorInt   Цвет ссылки
 *
 * @author sa.nikitin
 */
@Suppress("unused")
class ClickableTextSpan(
    private val id: Int,
    private val clickListener: IdentifiableTextClickListener,
    @ColorInt
    private val linkColorInt: Int? = null
) : ClickableSpan() {

    override fun onClick(widget: View) {
        clickListener.onTextClick(id)
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
        if (linkColorInt != null) ds.color = linkColorInt
    }
}