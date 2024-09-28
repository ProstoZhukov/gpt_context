package ru.tensor.sbis.scanner.ui.scannedimagelist.options

import androidx.annotation.StringRes
import ru.tensor.sbis.scanner.R

/**
 * @author am.boldinov
 */
enum class ScannedImageOption(@StringRes val nameResId: Int) {
    SAVE_AS_IMAGE(R.string.scanner_save_as_image),
    SAVE_TO_PDF(R.string.scanner_save_to_pdf),
    SAVE_TO_GALLERY(R.string.scanner_save_to_gallery);

    fun getValue(): Int {
        return ordinal
    }

    companion object {

        @JvmStatic
        fun fromValue(value: Int): ScannedImageOption {
            return values()[value]
        }
    }
}