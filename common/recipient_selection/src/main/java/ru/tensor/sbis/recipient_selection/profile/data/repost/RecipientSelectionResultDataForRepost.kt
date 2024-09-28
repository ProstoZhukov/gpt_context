package ru.tensor.sbis.recipient_selection.profile.data.repost

import android.os.Parcel
import ru.tensor.sbis.android_ext_decl.DefaultParcelable
import ru.tensor.sbis.communication_decl.employeeselection.result.RecipientSelectionForRepostItemType
import ru.tensor.sbis.communication_decl.employeeselection.result.RecipientSelectionResultDataForRepostContract
import java.util.UUID

/**
 * Данные выбранного item'а для списка получателей репоста новости
 */
data class RecipientSelectionResultDataForRepost(
        override val uuid: UUID?,
        override val title: String,
        override val subtitle: String?,
        override val imageUrl: String?,
        override val hasNestedItems: Boolean = false,
        override val counter: Int = 0,
        override val type: RecipientSelectionForRepostItemType
) : RecipientSelectionResultDataForRepostContract {

        companion object {
                @JvmField
                val CREATOR = DefaultParcelable.generateCreator(::RecipientSelectionResultDataForRepost)

                val PARCELABLE_KEY = RecipientSelectionResultDataForRepost::class.java.canonicalName
        }

        constructor(parcel: Parcel) : this(UUID.fromString(parcel.readString()), parcel.readString()!!, parcel.readString(), parcel.readString(), parcel.readByte() != 0.toByte(), parcel.readInt(), RecipientSelectionForRepostItemType.fromValue(parcel.readInt()))

        override fun writeToParcel(dest: Parcel, flags: Int) {
                dest.writeString(uuid.toString())
                dest.writeString(title)
                dest.writeString(subtitle)
                dest.writeString(imageUrl)
                dest.writeByte(if (hasNestedItems) 1.toByte() else 0.toByte())
                dest.writeInt(counter)
                dest.writeInt(type.ordinal)
        }

        override fun describeContents(): Int = 0

}