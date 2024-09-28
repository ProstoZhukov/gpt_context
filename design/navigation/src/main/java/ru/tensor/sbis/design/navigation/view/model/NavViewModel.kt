package ru.tensor.sbis.design.navigation.view.model

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.design.navigation.view.model.content.EmptyItemContentViewModel
import ru.tensor.sbis.design.navigation.view.model.content.ItemContentViewModel
import ru.tensor.sbis.design.navigation.view.model.content.ItemContentViewModelImpl
import ru.tensor.sbis.design.navigation.view.model.content.NavigationItemContent
import ru.tensor.sbis.design.navigation.view.model.icon.NavigationIconViewModel
import ru.tensor.sbis.design.navigation.view.model.icon.NavigationIconViewModelImpl
import ru.tensor.sbis.design.navigation.view.model.label.NavigationLabelViewModel
import ru.tensor.sbis.design.navigation.view.model.label.NavigationLabelViewModelImpl
import ru.tensor.sbis.design.navigation.view.model.label.NavigationNavLabelViewModel
import ru.tensor.sbis.design.navigation.view.model.label.NavigationNavLabelViewModelImpl
import ru.tensor.sbis.design.navigation.view.view.navmenu.icon_button.NavIconButton

/**
 * Реализация [NavigationViewModel]. Необходимо освобождение ресурсов после использования [Disposable].
 *
 * @author ma.kolpakov
 * Создан 11/9/2018
 */
internal class NavViewModel<out E : NavigationItem> private constructor(
    private val item: E,
    override val state: BehaviorSubject<NavigationItemState>,
    override val navItemContent: NavigationItemContent?,
    override val navIconButton: NavIconButton?,
    private val onSelectListener: (E, String?) -> Unit
) : NavigationViewModel,
    NavigationIconViewModel by NavigationIconViewModelImpl(item.iconObservable, state),
    NavigationNavLabelViewModel by NavigationNavLabelViewModelImpl(item.labelObservable),
    NavigationLabelViewModel by NavigationLabelViewModelImpl(item.labelObservable),
    ItemContentViewModel by navItemContent?.run(::ItemContentViewModelImpl) ?: EmptyItemContentViewModel {

    constructor(
        item: E,
        content: NavigationItemContent?,
        counter: NavigationCounter?,
        navIconButton: NavIconButton?,
        onSelectListener: (E, String?) -> Unit
    ) : this(
        item,
        BehaviorSubject.createDefault(UnselectedState),
        content,
        navIconButton,
        onSelectListener
    ) {
        this.counter = counter
    }

    private val unreadCounter = BehaviorSubject.create<Int>()
    private val unviewedCounter = BehaviorSubject.create<Int>()
    private val totalCounter = BehaviorSubject.create<Int>()

    private var counter: NavigationCounter? = null

    private val compositeCounterObservable by lazy {
        createCompositeCounterObservable(
            unviewedCounter,
            unreadCounter
        )
    }

    override var ordinal: Int = 0
    override var parentOrdinal: Int? = null

    override val navViewUnviewedCounter: Observable<String> = unviewedCounter.map(DEFAULT_FORMAT::apply)
    override val navViewTotalCounter: Observable<String> = totalCounter.map(DEFAULT_FORMAT::apply)

    override val tabNavViewCounterObservable: Observable<Int>
        get() = when {
            counter?.useTotalCounterAsSecondary() == true -> compositeCounterObservable.map { it.count }
            counter?.useCounterFromController() == true -> counter?.newCounter ?: unviewedCounter
            else -> unviewedCounter
        }

    override val tabNavViewCounterUseSecondaryBackgroundColorObservable: Observable<Boolean>
        get() = when {
            counter?.useTotalCounterAsSecondary() == true -> createCompositeCounterObservable(
                unviewedCounter,
                unreadCounter
            ).map { it.isSecondary }

            else -> Observable.just(false)
        }

    override fun onSelect(sourceName: String) {
        onSelectListener(item, sourceName)
    }

    override fun updateCounters(counters: NavigationCounters) {
        unreadCounter.onNext(counters.unreadCounter)
        unviewedCounter.onNext(counters.unviewedCounter)
        totalCounter.onNext(counters.totalCounter)
    }
}