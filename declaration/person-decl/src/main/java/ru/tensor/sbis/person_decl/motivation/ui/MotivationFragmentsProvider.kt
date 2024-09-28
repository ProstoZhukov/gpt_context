package ru.tensor.sbis.person_decl.motivation.ui

import androidx.fragment.app.Fragment
import ru.tensor.sbis.motivation_decl.features.common.FragmentOpenArgs
import ru.tensor.sbis.motivation_decl.features.common.ToolbarData
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.Date
import java.util.UUID

/**
 * Провайдинг экранов модуля "Мотивация".
 *
 * @author ra.temnikov
 */
interface MotivationFragmentsProvider : Feature {

    /**
     * Создание основного хост-фрагмента модуля мотивации с
     * внутренней реализацией навигации по экранам.
     * Содержит в себе все вкладки (Зарплата, Выплаты, Стимулы)
     * Использует реализацию Master-Detail компонента SbisList
     * @param startTab [MotivationStartTabs] вкладка, которую необходимо открыть.
     * @param showTabbarBackArrow [Boolean] флаг для отображения кнопки назад в таббаре, true - отображаем
     * @param needAddDefaultTopPadding [Boolean] флаг для добавление паддинга на величину StatusBarHeight, true - добавляем
     * @param swipeBackEnabledForHost [Boolean] флаг для включения swipeBack на хостовом фрагменте, true - включен
     * По умолчанию открывается [MotivationStartTabs.SALARY_TAB] "Зарплата"
     * @return [Fragment] который можно положить в свой контейнер
     */
    fun createMotivationFragment(
        startTab: MotivationStartTabs = MotivationStartTabs.SALARY_TAB,
        showTabbarBackArrow: Boolean = false,
        needAddDefaultTopPadding: Boolean = false,
        swipeBackEnabledForHost: Boolean = true,
    ): Fragment

    /**
     * Создание хост-фрагмента экрана Зарплата модуля мотивации с
     * внутренней реализацией навигации по экранам.
     * Содержит только фрагмент Зарплата (без Выплат и Стимулов)
     * @param personUUID - [UUID] пользователя
     * @param userName - [String] имя сотрудника
     * @param userDepartment - [String] компания сотрудника либо отдел
     * @param userImageURL - [String] ссылка на аватар сотрудника
     * @param needShowInternalToolbar - [Boolean] флаг отображение внутреннего тулбара
     * @param needShowPrePaymentBanner - [Boolean] флаг отображение баннера ближайшей выплаты и
     *                                   досрочного аванса.
     *@param needAddDefaultTopPadding - [Boolean] флаг добавления отступа под статус-бар

     * @return [Fragment] который можно положить в свой контейнер
     */
    fun createMotivationForDetailFragment(
        personUUID: UUID,
        userName: String? = null,
        userDepartment: String? = null,
        userImageURL: String? = null,
        needShowInternalToolbar: Boolean = true,
        needShowPrePaymentBanner: Boolean = false,
        needAddDefaultTopPadding: Boolean = false
    ): Fragment

    //region Методы для реализации навигации вне модуля
    /**
     * Создает экран с зарплатой пользователя.
     * @param personUUID - [UUID] пользователя
     * @param needShowPrePaymentBanner - необходимо ли показывать баннер ближайшей выплаты и
     * запроса досрочного аванса.
     */
    fun createMotivationSalaryForPerson(
        personUUID: UUID,
        needShowPrePaymentBanner: Boolean,
        receiverKey: String
    ): Fragment

    /**
     * Создает экран Расшифровка месяца/года.
     *
     * @param periodFrom - [Date] дата начала периода
     * @param periodTo - [Date] дата конца периода
     */
    fun createMotivationSalaryDetailScreen(
        periodFrom: Date,
        periodTo: Date,
        needAddDefaultTopPadding: Boolean
    ): Fragment

    /**
     * Создает экран Выплаты.
     */
    fun createPayoutsDetails(): Fragment

    /**
     * Создает экран Стимулы.
     */
    fun createStimulus(): Fragment


    /** Создает экран зарплаты */
    fun createSalary(
        personUuid: UUID,
        isTablet: Boolean,
        needShowClosestPaymentBanner: Boolean,
        needShowPayouts: Boolean,
        toolbarData: ToolbarData?,
        openArgs: FragmentOpenArgs
    ): Fragment

    /** Создает экран KPI */
    fun createKpi(
        personUuid: UUID,
        initialMonth: Date,
        toolbarData: ToolbarData?,
        openArgs: FragmentOpenArgs
    ): Fragment

    /** Создает экран рейтингов */
    fun createRatings(
        personUuid: UUID,
        isTablet: Boolean,
        toolbarData: ToolbarData?,
        openArgs: FragmentOpenArgs
    ): Fragment
    //endregion
}

/**
 * Событие выбора месяца по клику на айтем в списке месяцев (на разводящей)
 *
 * @param dateFrom начало периода текушего месяца
 * @param dateTo - конец периода текущего месяца
 */
data class SelectedMonthPeriodEvent(val dateFrom: Date, val dateTo: Date, val receiverKey: String)