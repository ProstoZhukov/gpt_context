package ru.tensor.sbis.wheel_time_picker.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ru.tensor.sbis.design.container.ContainerViewModel
import ru.tensor.sbis.design.container.FragmentContent
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.wheel_time_picker.R
import ru.tensor.sbis.wheel_time_picker.data.PeriodPickerMode
import ru.tensor.sbis.wheel_time_picker.data.TimePickerParameters

/**
 * Реализация [FragmentContent] для диалога выбора даты и времени.
 *
 * @author us.bessonov
 */
internal class PeriodPickerDialogContainer(
    private val parameters: TimePickerParameters
) : FragmentContent {

    private val periodPickerMode: PeriodPickerMode?
        get() = parameters.periodPickerMode

    override fun theme() = if (periodPickerMode == PeriodPickerMode.START
        || periodPickerMode == PeriodPickerMode.ONE_DAY_DURATION) {
        R.style.TimeSelectionPeriodPickerDialogThemeOneDay
    } else {
        R.style.TimeSelectionPeriodPickerDialogTheme
    }

    private lateinit var fragment: PeriodPickerDialogContent
    private lateinit var containerViewModel: ContainerViewModel

    override fun getFragment(containerFragment: SbisContainerImpl): Fragment {
        fragment = PeriodPickerDialogContent.newInstance(parameters)
        initContent(containerFragment)

        return fragment
    }

    override fun onRestoreFragment(containerFragment: SbisContainerImpl, fragment: Fragment) {
        this.fragment = fragment as PeriodPickerDialogContent
        initContent(containerFragment)
    }

    private fun initContent(containerFragment: SbisContainerImpl) {
        containerViewModel = containerFragment.getViewModel()
        containerFragment.viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            containerViewModel.onCancelContainer.collect {
                fragment.onCancel()
            }
        }

        containerFragment.viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            containerViewModel.onDismissContainer.collect {
                fragment.onDismiss()
            }
        }
    }
}