package ru.tensor.sbis.design.view.input.text.api.multi_line

import android.text.Spanned
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.getTextWidth
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.utils.extentions.clearSpans
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.base.api.BaseInputViewController
import ru.tensor.sbis.design.view.input.base.api.BaseInputViewControllerApi
import ru.tensor.sbis.design.view.input.base.utils.InputViewChangeListener
import ru.tensor.sbis.design.view.input.base.utils.UpdateState
import ru.tensor.sbis.design.view.input.text.utils.MultilineEllipsizeSpan
import ru.tensor.sbis.design.view.input.text.utils.model.MultilineRightIcon
import ru.tensor.sbis.design.view.input.text.utils.style.MultilineStyleHolder
import ru.tensor.sbis.design.view.input.text.utils.style.MultilineStyleHolder.Companion.DEFAULT_MAX_LINES
import ru.tensor.sbis.design.view.input.text.utils.style.MultilineStyleHolder.Companion.DEFAULT_MIN_LINES

/**
 * Класс для управления состоянием и внутренними компонентами многострочного поля ввода.
 *
 * @author ps.smirnyh
 */
internal class MultiLineInputViewController(
    private val baseInputViewController: BaseInputViewController = BaseInputViewController()
) : BaseInputViewControllerApi by baseInputViewController, MultiLineInputViewApi {

    private val styleHolder = MultilineStyleHolder()
    private val ellipsizeUpdater = Runnable {
        if (inputView.lineCount <= maxLines) {
            if (!inputView.isLaidOut) {
                forceLayout()
            }
            return@Runnable
        }
        val layout = inputView.layout ?: return@Runnable
        val text = inputView.text ?: return@Runnable
        val range = if (maxLines == 1) {
            0..text.length
        } else {
            layout.getLineStart(maxLines - 1)..layout.getLineEnd(maxLines - 1)
        }
        text.setSpan(
            MultilineEllipsizeSpan(range),
            range.first,
            text.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )

        forceLayout()
    }

    private val ellipsizeHintUpdater = Runnable {
        if (inputView.paint.getTextWidth(inputView.hint ?: "") > inputView.measuredWidth) {
            val ellipsizeHint = TextUtils.ellipsize(
                inputView.hint,
                inputView.paint,
                inputView.measuredWidth.toFloat(),
                TextUtils.TruncateAt.END
            )
            if (inputView.hint != ellipsizeHint) {
                inputView.hint = ellipsizeHint
                inputView.post { inputView.safeRequestLayout() }
            }
        }
    }

    private var layoutChangeListener: InputViewChangeListener? = null

    override var inputType: Int
        get() = inputView.inputType
        set(value) {
            inputView.setRawInputType(value)
        }

    override var minLines: Int
        get() = inputView.minLines
        set(value) {
            if (value == maxLines) {
                return
            }
            require(value in DEFAULT_MIN_LINES..DEFAULT_MAX_LINES) {
                "minLines must be greater or equal $DEFAULT_MIN_LINES and less or equal than $DEFAULT_MAX_LINES"
            }
            require(value <= maxLines) {
                "minLines($value) must be less or equal than maxLines($maxLines)"
            }
            inputView.minLines = value
        }

    override var maxLines: Int
        get() = inputView.maxLines
        set(value) {
            if (value == maxLines) {
                return
            }
            require(value in DEFAULT_MIN_LINES..DEFAULT_MAX_LINES) {
                "maxLines must be greater or equal $DEFAULT_MIN_LINES and less or equal than $DEFAULT_MAX_LINES"
            }
            require(value >= minLines) {
                "maxLines($value) must be greater or equal than minLines($minLines)"
            }
            inputView.maxLines = value
            updateEllipsisCallback.onChange()
        }

    override val lineHeight: Int
        get() = inputView.lineHeight

    override val lineCount: Int
        get() = inputView.lineCount

    override var rightIconView: MultilineRightIcon? = null
        set(value) {
            field = value
            if (value != null) {
                iconView.configure {
                    text = value.iconText.toString()
                    if (value.color != null) paint.color = value.color
                    isVisible = true
                }
            } else {
                iconView.configure {
                    isVisible = false
                }
            }
            baseInputView.safeRequestLayout()
        }

    override var onRightIconViewClickListener: ((View) -> Unit)? = null

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
            updateHintCallback.onChange()
        }
        updateHintCallback = UpdateState {
            updateInputViewHint(inputView.isFocused)
            ellipsizeHint()
        }
        baseInputViewController.attach(baseInputView, attrs, defStyleAttr, defStyleRes)
        styleHolder.loadStyle(baseInputView.context, attrs, defStyleAttr, defStyleRes)
        applyStyles()
        inputView.keyListener = actualKeyListener // вызов updateKeyListener для android 7 и ниже
        iconView.setOnClickListener { _, _ -> onRightIconViewClickListener?.invoke(baseInputView) }
    }

    override fun updateOnPropertiesChanged(): Boolean {
        val superPropertyChanged = baseInputViewController.updateOnPropertiesChanged()
        val clearViewChanged = clearView.configure(checkDiffs = true) {
            isVisible = !isProgressVisible && !readOnly && isClearVisible && value.isNotEmpty()
        }
        val iconViewChanged = iconView.configure(checkDiffs = true) {
            padding = if (clearView.isVisible) {
                TextLayout.TextLayoutPadding(end = innerSpacing)
            } else {
                TextLayout.TextLayoutPadding(start = innerSpacing, end = innerSpacing)
            }
        }
        return if (superPropertyChanged || clearViewChanged || iconViewChanged) {
            baseInputView.safeRequestLayout()
            true
        } else {
            false
        }
    }

    override fun updateEllipsis(isFocus: Boolean) {
        inputView.text?.clearSpans<MultilineEllipsizeSpan>()
        if (isFocus) {
            return
        }
        if (inputView.layout == null) {
            inputView.postOnLayout(ellipsizeUpdater)
        } else {
            ellipsizeUpdater.run()
        }
    }

    private fun ellipsizeHint() {
        if (inputView.layout == null) {
            inputView.postOnLayout(ellipsizeHintUpdater)
        } else {
            ellipsizeHintUpdater.run()
        }
    }

    private fun applyStyles() = with(styleHolder) {
        minLines = property.minLines
        maxLines = property.maxLines

        val iconText = property.iconText
        iconView.configure {
            if (iconText != null) text = iconText
            paint.color = property.iconColor
            isVisible = iconText != null
        }
    }

    private fun forceLayout() {
        inputView.forceLayout()
        baseInputView.forceLayout()
        baseInputView.parent.requestLayout()
    }

    private fun View.postOnLayout(action: Runnable) {
        val runAction: () -> Unit = {
            if (isInLayout) {
                removeCallbacks(action)
                post(action)
            } else {
                action.run()
            }
            layoutChangeListener = null
        }

        if (isLaidOut && !isLayoutRequested) {
            runAction()
        } else {
            layoutChangeListener?.let(::removeOnLayoutChangeListener)
            layoutChangeListener = InputViewChangeListener(runAction).also(::addOnLayoutChangeListener)
        }
    }
}