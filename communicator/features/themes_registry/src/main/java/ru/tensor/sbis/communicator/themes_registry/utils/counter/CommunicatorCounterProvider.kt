package ru.tensor.sbis.communicator.themes_registry.utils.counter

import ru.tensor.sbis.communicator.declaration.counter.CommunicatorCounterModel
import ru.tensor.sbis.counter_provider.AbstractCounterProvider

/**
 * Провайдер счётчиков
 *
 * @param dataSource - репозиторий счётчиков
 */
internal class CommunicatorCounterProvider(
    dataSource: CommunicatorCounterRepository
) : AbstractCounterProvider<CommunicatorCounterModel, CommunicatorCounterRepository>(dataSource)