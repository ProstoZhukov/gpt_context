package ru.tensor.sbis.catalog_decl.catalog

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 *  Зона доступа модуля. Служит для проверки прав пользователя к реестру, карточке номенклатуры.
 */
const val PERMISSION_CATALOG = "Catalog"

/**
 * Описывает функционал, который данный модуль предоставляет
 *
 * @author sp.lomakin
 */
interface CatalogFeature : Feature {

    /**
     * Корзина, хранит количество и uuid номенклатур
     */
    fun getCatalogCartDataService(context: Context): CatalogCartDataService

    /**
     * Поставщик элементов каталога
     */
    fun getCatalogItemProvider(context: Context): CatalogItemProvider

    /**
     *  Реестр каталога на CRUD4
     */
    fun createCatalogHostFragment(params: CatalogParams = CatalogParams()): Fragment

    /**
     * Метод позволяет создавать фрагмент с детальной информацией по номенклатуре.
     *
     * Можно задать заголовок для Toolbar forceToolbarTitleStringId или forceToolBarTitle,
     * иначе будет использовано название категории
     *
     * @param nomenclatureUUID уникальный идентификатор номенклатуры
     * @param displayReports нужно ли загружать и отображать отчёты по номенклатуре. по умолчанию false
     * @param forceToolbarTitleStringId заголовок для Toolbar, имеет наивысший приоритет
     * @param forceToolBarTitle заголовок для Toolbar
     * @param swipeBackEnable скрывать фрагмент по свайпу
     * @param displayModifier нужно ли отображать модификаторы номенклатуры. по умолчанию false
     * @param editModeActive доступно ли редактирование карточки.
     * @param restrictTransitionToCodes ограничить переход на экран кодов
     */
    fun createNomenclatureFragment(
        nomenclatureUUID: UUID,
        displayReports: Boolean = false,
        @StringRes forceToolbarTitleStringId: Int? = null,
        forceToolBarTitle: String? = null,
        swipeBackEnable: Boolean = false,
        displayModifier: Boolean = false,
        layoutId: Int? = null,
        editModeActive: Boolean = false,
        restrictTransitionToCodes: Boolean = false
    ): Fragment

    /**
     *  @see createNomenclatureFragment
     *
     *  [nomenclatureId] целочисленный идентификатор номенклатуры.
     */
    fun createNomenclatureFragment(
        nomenclatureId: Long,
        editModeActive: Boolean = false,
        needModifyScrollState: Boolean = true
    ): Fragment

    /**
     *  Создать фрагмент для выбора номенклатуры.
     *
     *  Фрагмент реализует навигацию между категориями.
     *  Для обработки BackPress реализует ru.tensor.sbis.common.fragment.FragmentBackPress
     *
     *  Отправляет события в [CatalogExternalNavigationEventsProvider]
     *  Нажата кнопка "закрыть" [ClickToCloseCatalog]
     *  Выбрана номенклатура [SelectionNomenclaturesFromCatalog]
     *
     *  @see getExternalNavigationEventsProvider
     *
     *  @param priceListId идентификатор прайс листа
     *  @param initialSearchQuery поисковой запрос
     *  @param supportTransitionOnMultiSelectionMode поддерживается ли переключение в режим нескольких элементов.
     */
    fun createChooseFromCatalogHostFragment(
        initialSearchQuery: String?,
        priceListId: Long? = null,
        supportTransitionOnMultiSelectionMode: Boolean = true,
    ): Fragment

    /**
     *  Контракт фрагмента для выбора номеклатур каталога.
     */
    fun createCatalogSelectHostContract(
        requestKey: String = "CATALOG_SELECT_HOST_CONTRACT"
    ): FragmentContract<CatalogSelectHostFactory, CatalogSelectHostResult>

    /**
     * Фабрика фрагмента выбора номенклатуры
     */
    interface CatalogSelectHostFactory {
        fun create(
            dataParams: CatalogListDataParams,
            forceShowBackBtn: Boolean? = null,
            catalogListViewConfig: CatalogListViewConfig = CatalogListViewConfig(),
            asSideWindow: Boolean = false
        ): DialogFragment
    }

    /**
     * Результат фрагмента выбора номенклатуры
     */
    sealed interface CatalogSelectHostResult : Parcelable {
        @Parcelize
        data class SelectedItem(
            val item: CatalogItemData
        ) : CatalogSelectHostResult

        @Parcelize
        object Close : CatalogSelectHostResult
    }

    /**
     *  Поставщик внешних событий
     *
     *  @param appContext реализация CatalogSingletonComponentHolder
     */
    fun getExternalNavigationEventsProvider(appContext: Context): CatalogExternalNavigationEventsProvider

    /**
     *  Параметры экрана реестр номенклатур (Каталог).
     *
     *  @property displayReports нужно ли загружать и отображать отчёты по номенклатуре. по умолчанию false
     *  @property editModeActive режим редактирования. по умолчанию выключен
     *  @property catalogListViewConfig [CatalogListViewConfig]
     */
    @Parcelize
    data class CatalogParams(
        val displayReports: Boolean = false,
        val editModeActive: Boolean = false,
        val catalogListViewConfig: CatalogListViewConfig = CatalogListViewConfig()
    ) : Parcelable

    /**
     *  Параметры для запроса данных.
     */
    @Parcelize
    data class CatalogListDataParams(
        /**
         *  Родительсткая категория.
         */
        val parentFolder: CatalogItemData? = null,

        /**
         *  Идентификатор прайс листа.
         */
        val priceListId: Long? = null,

        /**
         *  Фильтровать по складу.
         */
        val byWarehouse: FilterByWarehouse? = null,

        /**
         *  Фильтровать по организации.
         */
        val byOrganization: FilterByOrganization? = null,

        /**
         * Необходимо ли открывать каталог в режиме редактирования.
         */
        val editModeActive: Boolean = false,

        /**
         *  Фильтровать по категории.
         */
        val byCategory: List<CategoryType>? = null,

        /**
         *  Фильтровать по весовым товарам.
         */
        val byMeasureUnitList: List<MeasureUnit>? = null,

        /**
         *  Фильтровать по типу учета наименования.
         */
        var byAccounting: List<AccountingType>? = null,

        /**
         *  Фильтровать по типу подвида учета наименования.
         */
        var bySubAccounting: List<SubAccounting>? = null,

        /**
         *  Фильтровать по кеговому пиву.
         */
        var byDraftBeer: Boolean = false,

        /**
         *  Показывать разделы и/или номенклатуры.
         *  true - разделы, false - номенклатуры, null - всё
         */
        var foldersOrLeafs: Boolean? = null,

        /**
         *  Сортировка.
         */
        var sort: CatalogSortType? = null,

        /**
         *  Тип фильтра (изменяет логику сохранения фильтра).
         */
        var filterType: FilterType = FilterType.CATALOG
    ) : Parcelable

    /**
     *  Типы фильтра (откуда был открыт каталог)
     */
    enum class FilterType {
        DOCUMENT,
        CATALOG
    }

    /**
     *  Конфигурация списка номенклатур
     */
    @Parcelize
    data class CatalogListViewConfig(

        /**
         *  Использовать групировку категорий в режиме поиска.
         */
        val groupCategoriesInSearchMode: Boolean = false,

        /**
         *  Режим выбора резделов.
         */
        val selectFolders: Boolean = false,

        /**
         *  Доступен фильтр.
         */
        val filterAvailable: Boolean = false,

        /**
         *  Доступна сортировка.
         */
        val sortingAvailable: Boolean = true,

        /**
         * Визуализация реестра в новой компоновке без тулбара.
         * - сканер штрихкодов вынесен из тулбара в fab кнопку
         * - для навигации назад используется разделитель-заголовка 'CurrentFolderView'
         */
        val newDesign: Boolean = false,

        /**
         * Необходимость показа тулбара для нового дизайна
         *
         * @see newDesign
         */
        val needToolbar: Boolean = false,

        /**
         * Будет ли показываться на полный экран каталог. Если будет, то перекрашивается статусбар.
         */
        val fullScreenStatusBar: Boolean = false,

        /**
         * Доступность отображения складских остатков в списке номенклатур.
         */
        val stockBalanceEnabled: Boolean = true,

        /**
         *  Нужна ли задержка при клике по номенклатуре.
         */
        var needClickNomenclatureTimeout: Boolean = false

    ) : Parcelable

    /**
     *  Фильтр по складу.
     */
    @Parcelize
    class FilterByWarehouse(val warehouseId: Long?, val warehouseName: String?) : Parcelable

    /**
     *  Фильтр по организации.
     */
    @Parcelize
    class FilterByOrganization(val organizationId: Long?, val organizationName: String?) : Parcelable

    /**
     *  Выбранна директория каталога.
     */
    class ClickFolder(
        val catalogFolder: CatalogItemData?,
        val filterByWarehouse: FilterByWarehouse? = null,
        val filterByOrganization: FilterByOrganization? = null,
        val sort: CatalogSortType? = null,
    ) : NavigationEvent

    /**
     *  Выбрана директория или номенклатура каталога.
     */
    class SelectItem(val item: CatalogItemData) : NavigationEvent

    /**
     *  Выбрано несколько директорий каталога.
     */
    class SelectFolders(val catalogFolders: List<CatalogItemData>) : NavigationEvent

    /**
     *  Выбранна номенклатура.
     */
    class ClickNomenclature(val catalogNomenclature: CatalogItemData) : NavigationEvent

    /**
     *  Множественный выбор номенклатур из каталога
     */
    class SelectionNomenclaturesFromCatalog(val items: List<CatalogItemData>) : NavigationEvent

    /**
     *  Корзина
     */
    object ClickCartBtn : NavigationEvent

    /**
     *  Оплата
     */
    object ClickPaymentBtn : NavigationEvent

    /**
     * Клик по кнопке добавления раздела в каталог.
     */
    class ClickAddFolder(val parentUuid: UUID?) : NavigationEvent

    /**
     *  Нажата кнопка закрыть каталог
     */
    object ClickToCloseCatalog : NavigationEvent

    /**
     * Событие о необходимости сбросить скролл.
     */
    object ResetScrollState : NavigationEvent
}