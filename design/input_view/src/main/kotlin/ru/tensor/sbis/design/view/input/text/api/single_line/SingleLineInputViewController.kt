package ru.tensor.sbis.design.view.input.text.api.single_line

import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutTouchManager
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.utils.extentions.ellipsizeOnFocusChange
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.base.api.BaseInputViewController
import ru.tensor.sbis.design.view.input.base.api.BaseInputViewControllerApi
import ru.tensor.sbis.design.view.input.base.utils.InputViewAccessibilityDelegate
import ru.tensor.sbis.design.view.input.base.utils.UpdateState
import ru.tensor.sbis.design.view.input.text.utils.style.SingleLineStyleHolder

/**
 * Класс для управления состоянием и внутренними компонентами однострочных полей ввода.
 *
 * @author ps.smirnyh
 */
internal class SingleLineInputViewController(
    private val baseInputViewController: BaseInputViewController = BaseInputViewController()
) : BaseInputViewControllerApi by baseInputViewController, SingleLineInputViewControllerApi {

    private val styleHolder = SingleLineStyleHolder()

    override val linkView: TextLayout by lazy {
        val isFontIcon = styleHolder.style.fontIcon
        TextLayout {
            paint.color = styleHolder.style.linkTextColor
            paint.textSize =
                if (isFontIcon) styleHolder.style.linkIconSize else styleHolder.style.linkTextSize
            if (isFontIcon) {
                paint.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            }
            includeFontPad = false
        }.apply { id = R.id.input_view_link }
    }

    override var linkText: String
        get() = linkView.text.toString()
        set(value) {
            linkView.buildLayout {
                text = value
            }
            updatePropertyCallback.onChange()
        }

    override var isLinkVisible: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            updatePropertyCallback.onChange()
        }

    override var onLinkClickListener: ((View) -> Unit)? = null

    override var isFontIconLink: Boolean = false
        set(value) {
            val isNeedInvalidate = field != value
            field = value
            linkView.textPaint.textSize = if (value) styleHolder.style.linkIconSize else styleHolder.style.linkTextSize
            linkView.textPaint.typeface = if (value) {
                TypefaceManager.getSbisMobileIconTypeface(context)
            } else {
                TypefaceManager.getRobotoRegularFont(context)
            }
            val isNeedRequestLayout = linkView.configure {
                includeFontPad = value
            }
            if (isNeedRequestLayout) {
                baseInputView.safeRequestLayout()
            } else {
                if (isNeedInvalidate) {
                    baseInputView.invalidate()
                }
            }
        }

    override val touchManager: TextLayoutTouchManager by lazy {
        TextLayoutTouchManager(baseInputView, titleView, clearView, iconView, linkView)
    }

    override fun attach(
        baseInputView: BaseInputView,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        updateEllipsisCallback = UpdateState {
            updateEllipsis(inputView.isFocused)
        }
        updatePropertyCallback = UpdateState {
            updateOnPropertiesChanged()
        }
        updateFocusCallback = UpdateState {
            updateOnFocusChanged(inputView.isFocused)
        }
        baseInputViewController.attach(baseInputView, attrs, defStyleAttr, defStyleRes)
        styleHolder.loadStyle(baseInputView.context, attrs, defStyleAttr, defStyleRes)
        applyStyles()
        baseInputView.initAccessibilityDelegate(
            InputViewAccessibilityDelegate(
                baseInputView,
                inputView,
                titleView,
                clearView,
                iconView,
                validationStatusView,
                linkView
            )
        )
        linkView.setOnClickListener { _, _ -> onLinkClickListener?.invoke(baseInputView) }
        inputView.keyListener = actualKeyListener // вызов updateKeyListener для android 7 и ниже
        updateEllipsisCallback.onChange()
    }

    override fun updateOnFocusChanged(isFocus: Boolean) {
        updateInputViewHint(isFocus)
    }

    override fun updateOnPropertiesChanged(): Boolean {
        val superPropertyChanged = baseInputViewController.updateOnPropertiesChanged()
        val visibilityPropertiesChanged = updateInternalVisibility(isProgressVisible, isClearVisible, readOnly)
        val iconViewChanged = iconView.configure(checkDiffs = true) {
            padding = when {
                clearView.isVisible -> TextLayout.TextLayoutPadding(end = innerSpacing)
                else -> TextLayout.TextLayoutPadding(start = innerSpacing, end = innerSpacing)
            }
        }
        return if (superPropertyChanged || visibilityPropertiesChanged || iconViewChanged) {
            baseInputView.safeRequestLayout()
            true
        } else {
            false
        }
    }

    override fun updateInternalVisibility(
        isProgressVisible: Boolean,
        isClearVisible: Boolean,
        isReadOnly: Boolean
    ): Boolean {
        val clearViewChanged = clearView.configure(checkDiffs = true) {
            isVisible = !isProgressVisible && !isReadOnly && isClearVisible && value.isNotEmpty()
        }
        val linkViewChanged = linkView.configure(checkDiffs = true) {
            isVisible = when {
                isProgressVisible || !isLinkVisible || linkText.isEmpty() -> false
                else -> true
            }
            padding = getLinkViewPadding()
        }
        return clearViewChanged || linkViewChanged
    }

    override fun updateEllipsis(isFocus: Boolean) {
        inputView.ellipsizeOnFocusChange(
            hasFocus = isFocus,
            actualKeyListener = actualKeyListener,
            showKeyboard = !onHideKeyboard
        )
    }

    private fun applyStyles() = with(styleHolder) {
        linkView.textPaint.color = style.linkTextColor
        isFontIconLink = style.fontIcon
        linkText = property.linkText
        ViewCompat.setImportantForAutofill(inputView, property.importantForAutofill)
        ViewCompat.setAutofillHints(inputView, property.autofillHints)
    }

    private fun getLinkViewPadding(): TextLayout.TextLayoutPadding {
        val startPadding = if (clearView.isVisible || iconView.isVisible) {
            0
        } else {
            innerSpacing
        }
        val endPadding = if (isFontIconLink) {
            innerSpacing
        } else {
            0
        }
        return TextLayout.TextLayoutPadding(start = startPadding, end = endPadding)
    }
}