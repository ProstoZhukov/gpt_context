package ru.tensor.sbis.design.theme.res

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.SbisMobileIcon
import java.io.Serializable

/**
 * Текстовая модель, которая поддерживается платформенной командой.
 */
sealed interface PlatformSbisString : SbisString {
    /**
     * Текст, заданный значением.
     */
    data class Value(
        val string: String
    ) : PlatformSbisString, Serializable {
        override fun getCharSequence(context: Context) = string
    }

    /**
     * Текст, заданный через [kotlin.CharSequence].
     */
    @Parcelize
    data class CharSequence(
        val charSequence: kotlin.CharSequence
    ) : PlatformSbisString, Parcelable {
        override fun getCharSequence(context: Context) = charSequence
    }

    /**
     * Текст, заданный ресурсом.
     */
    data class Res(
        @StringRes
        val stringRes: Int,
    ) : PlatformSbisString, Serializable {
        override fun getCharSequence(context: Context) = context.getString(stringRes)
    }

    /**
     * Текст, заданный ресурсом с набором аргументов.
     */
    data class ResWithArgs(
        @StringRes
        val stringRes: Int,
        val args: Array<out Any>?
    ) : PlatformSbisString {

        override fun getCharSequence(context: Context) = if (args != null) {
            context.getString(stringRes, *args)
        } else {
            context.getString(stringRes)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ResWithArgs

            if (stringRes != other.stringRes) return false
            if (args != null) {
                if (other.args == null) return false
                if (!args.contentEquals(other.args)) return false
            } else if (other.args != null) return false

            return true
        }

        override fun hashCode(): Int {
            var result = stringRes
            result = 31 * result + (args?.contentHashCode() ?: 0)
            return result
        }
    }

    /**
     * Текст, заданный иконкой.
     */
    data class Icon(
        val icon: SbisMobileIcon.Icon,
    ) : PlatformSbisString {
        override fun getCharSequence(context: Context) = icon.character.toString()
    }
}

/** @SelfDocumented */
fun createString(string: String) = PlatformSbisString.Value(string)
/** @SelfDocumented */
fun createRes(@StringRes stringRes: Int) = PlatformSbisString.Res(stringRes)
