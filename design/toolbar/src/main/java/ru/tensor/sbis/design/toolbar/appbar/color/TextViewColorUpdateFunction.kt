package ru.tensor.sbis.design.toolbar.appbar.color

import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.toolbar.R
import ru.tensor.sbis.design.toolbar.appbar.model.ColorModel

/**
 * @author ma.kolpakov
 * Создан 9/29/2019
 */
internal object TextViewColorUpdateFunction : ColorUpdateFunction<SbisTextView> {

    override fun updateColorModel(view: SbisTextView, model: ColorModel?) {
        view.setTextAppearance(
            view.context,
            when {
                model == null || model.darkText -> R.style.SbisAppBar_Text_Dark
                else -> R.style.SbisAppBar_Text
            }
        )
    }
}