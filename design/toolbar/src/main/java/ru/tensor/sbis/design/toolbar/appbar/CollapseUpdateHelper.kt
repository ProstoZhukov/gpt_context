package ru.tensor.sbis.design.toolbar.appbar

import android.view.View
import androidx.annotation.FloatRange
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile.titleview.SbisTitleView
import ru.tensor.sbis.design.profile.titleview.utils.SbisAppBarTitleViewHelper
import ru.tensor.sbis.design.profile_decl.titleview.Default
import ru.tensor.sbis.design.profile_decl.titleview.ListContent
import ru.tensor.sbis.design.profile_decl.titleview.TitleViewItem
import ru.tensor.sbis.design.toolbar.appbar.model.AppBarModel
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.AnimationUtils.lerp
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.CollapsingToolbarLayout
import ru.tensor.sbis.design.toolbar.util.collapsingimage.CollapsingPersonViewShapedDrawer
import ru.tensor.sbis.design.utils.checkSafe

private const val OPAQUE_COLLAPSED_BACKGROUND_ALPHA = 0f
private const val TRANSPARENT_COLLAPSED_BACKGROUND_ALPHA = 0.3f
private const val MAX_OFFSET_FOR_IMAGE_ALPHA = 0.3f

/**
 * Обновляет содержимое графической шапки, в зависимости от степени разворота
 *
 * @author us.bessonov
 */
class CollapseUpdateHelper(
    private val appBar: SbisAppBarLayout,
    private val collapsingToolbar: CollapsingToolbarLayout,
    private val backgroundView: View,
    private val titleView: SbisTitleView?
) {

    private var currentOffset = 0f
    private var collapsedAlpha = OPAQUE_COLLAPSED_BACKGROUND_ALPHA

    private val titleViewHelper: SbisAppBarTitleViewHelper?
        get() = titleView?.appBarTitleViewHelper

    /** @SelfDocumented */
    fun updateModel(model: AppBarModel) = with(model.content) {
        checkSafe(titleView != null || collapsedSubtitle.isEmpty() && photoData == null) {
            "You should use Toolbar from [design] " +
                "and set 'SbisAppBarLayout_titleView' to display collapsedSubtitle and photoData"
        }
        titleView?.apply {
            content = photoData?.let { photoData ->
                val item = TitleViewItem(photoData, title, collapsedSubtitle)
                ListContent(listOf(item))
            } ?: Default(title, collapsedSubtitle)
            singleLineTitle = collapsedSubtitle.isEmpty()
            appBarTitleViewHelper.setTitleAlpha(0f)
        }

        (backgroundView as? PersonView)?.apply {
            photoData?.let(::setData)
            if (isImageCollapsing()) {
                val shapedDrawer = CollapsingPersonViewShapedDrawer(
                    this,
                    titleViewHelper!!,
                    appBar,
                    collapsingToolbar
                )
                shapedDrawer.collapsingImageStateListener = collapsingToolbar.collapsingImageStateListener
                setShapedDrawer(shapedDrawer)
                collapsingToolbar.setSnapMode(true)
            }
        }

        collapsingToolbar.title = title
        collapsingToolbar.subtitle = subTitle
        collapsingToolbar.setRightSubtitle(comment)

        collapsedAlpha = if (model.color?.fillOpaqueWhenCollapsed == true) {
            OPAQUE_COLLAPSED_BACKGROUND_ALPHA
        } else {
            TRANSPARENT_COLLAPSED_BACKGROUND_ALPHA
        }

        model.color?.mainColor
            ?.let(appBar::setBackgroundColor)
        updateOffset(currentOffset)
    }

    /**
     * Обновляет представление, в зависимости от степени разворота
     */
    fun updateOffset(@FloatRange(from = 0.0, to = 1.0) normalizedOffset: Float) {
        currentOffset = normalizedOffset
        if (isImageCollapsing()) {
            val titleImageAlpha = if (normalizedOffset > 0) 0f else 1f
            titleViewHelper!!.setImageAlpha(titleImageAlpha)
            backgroundView.alpha = 1f - titleImageAlpha
        } else {
            if (backgroundView != appBar) {
                backgroundView.alpha = collapsedAlpha + normalizedOffset * (1 - collapsedAlpha)
            }
            titleViewHelper?.setImageAlpha(getImageAlpha(normalizedOffset))
        }
        titleViewHelper?.animateSubtitleVisibility(isVisible = normalizedOffset == 0f)
    }

    private fun getImageAlpha(@FloatRange(from = 0.0, to = 1.0) normalizedOffset: Float) =
        lerp(1f, 0f, normalizedOffset.coerceAtMost(MAX_OFFSET_FOR_IMAGE_ALPHA) / MAX_OFFSET_FOR_IMAGE_ALPHA)

    private fun isImageCollapsing() = backgroundView is PersonView && titleViewHelper?.hasImage() == true
}