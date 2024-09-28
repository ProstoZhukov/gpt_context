package ru.tensor.sbis.shift_hours.data

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt

/**
 * Модель блока события в смене
 * @property timeStart String? строковое представление времени начала события (если передано null,
 * то текст будет размещен слева, в противном случае справа или по центру)
 * @property timeEnd String? строковое представление времени окончания события (если передано null,
 * то текст будет размещен справа, в противном случае слева или по центру)
 * @property needDrawTime Boolean флаг, указывающий на то, нужно ли на вью смены отрисовывать время
 * данного блока события (по умолчанию предполагается, что в смене может содержаться несколько блоков
 * событий, при этом нужно будет только время рабочего дня, флаг для данного блока будет true)
 * @property columnStart Int? сдвиг начала блока события относительно начала смены (по умолчанию это процент
 * сдвига относительно начала вью, но программист может задать другое значение, например, количество
 * секунд с начала суток; если будет передано null, то блок отрисуется с начала вью)
 * @property columnEnd Int? сдвиг конца блока события относительно начала смены (по умолчанию это процент
 * сдвига относительно начала вью, но программист может задать другое значение, например, количество
 * секунд с начала суток; если будет передано null, то блок отрисуется до конца вью)
 * @property backgroundColor Int? цвет блока события (для рабочего времени, отгула, etc. различен); если равен null,
 * то при отрисовке будет использоваться стандартный цвет (цвет рабочего времени)
 * @property textColor Int? цвет текста блока события (например, для отмененной смены текст может
 * быть красным); если равен null, то при отрисовке будет использоваться стандартный цвет
 * @property shiftType тип смены в графике
 * тип блока
 * @property typeBlock
 */
data class EventTimeBlock(
    var timeStart: String? = null,
    var timeEnd: String? = null,
    val needDrawTime: Boolean = false,
    var columnStart: Int? = null,
    var columnEnd: Int? = null,
    @ColorInt val backgroundColor: Int? = null,
    val backgroundDrawable: Drawable? = null,
    @ColorInt val textColor: Int? = null,
    var shiftType: ShiftType = ShiftType.NORMAL,
    var typeBlock: TimeBlockTypeUI? = null
) {

    /**Вернет инстину, если время начала блока равно времени его окончания*/
    fun columnStartEqualsEnd() =
        this.columnStart == this.columnEnd
}