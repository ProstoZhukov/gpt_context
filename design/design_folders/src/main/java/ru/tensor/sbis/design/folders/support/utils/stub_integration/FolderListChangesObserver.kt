package ru.tensor.sbis.design.folders.support.utils.stub_integration

import androidx.lifecycle.Observer
import ru.tensor.sbis.design.folders.FoldersView

/**
 * Подписка на состояния компонента панель папок [FoldersView], которые влияют на изменение размера
 * заглушки под ним
 *
 * @author ma.kolpakov
 */
internal class FolderListChangesObserver(
    private val stubViewMediator: StubViewMediator
) : Observer<List<*>?> {

    private var currentListSize = 0

    var isExistAdditionalCommand = false
        set(value) {
            if (value != field) {
                field = value
                stubViewMediator.onContentChanged(isFolderViewCompact, currentListSize, isExistAdditionalCommand)
            }
        }

    var isFolderViewCompact = false
        set(value) {
            if (value != field) {
                field = value
                stubViewMediator.onContentChanged(isFolderViewCompact, currentListSize, isExistAdditionalCommand)
            }
        }

    override fun onChanged(t: List<*>?) {
        val newSize = t?.size ?: 0
        if (!isFolderViewCompact && newSize != currentListSize ||
            // не было папок и появились или удалили все папки
            currentListSize == 0 && newSize != 0 ||
            newSize == 0 && currentListSize != 0
        ) {
            currentListSize = newSize
            stubViewMediator.onContentChanged(isFolderViewCompact, currentListSize, isExistAdditionalCommand)
        } else {
            currentListSize = newSize
        }
    }
}