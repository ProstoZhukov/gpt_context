package ru.tensor.sbis.design.period_picker.view.period_picker.big.ui

import android.view.View
import com.arkivanov.mvikotlin.core.view.BaseMviView
import ru.tensor.sbis.design.period_picker.databinding.PeriodPickerFragmentBinding
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerView.Model
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerView.Event
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.listener.CompleteButtonClickListener
import ru.tensor.sbis.design.period_picker.view.utils.updateDate
import java.util.Calendar

/**
 * Обертка над [View] фрагмента Большого выбора периода периода.
 *
 * @author mb.kruglova
 */
internal class PeriodPickerViewImpl(
    private val view: View,
    private val isSingleClick: Boolean,
    private val isOneDaySelection: Boolean,
    private val displayedRange: SbisPeriodPickerRange
) : BaseMviView<Model, Event>(), PeriodPickerView {

    private val binding = PeriodPickerFragmentBinding.bind(view)
    private val mode: SbisPeriodPickerMode
        get() = SbisPeriodPickerMode.getMode(binding.modeButton.isChecked)

    private var startPeriod: Calendar? = null
    private var endPeriod: Calendar? = null

    private val completeButtonListener = CompleteButtonClickListener(
        binding.root,
        binding.startDate,
        binding.endDate,
        isSingleClick,
        isOneDaySelection,
        displayedRange,
        ::dispatch
    )

    init {
        binding.modeButton.setOnClickListener {
            dispatch(Event.ClickModeButton(mode, startPeriod, endPeriod))
        }

        binding.currentDate.setOnClickListener {
            dispatch(Event.ClickCurrentDateButton)
        }

        binding.completeButton.setOnClickListener(completeButtonListener)
    }

    override fun render(model: Model) {
        startPeriod = model.getStartPresetPeriod()
        endPeriod = model.getEndPresetPeriod()

        // Обновить значения в полях ввода согласно измененным значениям в модели.
        binding.startDate.updateDate(startPeriod ?: model.getStartPeriod())
        binding.endDate.updateDate(endPeriod ?: model.getEndPeriod())

        binding.periodPickerPeriodDate.updateDate(
            startPeriod ?: model.getStartPeriod(),
            endPeriod ?: model.getEndPeriod()
        )

        binding.modeButton.isChecked = model.getMode() == SbisPeriodPickerMode.MONTH

        /* Обновить значения для слушателя события клика по кнопке окончания выбора периода
        согласно измененным значениям в модели.*/
        completeButtonListener.updatePeriod(startPeriod, endPeriod)
    }
}