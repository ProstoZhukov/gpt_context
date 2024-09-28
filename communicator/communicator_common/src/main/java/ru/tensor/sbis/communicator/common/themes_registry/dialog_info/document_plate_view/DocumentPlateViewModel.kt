package ru.tensor.sbis.communicator.common.themes_registry.dialog_info.document_plate_view

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

/**
 * Вью-модель таблички о документе
 *
 * @author da.zhukov
 */
@Parcelize
data class DocumentPlateViewModel(
    val soloDocumentTitle: String,
    val documentTitle: String,
    val documentSubTitle: String,
    @StringRes
    val iconRes: Int
) : Parcelable