package ru.tensor.sbis.design.cloud_view.content.utils

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor

/**
 * Стандартный холдер ресурсов [MessageResourcesHolder] для кастомизаций сервисного текста в CloudView.
 *
 * @author vv.chekurda
 */
open class BaseMessageResourceHolder(protected val context: Context) : MessageResourcesHolder {
    companion object {
        private const val TEXT = 0
        const val DEFAULT_SERVICE_TYPE = 1
        const val SIGN_REQUEST = 2
        const val SIGNED = 3
        const val NOT_SIGNED = 4
        const val DIALOG_INVITE = 5
        const val DOCUMENT_ACCESS = 6
        const val FILE_ACCESS_REQUEST = 7
        const val FILE_ACCESS_REQUEST_GRANTED = 8
        const val FILE_ACCESS_REQUEST_REJECTED = 9
        private const val DISABLED_SERVICE = 10
        private const val sMessageEntityTypeSize = 11
        private const val sCertificateBadgeColorsCount = 2
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        TEXT,
        DEFAULT_SERVICE_TYPE,
        SIGN_REQUEST,
        SIGNED,
        NOT_SIGNED,
        DIALOG_INVITE,
        DOCUMENT_ACCESS,
        FILE_ACCESS_REQUEST,
        FILE_ACCESS_REQUEST_GRANTED,
        FILE_ACCESS_REQUEST_REJECTED,
        DISABLED_SERVICE
    )
    internal annotation class MessageEntityType

    @ColorInt
    private val mTextColor = IntArray(sMessageEntityTypeSize)

    @ColorInt
    private val mCertificateBadgeColor = IntArray(sCertificateBadgeColorsCount)

    @ColorInt
    private val mOwnerInfoColor = IntArray(sCertificateBadgeColorsCount)

    @ColorInt
    private val primaryTextColor = StyleColor.PRIMARY.getTextColor(context)

    protected open fun fillTextColor(@MessageEntityType type: Int) {
        when (type) {
            TEXT -> mTextColor[TEXT] = TextColor.DEFAULT.getValue(context)
            DEFAULT_SERVICE_TYPE -> mTextColor[DEFAULT_SERVICE_TYPE] = StyleColor.UNACCENTED.getTextColor(context)
            SIGN_REQUEST -> mTextColor[SIGN_REQUEST] = primaryTextColor
            SIGNED -> mTextColor[SIGNED] = primaryTextColor
            NOT_SIGNED -> mTextColor[NOT_SIGNED] = primaryTextColor
            DIALOG_INVITE -> mTextColor[DIALOG_INVITE] = TextColor.LABEL_CONTRAST.getValue(context)
            DOCUMENT_ACCESS -> mTextColor[DOCUMENT_ACCESS] = StyleColor.UNACCENTED.getTextColor(context)
            DISABLED_SERVICE -> mTextColor[DISABLED_SERVICE] = TextColor.READ_ONLY.getValue(context)
            FILE_ACCESS_REQUEST -> mTextColor[FILE_ACCESS_REQUEST] = primaryTextColor
            FILE_ACCESS_REQUEST_GRANTED -> mTextColor[FILE_ACCESS_REQUEST_GRANTED] =
                StyleColor.SUCCESS.getTextColor(context)

            FILE_ACCESS_REQUEST_REJECTED -> mTextColor[FILE_ACCESS_REQUEST_REJECTED] =
                StyleColor.DANGER.getTextColor(context)
        }
    }

    /** @SelfDocumented
     */
    @ColorInt
    override fun getTextColor(@MessageEntityType type: Int): Int {
        if (mTextColor[type] == 0) {
            fillTextColor(type)
        }
        return mTextColor[type]
    }

    /** @SelfDocumented
     */
    @ColorInt
    override fun getCertificateBadgeColor(mine: Boolean): Int =
        getColor(mine, mCertificateBadgeColor, false)

    @ColorInt
    override fun getOwnerInfoColor(mine: Boolean): Int =
        getColor(mine, mOwnerInfoColor, true)

    protected open fun getColor(mine: Boolean, arrayColor: IntArray, isTextColor: Boolean): Int {
        val secondaryTextColor = StyleColor.SECONDARY.getTextColor(context)
        val secondaryIconColor = StyleColor.SECONDARY.getIconColor(context)
        val primaryIconColor = StyleColor.PRIMARY.getIconColor(context)
        val color: Int
        if (mine) {
            if (arrayColor[0] == 0) {
                arrayColor[0] = if (isTextColor) secondaryTextColor else secondaryIconColor
            }
            color = arrayColor[0]
        } else {
            if (arrayColor[1] == 0) {
                arrayColor[1] = if (isTextColor) primaryTextColor else primaryIconColor
            }
            color = arrayColor[1]
        }
        return color
    }
}