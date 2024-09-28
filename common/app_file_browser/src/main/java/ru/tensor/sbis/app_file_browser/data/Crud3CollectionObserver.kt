package ru.tensor.sbis.app_file_browser.data

import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.mfb.generated.CollectionObserverOfFileInfo
import ru.tensor.sbis.mfb.generated.FileInfo
import ru.tensor.sbis.mfb.generated.ItemWithIndexOfFileInfo
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition

/**
 * Проксирует событие из колбека контроллера в колбек списочного компонента.
 *
 * @author us.bessonov
 */
internal class Crud3CollectionObserver(
    private val callback: ObserverCallback<ItemWithIndexOfFileInfo, FileInfo>
) : CollectionObserverOfFileInfo() {

    override fun onAdd(param: ArrayList<ItemWithIndexOfFileInfo>) = callback.onAdd(param)

    override fun onAddStub(stubType: StubType, position: ViewPosition) = callback.onAddStub(stubType, position)

    override fun onAddThrobber(position: ViewPosition) = callback.onAddThrobber(position)

    override fun onBeginUpdate() = Unit

    override fun onEndUpdate() = Unit

    override fun onMove(param: ArrayList<IndexPair>) = callback.onMove(param)

    override fun onRemove(index: ArrayList<Long>) = callback.onRemove(index)

    override fun onRemoveStub() = callback.onRemoveStub()

    override fun onRemoveThrobber() = callback.onRemoveThrobber()

    override fun onReplace(param: ArrayList<ItemWithIndexOfFileInfo>) = callback.onReplace(param)

    override fun onReset(items: ArrayList<FileInfo>) = callback.onReset(items)
}