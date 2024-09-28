package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.store

import android.content.Context
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.communicator.declaration.crm.model.CRMCheckableFilterType
import ru.tensor.sbis.communicator.declaration.crm.model.CRMRadioButtonFilterType

/**
 * Вспомогательный класс для управления фильтрами CRM чата.
 *
 * @param context Контекст для доступа к ресурсам.
 * @param initFilterModel Модель фильтра начального состояния.
 * @param defFilterModel Модель фильтра по умолчанию.
 *
 * @author da.zhukov
 */
internal class CrmChatFilterHelper(
    private val context: Context,
    private val initFilterModel: CRMChatFilterModel,
    private val defFilterModel: CRMChatFilterModel
) {

    private val currentFilters: MutableSet<String> = mutableSetOf()
    private val availableSelectedTypesFilters by lazy {
        listOf(
            context.getString(CRMRadioButtonFilterType.MY.textRes),
            context.getString(CRMRadioButtonFilterType.FROM_MY_CHANNELS.textRes)
        )
    }

    /**
     * Возвращает модель фильтра по умолчанию.
     *
     * @return Модель фильтра по умолчанию.
     */
    fun defFilterModel(): CRMChatFilterModel {
        return defFilterModel
    }

    /**
     * Возвращает модель начального фильтра.
     *
     * @return Модель начального фильтра.
     */
    fun initFilterModel(): CRMChatFilterModel {
        return initFilterModel
    }

    /**
     * Устанавливает фильтры на значения по умолчанию и возвращает их.
     *
     * @return Набор строковых значений фильтров по умолчанию.
     */
    fun defFilter(): Set<String> {
        currentFilters.clear()
        return currentFilters.apply {
            prepareSelectedFilterTitle(defFilterModel.type)
            prepareExpiredFilterTitle(defFilterModel.isExpired)
        }
    }

    /**
     * Устанавливает фильтры на значения начального состояния и возвращает их.
     *
     * @return Множество строковых значений начальных фильтров.
     */
    fun initFilter(): MutableSet<String> {
        currentFilters.clear()
        currentFilters.apply {
            prepareSelectedFilterTitle(initFilterModel.type)
            prepareExpiredFilterTitle(initFilterModel.isExpired)
            prepareFilterEntity(
                type = initFilterModel.type,
                operatorsNames = initFilterModel.operatorIds.second,
                clientsNames = initFilterModel.clientIds.second,
                channelsNames = initFilterModel.channelIds.second,
                sourcesNames = initFilterModel.sourceIds.second,
            )
        }
        return currentFilters
    }

    /**
     * Возвращает фильтры с обновленным выбранным типом.
     *
     * @param type Новый выбранный тип фильтра.
     * @return Множество строковых значений фильтров с новым выбранным типом.
     */
    fun filtersWithNewSelectedType(type: CRMRadioButtonFilterType, operatorNames: List<String>): Set<String> {
        currentFilters.prepareSelectedFilterTitle(type, operatorNames)
        return currentFilters
    }

    /**
     * Возвращает фильтры с обновленным состоянием просрочки.
     *
     * @param isExpired Новое состояние просрочки фильтра.
     * @return Множество строковых значений фильтров с обновленным состоянием просрочки.
     */
    fun filtersWithNewExpiredState(isExpired: Boolean): Set<String> {
        currentFilters.prepareExpiredFilterTitle(isExpired)
        return currentFilters
    }

    /**
     * Возвращает фильтры с обновленными сущностями.
     *
     * @param isExpired Новое состояние просрочки фильтра
     * @param type Новый тип фильтра.
     * @param operatorsNames Список имен операторов.
     * @param clientsNames Список имен клиентов.
     * @param channelsNames Список имен каналов.
     * @param sourcesNames Список имен источников.
     * @return Множество строковых значений фильтров с обновленными сущностями.
     */
    fun filtersWithNewEntity(
        isExpired: Boolean,
        type: CRMRadioButtonFilterType,
        operatorsNames: ArrayList<String>,
        clientsNames: ArrayList<String>,
        channelsNames: ArrayList<String>,
        sourcesNames: ArrayList<String>
    ): Set<String> {
        currentFilters.clear()
        currentFilters.prepareSelectedFilterTitle(type, operatorsNames)
        currentFilters.prepareExpiredFilterTitle(isExpired)
        currentFilters.prepareFilterEntity(
            type,
            operatorsNames,
            clientsNames,
            channelsNames,
            sourcesNames
        )
        return currentFilters
    }

    /**
     * Подготавливает заголовок фильтра для состояния просрочки.
     *
     * @param isExpired Состояние просрочки.
     */
    private fun MutableSet<String>.prepareExpiredFilterTitle(isExpired: Boolean) {
        val expiredFilterTitle = context.getString(CRMCheckableFilterType.EXPIRED.textRes)
        if (isExpired) {
            add(expiredFilterTitle)
        } else {
            remove(expiredFilterTitle)
        }
    }

    /**
     * Подготавливает заголовок выбранного фильтра.
     *
     * @param type Тип фильтра.
     */
    private fun MutableSet<String>.prepareSelectedFilterTitle(
        type: CRMRadioButtonFilterType,
        operatorNames: List<String>? = null) {

        // сначала удалим все доступные выбранные фильтры, чтобы после добавить один нужный
        availableSelectedTypesFilters.forEach {
            this.remove(it)
        }
        val newSelectedFilter = context.getString(type.textRes)
        val needShowFilterTitle = availableSelectedTypesFilters.contains(newSelectedFilter)

        if (needShowFilterTitle) {
            add(newSelectedFilter)
        }
        if (type != CRMRadioButtonFilterType.DEFINED_OPERATORS) {
            operatorNames?.forEach {
                this.remove(it)
            }
        }
    }

    /**
     * Подготавливает сущности фильтра.
     *
     * @param type Тип фильтра.
     * @param operatorsNames Список имен операторов.
     * @param clientsNames Список имен клиентов.
     * @param channelsNames Список имен каналов.
     * @param sourcesNames Список имен источников.
     */
    private fun MutableSet<String>.prepareFilterEntity(
        type: CRMRadioButtonFilterType,
        operatorsNames: ArrayList<String>,
        clientsNames: ArrayList<String>,
        channelsNames: ArrayList<String>,
        sourcesNames: ArrayList<String>
    ) {
        if (type == CRMRadioButtonFilterType.DEFINED_OPERATORS) addAll(operatorsNames)
        addAll(clientsNames)
        addAll(channelsNames)
        addAll(sourcesNames)
    }
}
