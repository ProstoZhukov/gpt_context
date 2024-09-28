package ru.tensor.sbis.onboarding.ui.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer

internal inline fun <T : Fragment> T.withArgs(argsBuilder: Bundle.() -> Unit): T =
    this.apply {
        arguments = Bundle().apply(argsBuilder)
    }

internal inline fun <reified T> Fragment.getParentFragmentAs(): T? {
    if (parentFragment is T) {
        return parentFragment as T
    }
    return null
}

internal operator fun DisposableContainer.plusAssign(d: Disposable) {
    add(d)
}