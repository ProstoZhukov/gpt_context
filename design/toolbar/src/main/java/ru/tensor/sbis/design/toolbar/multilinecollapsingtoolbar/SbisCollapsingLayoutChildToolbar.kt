package ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar

import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.design.profile.titleview.SbisTitleView
import ru.tensor.sbis.design.profile.titleview.utils.SbisAppBarTitleViewHelper
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.design.utils.extentions.getMargins
import ru.tensor.sbis.design.utils.requireNotNullSafe

/**
 * Реализация [CollapsingLayoutChildToolbar], использующая [Toolbar] из общих компонентов
 *
 * @author us.bessonov
 */
class SbisCollapsingLayoutChildToolbar(private val toolbar: Toolbar) : CollapsingLayoutChildToolbar {

    private val titleView: SbisTitleView? by lazy {
        requireNotNullSafe(toolbar.getCustomView()) {
            "Collapsing text behaviour is not supported when using Toolbar without SbisTitleView"
        }
    }

    private val appBarTitleViewHelper: SbisAppBarTitleViewHelper? by lazy {
        titleView?.appBarTitleViewHelper
    }

    override fun getView() = toolbar

    override fun addDummyView(view: View) = toolbar.customViewContainer.addView(
        view,
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    override fun getTitle(): CharSequence = ""

    override fun getCustomTitleTextSize() = appBarTitleViewHelper?.getTitleTextSize()

    override fun getTitleMarginStart() = appBarTitleViewHelper?.getTitleLeft() ?: 0

    override fun getTitleMarginEnd() =
        -(titleView?.getMargins()?.end ?: 0) - (titleView?.paddingEnd ?: 0)

    override fun getTitleMarginTop(): Int {
        return appBarTitleViewHelper?.getTitleTop() ?: 0
    }

    override fun getTitleMarginBottom() = 0
}