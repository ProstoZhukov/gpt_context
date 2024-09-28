package ru.tensor.sbis.person_decl.profile.model

import android.os.Parcel
import android.text.SpannableString
import android.text.TextUtils
import ru.tensor.sbis.android_ext_decl.DefaultParcelable
import ru.tensor.sbis.android_ext_decl.extReadBoolean
import ru.tensor.sbis.android_ext_decl.extWriteBoolean
import java.util.UUID

/**
 * Модель контакта профиля
 *
 * @author Subbotenko Dmitry
 */
class ProfileContact : DefaultParcelable {
    val uuid: UUID?
    val personUUID: UUID?

    /**
     * visibility - текущая видимость
     * minVisibility - минимальная видимость, которую мы можем выставить. Приходит с онлайна. Таким образом мы не должны давать возможность выставить видимость ниже, чем требует онлайн.
     * Например, корпоративная почта. Для нее нельзя задавать видимость "Скрыто". В этом случае в minVisibility придет for_colleagues и в менюшке нам нужно пункт "Скрыто" убрать.
     */
    var visibility: VisibilityStatus
    var minVisibility: VisibilityStatus
    val type: ProfileContactType
    var info: SpannableString
    val verified: Boolean
    var editable: Boolean
    val personal: Boolean
    val boundMessengers: String
    val isTelegramBound: Boolean
    val isWhatsAppBound: Boolean
    val isViberBound: Boolean

    constructor() {
        uuid = null
        personUUID = null
        visibility = VisibilityStatus.NONE
        minVisibility = VisibilityStatus.NONE
        type = ProfileContactType.OTHER
        info = SpannableString("")
        verified = false
        editable = false
        personal = false
        boundMessengers = ""
        isTelegramBound = false
        isWhatsAppBound = false
        isViberBound = false
    }

    constructor(
        uuid: UUID?,
        personUUID: UUID?,
        visibility: VisibilityStatus,
        minVisibility: VisibilityStatus,
        type: ProfileContactType,
        info: SpannableString,
        verified: Boolean,
        editable: Boolean,
        personal: Boolean,
        boundMessengers: String
    ) {
        this.uuid = uuid
        this.personUUID = personUUID
        this.visibility = visibility
        this.minVisibility = minVisibility
        this.type = type
        this.info = info
        this.verified = verified
        this.editable = editable
        this.personal = personal
        this.boundMessengers = boundMessengers
        isTelegramBound = boundMessengers.contains(TELEGRAM)
        isWhatsAppBound = boundMessengers.contains(WHATS_APP)
        isViberBound = boundMessengers.contains(VIBER)
    }


    constructor(parcel: Parcel) {
        uuid = if (parcel.readByte().toInt() == 0) null else UUID.fromString(parcel.readString())
        personUUID = if (parcel.readByte().toInt() == 0) null else UUID.fromString(parcel.readString())
        visibility = VisibilityStatus.values()[parcel.readInt()]
        minVisibility = VisibilityStatus.values()[parcel.readInt()]
        type = ProfileContactType.values()[parcel.readInt()]
        info = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel) as SpannableString
        verified = parcel.extReadBoolean()
        editable = parcel.extReadBoolean()
        personal = parcel.extReadBoolean()
        boundMessengers = parcel.readString()!!
        isTelegramBound = parcel.extReadBoolean()
        isWhatsAppBound = parcel.extReadBoolean()
        isViberBound = parcel.extReadBoolean()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        if (uuid != null) {
            out.writeByte(1.toByte())
            out.writeString(uuid.toString())
        } else {
            out.writeByte(0.toByte())
        }

        if (personUUID != null) {
            out.writeByte(1.toByte())
            out.writeString(uuid.toString())
        } else {
            out.writeByte(0.toByte())
        }

        out.writeInt(visibility.ordinal)
        out.writeInt(minVisibility.ordinal)
        out.writeInt(type.ordinal)
        TextUtils.writeToParcel(info, out, flags)
        out.extWriteBoolean(verified)
        out.extWriteBoolean(editable)
        out.extWriteBoolean(personal)
        out.writeString(boundMessengers)
        out.extWriteBoolean(isTelegramBound)
        out.extWriteBoolean(isWhatsAppBound)
        out.extWriteBoolean(isViberBound)
    }

    override fun toString(): String {
        return "ProfileContact(" +
            "uuid=$uuid, " +
            "personUUID=$personUUID, " +
            "visibility=$visibility, " +
            "minVisibility=$minVisibility, " +
            "type=$type, " +
            "info='$info', " +
            "verified=$verified, " +
            "editable=$editable, " +
            "personal=$personal, " +
            "boundMessengers='$boundMessengers, " +
            "isTelegramBound=$isTelegramBound, " +
            "isWhatsAppBound=$isWhatsAppBound, " +
            "isViberBound=$isViberBound)"
    }

    companion object {
        @JvmField
        val CREATOR = DefaultParcelable.generateCreator(::ProfileContact)
        private const val TELEGRAM = "telegram"
        private const val WHATS_APP = "whatsapp"
        private const val VIBER = "viber"
    }
}
