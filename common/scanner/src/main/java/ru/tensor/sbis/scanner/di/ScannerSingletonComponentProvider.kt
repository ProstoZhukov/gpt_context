package ru.tensor.sbis.scanner.di

import android.content.Context
import ru.tensor.sbis.scanner.ScannerPlugin

/**
 * @author am.boldinov
 */
// TODO убрать когда взлетит fromContext вызов из котлин
class ScannerSingletonComponentProvider {

    companion object {
        @JvmStatic
        fun get(context: Context): ScannerSingletonComponent {
            /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
            return ScannerPlugin.singletonComponent
        }
    }

}