package ru.tensor.sbis.design.view.input.text.api.multi_line

import android.view.View
import androidx.annotation.IntRange
import androidx.annotation.Px
import ru.tensor.sbis.design.view.input.text.MultilineInputView
import ru.tensor.sbis.design.view.input.text.utils.model.MultilineRightIcon
import ru.tensor.sbis.design.view.input.text.utils.style.MultilineStyleHolder.Companion.DEFAULT_MAX_LINES
import ru.tensor.sbis.design.view.input.text.utils.style.MultilineStyleHolder.Companion.DEFAULT_MIN_LINES

/**
 * Api многострочного поля ввода [MultilineInputView].
 *
 * @author ps.smirnyh
 */
interface MultiLineInputViewApi {

    /**
     * Минимальное количество строк для поля ввода.
     */
    @get:IntRange(from = DEFAULT_MIN_LINES.toLong(), to = DEFAULT_MAX_LINES.toLong())
    @setparam:IntRange(from = DEFAULT_MIN_LINES.toLong(), to = DEFAULT_MAX_LINES.toLong())
    var minLines: Int

    /**
     * Максимальное количество строк для поля ввода.
     */
    @get:IntRange(from = DEFAULT_MIN_LINES.toLong(), to = DEFAULT_MAX_LINES.toLong())
    @setparam:IntRange(from = DEFAULT_MIN_LINES.toLong(), to = DEFAULT_MAX_LINES.toLong())
    var maxLines: Int

    /**
     * Получить высоту одной строки текста в поле ввода.
     */
    @get:Px
    val lineHeight: Int

    /**
     * Количество строк текста.
     */
    val lineCount: Int

    /**
     * Правая иконка в многострочном поле ввода.
     */
    var rightIconView: MultilineRightIcon?

    /**
     * Слушатель клика по правой иконке.
     */
    var onRightIconViewClickListener: ((View) -> Unit)?
}