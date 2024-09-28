/**
 * Набор адаптеров для биндинга, которые специфичны для компонентов навигации.
 *
 * @author ma.kolpakov
 * Создан 2/5/2019
 */
package ru.tensor.sbis.design.navigation.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.navigation.databinding.NavigationMenuFooterItemBinding
import ru.tensor.sbis.design.navigation.databinding.NavigationMenuHeaderBinding
import ru.tensor.sbis.design.navigation.databinding.NavigationMenuLogoHeaderBinding
import ru.tensor.sbis.design.navigation.view.model.NavigationFooterViewModel
import ru.tensor.sbis.design.navigation.view.model.NavigationHeaderData
import ru.tensor.sbis.design.navigation.view.model.NavigationHeaderViewModel
import ru.tensor.sbis.design.navigation.view.model.NavigationItemState
import ru.tensor.sbis.design.navigation.view.model.SelectedByUserState
import ru.tensor.sbis.design.navigation.view.model.SelectedSame
import ru.tensor.sbis.design.navigation.view.model.SelectedState
import ru.tensor.sbis.design.navigation.view.model.UnselectedState

/** Фабрика создания дополнительного контента. */
internal typealias ItemContentFactory = (context: Context, parent: ViewGroup) -> View

/**
 * Установка состояния активности [View.isActivated] в зависимости от состояния элемента меню.
 *
 * @param state новое состояние [NavigationItemState] элемента меню
 */
@BindingAdapter("selected")
internal fun View.setActivateByState(state: NavigationItemState?) {
    state ?: return
    isSelected = when (state) {
        is SelectedByUserState, is SelectedState, is SelectedSame -> true
        is UnselectedState -> false
    }
}

/**
 * Добавить view, созадаваемую в [createContentView].
 */
@BindingAdapter("itemContent")
internal fun ViewGroup.setContent(createContentView: ItemContentFactory?) {
    if (createContentView == null) {
        removeAllViews()
    } else {
        addView(createContentView(context, this))
    }
}

/** @SelfDocumented */
internal fun NavigationMenuFooterItemBinding.bindViewModel(
    vm: NavigationFooterViewModel,
    lifecycleOwner: LifecycleOwner
) {
    initAndObserve(vm.newCounter, lifecycleOwner) {
        badgeLeft.text = it
        badgeLeft.isVisible = !it.isNullOrEmpty()
    }
    initAndObserve(vm.countersDividerVisible, lifecycleOwner) {
        badgeDivider.isVisible = it ?: false
    }
    initAndObserve(vm.totalCounter, lifecycleOwner) {
        badgeRight.text = it
        badgeRight.isVisible = !it.isNullOrEmpty()
    }
}

/** @SelfDocumented */
internal fun NavigationMenuLogoHeaderBinding.bindViewModel(
    vm: NavigationHeaderViewModel,
    lifecycleOwner: LifecycleOwner
) {
    root.setOnClickListener {
        vm.onClicked()
    }

    (vm.data as? NavigationHeaderData.LogoData)?.let {
        logoView.type = it.logo
    }

    initAndObserve(vm.isSelected, lifecycleOwner) {
        root.isSelected = it ?: false
        navigationHeaderMarker.visibility = if (it == true) View.VISIBLE else View.INVISIBLE
    }
    initAndObserve(vm.newCounter, lifecycleOwner) {
        badgeLeft.text = it
        badgeLeft.isVisible = !it.isNullOrEmpty()
        smoothlyLogoCutting()
    }
    initAndObserve(vm.countersDividerVisible, lifecycleOwner) {
        badgeDivider.isVisible = it ?: false
        smoothlyLogoCutting()
    }
    initAndObserve(vm.totalCounter, lifecycleOwner) {
        badgeRight.text = it
        badgeRight.isVisible = !it.isNullOrEmpty()
        smoothlyLogoCutting()
    }
}

/**
 * Затенить изображение в шапке.
 */
@Suppress("unused")
internal fun NavigationMenuHeaderBinding.smoothlyLogoCutting() {
    val badgesGroupWidth = badgeLeft.marginStart + badgeLeft.width + badgeLeft.marginEnd +
        badgeDivider.marginStart + badgeDivider.width + badgeDivider.marginEnd +
        badgeRight.marginStart + badgeRight.width + badgeRight.marginEnd + root.paddingEnd
    val logoWidth = logo.marginStart + logo.width + logo.marginEnd
    // Проверяем помещается ли лого без обрезания
    if (root.width > 0 && root.width < badgesGroupWidth + logoWidth) {
        logo.updateLayoutParams { width = root.width - badgesGroupWidth - logo.marginStart - logo.marginEnd }
        logo.fadeEdgeSize = root.context.resources.getDimensionPixelSize(R.dimen.navigation_menu_item_fade_size)
    }
}

/**
 * Затенить изображение в шапке.
 */
internal fun NavigationMenuLogoHeaderBinding.smoothlyLogoCutting() {
    val badgesGroupWidth = badgeLeft.marginStart + badgeLeft.width + badgeLeft.marginEnd +
        badgeDivider.marginStart + badgeDivider.width + badgeDivider.marginEnd +
        badgeRight.marginStart + badgeRight.width + badgeRight.marginEnd + root.paddingEnd
    val logoWidth = logoContainer.marginStart + logoContainer.width + logoContainer.marginEnd
    // Проверяем помещается ли контейнер лого без обрезания
    if (root.width > 0 && root.width < badgesGroupWidth + logoWidth) {
        logoContainer.updateLayoutParams {
            width = root.width - badgesGroupWidth - logoContainer.marginStart - logoContainer.marginEnd
        }
        logoContainer.fadeEdgeSize =
            root.context.resources.getDimensionPixelSize(R.dimen.navigation_menu_item_fade_size)
    }
}

/** @SelfDocumented */
internal fun <T> initAndObserve(
    liveData: LiveData<T>,
    lifecycleOwner: LifecycleOwner,
    onChanged: (T?) -> Unit
) {
    onChanged(liveData.value)
    liveData.observe(lifecycleOwner) {
        onChanged(it)
    }
}