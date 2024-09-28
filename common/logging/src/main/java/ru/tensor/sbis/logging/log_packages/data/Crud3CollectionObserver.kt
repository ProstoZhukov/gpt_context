package ru.tensor.sbis.logging.log_packages.data

import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.platform.logdelivery.generated.CollectionObserverOfLogPackageViewModel
import ru.tensor.sbis.platform.logdelivery.generated.ItemWithIndexOfLogPackageViewModel
import ru.tensor.sbis.platform.logdelivery.generated.LogPackageViewModel
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition

/**
 * Реализция проксирует событие из колбека контроллера в колбек списочного компонента.
 */
class Crud3CollectionObserver(private val callback: ObserverCallback<ItemWithIndexOfLogPackageViewModel, LogPackageViewModel>) :
    CollectionObserverOfLogPackageViewModel() {

    override fun onReset(items: ArrayList<LogPackageViewModel>) {
        callback.onReset(items)
    }

    override fun onRemove(index: ArrayList<Long>) {
        callback.onRemove(index)
    }

    override fun onMove(param: ArrayList<IndexPair>) {
        callback.onMove(param)
    }

    override fun onAdd(param: ArrayList<ItemWithIndexOfLogPackageViewModel>) {
        callback.onAdd(param)
    }

    override fun onReplace(param: ArrayList<ItemWithIndexOfLogPackageViewModel>) {
        callback.onReplace(param)
    }

    override fun onBeginUpdate() = Unit

    override fun onEndUpdate() = Unit

    override fun onAddThrobber(position: ViewPosition) {
        callback.onAddThrobber(position)
    }

    override fun onRemoveThrobber() {
        callback.onRemoveThrobber()
    }

    override fun onAddStub(stubType: StubType, position: ViewPosition) {
        callback.onAddStub(stubType, position)
    }

    override fun onRemoveStub() {
        callback.onRemoveStub()
    }
}