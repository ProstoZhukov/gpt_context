package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.ui.rate_icons_view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.core.view.get
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.data.CRMRateIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.extentions.setHorizontalPadding
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Вью оценки качества работы оператора.
 *
 * @author dv.baranov
 */
@SuppressLint("ViewConstructor")
internal class RateIconsView(
    private val context: Context,
    private val onRateIconClick: (rate: Int) -> Unit,
    private val rateIconsModels: List<CRMRateIcon>,
) : LinearLayout(context) {

    init {
        prepareView()
    }

    fun setRate(rate: Int) {
        val index = rateIconsModels.indexOfFirst { icon -> icon.rateValue == rate }
        if (index != -1) {
            handleRatingChanges(index)
        }
    }

    private fun prepareView() {
        layoutParams = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        }
        removeAllViews()
        addIcons()
    }

    private fun addIcons() {
        for (i in rateIconsModels.indices) {
            addView(createIcon(i))
        }
    }

    private fun createIcon(index: Int): SbisTextView = createDefaultIcon(index).apply {
        text = rateIconsModels[index].defaultIcon
    }

    private fun createDefaultIcon(index: Int): SbisTextView = SbisTextView(context).apply {
        setTypeface(TypefaceManager.getSbisMobileIconTypeface(context), Typeface.NORMAL)
        setTextColor(context.getThemeColorInt(CRMRateIcon.Default.defaultColor))
        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getDimenPx(RDesign.attr.iconSize_3xl).toFloat())
        setOnClickListener {
            onRateIconClick(rateIconsModels[index].rateValue)
        }
        setHorizontalPadding(context.getDimenPx(RDesign.attr.offset_s))
    }

    private fun handleRatingChanges(index: Int) {
        setDefaultColorForAllIcons()
        changeRating(index)
    }

    private fun setDefaultColorForAllIcons() {
        for (index in 0 until childCount) {
            val iconModel = rateIconsModels[index]
            getChildAt(index).safeSetIconParams(iconModel.defaultColor, iconModel.defaultIcon)
        }
    }

    private fun changeRating(index: Int) {
        if (rateIconsModels[index] is CRMRateIcon.Star) {
            for (i in 0..index) {
                setIconViewActive(i)
            }
        } else {
            setIconViewActive(index)
        }
    }

    private fun setIconViewActive(index: Int) {
        get(index).apply {
            val iconModel = rateIconsModels[index]
            safeSetIconParams(iconModel.activeColor, iconModel.activeIcon)
        }
    }

    private fun View.safeSetIconParams(@AttrRes colorAttr: Int? = null, icon: String? = null) {
        castTo<SbisTextView>()?.let { view ->
            colorAttr?.let { view.setTextColor(context.getThemeColorInt(it)) }
            icon?.let { view.text = icon }
        }
    }
}
