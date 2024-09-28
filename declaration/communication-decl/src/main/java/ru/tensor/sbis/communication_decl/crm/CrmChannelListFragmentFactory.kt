package ru.tensor.sbis.communication_decl.crm

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.io.Serializable
import java.util.UUID

/**
 * Фабрика для создания экрана с каналами crm.
 *
 * @author da.zhukov
 */
interface CrmChannelListFragmentFactory : Feature {

    /**
     * Создать фрагмент экрана каналов crm.
     *
     * @param case сценарий открытия списка каналов crm.
     */
    fun createCrmChannelListFragment(case: CrmChannelListCase): Fragment

    companion object {

        /**
         * Ключ получения результата для Fragment Result API при создании консультации со стороны оператора.
         */
        const val CRM_CHANNEL_CONSULTATION_RESULT = "CRM_CHANNEL_CONSULTATION_RESULT"

        /**
         * Ключ получения результата для Fragment Result API при переназначении другому оператору.
         */
        const val CRM_CHANNEL_OPERATOR_RESULT = "CRM_CHANNEL_OPERATOR_RESULT"

        /**
         * Ключ получения результата для originId при создании консультации со стороны оператора/переназначении другому оператору.
         */
        const val CRM_CHANNEL_ORIGIN_ID = "CRM_CHANNEL_ORIGIN_ID"

        /**
         * Ключ получения результата для channelName при переназначении консультации другому оператору.
         */
        const val CRM_CHANNEL_NAME = "CRM_CHANNEL_NAME"

        /**
         * Ключ получения результата для contactId при создании консультации со стороны оператора.
         */
        const val CRM_CHANNEL_CONSULTATION_CONTACT_ID = "CRM_CHANNEL_CONSULTATION_CONTACT_ID"

        /**
         * Ключ получения результата для channelsType при создании консультации со стороны оператора.
         */
        const val CRM_CHANNEL_CONSULTATION_CHANNEL_TYPE = "CRM_CHANNEL_CONSULTATION_CHANNEL_TYPE"
    }
}

/**
 * Сценарий открытия списка каналов crm.
 *
 * @author da.zhukov
 */
sealed interface CrmChannelListCase: Serializable {

    /**
     * Сценарий открытия списка каналов crm для переназначения консультации.
     *
     * @property consultationId идентификатор консультации.
     */
    data class CrmChannelReassignCase(val consultationId: UUID) : CrmChannelListCase

    /**
     * Сценарий открытия списка каналов crm для использовании в фильтре.
     * @property type          сценарий открытия каналов.
     * @property currentFilter текущий фильтр по каналам.
     */
    data class CrmChannelFilterCase(
        val type: CrmChannelFilterType = CrmChannelFilterType.REGISTRY,
        val currentFilter: Pair<ArrayList<UUID>, ArrayList<String>> = arrayListOf<UUID>() to arrayListOf()
    ) : CrmChannelListCase

    /**
     * Сценарий открытия списка каналов crm для создания консультации.
     *
     * @property originUuid идентификатор источника создания консультации(консультация/обращение).
     * @property consultationAuthorId идентификатор автора консультации.
     */
    data class CrmChannelConsultationCase(val originUuid: UUID, val consultationAuthorId: UUID?) : CrmChannelListCase
}

/**
 * Тип сценария открытия каналов для использования в фильтрации .
 */
enum class CrmChannelFilterType {
    /**
     * Для использования в фильтрации реестра.
     */
    REGISTRY,
    /**
     * Для использования в фильтрации переназначения другому оператору.
     */
    OPERATOR
}

