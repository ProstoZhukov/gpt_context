package ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import ru.tensor.sbis.design.toolbar.R

/**
 * Реализация [CollapsingLayoutChildToolbar], использующая стандартный [Toolbar]
 *
 * @author us.bessonov
 */
internal class DefaultCollapsingLayoutChildToolbar(private val toolbar: Toolbar) : CollapsingLayoutChildToolbar {

    init {
        // Выставляем id кнопке назад в стандартном туллбаре для автотестов
        toolbar.children.firstOrNull { it is AppCompatImageButton }?.apply {
            id = if (id == View.NO_ID) R.id.toolbar_left_icon else id
        }
    }

    override fun getView() = toolbar

    override fun getTitle(): CharSequence? = toolbar.title

    override fun addDummyView(view: View) {
        toolbar.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun getTitleMarginStart() = toolbar.titleMarginStart

    override fun getTitleMarginEnd() = toolbar.titleMarginEnd

    override fun getTitleMarginTop() = toolbar.titleMarginTop

    override fun getTitleMarginBottom() = toolbar.titleMarginBottom

}