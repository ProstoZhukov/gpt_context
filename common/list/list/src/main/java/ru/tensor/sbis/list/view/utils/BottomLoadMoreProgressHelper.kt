package ru.tensor.sbis.list.view.utils

import androidx.annotation.CheckResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.list.view.adapter.SbisAdapter
import ru.tensor.sbis.list.view.decorator.DecoratorHolder
import ru.tensor.sbis.list.view.utils.ProgressItemPlace.*

/**
 * Класс для отображения индикатора постраничной подгрузки и отступа снизу.
 * Отступ для FAB снизу появляется только когда индикатора подгрузки не показывается.
 *
 * @property hasFabPaddingSubject BehaviorSubject<Boolean>
 * @property hasBottomLoadMoreProgressSubject BehaviorSubject<Boolean>
 * @property itemProgress ProgressItem
 * @constructor
 */
internal class BottomLoadMoreProgressHelper(
    var isHorizontal: Boolean = false,
    private val hasFabPaddingSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false),
    private val hasNavPaddingSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false),
    private val hasBottomLoadMoreProgressSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(
        false
    ),
    private val itemProgressBottom: ProgressItem = ProgressItem(BOTTOM),
    private val itemProgressLeft: ProgressItem = ProgressItem(LEFT)

) {
    /**
     * Виден ли элемент для отображения прогресса.
     */
    fun isAdded() = hasBottomLoadMoreProgressSubject.value == true

    /**
     * Прикрепление логики к [RecyclerView].
     * @param recyclerView RecyclerView
     * @param layoutManager LinearLayoutManager
     * @param adapter SbisAdapter
     * @param decoratorHolder DecoratorHolder
     * @return Disposable подписка, которую нужно освободить при уничтожении [RecyclerView]
     */
    @CheckResult
    fun attach(
        recyclerView: RecyclerView,
        layoutManager: LinearLayoutManager,
        adapter: SbisAdapter,
        decoratorHolder: DecoratorHolder
    ): Disposable {
        return CompositeDisposable(
            Observable.combineLatest(
                hasNavPaddingSubject,
                hasFabPaddingSubject,
                hasBottomLoadMoreProgressSubject,
                Function3<Boolean, Boolean, Boolean, Pair<Boolean, Boolean>>() { hasNavPadding, hasFabPadding, hasBottomLoadMoreProgress ->
                    return@Function3 Pair(
                        hasNavPadding && !hasBottomLoadMoreProgress,
                        hasFabPadding && !hasBottomLoadMoreProgress
                    )
                }
            ).subscribe {
                if (it.first || it.second) {
                    addLastItemBottomPaddingAndScrollIfNeeded(
                        recyclerView,
                        decoratorHolder,
                        it.first,
                        it.second
                    )
                } else {
                    removeLastItemBottomPadding(decoratorHolder, recyclerView)
                }
            },
            hasBottomLoadMoreProgressSubject
                .subscribe { hasLoadProgress ->
                    adapter.apply {
                        if (hasLoadProgress) {
                            addLast(getItemProgress())
                        } else removeLast(getItemProgress())
                    }
                }
        )
    }

    private fun getItemProgress() = if (isHorizontal) itemProgressLeft else itemProgressBottom

    /**
     * Добавить отступ снизу для FAB.
     * @param has Boolean
     */
    fun fabPadding(has: Boolean) {
        hasFabPaddingSubject.onNext(has)
    }

    /**
     * Добавить отступ снизу для навигационного меню.
     * @param has Boolean
     */
    fun navMenuPadding(has: Boolean) {
        hasNavPaddingSubject.onNext(has)
    }

    /**
     * Показать индикатор подгрузки.
     * @param has Boolean
     */
    fun setShowProgress(has: Boolean) {
        hasBottomLoadMoreProgressSubject.onNext(has)
    }

    private fun removeLastItemBottomPadding(
        decoratorHolder: DecoratorHolder,
        recyclerView: RecyclerView
    ) {
        decoratorHolder.removeLastItemBottomPadding(recyclerView)
    }

    private fun addLastItemBottomPaddingAndScrollIfNeeded(
        recyclerView: RecyclerView,
        decoratorHolder: DecoratorHolder,
        hasNavPadding: Boolean,
        hasFabPadding: Boolean
    ) {
        decoratorHolder.makeSureLastItemPaddingDecoratorIsAdded(recyclerView, hasNavPadding, hasFabPadding)
    }
}