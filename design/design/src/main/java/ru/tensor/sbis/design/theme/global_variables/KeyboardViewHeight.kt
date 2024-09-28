package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.AbstractHeight
import ru.tensor.sbis.design.theme.models.KeyboardViewHeightModel
import ru.tensor.sbis.design.theme.utils.getDimen
import ru.tensor.sbis.design.theme.utils.getDimenPx

/**
 * Линейка размеров кнопок виртуальной клавиатуры из глобальных переменных.
 *
 * Реализует [KeyboardViewHeightModel].
 *
 * @author ra.geraskin
 */
enum class KeyboardViewHeight(
    @AttrRes private val dimenAttrRes: Int
) : KeyboardViewHeightModel, AbstractHeight {

    S(R.attr.itemSizeKeyboardViewS),
    M(R.attr.itemSizeKeyboardViewM),
    L(R.attr.itemSizeKeyboardViewL);

    override val globalVar = this

    /**
     * @see Context.getDimen
     */
    @Dimension
    override fun getDimen(context: Context) = ThemeTokensProvider.getDimen(context, dimenAttrRes)

    /**
     * @see Context.getDimenPx
     */
    @Dimension
    override fun getDimenPx(context: Context) = ThemeTokensProvider.getDimenPx(context, dimenAttrRes)

    companion object {
        /**
         * Высота контролла по умолчанию.
         */
        val DEFAULT = L
    }
}
