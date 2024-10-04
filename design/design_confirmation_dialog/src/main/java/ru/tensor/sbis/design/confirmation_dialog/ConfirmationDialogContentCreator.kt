package ru.tensor.sbis.design.confirmation_dialog

import android.os.Parcelable
import androidx.annotation.DimenRes
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.container.ContentCreator
import ru.tensor.sbis.design.container.ViewContent
import ru.tensor.sbis.design.design_confirmation.R

/**
 * Создатель контента для контейнера
 *
 * @author ma.kolpakov
 */
@Parcelize
internal class ConfirmationDialogContentCreator(
    private val confirmation: ConfirmationDialog<*>,
    @DimenRes private val customWidth: Int = R.dimen.design_confirmation_dialog_width
) : ContentCreator<ViewContent>, Parcelable {
    @Suppress("UNCHECKED_CAST")
    override fun createContent() = ConfirmationDialogContent(confirmation as ConfirmationDialog<Any>, customWidth)
}
