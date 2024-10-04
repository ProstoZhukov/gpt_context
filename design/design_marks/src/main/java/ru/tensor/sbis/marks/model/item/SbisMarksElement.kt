package ru.tensor.sbis.marks.model.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.theme.res.SbisString
import ru.tensor.sbis.marks.model.SbisMarksCheckboxStatus

/**
 * Общий интерфейс модели пометки.
 *
 * @author ra.geraskin
 */
@Parcelize
sealed interface SbisMarksElement : Parcelable {

    /**
     * Идентификатор пометки.
     */
    val id: CharSequence

    /**
     * Заголовок пометки.
     */
    val title: SbisString

    /**
     * Значение выделения чекбокса.
     */
    var checkboxValue: SbisMarksCheckboxStatus

    /**
     * @SelfDocumented
     */
    fun copyElement(): SbisMarksElement

}