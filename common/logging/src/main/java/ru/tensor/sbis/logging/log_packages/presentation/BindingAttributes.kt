package ru.tensor.sbis.logging.log_packages.presentation

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.common.util.date.DateFormatTemplate
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.logging.R
import ru.tensor.sbis.logging.data.LogPackage
import ru.tensor.sbis.logging.data.LogPackageDeliveryStatus
import ru.tensor.sbis.platform.logdelivery.generated.DeliveryStatus
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import java.text.SimpleDateFormat
import java.util.*

import ru.tensor.sbis.design.R as RDesign

/**@SelfDocumented*/
private val datePattern = SimpleDateFormat(
    DateFormatTemplate.DATE_WITH_FULL_MONTH_AND_TIME_WITH_SECONDS_WITHOUT_YEAR_GENITIVE.template,
    Locale.getDefault()
)

/**@SelfDocumented*/
@BindingAdapter("secondaryProgressTint")
internal fun ProgressBar.setSecondaryProgressTint(item: LogPackageItemViewModel) {
    secondaryProgressTintList = ColorStateList.valueOf(
        getColorFromAttr(
            item.secondaryProgressTint()
        )
    )
}

/**@SelfDocumented*/
@BindingAdapter("backgroundProgressTint")
internal fun ProgressBar.setBackgroundProgressTint(item: LogPackageItemViewModel) {
    backgroundTintList = ColorStateList.valueOf(
        getColorFromAttr(
            item.backgroundProgressTint()
        )
    )
}

/**@SelfDocumented*/
@BindingAdapter("state", "progress")
internal fun TextView.setProgressText(deliveryStatus: DeliveryStatus, progress: Int) {
    text = getProgressText(context, deliveryStatus, progress)
}

/**@SelfDocumented*/
@BindingAdapter("state", "progress")
internal fun SbisTextView.setProgressText(deliveryStatus: DeliveryStatus, progress: Int) {
    text = getProgressText(context, deliveryStatus, progress)
}

private fun getProgressText(context: Context, deliveryStatus: DeliveryStatus, progress: Int): String =
    when (deliveryStatus) {
        DeliveryStatus.COMPRESSING,
        DeliveryStatus.COLLECTING -> {
            ""
        }
        DeliveryStatus.INETWAITING,
        DeliveryStatus.WAITING,
        DeliveryStatus.WIFIWAITING -> {
            context.getString(R.string.logging_mobile_icon_clock)
        }
        DeliveryStatus.INPROCESS -> {
            progress.toString()
        }
        DeliveryStatus.SENT -> {
            context.getString(R.string.logging_mobile_icon_check)
        }
    }

/**
 * Присваивает статус в [SbisTextView] в зависимости от [LogPackageDeliveryStatus].
 */
@BindingAdapter("name")
internal fun SbisTextView.setDisplayName(item: LogPackage) {
    val name = item.incidentId?.toString() ?: ""
    text = when (item.deliveryStatus) {
        LogPackageDeliveryStatus.PREPARING -> context.getString(R.string.logging_preparing_sending_logs)
        LogPackageDeliveryStatus.WAITING -> context.getString(R.string.logging_waiting_sending_logs)
        LogPackageDeliveryStatus.WIFIWAITING -> context.getString(R.string.logging_waiting_wifi_sending_logs)
        LogPackageDeliveryStatus.INETWAITING -> context.getString(R.string.logging_waiting_inet_sending_logs)
        LogPackageDeliveryStatus.INPROCESS -> context.getString(R.string.logging_sending_logs_in_progress)
        LogPackageDeliveryStatus.SENT -> name
    }
}

/**
 * Присваивает статус в [SbisTextView] в зависимости от [DeliveryStatus].
 */
@BindingAdapter("name")
internal fun SbisTextView.setDisplayName(item: LogPackageItemViewModel) {
    val name = item.incidentId
    text = when (item.status) {
        DeliveryStatus.COLLECTING,
        DeliveryStatus.COMPRESSING -> context.getString(R.string.logging_preparing_sending_logs)
        DeliveryStatus.WAITING -> context.getString(R.string.logging_waiting_sending_logs)
        DeliveryStatus.WIFIWAITING -> context.getString(R.string.logging_waiting_wifi_sending_logs)
        DeliveryStatus.INETWAITING -> context.getString(R.string.logging_waiting_inet_sending_logs)
        DeliveryStatus.INPROCESS -> context.getString(R.string.logging_sending_logs_in_progress)
        DeliveryStatus.SENT -> name
    }
}

/**@SelfDocumented*/
@BindingAdapter("isDragLocked")
internal fun SwipeableLayout.setDragLocked(item: LogPackage) {
    isDragLocked = false
}

/**@SelfDocumented*/
@BindingAdapter("isDragLocked")
internal fun SwipeableLayout.setDragLocked(item: LogPackageItemViewModel) {
    isDragLocked = false
}

@BindingAdapter("isSwipeToDismissLocked")
internal fun SwipeableLayout.isSwipeToDismissLocked(value: Boolean) {
    isSwipeToDismissLocked = value
}

/**@SelfDocumented*/
@BindingAdapter("progressIconTextSizeDimen")
internal fun SbisTextView.setProgressIconTextSizeDimen(item: LogPackage) {
    val dimension = context.getDimen(
        when {
            item.isWaiting() || item.isWifiWaiting() || item.isInetWaiting() -> RDesign.attr.iconSize_4xl
            else -> RDesign.attr.iconSize_xs
        }
    )
    setTextSize(
        TypedValue.COMPLEX_UNIT_PX,
        dimension
    )
}

/**@SelfDocumented*/
@BindingAdapter("progressIconTextSizeDimen")
internal fun SbisTextView.setProgressIconTextSizeDimen(item: LogPackageItemViewModel) {
    val dimension = context.getDimen(
        when {
            item.isWaiting() || item.isWifiWaiting() || item.isInetWaiting() -> RDesign.attr.iconSize_4xl
            else -> RDesign.attr.iconSize_xs
        }
    )
    setTextSize(
        TypedValue.COMPLEX_UNIT_PX,
        dimension
    )
}

/**@SelfDocumented*/
@BindingAdapter("progressIconColor")
internal fun SbisTextView.setProgressIconColor(item: LogPackage) {
    setTextColor(getColorFromAttr(item.progressIconColor()))
}

/**@SelfDocumented*/
@BindingAdapter("progressIconColor")
internal fun SbisTextView.setProgressIconColor(item: LogPackageItemViewModel) {
    setTextColor(getColorFromAttr(item.progressIconColor()))
}

/**@SelfDocumented*/
@BindingAdapter("progressTint")
internal fun ProgressBar.setProgressTint(item: LogPackage) {
    progressTintList = ColorStateList.valueOf(
        getColorFromAttr(item.progressTint())
    )
}

/**@SelfDocumented*/
@BindingAdapter("progressTint")
internal fun ProgressBar.setProgressTint(item: LogPackageItemViewModel) {
    progressTintList = ColorStateList.valueOf(
        getColorFromAttr(item.progressTint())
    )
}

/**@SelfDocumented*/
@BindingAdapter("progressToShow")
internal fun ProgressBar.setProgress(item: LogPackage) {
    progress = item.progressToShow()
}

/**@SelfDocumented*/
@BindingAdapter("progressToShow")
internal fun ProgressBar.setProgress(item: LogPackageItemViewModel) {
    progress = item.progressToShow()
}

/**@SelfDocumented*/
@BindingAdapter("deliveryTime")
internal fun SbisTextView.setDeliveryTime(item: LogPackage) {
    text = datePattern.format(item.endTime ?: item.startTime)
}

/**@SelfDocumented*/
@BindingAdapter("logPackageSize")
internal fun SbisTextView.setLogPackageSize(item: LogPackage) {
    text = context.getString(
        when (item.isLargeSize) {
            true -> R.string.logging_log_package_size_megabytes
            else -> R.string.logging_log_package_size_kilobytes
        },
        item.size
    )
}

/**@SelfDocumented*/
@BindingAdapter("visible_or_invisible")
internal fun View.visibleOrInvisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

/**@SelfDocumented*/
@BindingAdapter("secondaryProgressTint")
internal fun ProgressBar.setSecondaryProgressTint(item: LogPackage) {
    secondaryProgressTintList = ColorStateList.valueOf(
        getColorFromAttr(item.secondaryProgressTint())
    )
}

/**@SelfDocumented*/
@BindingAdapter("backgroundProgressTint")
internal fun ProgressBar.setBackgroundProgressTint(item: LogPackage) {
    backgroundTintList = ColorStateList.valueOf(
        getColorFromAttr(item.backgroundProgressTint())
    )
}

/**@SelfDocumented*/
@BindingAdapter("state", "progress")
internal fun TextView.setProgressText(deliveryStatus: LogPackageDeliveryStatus, progress: Int) {
    text = getProgressText(context, deliveryStatus, progress)
}

/**@SelfDocumented*/
@BindingAdapter("state", "progress")
internal fun SbisTextView.setProgressText(deliveryStatus: LogPackageDeliveryStatus, progress: Int) {
    text = getProgressText(context, deliveryStatus, progress)
}

private fun getProgressText(context: Context, deliveryStatus: LogPackageDeliveryStatus, progress: Int): String =
    when (deliveryStatus) {
        LogPackageDeliveryStatus.PREPARING -> {
            ""
        }
        LogPackageDeliveryStatus.INETWAITING,
        LogPackageDeliveryStatus.WAITING,
        LogPackageDeliveryStatus.WIFIWAITING -> {
            context.getString(R.string.logging_mobile_icon_clock)
        }
        LogPackageDeliveryStatus.INPROCESS -> {
            progress.toString()
        }
        LogPackageDeliveryStatus.SENT -> {
            context.getString(R.string.logging_mobile_icon_check)
        }
    }