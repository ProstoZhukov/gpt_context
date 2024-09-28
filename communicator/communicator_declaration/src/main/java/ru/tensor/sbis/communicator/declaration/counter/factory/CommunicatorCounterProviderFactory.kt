package ru.tensor.sbis.communicator.declaration.counter.factory

import ru.tensor.sbis.communicator.declaration.counter.CommunicatorCounterModel
import ru.tensor.sbis.toolbox_decl.counters.CounterProvider
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика интерфейса поставщика данных для счетчиков.
 *
 * @author da.zhukov
 */
interface CommunicatorCounterProviderFactory : Feature {

    /**
     * Поставщик данных для счетчиков.
     */
    val communicatorCounterProvider: CounterProvider<CommunicatorCounterModel>
}
