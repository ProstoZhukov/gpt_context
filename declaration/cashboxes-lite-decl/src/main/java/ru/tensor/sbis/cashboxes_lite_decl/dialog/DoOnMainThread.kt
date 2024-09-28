package ru.tensor.sbis.cashboxes_lite_decl.dialog

import android.os.Handler
import android.os.Looper

/**
 * Выполняет блок кода сразу, если вызвана на Main Thread,
 * иначе ставит в очередь на Main Thread.
 */
fun doOnMainThread(block: () -> Unit) {
    Looper.getMainLooper().let { mainLooper ->
        if (Looper.myLooper() == mainLooper) {
            block.invoke()
        } else {
            Handler(mainLooper).post(block::invoke)
        }
    }
}