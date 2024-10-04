package ru.tensor.sbis.design.view.input.mask

import android.content.res.TypedArray
import android.util.AttributeSet
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.mask.api.BaseMaskInputViewController

/**
 * Класс для управления состоянием и внутренними компонентами поля ввода с маской.
 *
 * @author ps.smirnyh
 */
internal class MaskInputViewController : BaseMaskInputViewController() {
    override fun checkMask(mask: String): Boolean = true

    override fun createMaskTextWatcher(
        maskInputViewArray: TypedArray,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ): BaseMaskInputViewTextWatcher =
        MaskInputViewTextWatcher(
            inputView,
            valueChangedWatcher,
            maskInputViewArray.getString(R.styleable.MaskInputView_inputView_mask) ?: "***"
        )
}