package ru.tensor.sbis.design.toolbar.appbar.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout

/**
 * Модель фона для [SbisAppBarLayout]
 *
 * @author ma.kolpakov
 * Создан 9/23/2019
 */
sealed class BackgroundModel : Parcelable

/**
 * Модель фона с возможностью показать заглушку [placeholderRes] на время загрузки основной картинки [imageUrl]
 *
 * @property imageUrl адрес картинки в облаке
 * @property placeholderRes ресурс картинки-заглушки. Определяется в зависимости от контекста использования
 */
data class ImageBackground(
    val imageUrl: String,
    @DrawableRes
    val placeholderRes: Int? = null
) : BackgroundModel() {

    // region Parcelable implementation
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeValue(placeholderRes)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ImageBackground> {
        override fun createFromParcel(parcel: Parcel): ImageBackground = ImageBackground(parcel)
        override fun newArray(size: Int): Array<ImageBackground?> = arrayOfNulls(size)
    }
    // endregion Parcelable implementation
}

/**
 * Модель фона с заливкой цветом [color]
 */
data class ColorBackground(
    @ColorInt
    val color: Int
) : BackgroundModel() {

    // region Parcelable implementation
    constructor(parcel: Parcel) : this(parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) = parcel.writeInt(color)

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ColorBackground> {
        override fun createFromParcel(parcel: Parcel): ColorBackground = ColorBackground(parcel)
        override fun newArray(size: Int): Array<ColorBackground?> = arrayOfNulls(size)
    }
    // endregion Parcelable implementation
}

/**
 * Модель фона, который определяется внутренней реализацией
 */
object UndefinedBackground : BackgroundModel(), Parcelable.Creator<UndefinedBackground> {

    /**
     * Метка записи. Сам объект не хранит состояние и не записывается
     */
    private val parcelableMarker = javaClass.canonicalName

    // region Parcelable implementation
    @JvmField
    val CREATOR = this

    override fun writeToParcel(dest: Parcel, flags: Int) = dest.writeString(parcelableMarker)

    override fun describeContents(): Int = 0

    override fun createFromParcel(source: Parcel): UndefinedBackground {
        val marker = source.readString()
        return if (marker == parcelableMarker)
            UndefinedBackground
        else
            throw IllegalStateException("Unexpected parcelable marker `$marker` for UndefinedBackground instance")
    }

    override fun newArray(size: Int): Array<UndefinedBackground?> = arrayOfNulls(size)
    // endregion Parcelable implementation
}