package ru.tensor.sbis.design.navigation.view.adapter

import android.content.Context
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.design.navigation.view.model.NavigationViewModel
import ru.tensor.sbis.design.navigation.view.model.SelectedByUserState
import ru.tensor.sbis.design.navigation.view.model.SelectedSame
import ru.tensor.sbis.design.navigation.view.model.SelectedState
import ru.tensor.sbis.design.navigation.view.model.UnselectedState
import ru.tensor.sbis.design.navigation.view.view.navmenu.ItemTitleRightAlignmentHolder
import ru.tensor.sbis.design.navigation.view.view.navmenu.NavMenuItemView
import ru.tensor.sbis.design.navigation.view.view.navmenu.NavMenuItemViewController
import ru.tensor.sbis.design.navigation.view.view.navmenu.NavViewSharedStyle

/**
 * Реализация [NavigationViewHelper] для аккордеона.
 *
 * @author ma.kolpakov
 */
internal class NavMenuItemViewHelper(
    private val context: Context,
    @AttrRes private val defStyleAttr: Int,
    @StyleRes private val defStyleRes: Int,
    private val navMenuItemSharedDimens: NavViewSharedStyle
) : NavigationViewHelper {

    private val itemTitleRightAlignmentHolder = ItemTitleRightAlignmentHolder()

    override var isUsedNavigationIcons: Boolean
        set(value) {
            navMenuItemSharedDimens.isUsedNavigationIcons = value
        }
        get() = navMenuItemSharedDimens.isUsedNavigationIcons

    override lateinit var sourceName: String
    override lateinit var lifecycleOwner: LifecycleOwner

    override fun createView(viewModel: NavigationViewModel): Pair<View, Disposable> {
        val navMenuItemView = NavMenuItemView(
            context,
            null,
            defStyleAttr,
            defStyleRes,
            NavMenuItemViewController(itemTitleRightAlignmentHolder),
            navMenuItemSharedDimens
        )

        return navMenuItemView to bind(viewModel, navMenuItemView)
    }

    private fun bind(vm: NavigationViewModel, view: NavMenuItemView) = with(vm) {
        val disposable = CompositeDisposable()
        disposable.add(
            state.subscribe {
                view.isSelected = when (it) {
                    is SelectedByUserState, is SelectedState, is SelectedSame -> true
                    is UnselectedState -> false
                }
            }
        )
        disposable.add(navViewIcon.subscribe { view.setIconRes(it) })
        disposable.add(calendarDayNumber.subscribe { view.setIconCalendarDay(it) })
        disposable.add(navigationLabel.subscribe { view.setLabel(it) })
        navViewUnviewedCounter.observeOn(AndroidSchedulers.mainThread())?.subscribe {
            view.counter = it
        }?.let {
            disposable.add(it)
        }
        navViewTotalCounter.observeOn(AndroidSchedulers.mainThread())?.subscribe {
            view.counterSecondary = it
        }?.let { disposable.add(it) }

        view.content = navItemContent

        navItemContent?.apply {
            disposable.add(
                visibility.subscribe {
                    view.contentVisible = it
                }
            )

            disposable.add(
                isExpand.subscribe {
                    onExpandClicked(view, it)
                }
            )

            contentExpanded.observe(lifecycleOwner) {
                view.contentExpanded = it
            }
        }

        view.buttonIconRes = ResourcesCompat.ID_NULL

        view.ordinal = vm.ordinal

        vm.parentOrdinal?.let {
            val rightAlignmentDisposable = view.setRightAlignment(it)
            rightAlignmentDisposable?.let { disp ->
                disposable.add(disp)
            }
        }

        navIconButton?.let { iconButton ->
            disposable.add(
                iconButton.icon
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        view.buttonIconRes = it
                        view.iconButtonClickListener = { iconButton.clickListener.onIconClicked() }
                    }
            )
        }
        view.setOnClickListener { onSelect(sourceName) }

        view.expandIconButtonClickListener = {
            onExpandClicked(view)
        }
        disposable
    }
}
