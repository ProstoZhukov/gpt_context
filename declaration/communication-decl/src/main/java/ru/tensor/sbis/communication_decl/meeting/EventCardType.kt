package ru.tensor.sbis.communication_decl.meeting

import java.io.Serializable
import java.util.*

/**
 * @author is.mosin
 *
 * Тип действия над карточкой совещания, вебинара, мероприятия
 */
sealed class EventCardType : Serializable {
    /**
     * Отобразить карточку совещания
     * @param docUuid - UUID документа (совещание)
     * @param notificationUuid - идентификатор уведомления
     */
    data class Meeting @JvmOverloads constructor(val docUuid: String, val notificationUuid: String? = null) : EventCardType()

    /**
     * Отобразить карточку вебинара / мероприятия
     * @param entityUuidOrName - здесь может передаваться как непосредственно [UUID] документа (вебинар/событие), так и
     * короткое наизвание (в случае именной ссылки)
     * либо url на трансляцию
     * @param notificationUuid - идентификатор уведомления
     * @param showTranslation - признак того, что необходимо после открытия карточки
     * сразу перейти на экран трансляции если она началась
     */
     data class Webinar @JvmOverloads constructor (val entityUuidOrName: String, val notificationUuid: String? = null,  val showTranslation: Boolean = false) : EventCardType()

    /**
     * Отобразить карточку создания совещания
     */
    data class MeetingCreation @JvmOverloads constructor(val startDate: Date? = null, val participantUUIDS: List<UUID>? = null, val type: EventCreationType = EventCreationType.CONFERENCE) : EventCardType()


    companion object {
        private const val serialVersionUID: Long = 9112345632194914679L
    }
}

/**
 * Перечисление возможных типов для создания события
 */
enum class EventCreationType {
    CONFERENCE,
    VIDEO_CONFERENCE
}