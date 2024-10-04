package ru.tensor.sbis.marks.model.title

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.theme.res.SbisString

/**
 * Модель представления заголовка пометки. Реализация для возможности парцелизации заголовков вместе с моделями пометок.
 *
 * @author ra.geraskin
 */
@Parcelize
sealed interface SbisMarksTitle : Parcelable, SbisString {

    /**
     * Заголовок, заданный значением.
     */
    @Parcelize
    class Value(val charSequence: CharSequence) : SbisMarksTitle {
        override fun getCharSequence(context: Context): CharSequence = charSequence

        override fun getString(context: Context): String = charSequence.toString()
    }

    /**
     * Заголовок, заданный ресурсом.
     */
    @Parcelize
    class Res(@StringRes val stringRes: Int) : SbisMarksTitle {
        override fun getCharSequence(context: Context) = context.getString(stringRes)

        override fun getString(context: Context) = context.getString(stringRes)

    }

    companion object {

        /**
         * Функция конвертер [PlatformSbisString] в [SbisMarksTitle] для возможности дальнейшей парцелизации заголовков.
         *
         * ВАЖНО! При конвертации из [PlatformSbisString.ResWithArgs] теряется массив аргументов ресурса.
         */
        fun convertFromPlatformString(platformString: PlatformSbisString): SbisMarksTitle = when (platformString) {
            is PlatformSbisString.Res -> Res(platformString.stringRes)
            is PlatformSbisString.Icon -> Value(platformString.icon.character.toString())
            is PlatformSbisString.Value -> Value(platformString.string)
            is PlatformSbisString.ResWithArgs -> Res(platformString.stringRes)
            is PlatformSbisString.CharSequence -> Value(platformString.charSequence)
        }
    }
}
