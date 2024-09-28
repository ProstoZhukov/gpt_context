package ru.tensor.sbis.design.navigation.view.adapter

import android.view.View
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.design.navigation.view.model.NavigationViewModel

/**
 * Хелпер для создания view элемента навигации.
 *
 * @author ma.kolpakov
 */
internal interface NavigationViewHelper {

    /** Используется ли навигационный шрифт для иконок пунктов. */
    var isUsedNavigationIcons: Boolean

    /** Название создателя хелпера (ННП или аккордеон), используется для аналитики. */
    var sourceName: String

    /** @SelfDocumented */
    var lifecycleOwner: LifecycleOwner

    /** @SelfDocumented */
    fun createView(viewModel: NavigationViewModel): Pair<View, Disposable>

}