package ru.tensor.sbis.design.selection.ui.list

import androidx.annotation.CheckResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchMode
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchMode.PREFETCH
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchMode.RELOAD
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.base.domain.ListInteractor
import ru.tensor.sbis.list.base.domain.boundary.View

private typealias SelectionEntity<T, U, V> = SelectionListScreenEntity<T, U, V>

/**
 * @author ma.kolpakov
 */
internal class SelectionListInteractorImpl<SERVICE_RESULT : Any, FILTER, ANCHOR>(
    private val interactorDelegate: ListInteractor<SelectionEntity<SERVICE_RESULT, FILTER, ANCHOR>>,
    lifecycle: Lifecycle,
    private val isCacheEnabled: Boolean = true,
    private val disposable: SerialDisposable = SerialDisposable()
) : SelectionListInteractor<SERVICE_RESULT, FILTER, ANCHOR, SelectionEntity<SERVICE_RESULT, FILTER, ANCHOR>>,
    LifecycleObserver,
    Disposable by disposable,
    ListInteractor<SelectionEntity<SERVICE_RESULT, FILTER, ANCHOR>> by interactorDelegate {

    /**
     * Количество выбранных элементов и спользуется для того, чтобы запрашивать подгрузку только при
     * добавлении в список выбора. При удалении из списка, элемент перемещается в общий список без
     * обращения к контроллеру
     */
    private var selectionCount = 0

    init {
        lifecycle.addObserver(this)
    }

    override fun applySelection(
        selection: List<SelectorItemModel>,
        entity: SelectionEntity<SERVICE_RESULT, FILTER, ANCHOR>,
        view: View<SelectionEntity<SERVICE_RESULT, FILTER, ANCHOR>>
    ) {
        entity.setSelection(selection)
        when {
            selection.size > selectionCount || !isCacheEnabled && selection.size != selectionCount -> {
                selectionCount = selection.size
                disposable.set(updateEntity(entity, view))
            }
            entity.isStub() -> view.showStub(entity, immediate = true)
            else -> view.showData(entity)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun dispose() {
        disposable.dispose()
        interactorDelegate.dispose()
    }

    @CheckResult
    private fun updateEntity(
        entity: SelectionEntity<SERVICE_RESULT, FILTER, ANCHOR>,
        view: View<SelectionEntity<SERVICE_RESULT, FILTER, ANCHOR>>
    ): Disposable {
        return synchronized(entity) {
            val prefetchMode = entity.needToPrefetch()
            when {
                entity.isStub() -> {
                    view.showStub(entity, immediate = true)
                    Disposables.disposed()
                }
                prefetchMode != null -> update(prefetchMode, entity, view)
                else -> {
                    view.showData(entity)
                    Disposables.disposed()
                }
            }
        }
    }

    @CheckResult
    private fun update(
        prefetchMode: PrefetchMode,
        entity: SelectionEntity<SERVICE_RESULT, FILTER, ANCHOR>,
        view: View<SelectionEntity<SERVICE_RESULT, FILTER, ANCHOR>>
    ) = when (prefetchMode) {
        PREFETCH -> {
            when {
                entity.hasNext() -> nextPage(entity, view)
                entity.hasPrevious() -> previousPage(entity, view)
                else -> {
                    view.showData(entity)
                    Disposables.disposed()
                }
            }
        }
        RELOAD -> refresh(entity, view)
    }
}