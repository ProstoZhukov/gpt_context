package ru.tensor.sbis.design.utils

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import ru.tensor.sbis.design.R

/**
 * Утилитные функции для BottomSelectionPane
 * @deprecated use {@link ru.tensor.sbis.design_dialogs.fragment.BottomSelectionIconsUtil} instead.
 */
@Deprecated("Дубликат не использовать", ReplaceWith("ru.tensor.sbis.design_dialogs.fragment.BottomSelectionIconsUtil"))
object BottomSelectionIconsUtil {

    @JvmStatic
    @StringRes
    @Suppress("unused")
    /**@SelfDocumented**/
    fun getIconForOption(@StringRes option: Int): Int? {
        return when (option) {
            R.string.design_edit_photo_camera       -> R.string.design_mobile_icon_camera_black
            R.string.design_edit_photo_from_gallery -> R.string.design_mobile_icon_gallery
            R.string.design_edit_photo_delete       -> R.string.design_mobile_icon_delete_item
            ProfileOption.CALENDAR.textRes          -> ProfileOption.CALENDAR.iconRes
//            ProfileOption.SAVE_TO_DEVICE.textRes    -> ProfileOption.SAVE_TO_DEVICE.iconRes
            ProfileOption.SUBSCRIBE.textRes         -> ProfileOption.SUBSCRIBE.iconRes
            ProfileOption.UNSUBSCRIBE.textRes       -> ProfileOption.UNSUBSCRIBE.iconRes
            ProfileOption.COPY_LINK.textRes         -> ProfileOption.COPY_LINK.iconRes
            ProfileOption.PIV.textRes               -> ProfileOption.PIV.iconRes
            ProfileOption.REPORT_OR_BLOCK.textRes          -> ProfileOption.REPORT_OR_BLOCK.iconRes
            OpinionRateOption.LIKE.textRes          -> OpinionRateOption.LIKE.iconRes
            OpinionRateOption.DISLIKE.textRes       -> OpinionRateOption.DISLIKE.iconRes
            else                                    -> null
        }
    }

    @JvmStatic
    @ColorRes
    @Suppress("unused")
    /**@SelfDocumented**/
    fun getIconColorForOption(@StringRes option: Int): Int {
        return when (option) {
            R.string.design_edit_photo_delete,
            OpinionRateOption.DISLIKE.textRes -> R.color.text_color_error
            OpinionRateOption.LIKE.textRes    -> R.color.text_color_forgiven
            else                              -> R.color.blue_text_color
        }
    }

}

/**@SelfDocumented**/
enum class ProfileOption(
    @StringRes val iconRes: Int,
    @StringRes val textRes: Int
) {
    CALENDAR(R.string.design_mobile_icon_calendar_meeting, R.string.calendar_title),
//    SAVE_TO_DEVICE(R.string.design_mobile_icon_save, R.string.design_menu_item_save_to_device),
    SUBSCRIBE(R.string.design_mobile_icon_subscribe, R.string.design_menu_item_subscribe),
    UNSUBSCRIBE(R.string.design_mobile_icon_unsubscribe, R.string.design_menu_item_unsubscribe),
    COPY_LINK(R.string.design_mobile_icon_link, R.string.design_menu_item_copy_link),
    PIV(R.string.design_mobile_icon_like_null_icon, R.string.design_menu_item_piv),
    REPORT_OR_BLOCK(R.string.design_mobile_icon_alert_null, R.string.design_menu_report_or_block);

    /**@SelfDocumented**/
    fun getTitle(context: Context): CharSequence = context.getString(textRes)
    /**@SelfDocumented**/
    fun getIcon(context: Context): CharSequence = context.getString(iconRes)
}

/**@SelfDocumented**/
enum class OpinionRateOption(
    @StringRes val iconRes: Int,
    @StringRes val textRes: Int
) {
    LIKE(R.string.design_mobile_icon_like_icon, R.string.design_opinion_rating_like),
    DISLIKE(R.string.design_mobile_icon_dislike_icon, R.string.design_opinion_rating_dislike)
}