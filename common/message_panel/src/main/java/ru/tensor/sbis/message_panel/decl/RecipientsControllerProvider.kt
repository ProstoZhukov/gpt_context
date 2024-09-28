package ru.tensor.sbis.message_panel.decl

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.generated.RecipientsController
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик контроллера адресатов
 *
 * @author vv.chekurda
 */
fun interface RecipientsControllerProvider : Feature {
    fun getRecipientsController(): DependencyProvider<RecipientsController>
}