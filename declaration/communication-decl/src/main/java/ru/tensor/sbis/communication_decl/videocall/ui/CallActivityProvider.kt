package ru.tensor.sbis.communication_decl.videocall.ui

import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Interface which provides CallActivity Intent
 *
 * @author is.mosin
 */
interface CallActivityProvider : Feature {

    /**
     * Инициирует и совершает SIP звонок посредством внутренней телефонии
     * либо отображает фрагмент [DialogFragment], предоставляющий список опций(способов) совершения звонка:
     * - Через нашу компанию. SIP-звонок через АТС компании
     * - С моего телефона - звонок через стандартную звонилку мобильного телефона.
     * - Если номер звонящего (с 4х значного), то сразу звонок через sip.
     * @param context - [Context] Вызывающий контекст
     * @param fragmentManager - [FragmentManager] FragmentManager для отображения диалога
     * @param memberToCallId - ID персоны или компании которой будет совершаться звонок
     * @param isSIPTelephonyCanBeUsed - признак того, хотим ли в принципе давать возможность звонка по
     * внутренней телефонии компании и отображать опции выбора для пользователя.
     * @param phoneNumber - номер телефона персоны илли организации
     * @param isCompany - [Boolean] признак звонка в организацию
     * @param calledName - опционально - имя пользователя или компании которой звоним.
     * если не указать и звонок совершается персоне то будет загружен профиль и данные взяты из него.
     * @param calledPhotoUrl - опционально - url фото для отображения на экране исходяшего звонка
     * если не указать и звонок совершается персоне то будет загружен профиль и данные взяты из него.
     */
    fun performSipOrPhoneCall(
        context: Context,
        fragmentManager: FragmentManager,
        memberToCallId: String?,
        isSIPTelephonyCanBeUsed: Boolean,
        phoneNumber: String?,
        isCompany: Boolean,
        calledName: String? = null,
        calledPhotoUrl: String? = null
    )

    /**
     * Совершение исходящего видеозвонка
     *
     * @param personId - UUID/Login персоны, которому совершаем звонок
     * @param isVideoCall - звонок с включеным видео или нет
     * @param phoneNumbers - список телефонов
     */
    fun performOutgoingCall(
        personId: String,
        isVideoCall: Boolean,
        phoneNumbers: List<String>? = null
    )

    /**
     * Совершение исходящего видеозвонка по ссылке на звонок
     *
     * @param context - Context
     * @param callHref - ссылка на звонок
     */
    fun performOutgoingCall(context: Context, callHref: String)

    /**
     * Совершение исходящего звонка в видеосовещание
     *
     * @param context           - Context
     * @param conversationId    - идентификатор виртуальной комнаты совещания или URI на звонок (VideoCallLink)
     * @param isVideoEnabled    - признак того, необходимо ли по умолчанию включить видео при совершении звонка
     * @param meetingId         - id совещания
     * @param meetingName       - название совещания
     * @param participantsUuids - список идентификаторов участников
     */
    fun performMeetingOutgoingCall(
        context: Context,
        conversationId: String,
        isVideoEnabled: Boolean,
        meetingId: String? = null,
        meetingName: String? = null,
        participantsUuids: List<String>? = null
    )
}