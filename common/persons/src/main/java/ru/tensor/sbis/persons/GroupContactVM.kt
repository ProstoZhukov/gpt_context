package ru.tensor.sbis.persons

import android.os.Parcel
import android.os.ParcelUuid
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.android_ext_decl.DefaultParcelable
import ru.tensor.sbis.android_ext_decl.DefaultParcelable.Companion.generateCreator
import ru.tensor.sbis.communication_decl.model.FolderType
import ru.tensor.sbis.android_ext_decl.readEnum
import ru.tensor.sbis.android_ext_decl.writeEnum
import ru.tensor.sbis.persons.recipientselection.RecipientSelectionItem
import java.util.*

/**
 * Data class for group contact at recipient selection screen.
 *
 * @property id             id для группы сотрудников
 * @property uuid           uuid для группы сотрудников
 * @property groupName      имя группы сотрудников
 * @property count          количесвто сотрудников в группе
 * @property groupChiefName имя начальника группы сотрудников
 * @property folderType     тип группы
 * @property personUuids    список uuid-ов этой группы
 * @property persons        список персон этой группы
 */

data class GroupContactVM(
    var id: Long,
    var uuid: UUID,
    var groupName: String,
    var count: Int,
    var groupChiefName: String?,
    var folderType: FolderType?,
    var personUuids: List<UUID>?,
    var persons: List<ContactVM>?
) : RecipientSelectionItem, DefaultParcelable {

    @Suppress("DEPRECATION")
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readSerializable() as UUID,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString(),
        parcel.readEnum<FolderType>(),
        UUIDUtils.fromParcelUuids(parcel.createTypedArrayList(ParcelUuid.CREATOR)),
        parcel.createTypedArrayList(ContactVM.CREATOR)
    )

    override fun getItemCount(): Int {
        return this.count
    }

    override fun getUUID(): UUID {
        return uuid
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GroupContactVM

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeSerializable(uuid)
        dest.writeString(groupName)
        dest.writeInt(count)
        dest.writeString(groupChiefName)
        dest.writeEnum(folderType)
        dest.writeTypedList(UUIDUtils.toParcelUuids(personUuids))
        dest.writeTypedList(this.persons)
    }

    companion object {
        @JvmField
        val CREATOR = generateCreator(::GroupContactVM)
    }
}
