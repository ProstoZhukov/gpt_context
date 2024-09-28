package ru.tensor.sbis.pdf_viewer.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Параметры просмотрщика pdf документов
 *
 * @author ia.nikitin
 */
@Parcelize
data class PdfViewerArgs(val path: String) : Parcelable