package ru.tensor.sbis.communicator_support_channel_list.utills

import androidx.databinding.BindingAdapter
import com.facebook.drawee.view.SimpleDraweeView
import ru.tensor.sbis.design.profile.R


/**
 * Установка данных для показа фотографии
 */
@BindingAdapter(value = ["communicator_support_channel_photoUrl"], requireAll = true)
fun SimpleDraweeView.setPersonData(
    photoUrl: String?
) {
    val photoUrlFormatted = photoUrl?.replace(
        "%d",
        this.context.resources.getDimensionPixelSize(R.dimen.design_profile_person_view_size_s).toString()
    )
    setImageURI(photoUrlFormatted)
}
