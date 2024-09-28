/**
 * Набор утилит для установки параметров view на основе выравнивания.
 *
 * @author da.zolotarev
 */
package ru.tensor.sbis.design.topNavigation.util

import android.view.Gravity
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.topNavigation.util.AlignmentHelper.Companion.HORIZONTAL_BIAS_CENTER_ALIGNMENT
import ru.tensor.sbis.design.view.input.base.BaseInputView

/**
 * Модель параметров view для выравнивания.
 */
internal data class ViewAlignmentParams(
    var gravity: Int = Gravity.LEFT,
    var matchConstraintMaxWidth: Int = 0,
    var startToEnd: Int = ConstraintSet.UNSET,
    var endToStart: Int = ConstraintSet.UNSET,
    var parentId: Int = ConstraintSet.UNSET,
    var marginStart: Int = 0,
    var marginEnd: Int = 0,
    var horizontalBiasValue: Float = HORIZONTAL_BIAS_CENTER_ALIGNMENT
)

/** @SelfDocumented. */
internal fun BaseInputView?.updateViewParams(viewParams: ViewAlignmentParams) = this.updateViewParams(
    viewParams.matchConstraintMaxWidth,
    viewParams.startToEnd,
    viewParams.endToStart,
    viewParams.parentId,
    viewParams.marginStart,
    viewParams.marginEnd,
    viewParams.horizontalBiasValue
) { it.gravity = viewParams.gravity }

/** @SelfDocumented. */
internal fun SbisTextView?.updateViewParams(viewParams: ViewAlignmentParams) = this.updateViewParams(
    viewParams.matchConstraintMaxWidth,
    viewParams.startToEnd,
    viewParams.endToStart,
    viewParams.parentId,
    viewParams.marginStart,
    viewParams.marginEnd,
    viewParams.horizontalBiasValue
) { it.gravity = viewParams.gravity }

private fun <T : View> T?.updateViewParams(
    matchConstraintMaxWidth: Int,
    startToEnd: Int,
    endToStart: Int,
    parentId: Int,
    marginStart: Int,
    marginEnd: Int,
    horizontalBias: Float,
    setGravity: (T) -> Unit
) = this?.updateLayoutParams<ConstraintLayout.LayoutParams> {
    this.startToEnd = startToEnd
    this.endToStart = endToStart
    this.startToStart = parentId
    this.endToEnd = parentId
    setGravity(this@updateViewParams)
    this.matchConstraintMaxWidth = matchConstraintMaxWidth
    this.marginStart = marginStart
    this.marginEnd = marginEnd
    this.horizontalBias = horizontalBias
}