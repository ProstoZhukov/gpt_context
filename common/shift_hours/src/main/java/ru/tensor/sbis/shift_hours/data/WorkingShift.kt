package ru.tensor.sbis.shift_hours.data

import androidx.annotation.ColorInt

/**
 * Модель смены сотрудника для одного дня
 * @property shiftDayTypeTitle String? заголовок дня ("выходной", "больничный", etc.; если
 * значение null, то для отрисовки текста берется событие в списке [eventTimeBlocks] с needDrawTime=true)
 * @property shiftDayTypeIcon String? иконка дня (используется в сменах с документом на весь день:
 * больничный, отпуск, etc.; если имеет значение не null, то текст будет размещен слева, в противном
 * случае посередине)
 * @property shiftBackgroundColor Int? фоновый цвет смены (различный для выходных, отпусков, etc.); если равен null,
 * то при отрисовке будет использоваться стандартный цвет (цвет фона рабочего времени)
 * @property shiftTextColor Int? цвет текста смены (в специфичном случае может быть другим, как,
 * например, в случае с командировкой - коричневым); если равен null, то при отрисовке будет использоваться стандартный
 * цвет, если не null то есть приглашение в точку продаж и рисуем несколько смен
 * @property needDrawTimeBlocks Boolean флаг, который указывает, нужна ли отрисовка блоков
 * событий рабочего времени, отгулов, etc.
 * @property eventTimeBlocks ArrayList<EventTimeBlock> список блоков событий для отрисовки
 * @property totalColumns Int общее количество шагов отрисовки блоков событий (по умолчанию 100 -
 * это 100% ширины вью, но программист может задать другое значение, например, 86400 - количество
 * секунд в сутках
 */
data class WorkingShift(
    var shiftDayTypeTitle: String? = null,
    var shiftDayTypeIcon: String? = null,
    @ColorInt var shiftBackgroundColor: Int? = null,
    @ColorInt var shiftTextColor: Int? = null,
    var needDrawTimeBlocks: Boolean = false,
    var eventTimeBlocks: ArrayList<EventTimeBlock> = ArrayList(),
    var totalColumns: Int = 100
) {

    /**
     * Приглашение в точку продаж есть
     */
    val isPartTime: Boolean
        get() {
            return shiftTextColor != null && needDrawTimeBlocks
        }
}