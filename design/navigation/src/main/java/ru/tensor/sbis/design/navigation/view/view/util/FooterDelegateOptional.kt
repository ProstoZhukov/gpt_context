package ru.tensor.sbis.design.navigation.view.view.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel

/**
 * Методы для установки подписки на [NavigationItemLabel] в подвале.
 *
 * Нужны для переименования подвала через сервис навигации.
 *
 * @author da.zolotarev
 */
interface FooterDelegateOptional<VB : ViewBinding> {

    /**
     * Подписаться на обновление [NavigationItemLabel].
     */
    fun setItemLabelSubscription(
        binding: VB,
        lifecycleOwner: LifecycleOwner,
        liveData: LiveData<NavigationItemLabel>,
    ) = Unit

    /**
     * @SelfDocumented
     */
    fun getNavigationItem(): NavigationItem? = null
}