package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter

import android.content.Context
import android.content.SharedPreferences
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.communicator.declaration.crm.model.CRMRadioButtonFilterType
import ru.tensor.sbis.consultations.generated.ConsultationOperatorFilterMode
import ru.tensor.sbis.consultations.generated.ConsultationService

private const val CRM_CHAT_FILTER_PREFERENCES = "CRM_CHAT_FILTER_PREFERENCES"

private const val CRM_CHAT_FILTER_IS_EXPIRED = "CRM_FILTER_IS_EXPIRED"
private const val CRM_CHAT_FILTER_TYPE = "CRM_CHAT_FILTER_TYPE"
private const val CRM_CHAT_FILTER_OPERATOR_IDS = "CRM_CHAT_FILTER_OPERATOR_IDS"
private const val CRM_CHAT_FILTER_CLIENTS_IDS = "CRM_CHAT_FILTER_CLIENTS_IDS"
private const val CRM_CHAT_FILTER_CHANNELS_IDS = "CRM_CHAT_FILTER_CHANNELS_IDS"
private const val CRM_CHAT_FILTER_SOURCE_IDS = "CRM_CHAT_FILTER_SOURCE_IDS"
private const val CRM_CHAT_FILTER_OPERATOR_VALUE = "CRM_CHAT_FILTER_OPERATOR_VALUE"
private const val CRM_CHAT_FILTER_CLIENTS_VALUE = "CRM_CHAT_FILTER_CLIENTS_VALUE"
private const val CRM_CHAT_FILTER_CHANNELS_VALUE = "CRM_CHAT_FILTER_CHANNELS_VALUE"
private const val CRM_CHAT_FILTER_SOURCE_VALUE = "CRM_CHAT_FILTER_SOURCE_VALUE"
private const val CRM_CHAT_FILTERS_TITLE = "CRM_CHAT_FILTER_TITLE"

/**
 * Класс для работы с [SharedPreferences].
 * Инкапсулирует логику работы с [SharedPreferences] для модуля чаты оператора.
 *
 * @author da.zhukov
 */
internal class CRMChatFilterPreferences(context: Context) {

    private val prefs = context.getSharedPreferences(CRM_CHAT_FILTER_PREFERENCES, Context.MODE_PRIVATE)
    private val currentFilterModel by lazy {
        CRMChatFilterModel(
            isExpired = prefs.getBoolean(CRM_CHAT_FILTER_IS_EXPIRED, false),
            type = CRMRadioButtonFilterType.values()[prefs.getInt(CRM_CHAT_FILTER_TYPE, operatorFilter.ordinal)],
            operatorIds = (prefs.getStringSet(CRM_CHAT_FILTER_OPERATOR_IDS, null)?.mapTo(ArrayList()) { UUIDUtils.fromString(it) } ?: arrayListOf()) to (prefs.getStringSet(CRM_CHAT_FILTER_OPERATOR_VALUE, null)?.asArrayList() ?: arrayListOf()),
            clientIds = (prefs.getStringSet(CRM_CHAT_FILTER_CLIENTS_IDS, null)?.mapTo(ArrayList()) { UUIDUtils.fromString(it) } ?: arrayListOf()) to (prefs.getStringSet(CRM_CHAT_FILTER_CLIENTS_VALUE, null)?.asArrayList() ?: arrayListOf()),
            channelIds = (prefs.getStringSet(CRM_CHAT_FILTER_CHANNELS_IDS, null)?.mapTo(ArrayList()) { UUIDUtils.fromString(it) } ?: arrayListOf()) to (prefs.getStringSet(CRM_CHAT_FILTER_CHANNELS_VALUE, null)?.asArrayList() ?: arrayListOf()),
            sourceIds = (prefs.getStringSet(CRM_CHAT_FILTER_SOURCE_IDS, null)?.mapTo(ArrayList()) { UUIDUtils.fromString(it) } ?: arrayListOf()) to (prefs.getStringSet(CRM_CHAT_FILTER_SOURCE_VALUE, null)?.asArrayList() ?: arrayListOf())
        )
    }

    private val currentFilterTitle by lazy {
        prefs.getString(
            CRM_CHAT_FILTERS_TITLE,
            null
        ) ?: if (operatorFilter == ConsultationOperatorFilterMode.MINE) {
            context.getString(CRMRadioButtonFilterType.MY.textRes)
        } else {
            StringUtils.EMPTY
        }
    }

    private val operatorFilter by lazy {
        if (ConsultationService.instance().getIsCurrentUserOperator()) {
            ConsultationOperatorFilterMode.MINE
        } else {
            ConsultationOperatorFilterMode.ALL
        }
    }

    /**
     * Сохранить состаяние фильтра.
     */
    fun saveState(filter: CRMChatFilterModel, filterTitle: List<String>) {
        prefs.edit()
            .apply {
                putBoolean(CRM_CHAT_FILTER_IS_EXPIRED, filter.isExpired)
                putInt(CRM_CHAT_FILTER_TYPE, filter.type.ordinal)
                putStringSet(CRM_CHAT_FILTER_OPERATOR_IDS, filter.operatorIds.first.mapTo(LinkedHashSet()) { it.toString() })
                putStringSet(CRM_CHAT_FILTER_CLIENTS_IDS, filter.clientIds.first.mapTo(LinkedHashSet()) { it.toString() })
                putStringSet(CRM_CHAT_FILTER_CHANNELS_IDS, filter.channelIds.first.mapTo(LinkedHashSet()) { it.toString() })
                putStringSet(CRM_CHAT_FILTER_SOURCE_IDS, filter.sourceIds.first.mapTo(LinkedHashSet()) { it.toString() })
                putStringSet(CRM_CHAT_FILTER_OPERATOR_VALUE, filter.operatorIds.second.toSet())
                putStringSet(CRM_CHAT_FILTER_CLIENTS_VALUE, filter.clientIds.second.toSet())
                putStringSet(CRM_CHAT_FILTER_CHANNELS_VALUE, filter.channelIds.second.toSet())
                putStringSet(CRM_CHAT_FILTER_SOURCE_VALUE, filter.sourceIds.second.toSet())
                putString(CRM_CHAT_FILTERS_TITLE, filterTitle.joinToString())
            }
            .apply()
    }

    /**
     * Получить текущее состаяние фильтра.
     */
    fun currentState(): CRMChatFilterModel {
        return currentFilterModel
    }

    /**
     * Получить текущую строку в фильтре.
     */
    fun currentFilterTitle(): String {
        return currentFilterTitle
    }

    /**
     * Сбрасывает все сохранённые параметры.
     */
    fun clear() {
        prefs.edit()
            .apply { clear() }
            .apply()
    }
}