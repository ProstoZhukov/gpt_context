package androidx.lifecycle

import io.reactivex.disposables.Disposable
import java.io.Closeable
import java.util.UUID

/**
 * Закрывает Disposable при вызове метода [ViewModel.onCleared] переданной [viewModel].
 */
fun Disposable.closeItOnDestroyVm(viewModel: ViewModel) {
    viewModel.setTagIfAbsent(
        randomUuid(),
        Closeable {
            dispose()
        }
    )
}

/**
 * Выполняет код [doOnCleared] при вызове метода [ViewModel.onCleared].
 */
fun ViewModel.runOnDestroy(doOnCleared: () -> Unit) {
    setTagIfAbsent(
        randomUuid(), Closeable {
            doOnCleared()
        }
    )
}

private fun randomUuid() = UUID.randomUUID().toString()