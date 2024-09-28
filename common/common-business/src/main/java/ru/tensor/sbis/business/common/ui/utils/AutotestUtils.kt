package ru.tensor.sbis.business.common.ui.utils

import android.view.View
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment

/**
 * Поставщик динамических идентификаторов экранов для автотестов
 */
interface TestableIdProvider {

    @IdRes
    fun <T : Fragment> optId(fragment: T, position: Int): Int?
}

/**
 * Навесить идентификатор экрана для автотестов.
 * После создания корневой вью фрагмента [Fragment.onCreateView] навесить идентификтор вызовом [View.setId]
 */
fun View?.applyTestId(fragment: Fragment): View? {
    (this == null || fragment !is TestableIdProvider) && return this
    val screenClass = fragment::class.java
    val serialNumber = fragment.parentFragmentManager.fragments.count {
        screenClass == it::class.java || screenClass.isInstance(it)
    }
    val testId = (fragment as TestableIdProvider).optId(fragment, serialNumber - 1) ?: return this
    if (testId != ResourcesCompat.ID_NULL) {
        this?.rootView?.id = testId
    }
    return this
}