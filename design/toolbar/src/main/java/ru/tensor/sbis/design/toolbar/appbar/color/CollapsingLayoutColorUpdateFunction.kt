package ru.tensor.sbis.design.toolbar.appbar.color

import ru.tensor.sbis.design.toolbar.R
import ru.tensor.sbis.design.toolbar.appbar.model.ColorModel
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.CollapsingToolbarLayout

/**
 * @author ma.kolpakov
 * Создан 9/29/2019
 */
object CollapsingLayoutColorUpdateFunction : ColorUpdateFunction<CollapsingToolbarLayout> {

    override fun updateColorModel(view: CollapsingToolbarLayout, model: ColorModel?) {
        when {
            model == null || model.darkText -> {
                view.setCollapsedTitleTextAppearance(R.style.SbisAppBar_CollapsingLayout_TitleText_Collapsed_Dark)
                view.setExpandedTitleTextAppearance(R.style.SbisAppBar_CollapsingLayout_TitleText_Expanded_Dark)
                view.setSubtitleTextAppearance(R.style.SbisAppBar_CollapsingLayout_SubtitleText_Dark)
            }

            else -> {
                view.setCollapsedTitleTextAppearance(R.style.SbisAppBar_CollapsingLayout_TitleText_Collapsed)
                view.setExpandedTitleTextAppearance(R.style.SbisAppBar_CollapsingLayout_TitleText_Expanded)
                view.setSubtitleTextAppearance(R.style.SbisAppBar_CollapsingLayout_SubtitleText_Light)
            }
        }
    }
}