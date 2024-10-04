package ru.tensor.sbis.design.folders.support

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import ru.tensor.sbis.common.util.SingleLiveEvent
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand.Companion.EMPTY
import ru.tensor.sbis.design.folders.data.model.AdditionalCommandType
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.FolderActionType
import ru.tensor.sbis.design.folders.data.model.FolderType
import ru.tensor.sbis.design.folders.data.model.ROOT_FOLDER_ID
import ru.tensor.sbis.design.folders.support.extensions.attach
import ru.tensor.sbis.design.folders.support.extensions.getNameById
import ru.tensor.sbis.design.folders.support.listeners.FolderActionListener
import ru.tensor.sbis.design.folders.support.presentation.FolderListViewMode
import ru.tensor.sbis.design.folders.support.utils.SerialLifecycleDisposable
import ru.tensor.sbis.design.folders.support.utils.actionhandlers.DefaultFolderActionHandler
import ru.tensor.sbis.design.folders.support.utils.actionhandlers.SelectionFolderActionHandler
import timber.log.Timber
import javax.inject.Inject

/**
 * Вьюмодель для упрощённого отображения компонента папок.
 * * Автоматически обрабатывает все колбэки и выдаёт наружу готовые результаты.
 * * Автоматически показывает свёрнутое\развёрнутое состояние и заголовок текущей папки, а также сохраняет
 * состояние при смене конфигурации.
 *
 * @param foldersProvider провайдер папок
 * @param openFolderByClick открывать ли папку при клике:
 *   true - отобразится заголовок с именем папки и сработает [FolderActionListener.opened]
 *   false - просто сработает [FolderActionListener.opened]
 *
 * @see attach
 * @see FolderListViewMode
 *
 * @author ma.kolpakov
 */
class FoldersViewModel @Inject constructor(
    internal val foldersProvider: FoldersProvider,
    openFolderByClick: Boolean = true,
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val _isCompact = MutableLiveData(true)
    private val _currentFolderName = SingleLiveEvent<String>()
    private val _additionalCommand = MutableLiveData(EMPTY)
    private val _folderListViewMode = MutableLiveData(FolderListViewMode.HIDDEN)
    private val _error = MutableLiveData<String?>()
    private val _selectedFolderId = MutableLiveData<String?>()
    private val _folders = MutableLiveData(emptyList<Folder>())

    internal val folders: LiveData<List<Folder>> = _folders
    val isCompact: LiveData<Boolean> = _isCompact
    val currentFolderName: LiveData<String> = _currentFolderName
    internal val collapsingFolders: LiveData<List<Folder>> = MediatorLiveData<List<Folder>>().apply {
        addSource(folders) { folderList -> value = folderList.filter { folder -> folder.id != ROOT_FOLDER_ID } }
    }
    internal val selectionFolders: LiveData<List<Folder>> = MediatorLiveData<List<Folder>>().apply {
        addSource(folders) { folderList ->
            value = folderList.filter { it.canMove }.map {
                it.copy(type = FolderType.DEFAULT)
            }
        }
    }
    internal val additionalCommand: LiveData<AdditionalCommand> = _additionalCommand
    internal val folderListViewMode: LiveData<FolderListViewMode> = _folderListViewMode
    internal val error: LiveData<String?> = _error
    internal val selectedFolderId: LiveData<String?> = _selectedFolderId
    internal val isVisible: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        value = false
        addSource(collapsingFolders) { foldersList ->
            value = foldersList.isNotEmpty() || additionalCommand.value != EMPTY
        }
        addSource(additionalCommand) { command ->
            value = collapsingFolders.value!!.isNotEmpty() || command != EMPTY
        }
    }.distinctUntilChanged()
    internal val dataUpdated: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(collapsingFolders) { value = isDataUpdated() }
        addSource(additionalCommand) { value = isDataUpdated() }
    }

    internal val folderActionHandler = FolderActionHandlerDelegate()

    internal val lifecycleDisposable = SerialLifecycleDisposable()

    internal var position: Int = 0

    init {
        foldersProvider
            .getFolders()
            .subscribe(_folders::setValue) {
                Timber.e(it, "Unable to load folders list")
                _error.value = it.localizedMessage
            }
            .addTo(disposables)

        foldersProvider
            .getAdditionalCommand()
            .subscribe(_additionalCommand::postValue) {
                Timber.e(it, "Unable to load an additional command")
                _error.value = it.localizedMessage
            }
            .addTo(disposables)

        folderActionHandler.folderAction.subscribe(
            { (action, folderId) ->
                if (openFolderByClick &&
                    action == FolderActionType.CLICK &&
                    _folderListViewMode.value != FolderListViewMode.SELECTION
                ) {
                    val folder = collapsingFolders.value?.getNameById(folderId)
                    _currentFolderName.value = folder.orEmpty()
                }
            },
            Timber::e
        ).addTo(disposables)
    }

    /**
     * Устанавливает состояние в строку/списком. Компонент должен быть в строку при возвращении из папки. Чтобы не
     * происходило скачков в реестре, сворачивать компонент в строку нужно после того, как открыта папка с новым
     * списком (компонент папок к этому моменту уже не виден на экране)
     */
    fun setFoldersCompact(isCompact: Boolean) {
        if (_isCompact.value != isCompact) {
            _isCompact.value = isCompact
        }
    }

    /**
     * Открывает панель для выбора папок.
     *
     * [folderId] - Id папки, которая будет помечена при открытии диалога с папками
     */
    fun onFolderSelectionClicked(folderId: String? = null) {
        _selectedFolderId.value = folderId
        _folderListViewMode.value = FolderListViewMode.SELECTION
    }

    /**
     * Инициирует открытие папки, без клика на нее
     */
    fun initiateFolderOpening(folderId: String) {
        folderActionHandler.handleAction(FolderActionType.CLICK, folderId)
    }

    fun onCurrentFolderClicked() {
        _isCompact.value = true
        _currentFolderName.value = ""
    }

    internal fun onMoreClicked() {
        _folderListViewMode.value = FolderListViewMode.DEFAULT
    }

    /**
     * Публикация события для закрытия панели всех элементов. Вызывается снаружи, чтобы обеспечить очерёдность
     * доставки событий в подписки до переключения, а так же при закрытии по кнопке "Назад"
     *
     * @see DefaultFolderActionHandler
     * @see SelectionFolderActionHandler
     */
    internal fun onHideFoldersPanel() {
        resetFolderListViewMode()
    }

    internal fun resetFolderListViewMode() {
        if (_folderListViewMode.value != FolderListViewMode.HIDDEN) {
            _folderListViewMode.value = FolderListViewMode.HIDDEN
            _selectedFolderId.value = null
        }
    }

    override fun onCleared() = disposables.dispose()

    private fun isDataUpdated(): Boolean {
        val areFoldersEmpty = collapsingFolders.value!!.isEmpty()
        val isAdditionalCommandEmpty = additionalCommand.value!!.type == AdditionalCommandType.EMPTY
        return areFoldersEmpty && isAdditionalCommandEmpty
    }
}
