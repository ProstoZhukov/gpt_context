package ru.tensor.sbis.marks.model.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.marks.model.SbisMarksCheckboxStatus
import ru.tensor.sbis.marks.model.title.SbisMarksTitle

/**
 * Модель элемента платформенной пометки.
 *
 * @property icon Иконка платформенной пометки.
 *
 * @author ra.geraskin
 */
@Parcelize
data class SbisMarksIconElement internal constructor(
    override var id: CharSequence,
    override var title: SbisMarksTitle,
    override var checkboxValue: SbisMarksCheckboxStatus,
    val icon: SbisMobileIcon.Icon
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
        icon: SbisMobileIcon.Icon
    ) : this(
        id,
        SbisMarksTitle.convertFromPlatformString(title),
        checkboxValue,
        icon
    )

    override fun copyElement(): SbisMarksElement = this.copy()

}