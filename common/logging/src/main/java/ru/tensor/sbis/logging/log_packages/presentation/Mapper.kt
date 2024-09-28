package ru.tensor.sbis.logging.log_packages.presentation

import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.list.view.binding.BindingItem
import ru.tensor.sbis.list.view.binding.DataBindingViewHolderHelper
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.Options
import ru.tensor.sbis.logging.R
import ru.tensor.sbis.platform.logdelivery.generated.LogPackageViewModel

/**
 * Маппер модели контролера в элемент списка с опциями отображения.
 */
class Mapper(
    private val callback: LogPackageViewHolderCallback,
    private val clipboardCopier: ClipboardCopier
) : ItemInSectionMapper<LogPackageViewModel, AnyItem> {

    override fun map(item: LogPackageViewModel, defaultClickAction: (LogPackageViewModel) -> Unit): AnyItem {
        val data = LogPackageItemViewModel(
            uuid = item.id,
            incidentId = item.incidentId,
            startTime = item.startTime,
            size = item.size,
            status = item.status,
            progress = item.progress
        ).also { model ->
            model.onLongClickAction = {
                clipboardCopier.copy(model, it)
            }
        }
        return BindingItem(
            data,
            DataBindingViewHolderHelper(R.layout.logging_log_package_item, callback),
            options = Options(customSidePadding = true),
            comparable = data
        )
    }
}