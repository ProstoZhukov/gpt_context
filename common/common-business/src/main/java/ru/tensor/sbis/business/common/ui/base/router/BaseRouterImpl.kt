package ru.tensor.sbis.business.common.ui.base.router

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.android_ext_decl.viewprovider.OverlayFragmentHolder
import ru.tensor.sbis.business.common.ui.base.fragment.SectionFragmentsContainer
import ru.tensor.sbis.business.common.ui.fragment.hasFragment
import ru.tensor.sbis.business.common.ui.fragment.popLastBackStackState
import ru.tensor.sbis.business.common.ui.utils.period.toDatePeriod
import ru.tensor.sbis.common.util.dateperiod.DatePeriod
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.decl.SbisShortPeriodPickerVisualParams
import ru.tensor.sbis.design.utils.checkSafe
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.event_bus.subscribeOnLifecycleScope
import ru.tensor.sbis.mvvm.fragment.CommandRunner
import java.util.Calendar
import java.util.UUID

/**
 * Базовая реализация роутера.
 */
abstract class BaseRouterImpl : BaseRouter, CommandRunner() {

    private companion object {
        /**
         * Список ключей с коллбэками, для синхронизации работы между разными роутерами,
         * так как [FragmentManager.setFragmentResultListener] не поддерживает множественные слушатели.
         */
        val resultListenerMap = mutableMapOf<UUID, Pair<List<String>, (DatePeriod, String) -> Unit>>()
    }

    /**
     * Дефолтная реализация фичи компонента Выбора периода, чтобы не пробрасывать фичу в модули в которых она не нужна.
     */
    override val periodPickerFeature: SbisPeriodPickerFeature? = null

    override fun subscribePeriodPickerResult(
        key: UUID,
        resultKeyList: List<String>,
        action: (DatePeriod, String) -> Unit
    ) {
        resultListenerMap[key] = resultKeyList to action
    }

    override fun unsubscribePeriodPickerResult(key: UUID) {
        resultListenerMap.remove(key)
    }

    /**
     * Выполнить возврат на предыдущий экран с реализацией по умолчанию
     * @see BaseRouter.goBack
     */
    override fun goBack() = runCommandSticky { fragment, _ ->
        fragment.parentFragmentManager.popLastBackStackState()
        /* вызывем извлечение стэка на фрагменте контейнере [SectionFragmentsContainer]
        * чтобы избежать ошибочной очистки стэка если контейнер фрагментов вложен в чужой хост */
        val targetFragmentManager = fragment.activity?.supportFragmentManager
            ?.getManagerFromTreeWhen { it.hasFragment<SectionFragmentsContainer>() }
        targetFragmentManager?.run {
            popLastBackStackState()
        }
    }

    override fun showPopupNotification(message: String, style: SbisPopupNotificationStyle) =
        runCommandSticky { fragment, _ ->
            SbisPopupNotification.push(fragment.requireContext(), style, message)
        }

    override fun showPopupNotification(@StringRes messageResId: Int, style: SbisPopupNotificationStyle) =
        runCommandSticky { fragment, _ ->
            val context = fragment.requireContext()
            SbisPopupNotification.push(context, style, context.getString(messageResId))
        }

    /**
     * @see BaseRouter.showToast
     */
    override fun showToast(message: String) =
        runCommand { context_provider, _ ->
            Toast.makeText(context_provider.context, message, Toast.LENGTH_SHORT)
                .show()
        }

    /**
     * @see BaseRouter.showToast
     */
    override fun showToast(@StringRes messageResId: Int) =
        runCommand { context_provider, _ ->
            val context = context_provider.context
            Toast.makeText(context, context?.getString(messageResId), Toast.LENGTH_SHORT)
                .show()
        }

    override fun showPeriodPicker(
        resultKey: String?,
        startValue: Calendar?,
        endValue: Calendar?,
        selectionType: SbisPeriodPickerSelectionType,
        minDate: Calendar?,
        maxDate: Calendar?
    ) = runCommandSticky { fragment, _ ->
        fragment.setPeriodPickerResultListener()
        val currentDate = Calendar.getInstance()
        periodPickerFeature?.showPeriodPicker(
            fragment.requireContext(),
            fragment.requireActivity().supportFragmentManager,
            selectionType = selectionType,
            displayedRanges = if (minDate != null || maxDate != null)
                listOf(
                    SbisPeriodPickerRange(
                        minDate,
                        maxDate
                    )
                )
            else null,
            startValue = startValue,
            endValue = endValue ?: startValue,
            presetStartValue = currentDate,
            presetEndValue = currentDate,
            resultKey = resultKey ?: SbisPeriodPickerFeature.periodPickerResultKey
        )
    }

    override fun showShortPeriodPicker(
        resultKey: String?,
        startValue: Calendar?,
        endValue: Calendar?
    ) = runCommandSticky { fragment, _ ->
        fragment.setPeriodPickerResultListener()
        periodPickerFeature?.showShortPeriodPicker(
            fragment.requireContext(),
            fragment.requireActivity().supportFragmentManager,
            SbisShortPeriodPickerVisualParams(
                arrowVisible = true,
                chooseHalfYears = true,
                chooseMonths = true,
                chooseQuarters = true,
                chooseYears = true
            ),
            startValue = startValue,
            endValue = endValue ?: startValue,
            resultKey = resultKey ?: SbisPeriodPickerFeature.periodPickerResultKey
        )
    }

    override fun showCompactPeriodPicker(
        resultKey: String?,
        startValue: Calendar?,
        endValue: Calendar?,
        selectionType: SbisPeriodPickerSelectionType,
        minDate: Calendar?,
        maxDate: Calendar?
    ) = runCommandSticky { fragment, _ ->
        fragment.setPeriodPickerResultListener()
        periodPickerFeature?.showCompactPeriodPicker(
            fragment.requireContext(),
            fragment.requireActivity().supportFragmentManager,
            selectionType = selectionType,
            displayedRanges = if (minDate != null || maxDate != null)
                listOf(
                    SbisPeriodPickerRange(
                        minDate,
                        maxDate
                    )
                )
            else null,
            startValue = startValue,
            endValue = endValue ?: startValue,
            resultKey = resultKey ?: SbisPeriodPickerFeature.periodPickerResultKey
        )
    }

    /**
     * Получение делегата для отображения экрана поверх вего контента приложения.
     * По умолчанию, пытается использовать Activity.
     *
     * @param check требуется ли валидация наличия делегата
     * @throws IllegalStateException в случае отсутствия делегата при условии взведенной проверки [check].
     * Только для отладочной версии.
     */
    fun Fragment.getOverlayFragmentHolder(check: Boolean = true): OverlayFragmentHolder? {
        if (check) {
            checkSafe(activity is OverlayFragmentHolder) { "Активити $activity не реализует интерфейс OverlayFragmentHolder" }
        }
        return activity as? OverlayFragmentHolder
    }

    /**
     * Установить слушатель получения результата периода и поиск коллбэка по ключу в списке.
     */
    private fun Fragment.setPeriodPickerResultListener() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            SbisPeriodPickerFeature.periodPickerRequestKey,
            requireActivity()
        ) { _, bundle ->
            resultListenerMap.forEach { (_, value) ->
                value.first.forEach { key ->
                    val result = bundle.getParcelableUniversally(key) as? SbisPeriodPickerRange
                    result?.run { value.second.invoke(toDatePeriod(), key) }
                }
            }
        }
    }

    private fun FragmentManager.getManagerFromTreeWhen(
        checkCondition: (FragmentManager) -> Boolean
    ): FragmentManager? {
        if (checkCondition(this)) {
            return this
        }
        for (fragment in fragments) {
            val nestedManager = fragment.childFragmentManager.getManagerFromTreeWhen(checkCondition)
            if (nestedManager != null) return nestedManager
        }
        return null
    }
}
