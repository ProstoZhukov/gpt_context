package ru.tensor.sbis.base_components.checkablefiles

import java.io.File

/**
 * @SelfDocumented
 */
@Suppress("unused")
class CheckableFile(val file: File) {

    var isChecked = false

    fun switchChecked() {
        isChecked = !isChecked
    }

}