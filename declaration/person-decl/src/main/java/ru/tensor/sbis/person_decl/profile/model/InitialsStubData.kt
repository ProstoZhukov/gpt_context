package ru.tensor.sbis.person_decl.profile.model

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

/**
 * Данные заглушки фото с инициалами
 *
 * @author us.bessonov
 */
interface InitialsStubData : Parcelable {

    /** @SelfDocumented */
    val initials: String

    /** @SelfDocumented */
    @get:ColorInt
    val initialsBackgroundColor: Int

    /** @SelfDocumented */
    @get:ColorRes
    val initialsBackgroundColorRes: Int
}