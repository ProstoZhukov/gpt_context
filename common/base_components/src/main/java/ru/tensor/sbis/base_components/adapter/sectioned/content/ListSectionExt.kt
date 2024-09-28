package ru.tensor.sbis.base_components.adapter.sectioned.content

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.savedstate.SavedStateRegistry

/**
 * Набор расширений для конфигурирования секции.
 *
 * @author am.boldinov
 */

/**
 * Присоединяет секцию к жизненному циклу сохранения состояния [SavedStateRegistry].
 */
@JvmOverloads
fun ListSection<*, *, *>.connectToSavedStateRegistry(
    savedStateRegistry: SavedStateRegistry,
    key: String = this.javaClass.name
) {
    val stateProviderKey = "{$key}_state_provider"
    savedStateRegistry.registerSavedStateProvider(stateProviderKey) {
        Bundle().apply {
            onSaveInstanceState(this)
        }
    }
    savedStateRegistry.consumeRestoredStateForKey(stateProviderKey)?.apply {
        onRestoreInstanceState(this)
    }
}

/**
 * @see [globalToLocalPosition]
 * В случе если [globalPosition] выходит за границы секции, то метод вернет [RecyclerView.NO_POSITION].
 */
fun ListSection<*, *, *>.globalToLocalPosition(globalPosition: Int) =
    globalToLocalPosition(globalPosition, RecyclerView.NO_POSITION)

/**
 * Конвертирует глобальную позицию [globalPosition] из [RecyclerView] в локальную позицию секции.
 * В случе если [globalPosition] выходит за границы секции, то метод вернет результат [fallback] функции.
 */
fun ListSection<*, *, *>.globalToLocalPosition(globalPosition: Int, fallback: Int): Int {
    val start = getSectionOffset()
    val end = start + getItemCount() - 1
    return if (globalPosition in start..end) {
        globalPosition - start
    } else {
        fallback
    }
}

/**
 * Конвертирует локальную позицию внутри секции в глобальную позицию [RecyclerView].
 */
fun ListSection<*, *, *>.localToGlobalPosition(localPosition: Int): Int {
    return getSectionOffset() + localPosition
}

/**
 * Безопасно привязывает секцию ко View при условии если ранее она не была привязана.
 */
fun ListSection<*, *, *>.safeAttachToView() {
    if (!isAttachedToView) {
        attachToView()
    }
}

/**
 * Безопасно отвязывает секцию от View при условии если ранее она была привязана.
 */
fun ListSection<*, *, *>.safeDetachFromView() {
    if (isAttachedToView) {
        detachFromView()
    }
}