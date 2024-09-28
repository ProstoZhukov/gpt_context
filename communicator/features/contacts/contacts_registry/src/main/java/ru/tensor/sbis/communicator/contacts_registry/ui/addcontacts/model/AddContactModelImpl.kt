package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile

/**
 * Реализация модели [AddContactModel] для экрана добавления нового контакта
 *
 * @author vv.chekurda
 */
@Parcelize
internal data class AddContactModelImpl @JvmOverloads constructor(
    override val employee: EmployeeProfile,
    @ColorInt override val subtitleTextColor: Int,
    override val nameHighlight: List<Int> = listOf()
) : AddContactModel,
    Parcelable
