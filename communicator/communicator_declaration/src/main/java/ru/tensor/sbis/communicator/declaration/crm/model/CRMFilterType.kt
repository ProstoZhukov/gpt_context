package ru.tensor.sbis.communicator.declaration.crm.model

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.declaration.R

/**
 * Маркерный интерфейс типов фильтрации.
 *
 * @author da.zhukov
 */
sealed interface CRMFilterType {
    @get:StringRes
    val textRes: Int
}

/**
 * Перечисление типов фильтрации по операторам.
 *
 * @author da.zhukov
 */
enum class CRMRadioButtonFilterType(override val textRes: Int) : CRMFilterType {

    /**
     * Все.
     */
    ALL(R.string.communicator_crm_chat_filter_all),

    /**
     * Мои.
     */
    MY(R.string.communicator_crm_chat_filter_my),

    /**
     * Из моих каналов.
     */
    FROM_MY_CHANNELS(R.string.communicator_crm_chat_filter_from_my_channels),

    /**
     * Для выбранных операторов.
     */
    DEFINED_OPERATORS(R.string.communicator_crm_chat_filter_responsible),

    /**
     * Для невыбранных операторов.
     */
    OPERATOR_FILTER_NOT_CHOSEN(R.string.communicator_crm_chat_operator_filter_not_chosen)
}

/**
 * Перечисление типов фильтрации по источникам и ответствееным.
 *
 * @author da.zhukov
 */
enum class CRMOpenableFilterType(override val textRes: Int) : CRMFilterType {

    /**
     * Ответственный.
     */
    RESPONSIBLE(R.string.communicator_crm_chat_filter_responsible),

    /**
     * Клиент.
     */
    CLIENT(R.string.communicator_crm_chat_filter_client),

    /**
     * Канал.
     */
    CHANNEL(R.string.communicator_crm_chat_filter_channel),

    /**
     * Источник.
     */
    SOURCE(R.string.communicator_crm_chat_filter_source)
}

/**
 * Перечисление типов фильтрации.
 *
 * @author da.zhukov
 */
enum class CRMCheckableFilterType(override val textRes: Int) : CRMFilterType {

    /**
     * Просрочка
     */
    EXPIRED(R.string.communicator_crm_chat_filter_overdue)
}