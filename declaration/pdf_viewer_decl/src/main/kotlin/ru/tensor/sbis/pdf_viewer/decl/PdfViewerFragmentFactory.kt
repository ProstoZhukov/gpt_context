package ru.tensor.sbis.pdf_viewer.decl

import android.os.Parcelable
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика для создания фрагмента просмотра pdf
 *
 * @author ia.nikitin
 */
interface PdfViewerFragmentFactory : Feature, Parcelable {

    fun create(args: PdfViewerArgs): Fragment
}