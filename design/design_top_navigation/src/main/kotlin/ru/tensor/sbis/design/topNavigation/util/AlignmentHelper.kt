package ru.tensor.sbis.design.topNavigation.util

import android.view.Gravity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.doOnLayout
import ru.tensor.sbis.design.custom_view_tools.utils.getTextWidth
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.topNavigation.R
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.extentions.fullMeasuredWidth

/**
 * Класс для управления выравниванием заголовка и подзаголовка.
 *
 * @author da.zolotarev
 */
internal class AlignmentHelper(val view: SbisTopNavigationView) {
    private var leftContentWidth = 0
    private var rightContentWidth = 0
    private var titleViewWidth = 0
    private var subTitleViewWidth = 0

    private val marginEnd = Offset.XS.getDimenPx(view.context)

    private val leftViewAlignmentParams = ViewAlignmentParams(
        startToEnd = R.id.top_navigation_small_title_space,
        endToStart = R.id.top_navigation_custom_content,
        marginStart = view.getTitleAndSubtitleStartMargin(),
        marginEnd = marginEnd
    )

    private val notFitHalfCenterViewAlignmentParams = ViewAlignmentParams(
        gravity = if (leftContentWidth < rightContentWidth) Gravity.RIGHT else Gravity.LEFT,
        startToEnd = R.id.top_navigation_small_title_space,
        marginStart = view.getTitleAndSubtitleStartMargin(),
        endToStart = R.id.top_navigation_custom_content,
        marginEnd = marginEnd
    )
    private val fitCenterViewAlignmentParams = ViewAlignmentParams(
        gravity = Gravity.CENTER_HORIZONTAL,
        matchConstraintMaxWidth = view.measuredWidth - (leftContentWidth + rightContentWidth),
        parentId = R.id.top_navigation_small_content_container
    )

    /**
     * Выравнить подзаголовок и заголовок.
     */
    fun alignText(alignment: HorizontalAlignment) {
        when (alignment) {
            HorizontalAlignment.LEFT -> alignTitleSubtitleLeft()
            HorizontalAlignment.CENTER -> alignTitleSubtitleCenter()
            HorizontalAlignment.RIGHT -> Unit // Не поддерживается
        }
    }

    /**
     * Обновить параметры при измерении view.
     */
    fun onMeasureView() = view.apply {
        leftContentWidth = leftContent.measuredWidth

        rightContentWidth = customViewContainer.fullMeasuredWidth +
            noNetworkIcon.fullMeasuredWidth +
            rightBtnBack.fullMeasuredWidth +
            rightBtnContainer.fullMeasuredWidth

        titleViewWidth = titleView.getWidthWithMargins { it.getValueWidth() }
        subTitleViewWidth =
            subtitleView.getWidthWithMargins { it.paint.getTextWidth(text = it.text ?: "", byLayout = true) }

        leftViewAlignmentParams.apply { marginStart = view.getTitleAndSubtitleStartMargin() }
        notFitHalfCenterViewAlignmentParams.apply { marginStart = view.getTitleAndSubtitleStartMargin() }
        fitCenterViewAlignmentParams.apply {
            // Сдвигаем шапку влево на ширину левого контента, чтобы центрировать.
            marginStart = -leftContentWidth
            matchConstraintMaxWidth = view.measuredWidth - (leftContentWidth + rightContentWidth)
        }
    }

    private fun alignTitleSubtitleLeft() = view.doOnLayout {
        when (view.content) {
            is SbisTopNavigationContent.LargeTitle -> alignLargeTitleLeft()
            is SbisTopNavigationContent.SmallTitle -> {
                alignSmallTitleLeft()
                alignSmallSubtitleLeft()
            }

            is SbisTopNavigationContent.SmallTitleListContent -> {
                alignSmallTitleLeft()
                alignSmallSubtitleLeft()
            }

            else -> Unit
        }
    }

    private fun alignTitleSubtitleCenter() = view.doOnLayout {
        when (view.content) {
            is SbisTopNavigationContent.LargeTitle -> alignLargeTitleCenter()
            is SbisTopNavigationContent.SmallTitle -> {
                alignSmallTitleCenter()
                alignSmallSubTitleCenter()
            }

            is SbisTopNavigationContent.SmallTitleListContent -> {
                alignSmallTitleCenter()
                alignSmallSubTitleCenter()
            }

            else -> Unit
        }
    }

    private fun alignSmallTitleLeft() =
        view.titleView.updateViewParams(
            leftViewAlignmentParams.copy(horizontalBiasValue = HORIZONTAL_BIAS_LEFT_ALIGNMENT)
        )

    private fun alignSmallSubtitleLeft() = view.subtitleView.updateViewParams(leftViewAlignmentParams)

    private fun alignLargeTitleLeft() = view.titleView.updateViewParams(
        leftViewAlignmentParams.copy(
            startToEnd = ConstraintSet.UNSET,
            endToStart = R.id.top_navigation_custom_content,
            marginEnd = marginEnd,
            parentId = R.id.top_navigation_large_content_container,
            horizontalBiasValue = HORIZONTAL_BIAS_LEFT_ALIGNMENT
        )
    )

    private fun alignSmallTitleCenter() {
        when {
            isTitleSubtitleNotFit() -> view.titleView.updateViewParams(leftViewAlignmentParams)
            isTitleSubtitleHalfNotFit() -> view.titleView.updateViewParams(notFitHalfCenterViewAlignmentParams)
            else -> view.titleView.updateViewParams(fitCenterViewAlignmentParams)
        }
    }

    private fun alignSmallSubTitleCenter() {
        when {
            isTitleSubtitleNotFit() -> view.subtitleView.updateViewParams(leftViewAlignmentParams)
            isTitleSubtitleHalfNotFit() -> view.subtitleView.updateViewParams(notFitHalfCenterViewAlignmentParams)

            else -> view.subtitleView.updateViewParams(fitCenterViewAlignmentParams)
        }
    }

    private fun isTitleSubtitleNotFit() = isViewNotFit(titleViewWidth) && isViewNotFit(subTitleViewWidth)
    private fun isTitleSubtitleHalfNotFit() = isViewHalfNotFit(titleViewWidth) && isViewHalfNotFit(subTitleViewWidth)

    private fun alignLargeTitleCenter() {
        when {
            isViewNotFit(titleViewWidth) -> alignLargeTitleLeft()
            isViewHalfNotFit(titleViewWidth) -> view.titleView.updateViewParams(
                notFitHalfCenterViewAlignmentParams.copy(
                    startToEnd = ConstraintSet.UNSET,
                    endToStart = R.id.top_navigation_custom_content
                )
            )

            else -> view.titleView.updateViewParams(
                fitCenterViewAlignmentParams.copy(parentId = R.id.top_navigation_large_content_container)
            )
        }
    }

    /**
     * Не поместятся ли "половинки" текста в своих половинах шапки.
     */
    private fun isViewHalfNotFit(viewWidth: Int) = viewWidth / 2f > view.measuredWidth / 2f - rightContentWidth ||
        viewWidth / 2f > view.measuredWidth / 2f - leftContentWidth

    /**
     * Не поместится ли текст в шапке.
     */
    private fun isViewNotFit(viewWidth: Int) = viewWidth > view.measuredWidth - (leftContentWidth + rightContentWidth)

    companion object {
        const val HORIZONTAL_BIAS_LEFT_ALIGNMENT = 0.0f
        const val HORIZONTAL_BIAS_CENTER_ALIGNMENT = 0.5f
    }
}