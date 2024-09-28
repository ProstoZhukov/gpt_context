package ru.tensor.sbis.communicator.communicator_files.data

import ru.tensor.sbis.communicator.generated.CollectionObserverOfThemeAttachmentViewModel
import ru.tensor.sbis.communicator.generated.ItemWithIndexOfThemeAttachmentViewModel
import ru.tensor.sbis.communicator.generated.ThemeAttachmentViewModel
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition

/**
 * Реализция проксирует событие из колбека контроллера в колбек списочного компонента файлов переписки.
 * @param observer
 *
 * @author da.zhukov
 */
internal class CommunicatorFilesObserver(
    private val observer: ObserverCallback<ItemWithIndexOfThemeAttachmentViewModel, ThemeAttachmentViewModel>
) : CollectionObserverOfThemeAttachmentViewModel() {

    override fun onAdd(param: ArrayList<ItemWithIndexOfThemeAttachmentViewModel>) {
        observer.onAdd(param)
    }

    override fun onAddStub(stubType: StubType, position: ViewPosition) {
        observer.onAddStub(stubType, position)
    }

    override fun onAddThrobber(position: ViewPosition) {
        observer.onAddThrobber(position)
    }

    override fun onMove(param: ArrayList<IndexPair>) {
        observer.onMove(param)
    }

    override fun onRemove(index: ArrayList<Long>) {
        observer.onRemove(index)
    }

    override fun onRemoveStub() {
        observer.onRemoveStub()
    }

    override fun onRemoveThrobber() {
        observer.onRemoveThrobber()
    }

    override fun onReplace(param: ArrayList<ItemWithIndexOfThemeAttachmentViewModel>) {
        observer.onReplace(param)
    }

    override fun onReset(items: ArrayList<ThemeAttachmentViewModel>) {
        observer.onReset(items)
    }

    override fun onBeginUpdate() = Unit

    override fun onEndUpdate() = Unit
}