package ru.tensor.sbis.retail_decl

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.plugin_struct.feature.Feature

/** Фича для работы с часовыми поясами. */
interface TimeZonesFeature : Feature {

    /** @SelfDocumented */
    fun getTimeZonesDialogFragment(timeZoneName: String?, openedFromDevicesListScreen: Boolean = false): Fragment

    /** @SelfDocumented */
    fun getTimeZoneFormattedString(timeZoneName: String, timeZoneRusFullName: String): String

    /** @SelfDocumented */
    fun getTimeZoneSelectedEvent(timeZoneName: String?): TimeZoneSelectedEvent

    /** Подписка на результат. */
    fun getTimeZoneFragmentResult(
        fragmentManager: FragmentManager,
        lifecycleOwner: LifecycleOwner,
        actionOnResult: (timeZoneName: String) -> Unit
    )
}