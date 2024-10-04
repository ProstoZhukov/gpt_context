package ru.tensor.sbis.design.toolbar.appbar.color

import ru.tensor.sbis.design.profile.titleview.SbisTitleView
import ru.tensor.sbis.design.toolbar.R
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.design.toolbar.appbar.model.ColorModel

/**
 * Выполняет обновление цветовой модели [Toolbar], используемого в графической шапке
 *
 * @author us.bessonov
 */
internal class SbisToolbarColorUpdateFunction : ColorUpdateFunction<Toolbar> {

    override fun updateColorModel(view: Toolbar, model: ColorModel?) {
        updateToolbarTextStyle(view, isDarkText = model?.darkText != false)
    }
}

/**
 * Задаёт тёмный, либо светлый стиль текста в [Toolbar], содержащий [SbisTitleView]
 */
fun updateToolbarTextStyle(toolbar: Toolbar, isDarkText: Boolean) {
    val style = if (isDarkText) {
        R.style.SbisAppBar_Toolbar_Icon_Dark
    } else {
        R.style.SbisAppBar_Toolbar_Icon
    }
    toolbar.leftIcon.setTextAppearance(toolbar.context, style)
    (toolbar.getCustomView<SbisTitleView>())?.let {
        val subtitleColor = if (isDarkText) {
            R.color.toolbar_app_bar_title_view_subtitle_color_dark
        } else {
            R.color.toolbar_app_bar_title_view_subtitle_color_light
        }
        it.appBarTitleViewHelper.setSubTitleColor(subtitleColor)
    }
}