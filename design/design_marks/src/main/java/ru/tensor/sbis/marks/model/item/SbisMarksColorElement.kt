package ru.tensor.sbis.marks.model.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.marks.model.SbisMarksCheckboxStatus
import ru.tensor.sbis.marks.model.SbisMarksFontStyle
import ru.tensor.sbis.marks.model.title.SbisMarksTitle

/**
 * Модель элемента цветной пометки.
 *
 * @property color     Цвет иконки и заголовка пометки.
 * @property textStyle Стиль текста заголовка пометки.
 *
 * @author ra.geraskin
 */
@Parcelize
data class SbisMarksColorElement internal constructor(
    override var id: CharSequence,
    override var title: SbisMarksTitle,
    override var checkboxValue: SbisMarksCheckboxStatus,
    var color: SbisColor,
    @SbisMarksFontStyle var textStyle: Int = 0
) : SbisMarksElement, Parcelable {

    /**
     * Конструктор с возможностью вставки в качестве заголовка пометки объект типа [PlatformSbisString].
     *
     * ВАЖНО! При использовании [PlatformSbisString.ResWithArgs] в качестве title, теряется массив аргументов ресурса.
     */
    constructor(
        id: CharSequence,
        title: PlatformSbisString,
        checkboxValue: SbisMarksCheckboxStatus,
        color: SbisColor,
        @SbisMarksFontStyle textStyle: Int = 0
    ) : this(
        id,
        SbisMarksTitle.convertFromPlatformString(title),
        checkboxValue,
        color,
        textStyle
    )

    override fun copyElement(): SbisMarksElement = this.copy()

}