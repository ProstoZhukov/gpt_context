/**
 * Расширения для вьюмодели папок
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.folders.support.extensions

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.common.util.hasFragmentOrPendingTransaction
import ru.tensor.sbis.design.folders.FoldersView
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.support.FoldersViewModel
import ru.tensor.sbis.design.folders.support.extensions.dialogs.showCreateDialog
import ru.tensor.sbis.design.folders.support.listeners.FolderActionListener
import ru.tensor.sbis.design.folders.support.listeners.FoldersDataUpdateListener
import ru.tensor.sbis.design.folders.support.presentation.AllFoldersDialogFragment
import ru.tensor.sbis.design.folders.support.presentation.FolderListViewMode
import ru.tensor.sbis.design.folders.support.utils.SerialLifecycleDisposable
import ru.tensor.sbis.design.folders.support.utils.actionhandlers.DefaultFolderActionHandler
import ru.tensor.sbis.design.folders.support.utils.actionhandlers.FoldersActionConsumer
import ru.tensor.sbis.design.folders.support.utils.actionhandlers.SelectionFolderActionHandler
import ru.tensor.sbis.design.folders.support.utils.stub_integration.FolderListChangesObserver
import ru.tensor.sbis.design.folders.support.utils.stub_integration.StubViewMediator
import ru.tensor.sbis.design.folders.support.utils.viewModelKey
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.ContainerBottomSheet
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.MovablePanelVisualMode
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreator
import ru.tensor.sbis.design_notification.SbisPopupNotification
import timber.log.Timber
import ru.tensor.sbis.common.R as RCommon

private const val LAYOUT_UPDATE_DELAY = 10L

private const val ALL_FOLDERS_DEFAULT_FRAGMENT_TAG = "ALL_FOLDERS_DEFAULT_FRAGMENT_TAG"
private const val ALL_FOLDERS_SELECTION_FRAGMENT_TAG = "ALL_FOLDERS_SELECTION_FRAGMENT_TAG"

/**
 * Прикрепление ко view.
 * Необходимо вызывать в onCreate для активити
 *
 * @param foldersView компонент папок
 * @param actionsListener слушатель действий над папками
 * @param dataUpdateListener слушатель обновления данных
 * @param viewModelKey ключ, по которому можно получить [FoldersViewModel] из [host]
 *
 * @see detach
 */
fun FoldersViewModel.attach(
    host: FragmentActivity,
    foldersView: FoldersView,
    actionsListener: FolderActionListener,
    dataUpdateListener: FoldersDataUpdateListener? = null,
    viewModelKey: String? = null
) = attach(foldersView, host.supportFragmentManager, host, actionsListener, dataUpdateListener, viewModelKey)

/**
 * Освобождение подписок панели папок [foldersView] на жизненный цикл [host]
 *
 * @see attach
 */
fun FoldersViewModel.detach(
    host: FragmentActivity,
    foldersView: FoldersView
) = detach(host as LifecycleOwner, foldersView)

/**
 * Прикрепление ко view.
 * Необходимо вызывать в onCreateView для фрагментов
 *
 * @param foldersView компонент папок
 * @param actionsListener слушатель действий над папками
 * @param dataUpdateListener слушатель обновления данных
 * @param viewModelKey ключ, по которому можно получить [FoldersViewModel] из [host]
 *
 * @see detach
 */
fun FoldersViewModel.attach(
    host: Fragment,
    foldersView: FoldersView,
    actionsListener: FolderActionListener,
    dataUpdateListener: FoldersDataUpdateListener? = null,
    viewModelKey: String? = null,
) = attach(
    foldersView,
    host.childFragmentManager,
    host.viewLifecycleOwner,
    actionsListener,
    dataUpdateListener,
    viewModelKey,
)

/**
 * Освобождение подписок панели папок [foldersView] на жизненный цикл [Fragment.getViewLifecycleOwner] от [host]
 *
 * @see attach
 */
fun FoldersViewModel.detach(
    host: Fragment,
    foldersView: FoldersView
) = detach(host.viewLifecycleOwner, foldersView)

/**
 * Создаёт обект для управления высотой заглушки внутри [container]
 *
 * @param container [RecyclerView] или другой контейнер, где располагаются папки и заглушка
 */
fun FoldersViewModel.createStubViewMediator(host: FragmentActivity, container: View) =
    createStubViewMediator(host as LifecycleOwner, container)

/**
 * Создаёт обект для управления высотой заглушки внутри [container]
 *
 * @param container [RecyclerView] или другой контейнер, где располагаются папки и заглушка
 */
fun FoldersViewModel.createStubViewMediator(host: Fragment, container: View) =
    createStubViewMediator(host.viewLifecycleOwner, container)

/**
 * Основной метод, где настраивается взаимодействие view и view model
 */
private fun FoldersViewModel.attach(
    foldersView: FoldersView,
    fragmentManager: FragmentManager,
    lifecycleOwner: LifecycleOwner,
    actionsListener: FolderActionListener,
    dataUpdateListener: FoldersDataUpdateListener? = null,
    viewModelKey: String?,
) {
    currentFolderName.observe(lifecycleOwner) { folderName ->
        if (folderName.isEmpty()) {
            actionsListener.closed()
        } else {
            foldersView.showCurrentFolder(folderName)
        }
    }

    lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            foldersView.compactFoldersViewListPosition = position
        }

        override fun onDestroy(owner: LifecycleOwner) {
            position = foldersView.compactFoldersViewListPosition
        }
    })

    isCompact.observe(
        lifecycleOwner,
        { compact ->
            if (compact && !(additionalCommand.value != AdditionalCommand.EMPTY && folders.value?.size ?: 0 <= 1)) {
                foldersView.showCompactFolders()
            } else {
                foldersView.showFullFolders()
            }
        }
    )

    foldersView.compactFoldersViewListPosition = position

    collapsingFolders.observe(lifecycleOwner, foldersView::setFolders)

    additionalCommand.observe(lifecycleOwner, foldersView::setAdditionalCommand)

    val defaultActionHandler = DefaultFolderActionHandler(
        foldersView.context.applicationContext,
        this,
        fragmentManager,
        actionsListener,
        viewModelKey,
    )
    val selectionActionHandler = SelectionFolderActionHandler(this, actionsListener)
    val disposable = lifecycleDisposable
    lifecycleOwner.lifecycle.addObserver(disposable)
    folderListViewMode.observe(lifecycleOwner) { foldersViewMode ->
        if (foldersView.onlyRootFolder() && foldersViewMode == FolderListViewMode.SELECTION) {
            showCreateDialog(foldersView.context.applicationContext, fragmentManager, "", viewModelKey, true)
        } else {
            disposable.set(
                showAllFolders(
                    foldersViewMode,
                    viewModelKey,
                    fragmentManager,
                    defaultActionHandler,
                    selectionActionHandler
                )
            )
        }
    }

    error.observe(lifecycleOwner) { errorMessage ->
        val message = errorMessage.takeUnless { it.isNullOrEmpty() }
            ?: foldersView.resources.getString(RCommon.string.common_unknown_error)
        SbisPopupNotification.pushToast(foldersView.context, message)
    }

    selectedFolderId.observe(lifecycleOwner) { id ->
        foldersView.setSelectedFolder(id)
    }

    isVisible.observe(lifecycleOwner, { foldersView.isVisible = it })

    dataUpdateListener?.let {
        dataUpdated.observe(lifecycleOwner, it::updated)
    }

    foldersView.setActionHandler(this.folderActionHandler)
    foldersView.onFoldStateChanged(::setFoldersCompact)
    foldersView.onCurrentFolderClicked(::onCurrentFolderClicked)
    foldersView.onMoreClicked(::onMoreClicked)

    updateWidth(foldersView)
}

private fun FoldersViewModel.createStubViewMediator(
    lifecycleOwner: LifecycleOwner,
    container: View
): StubViewMediator {
    val mediator = StubViewMediator(container)
    val folderListObserver = FolderListChangesObserver(mediator)

    additionalCommand.observe(lifecycleOwner) { command ->
        folderListObserver.isExistAdditionalCommand = command != AdditionalCommand.EMPTY
    }
    isCompact.observe(lifecycleOwner) { isCompact ->
        folderListObserver.isFolderViewCompact = isCompact
    }
    collapsingFolders.observe(lifecycleOwner, folderListObserver)

    return mediator
}

/**
 * Освобождение подписок на [lifecycleOwner] и [foldersView].
 * Подписка [SerialLifecycleDisposable] не захватывается в [FoldersViewModel] (и не очищается), чтобы не утекал контекст
 * и fragment manager. События публикуются вовне и не затрагивают [foldersView]
 */
private fun FoldersViewModel.detach(lifecycleOwner: LifecycleOwner, foldersView: FoldersView) {
    currentFolderName.removeObservers(lifecycleOwner)
    isCompact.removeObservers(lifecycleOwner)
    collapsingFolders.removeObservers(lifecycleOwner)
    additionalCommand.removeObservers(lifecycleOwner)
    folderListViewMode.removeObservers(lifecycleOwner)
    error.removeObservers(lifecycleOwner)
    isVisible.removeObservers(lifecycleOwner)
    dataUpdated.removeObservers(lifecycleOwner)
    lifecycleDisposable.set(null)
    if (foldersView.onlyRootFolder()) resetFolderListViewMode()

    foldersView.clearListeners()
}

/**
 * Принудительная установка ширины панели папок.
 * При добавлении панели папок в RecyclerView не всегда корректно отрабатывало, пришлось добавить дилей.
 */
private fun updateWidth(foldersView: FoldersView) {
    foldersView.postDelayed(
        {
            foldersView.doOnLayout {
                it.updateLayoutParams {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                }
            }
        },
        LAYOUT_UPDATE_DELAY
    )
}

@CheckResult
private fun FoldersViewModel.showAllFolders(
    foldersViewMode: FolderListViewMode,
    viewModelKey: String?,
    fragmentManager: FragmentManager,
    defaultActionHandler: DefaultFolderActionHandler,
    selectionActionHandler: SelectionFolderActionHandler
): Disposable {
    return when (foldersViewMode) {
        FolderListViewMode.HIDDEN -> {
            val allFoldersFragment =
                (
                    fragmentManager.findFragmentByTag(ALL_FOLDERS_DEFAULT_FRAGMENT_TAG)
                        ?: fragmentManager.findFragmentByTag(ALL_FOLDERS_SELECTION_FRAGMENT_TAG)
                    )
                    as ContainerBottomSheet?
            allFoldersFragment?.closeContainer()
            folderActionHandler.folderAction.subscribe(
                FoldersActionConsumer(this, defaultActionHandler),
                Timber::e
            )
        }
        FolderListViewMode.DEFAULT -> {
            fragmentManager.apply {
                if (!hasFragmentOrPendingTransaction(ALL_FOLDERS_DEFAULT_FRAGMENT_TAG)) {
                    ContainerBottomSheet()
                        .setVisualMode(MovablePanelVisualMode(Gravity.START))
                        .setContentCreator(createAllFolderDialogCreator(viewModelKey))
                        .show(this, ALL_FOLDERS_DEFAULT_FRAGMENT_TAG)
                }
            }
            folderActionHandler.folderAction.subscribe(
                FoldersActionConsumer(this, defaultActionHandler),
                Timber::e
            )
        }
        FolderListViewMode.SELECTION -> {
            fragmentManager.apply {
                if (!hasFragmentOrPendingTransaction(ALL_FOLDERS_SELECTION_FRAGMENT_TAG)) {
                    ContainerBottomSheet()
                        .setVisualMode(MovablePanelVisualMode(Gravity.START))
                        .setContentCreator(createAllFolderDialogCreator(viewModelKey))
                        .show(this, ALL_FOLDERS_SELECTION_FRAGMENT_TAG)
                }
            }
            folderActionHandler.folderAction.subscribe(
                FoldersActionConsumer(this, selectionActionHandler),
                Timber::e
            )
        }
    }
}

private fun createAllFolderDialogCreator(viewModelKey: String?): ContentCreator = ContentCreator {
    AllFoldersDialogFragment().apply {
        arguments = Bundle().apply {
            this.viewModelKey = viewModelKey
        }
    }
}