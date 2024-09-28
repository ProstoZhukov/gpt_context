package ru.tensor.sbis.wrhdoc_decl.nomenclature

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.wrhdoc_decl.nomenclature.model.filter.NomenclatureFilter
import java.util.UUID

/**
 *  Реализует операции над списком наименований.
 *
 *  @author as.mozgolin
 */
interface NomenclatureListComponent {

    /**
     *  События компонента
     */
    val events: LiveData<Events>

    /**
     *  Состояние списка наименований
     */
    val state: StateFlow<NomenclatureListState>

    /**
     *  Режим редактирования активен.
     */
    fun setEditModeObservable(editMode: ObservableBoolean)

    /**
     *  Устанавливаем документ по uuid. Документ будет вычитан асинхронно.
     *  После установки документа придет событие [Events.DocumentInitiated]
     */
    fun setDocument(docUUID: UUID)

    /**
     *  Включение/отключение режима приёмки.
     */
    fun switchAcceptanceMode()

    /**
     * Установить фильтр для наименований.
     */
    fun setFilter(filter: NomenclatureFilter)

    /**
     * Принудительно обновить список номенклатур
     */
    fun refresh(force: Boolean = false)

    /**
     * Добавление номенклатуры сканированием. Откроется окно сканирования камерой телефона
     *
     * @return true - экран открыт,
     *         false - не октрыт, в случае когда документ на компоненте не установлен,
     *                 либо не вычитался еше. Нужно дождаться события [Events.DocumentInitiated]
     */
    fun addNomenclatureByScan(): Boolean

    /**
     * Выбор номенклатуры из каталога. Откроется окно выбора из каталога
     *
     * @return true - экран открыт,
     *         false - не октрыт, в случае когда документ на компоненте не установлен
     *                 либо не вычитался еше. Нужно дождаться события [Events.DocumentInitiated]
     */
    fun addNomenclatureFromCatalog(): Boolean

    /**
     * Открывает окно фильтра по наименованиям. Только для инвентаризации
     */
    fun showFilter()

    /**
     * Задать строку поиска по наименованиям.
     *
     * @param query: строка для поиска
     * @param isBarcodeSearchQuery: признак поиска баркода
     */
    fun search(query: String?, isBarcodeSearchQuery: Boolean = false)

    /** Открыть наименование документа */
    fun openNomenclature(uuid: UUID)

    /**
     * События для взаимодействия с карточкой документа
     */
    sealed interface Events {
        /**
         * Установлен и вычитан документ для списка наименований.
         * Приходит после того как документ вычитался, после вызова [setDocument]
         * или конструктора с указанным uuid документа
         */
        object DocumentInitiated : Events

        /**
         * Обновить документ
         */
        object RefreshDocument : Events

        /**
         * Закрыто окно наименования
         */
        object NomenclatureCardClosed : Events
    }

    /**
     * Состояние списка наименований
     *
     * @property data Список наименований
     * @property hasNomenclatures Наличие наименований
     * @property searchString Строка поиска
     * @property isSearchMode Режим поиска наименований
     * @property currentFilter Текущие значение фильтра
     * @property isFilterAvailable Поиск по фильтру
     * @property haveMore Есть ли еще наименования
     * @property isAcceptanceMode Состояние режима приёмки
     * @property allowAcceptanceMode Доступность режима приёмки
     * @property isLoading Загружаются наименования и серийники
     * @property documentInitiated Документ вычитан
     * @property isBarcodeSearch Происходит ли поиск по баркоду
     */
    data class NomenclatureListState(
        val data: List<AnyItem> = emptyList(),
        val hasNomenclatures: Boolean = false,
        val searchString: String? = null,
        val isSearchMode: Boolean = false,
        val currentFilter: NomenclatureFilter? = null,
        val isFilterAvailable: Boolean = false,
        val haveMore: Boolean = true,
        val isAcceptanceMode: Boolean = false,
        val allowAcceptanceMode: Boolean = false,
        val isLoading: Boolean = false,
        val documentInitiated: Boolean = false,
        val isBarcodeSearch: Boolean = false
    ) {
        val showNextSection: Boolean
            get() = (!haveMore || data.isEmpty()) && !isSearchMode
    }
}

/**
 * Интерфейс обработчика клика на сканирование
 */
typealias NomenclatureListScanClickHandler = () -> Unit