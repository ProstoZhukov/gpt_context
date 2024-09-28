package ru.tensor.sbis.android_ext_decl

import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Interface which provides MainActivity Intent
 */
fun interface MainActivityProvider : Feature {

    /**
     * Return MainActivity Intent
     */
    fun getMainActivityIntent(): Intent
}