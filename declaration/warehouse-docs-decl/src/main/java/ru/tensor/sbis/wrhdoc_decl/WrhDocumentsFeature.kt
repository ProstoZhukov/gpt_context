package ru.tensor.sbis.wrhdoc_decl

import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.settings_screen_decl.Item
import ru.tensor.sbis.wrhdoc_decl.base.view.UIHost
import ru.tensor.sbis.wrhdoc_decl.model.WrhDocumentsConfig
import ru.tensor.sbis.wrhdoc_decl.nomenclature.NomenclatureListComponent
import ru.tensor.sbis.wrhdoc_decl.nomenclature.NomenclatureListScanClickHandler
import ru.tensor.sbis.wrhdoc_decl.nomenclature.model.DocInfo
import ru.tensor.sbis.wrhdoc_decl.nomenclature.model.NomenclatureListConfig

/**
 * Публичный интерфейс ui модуля складские документы
 *
 * @author as.mozgolin
 */
interface WrhDocumentsFeature : Feature {

    /**
     * Создать хост фрагмент модуля.
     *
     * @param config - настройки главного экрана складских документов.
     */
    fun createDocumentHostFragment(config: WrhDocumentsConfig = WrhDocumentsConfig()): Fragment

    /**
     * Предоставляет аддон документов складского учета для встраивания в main_screen.
     *
     * @param config - настройки главного экрана складских документов.
     */
    fun createMainScreenAddon(config: WrhDocumentsConfig = WrhDocumentsConfig()): MainScreenAddon

    /**
     * Предоставляет аддон документов   для встраивания в main_screen.
     *
     * @param navItem - предоставляет возможность сконфигурировать и передать [NavigationItem] в отличие от дефолтного
     * @param config - настройки главного экрана складских документов.
     */
    fun createMainScreenAddon(
        navItem: NavigationItem,
        config: WrhDocumentsConfig = WrhDocumentsConfig()
    ): MainScreenAddon

    /**
     * Идентификатор аддона.
     */
    fun getWrhDocumentsMainScreenTabIdentifier(): String

    /**
     * Создает раздел настройки отображения документов
     */
    fun createRegistrySettingsItem(): Item

    /**
     * Создает компонент отображения наименований документа
     *
     * @param uiHost хост-экрана.
     * @param docInfo информация о документе.
     * @param config настройки отображения.
     * @param onScanClick прикладной обработчик кнопки сканирования.
     */
    fun createNomenclatureListComponent(
        uiHost: UIHost,
        docInfo: DocInfo,
        config: NomenclatureListConfig = NomenclatureListConfig(),
        onScanClick: NomenclatureListScanClickHandler? = null
    ): NomenclatureListComponent
}