package ru.tensor.sbis.modalwindows.optionscontent

import android.os.Parcel
import androidx.annotation.CallSuper
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable

/**
 * Класс создателя объекта, реализующего интерфейс [Content] для отображения в качестве контента в BaseContainerDialogFragment
 *
 * @author sr.golovkin on 27.12.2019
 */
abstract class DialogContentCreator(protected val fragmentTag: String? = null): ContentCreatorParcelable {

    @CallSuper
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fragmentTag)
    }

}