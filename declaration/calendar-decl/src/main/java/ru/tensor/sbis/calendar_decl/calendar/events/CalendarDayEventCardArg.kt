package ru.tensor.sbis.calendar_decl.calendar.events

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import ru.tensor.sbis.regulation.generated.Regulation
import java.util.Date
import java.util.UUID

/**
 * Варианты открытия карточки создания события
 */
@Parcelize
sealed interface CalendarDayEventCardArg : Parcelable {

    /** Открывать активити с флагом NEW_TASK */
    val newTask: Boolean

    /** Если не null, то подскроллить календарь к данной дате */
    val showDate: LocalDate?

    @Parcelize
    sealed interface CreateEvent : CalendarDayEventCardArg {

        /** регламент события */
        val regulation: Regulation?

        /** подтип события */
        val inputOption: InputOption?

        /** Данные о типе отпуска */
        val vacationTypeInfo: VacationTypeInfo?

        /** доп. параметры создания события */
        val params: CalendarDayEventParam

        /**
         * Создание карточки АО
         * @property regulation регламент события
         * @property inputOption подтип события
         * @property params доп. параметры создания события
         * @property newTask открывать активити с флагом NEW_TASK
         * */
        @Parcelize
        data class CreateExpense(
            override val regulation: Regulation? = null,
            override val newTask: Boolean = true,
        ) : CreateEvent {
            override val showDate: LocalDate?
                get() = null
            override val vacationTypeInfo: VacationTypeInfo?
                get() = null
            override val inputOption: InputOption?
                get() = null
            override val params: CalendarDayEventParam
                get() = CalendarDayEventParam.Empty
        }

        /**
         * Создание события только по дате начала
         * @property eventType тип события
         * @property date дата начала события
         * @property profileUUID UUID сотрудника, для которого создаем событие
         * @property regulation регламент события
         * @property inputOption подтип события
         * @property vacationTypeInfo данные о типе отпуска
         * @property params доп. параметры создания события
         * @property newTask открывать активити с флагом NEW_TASK
         * @property showDate если не null, то подскроллить календарь к данной дате
         */
        @Parcelize
        data class CreateByDate(
            val eventType: String,
            val date: LocalDate?,
            val profileUUID: UUID?,
            override val regulation: Regulation? = null,
            override val inputOption: InputOption? = null,
            override val vacationTypeInfo: VacationTypeInfo? = null,
            override val params: CalendarDayEventParam = CalendarDayEventParam.Empty,
            override val newTask: Boolean = true,
            override val showDate: LocalDate? = null,
        ) : CreateEvent

        /**
         * Созданеи события по времени начала и дате окончания
         * @property eventType тип события
         * @property startTime время начала события
         * @property endTime время окончания события
         * @property fromGrid событие создано нажатием на сетку календаря
         * @property profileUUID UUID сотрудника, для которого создаем событие
         * @property regulation регламент события
         * @property inputOption подтип события
         * @property vacationTypeInfo данные о типе отпуска
         * @property params доп. параметры создания события
         * @property newTask открывать активити с флагом NEW_TASK
         * @property showDate если не null, то подскроллить календарь к данной дате
         */
        @Parcelize
        data class CreateByTime(
            val eventType: String,
            val startTime: LocalDateTime,
            val endTime: LocalDateTime,
            val fromGrid: Boolean,
            val profileUUID: UUID?,
            val profileDepartmentUUID: UUID?,
            override val regulation: Regulation? = null,
            override val inputOption: InputOption? = null,
            override val vacationTypeInfo: VacationTypeInfo? = null,
            override val params: CalendarDayEventParam = CalendarDayEventParam.Empty,
            override val newTask: Boolean = true,
            override val showDate: LocalDate? = null,
        ) : CreateEvent

        /**
         * Создание корректировки командировки
         * @property correctionUUID UUID командировки, для которой создается корректировка
         * @property correctionCancelled корректировка отмены
         * @property correctionReason причина корректировки
         * @property correctionDateStart дата начала нового периода командировки
         * @property correctionDateEnd дата окончания нового периода командировки
         * @property profileUUID UUID сотрудника, для которого создаем событие
         */
        @Parcelize
        data class CreateBusinessTripCorrection(
            val correctionUUID: UUID,
            val correctionCancelled: Boolean,
            val correctionReason: String,
            val correctionDateStart: Date?,
            val correctionDateEnd: Date?,
            override val regulation: Regulation,
            val profileUUID: UUID?,
        ) : CreateEvent {
            override val newTask: Boolean
                get() = false
            override val showDate: LocalDate?
                get() = null
            override val inputOption: InputOption?
                get() = null
            override val vacationTypeInfo: VacationTypeInfo?
                get() = null
            override val params: CalendarDayEventParam
                get() = CalendarDayEventParam.Empty
        }
    }

    /**
     * Открытие существующего события
     */
    @Parcelize
    sealed interface OpenEvent : CalendarDayEventCardArg {

        /**
         * Открытие события по [saleId]
         * @property eventType тип события
         * @property newTask открывать активити с флагом NEW_TASK
         * @property showDate если не null, то подскроллить календарь к данной дате
         */
        @Parcelize
        data class OpenBySaleId(
            val eventType: String,
            val saleId: Long,
            override val newTask: Boolean = true,
        ) : CalendarDayEventCardArg {
            override val showDate: LocalDate?
                get() = null
        }

        /**
         * Открытие события по [eventUuid]
         * @property eventType тип события
         * @property documentLoadData данные о прикрепленном документе
         * @property newTask открывать активити с флагом NEW_TASK
         * @property openInEditMode открывать событие в режиме редактирования
         * @property docId id документа
         * @property profileUUID UUID сотрудника, для которого создаем событие
         * @property eventDateFromNotification дата события из уведомления (передается, если событие открыто из пуша)
         * @property errorMessage ошибка, которую нужно показать при открытии события (например, командировка
         * может быть создана с ошибкой, тогда эту ошибку нужно показать при открытии)
         * @property openEventParams доп. параметры открытия события
         * @property showDate если не null, то подскроллить календарь к данной дате
         */
        @Parcelize
        data class OpenByUuid(
            val eventType: String,
            val eventUuid: UUID,
            val documentLoadData: DocumentLoadData?,
            override val newTask: Boolean = true,
            val openInEditMode: Boolean = false,
            val docId: Long? = null,
            val profileUUID: UUID?,
            val eventDateFromNotification: String? = null,
            val errorMessage: String? = null,
            val openEventParams: CalendarOpenEventParams = CalendarOpenEventParams.Default,
        ) : CalendarDayEventCardArg {
            override val showDate: LocalDate?
                get() = null
        }
    }
}