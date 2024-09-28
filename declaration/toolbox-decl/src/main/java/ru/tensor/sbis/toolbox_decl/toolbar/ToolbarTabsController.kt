package ru.tensor.sbis.toolbox_decl.toolbar

import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Предназначен для управления видимостью вкладок разделов приложений, согласно данным от микросервиса.
 *
 * @author us.bessonov
 */
interface ToolbarTabsController : Feature {

    /**
     * События изменения выбранной вкладки.
     */
    val tabSelectionFlow: StateFlow<NavxIdDecl?>

    /**
     * Начать внешнее управление вкладками [TabsView].
     */
    fun attachView(tabsView: TabsView)

    /**
     * Уведомить о событии изменения состава вкладок в компоненте.
     */
    fun onTabsChanged(view: TabsView)

    /**
     * Служебный метод для управления доступными вкладками.
     */
    fun updateTabs(tabs: Set<NavigationTab>)

    /**
     * Указать, должна ли быть отключена вкладка с заданным идентификатором.
     * Состав вкладок определяет сервис навигации, но при необходимости можно отключать вкладки, в зависимости от
     * прикладной логики. Отключение сохраняется до тех пор, пока явно не будет отменено.
     */
    fun setTabDisabled(navxId: NavxIdDecl, isDisabled: Boolean)

    /**
     * Сбросить прикладные настройки скрытия вкладок, заданные посредством [setTabDisabled].
     */
    fun resetDisabledTabs()

    /** @SelfDocumented */
    fun setPrimaryCounter(navxId: NavxIdDecl, count: Int)

    /** @SelfDocumented */
    fun setSecondaryCounter(navxId: NavxIdDecl, count: Int)

    /** @SelfDocumented */
    fun setSelectedTabValue(navxId: NavxIdDecl?)
}