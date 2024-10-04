package ru.tensor.sbis.design.navigation.view.view.tabmenu.list

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.lifecycle.LifecycleOwner
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.design.counters.sbiscounter.InfoSbisCounterStyle
import ru.tensor.sbis.design.counters.sbiscounter.PrimarySbisCounterStyle
import ru.tensor.sbis.design.navigation.view.adapter.NavigationViewHelper
import ru.tensor.sbis.design.navigation.view.model.NavigationViewModel
import ru.tensor.sbis.design.navigation.view.model.SelectedByUserState
import ru.tensor.sbis.design.navigation.view.model.SelectedSame
import ru.tensor.sbis.design.navigation.view.model.SelectedState
import ru.tensor.sbis.design.navigation.view.model.UnselectedState
import ru.tensor.sbis.design.navigation.view.view.tabmenu.item.HorizontalTabItemView
import ru.tensor.sbis.design.navigation.view.view.tabmenu.item.TabItemViewApi
import ru.tensor.sbis.design.navigation.view.view.tabmenu.item.TabMenuItemSharedPaints
import ru.tensor.sbis.design.navigation.view.view.tabmenu.item.VerticalTabItemView
import timber.log.Timber

/**
 * Реализация [NavigationViewHelper] для ННП.
 *
 * @author ma.kolpakov
 */
internal class TabNavigationViewHelper(
    private val context: Context,
    attrs: AttributeSet?,
    @AttrRes private val defStyleAttr: Int,
    @StyleRes private val defStyleRes: Int,
    private val isHorizontal: Boolean
) : NavigationViewHelper {

    private val sharedPaints =
        TabMenuItemSharedPaints(context, attrs, defStyleAttr, defStyleRes, true)

    override var isUsedNavigationIcons: Boolean
        set(value) {
            sharedPaints.isUsedNavigationIcons = value
        }
        get() = sharedPaints.isUsedNavigationIcons

    override lateinit var sourceName: String
    override lateinit var lifecycleOwner: LifecycleOwner
    override fun createView(viewModel: NavigationViewModel): Pair<View, Disposable> {
        val itemView = if (isHorizontal)
            HorizontalTabItemView(context, null, defStyleAttr, defStyleRes, sharedPaints)
        else
            VerticalTabItemView(context, null, defStyleAttr, defStyleRes, sharedPaints)
        return itemView to bind(viewModel, itemView)
    }

    private fun <T> bind(vm: NavigationViewModel, view: T) where T : View, T : TabItemViewApi = with(vm) {
        val disposable = CompositeDisposable()
        disposable.add(
            state.subscribe {
                view.isSelected = when (it) {
                    is SelectedByUserState, is SelectedState, is SelectedSame -> true
                    is UnselectedState -> false
                }
            }
        )
        disposable.add(tabNavViewIcon.subscribe { view.setIconRes(it) })
        disposable.add(calendarDayNumber.subscribe { view.setIconCalendarDay(it) })
        disposable.add(navigationLabel.subscribe { view.viewLabel = it })

        /*
        Обнуление счётчика элементов, чтобы при связывании не оставалось прежнее значение. Новая
        подписка может никогда не опубликовать собственного значения, чтобы обновить.
         */
        view.counter = 0

        disposable.add(
            tabNavViewCounterObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ view.counter = it }, { Timber.w(it) })
        )
        disposable.add(
            tabNavViewCounterUseSecondaryBackgroundColorObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    view.counterStyle = if (it) InfoSbisCounterStyle else PrimarySbisCounterStyle
                }
        )
        view.setOnClickListener { onSelect(sourceName) }
        disposable
    }
}