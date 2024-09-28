package ru.tensor.sbis.design.cylinder.picker

import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer

/**
 * @author Subbotenko Dmitry
 */

internal operator fun DisposableContainer.plusAssign(d: Disposable) {
    add(d)
}