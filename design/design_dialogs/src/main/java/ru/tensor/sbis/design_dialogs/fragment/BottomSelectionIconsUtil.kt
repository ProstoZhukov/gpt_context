package ru.tensor.sbis.design_dialogs.fragment

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import ru.tensor.sbis.design.R as RDesign

/**
 * Утильный класс определяющий соответствие option<->icon
 */
object BottomSelectionIconsUtil {

    @JvmStatic
    @StringRes
    fun getIconForOption(@StringRes option: Int): Int? =
        when (option) {
            RDesign.string.design_edit_photo_camera -> RDesign.string.design_mobile_icon_camera_black
            RDesign.string.design_edit_photo_from_gallery -> RDesign.string.design_mobile_icon_gallery
            RDesign.string.design_edit_photo_delete -> RDesign.string.design_mobile_icon_delete_item
            ProfileOption.CALENDAR.textRes -> ProfileOption.CALENDAR.iconRes
            ProfileOption.SAVE_TO_DEVICE.textRes -> ProfileOption.SAVE_TO_DEVICE.iconRes
            ProfileOption.SUBSCRIBE.textRes -> ProfileOption.SUBSCRIBE.iconRes
            ProfileOption.UNSUBSCRIBE.textRes -> ProfileOption.UNSUBSCRIBE.iconRes
            ProfileOption.COPY_LINK.textRes -> ProfileOption.COPY_LINK.iconRes
            ProfileOption.PIV.textRes -> ProfileOption.PIV.iconRes
            ProfileOption.COMPLAIN.textRes -> ProfileOption.COMPLAIN.iconRes
            ProfileOption.COMPLAIN_USER.textRes -> ProfileOption.COMPLAIN_USER.iconRes
            ProfileOption.BLOCK_USER.textRes -> ProfileOption.BLOCK_USER.iconRes
            ProfileOption.UNBLOCK_USER.textRes -> ProfileOption.UNBLOCK_USER.iconRes
            OpinionRateOption.LIKE.textRes -> OpinionRateOption.LIKE.iconRes
            OpinionRateOption.DISLIKE.textRes -> OpinionRateOption.DISLIKE.iconRes
            else -> null
        }

    @JvmStatic
    @ColorRes
    fun getIconColorForOption(@StringRes option: Int): Int =
        when (option) {
            RDesign.string.design_edit_photo_delete,
            OpinionRateOption.DISLIKE.textRes,
            ProfileOption.BLOCK_USER.textRes -> RDesign.color.text_color_error
            OpinionRateOption.LIKE.textRes -> RDesign.color.text_color_forgiven
            else -> RDesign.color.blue_text_color
        }

    @JvmStatic
    @ColorRes
    fun getTextColorForOption(@StringRes option: Int): Int =
        when (option) {
            ProfileOption.BLOCK_USER.textRes -> RDesign.color.text_color_error
            else -> RDesign.color.blue_text_color
        }
}

enum class ProfileOption(
    @StringRes val iconRes: Int,
    @StringRes val textRes: Int
) {
    CALENDAR(RDesign.string.design_mobile_icon_calendar_meeting, RDesign.string.calendar_title),
    SAVE_TO_DEVICE(RDesign.string.design_mobile_icon_save, RDesign.string.design_menu_item_save_to_device),
    SUBSCRIBE(RDesign.string.design_mobile_icon_subscribe, RDesign.string.design_menu_item_subscribe),
    UNSUBSCRIBE(RDesign.string.design_mobile_icon_unsubscribe, RDesign.string.design_menu_item_unsubscribe),
    COPY_LINK(RDesign.string.design_mobile_icon_link, RDesign.string.design_menu_item_copy_link),
    PIV(RDesign.string.design_mobile_icon_like_null_icon, RDesign.string.design_menu_item_piv),
    COMPLAIN(
        RDesign.string.design_mobile_icon_alert_null,
        RDesign.string.design_menu_report_or_block
    ),
    COMPLAIN_USER(RDesign.string.design_mobile_icon_alert_null, RDesign.string.design_menu_report_user),
    BLOCK_USER(RDesign.string.design_mobile_icon_decline, RDesign.string.design_menu_block_user),
    UNBLOCK_USER(RDesign.string.design_mobile_icon_decline, RDesign.string.design_menu_unblock_user);

    fun getTitle(context: Context): CharSequence = context.getString(textRes)
    fun getIcon(context: Context): CharSequence? = context.getString(iconRes)
}

enum class OpinionRateOption(
    @StringRes val iconRes: Int,
    @StringRes val textRes: Int
) {
    LIKE(RDesign.string.design_mobile_icon_like_icon, RDesign.string.design_opinion_rating_like),
    DISLIKE(RDesign.string.design_mobile_icon_dislike_icon, RDesign.string.design_opinion_rating_dislike)
}