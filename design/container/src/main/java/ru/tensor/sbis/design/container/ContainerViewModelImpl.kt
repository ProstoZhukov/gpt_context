package ru.tensor.sbis.design.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/**
 * Вьюмодель контейнера
 * @author ma.kolpakov
 */
class ContainerViewModelImpl : ViewModel(), ContainerViewModel {
    internal val onCloseSelf = MutableSharedFlow<Unit>()
    internal val onUpdateContent = MutableSharedFlow<ContentCreator<Content>>()
    internal val onSetContent = MutableSharedFlow<ContentCreator<Content>>()

    override val onCancelContainer = MutableSharedFlow<Unit>()
    override val onDismissContainer = MutableSharedFlow<Unit>()

    /**
     * Дополнительный слушатель, передаваемый через контейнер.
     * Подробнее см. [SbisContainer.setOnDismissListener]
     */
    internal var additionalOnDismissListener: (() -> Unit)? = null

    init {
        viewModelScope.launch {
            onDismissContainer.collect {
                additionalOnDismissListener?.invoke()
            }
        }
    }

    override fun closeContainer() {
        viewModelScope.launch {
            onCloseSelf.emit(Unit)
        }
    }

    override fun showNewContent(contentCreator: ContentCreator<Content>) {
        viewModelScope.launch {
            onUpdateContent.emit(contentCreator)
        }
    }

    override fun setNewContent(contentCreator: ContentCreator<Content>) {
        viewModelScope.launch {
            onSetContent.emit(contentCreator)
        }
    }
}