package ru.tensor.sbis.common.di

import android.content.Context
import ru.tensor.sbis.common.CommonUtilsPlugin

class CommonSingletonComponentProvider {

    companion object {

        @JvmStatic
        fun get(context: Context): CommonSingletonComponent {
            /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
            return CommonUtilsPlugin.singletonComponent
        }

    }
}