package ru.tensor.sbis.logging.settings.view

import ru.tensor.sbis.base_components.adapter.vmadapter.ViewHolder
import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter
import ru.tensor.sbis.logging.BR
import ru.tensor.sbis.logging.R
import ru.tensor.sbis.logging.databinding.LoggingSettingsItemLogQueryPlanBinding
import ru.tensor.sbis.logging.databinding.LoggingSettingsItemWifiSliderBinding
import ru.tensor.sbis.logging.settings.model.CategoryVm
import ru.tensor.sbis.logging.settings.model.LogQueryPlanVm
import ru.tensor.sbis.logging.settings.model.OptionVm
import ru.tensor.sbis.logging.settings.model.WifiUploadVm

/**
 * Адаптер для преставления списка настроек.
 *
 * @param categoryClick
 * @param optionClick
 * @param wifiUploadChange
 *
 * @author av.krymov
 */
internal class LogSettingsAdapter(
    private val categoryClick: (category: CategoryVm) -> Unit,
    private val optionClick: (option: OptionVm) -> Unit,
    private val wifiUploadChange: (isWifiUpload: Boolean) -> Unit,
    private val logQueryPlanChange: (enabled: Boolean) -> Unit
) : ViewModelAdapter() {

    init {
        cell<CategoryVm>(
            layoutId = R.layout.logging_settings_item_category,
            areItemsTheSame = { _, _ -> true }
        )
        cell<OptionVm>(
            layoutId = R.layout.logging_settings_item_value,
            bindingId = BR.viewModel
        )
        cell<WifiUploadVm>(
            layoutId = R.layout.logging_settings_item_wifi_slider,
            areItemsTheSame = { _, _ -> true }
        )
        cell<LogQueryPlanVm>(
            layoutId = R.layout.logging_settings_item_log_query_plan,
            areItemsTheSame = { _, _ -> true }
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        when (val item = getViewModel(position)) {
            is CategoryVm -> holder.itemView.setOnClickListener { categoryClick.invoke(item) }
            is OptionVm -> holder.itemView.setOnClickListener { optionClick.invoke(item) }
            is WifiUploadVm -> {
                with(holder.binding as LoggingSettingsItemWifiSliderBinding) {
                    loggingWifiSwitch.apply {
                        isChecked = item.isWifiUpload
                        setOnCheckedChangeListener { _, isWifiUpload ->
                            wifiUploadChange(isWifiUpload)
                        }
                    }
                }
            }
            is LogQueryPlanVm -> {
                with(holder.binding as LoggingSettingsItemLogQueryPlanBinding) {
                    loggingLogToDbSwitch.apply {
                        isChecked = item.enabled
                        setOnCheckedChangeListener { _, enabled ->
                            logQueryPlanChange(enabled)
                        }
                    }
                }
            }
        }
    }
}