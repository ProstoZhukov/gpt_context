package ru.tensor.sbis.design.stubview.hint

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.Px
import org.jetbrains.annotations.TestOnly
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.util.dpToPx

/**
 * Style Holder для компонента заглушка-подсказка.
 * Стандарт: http://axure.tensor.ru/MobileStandart8/%D0%B7%D0%B0%D0%B3%D0%BB%D1%83%D1%88%D0%BA%D0%B8_24_1200_2.html
 *
 * @author ra.geraskin
 */
internal class StubViewHintStyleHolder private constructor() {

    /** Максимальная ширинка компонента по умолчанию. */
    @Px
    var defaultMaxWidth: Int = 0

    /** Нижний отступ текста подсказки. */
    @Px
    var hintTextMarginBottom: Int = 0

    /** Верхний отступ текста подсказки. */
    @Px
    var hintTextMarginTop: Int = 0

    /** Правый отступ текста подсказки. */
    @Px
    var hintTextMarginEnd: Int = 0

    /** Размер шрифта текста подсказки. */
    @Dimension
    var hintTextSize: Float = 0f

    /** Цвет текста подсказки. */
    @ColorInt
    var hintTextColor: Int = 0

    companion object {

        /** Максимальная ширинка компонента по умолчанию (dp). */
        // Согласовано: https://dev.sbis.ru/page/dialog/0775c4d3-f1e8-4d44-bf74-fe01f17928fa?message=2f76104c-e135-4fb8-899e-db4bc52eb77a&inviteduser=45883fa3-9b14-458b-97b3-3f55f527230d
        @TestOnly
        internal const val DEFAULT_VIEW_WIDTH = 230

        fun loadStyle(context: Context) = StubViewHintStyleHolder().apply {
            defaultMaxWidth = context.dpToPx(DEFAULT_VIEW_WIDTH)
            hintTextMarginBottom = Offset.S.getDimenPx(context)
            hintTextMarginEnd = Offset.S.getDimenPx(context)
            hintTextMarginTop = Offset.S.getDimenPx(context)
            hintTextSize = FontSize.X2L.getScaleOnDimen(context)
            hintTextColor = TextColor.PLACEHOLDER_LIST.getValue(context)
        }
    }

}