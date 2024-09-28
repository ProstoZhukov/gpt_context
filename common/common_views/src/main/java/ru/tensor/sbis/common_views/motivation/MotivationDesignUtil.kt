package ru.tensor.sbis.common_views.motivation

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.mikepenz.iconics.IconicsDrawable
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common_views.R
import ru.tensor.sbis.common_views.sbisview.SbisTitleView
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.text_span.span.CompoundImageSpan
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.design.toolbar.util.StatusBarHelper
import ru.tensor.sbis.design.utils.extentions.getActivity

/**
 * Утилита для работы с экранами мотивации
 *
 * @author am.boldinov
 */
object MotivationDesignUtil {

    /**
     * Стилизует тулбар и его содержимое в зависимости от переданных параметров
     */
    @JvmStatic
    fun stylizeToolbar(toolbar: Toolbar, titleView: SbisTitleView?, positive: Boolean) {
        val activity = toolbar.getActivity()
        // clear FLAG_TRANSLUCENT_STATUS flag:
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        var statusBarColor = getBackgroundColor(activity, positive)
        if (StatusBarHelper.getStatusBarColor(activity) != statusBarColor) {
            (toolbar.parent?.parent as? AppBarLayout)?.let {
                it.setBackgroundColor(statusBarColor)
                statusBarColor = Color.TRANSPARENT
            }
            StatusBarHelper.setStatusBarColor(activity, statusBarColor)
        }
        // finally change the color
        stylizeTranslucentToolbar(toolbar, titleView, positive)
    }

    /**
     * Стилизует тулбар и его содержимое, применимо к прозрачному статус-бару.
     */
    @JvmStatic
    fun stylizeTranslucentToolbar(toolbar: Toolbar, titleView: SbisTitleView?, positive: Boolean) {
        StatusBarHelper.setLightMode(toolbar.getActivity())
        toolbar.setMainColor(getBackgroundColor(toolbar.context, positive))
        val controlColor =
            ContextCompat.getColor(toolbar.context, R.color.common_views_motivation_toolbar_control_color)
        toolbar.leftIcon.setTextColor(controlColor)
        toolbar.rightIcon2.setTextColor(controlColor)
        titleView?.apply {
            val subtitleColor =
                ContextCompat.getColor(toolbar.context, R.color.common_views_motivation_toolbar_subtitle_color)
            setSubTitleTextColor(subtitleColor)
        } ?: toolbar.leftText.setTextColor(controlColor)
    }

    /**
     * Подбирает цвет взависимости от флага [positive] для ПиВ (true поощрение/ false взыскание).
     */
    @JvmStatic
    fun getBackgroundColor(context: Context, positive: Boolean): Int {
        val color = if (positive) {
            R.color.common_views_motivation_toolbar_background_color_positive
        } else {
            R.color.common_views_motivation_toolbar_background_color_negative
        }
        return ContextCompat.getColor(context, color)
    }

    /**
     * Добавить иконку документа автоПиВ к строке
     */
    @JvmStatic
    fun appendMotivationAutoDocIcon(
        stringBuilder: SpannableStringBuilder,
        context: Context
    ): SpannableStringBuilder {
        IconicsDrawable(context, SbisMobileIcon.Icon.smi_sbisbird).color(
            ContextCompat.getColor(context, R.color.common_views_motivation_icon_color)
        ).let { drawable ->
            // Добавляем иконку птички в конец
            stringBuilder.append(StringUtils.SPACE)
            stringBuilder.append(StringUtils.SPACE)
            stringBuilder.setSpan(
                CompoundImageSpan(drawable),
                stringBuilder.length - 1,
                stringBuilder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return stringBuilder
    }
}

