package ru.tensor.sbis.design.view.input.mask.api

import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.view.input.mask.BaseMaskInputViewTextWatcher
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewControllerApi

/**
 * Api для базового класса логики полей ввода с маской.
 *
 * @author ps.smirnyh
 */
internal interface BaseMaskInputViewControllerApi :
    SingleLineInputViewControllerApi,
    BaseMaskInputViewApi {

    /**
     * Создаёт объект логики применения маски при изменении текста в поле.
     * @return новый объект типа [BaseMaskInputViewTextWatcher].
     */
    fun createMaskTextWatcher(
        maskInputViewArray: TypedArray,
        attrs: AttributeSet?,
        @AttrRes
        defStyleAttr: Int,
        @StyleRes
        defStyleRes: Int
    ): BaseMaskInputViewTextWatcher
}