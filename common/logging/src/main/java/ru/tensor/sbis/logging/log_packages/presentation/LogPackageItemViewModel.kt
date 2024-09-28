package ru.tensor.sbis.logging.log_packages.presentation

import android.view.View
import androidx.annotation.AttrRes
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import ru.tensor.sbis.platform.logdelivery.generated.DeliveryStatus
import java.util.UUID
import ru.tensor.sbis.design.R as RDesign

/**
 * Класс, реализующий [ComparableItem] для сравнения элементов.
 * Также предоставляет методы для проверки статуса.
 *
 * @author av.krymov
 */
class LogPackageItemViewModel(
    val uuid: UUID,
    val incidentId: String,
    val startTime: String,
    val size: String,
    val status: DeliveryStatus,
    val progress: Int = 0,
    var onLongClickAction: (view: View) -> Unit = {}
) : ComparableItem<LogPackageItemViewModel> {

    /** @SelfDocumented */
    fun onLongClick(view: View){
        onLongClickAction.invoke(view)
    }

    /**@SelfDocumented*/
    fun isSent() = status == DeliveryStatus.SENT

    /**@SelfDocumented*/
    fun isPreparing() = status == DeliveryStatus.COLLECTING || status == DeliveryStatus.COMPRESSING


    /**@SelfDocumented*/
    fun isWaiting() = status == DeliveryStatus.WAITING


    /**@SelfDocumented*/
    fun isWifiWaiting() = status == DeliveryStatus.WIFIWAITING


    /**@SelfDocumented*/
    fun isInetWaiting() = status == DeliveryStatus.INETWAITING


    /**@SelfDocumented*/
    fun isProgress() = status == DeliveryStatus.INPROCESS

    override fun areTheSame(otherItem: LogPackageItemViewModel) = uuid == otherItem.uuid

    /**@SelfDocumented*/
    @AttrRes
    fun progressIconColor(): Int {
        return if (isWaiting() || isWifiWaiting() || isInetWaiting() || isProgress()) RDesign.attr.secondaryTextColor
        else RDesign.attr.successTextColor
    }

    /**@SelfDocumented*/
    @AttrRes
    fun progressTint(): Int {
        return if (isSent()) RDesign.attr.successBorderColor
        else RDesign.attr.secondaryBorderColor
    }

    /**@SelfDocumented*/
    @AttrRes
    fun backgroundProgressTint(): Int {
        return when {
            isSent() -> RDesign.attr.successBorderColor
            isWaiting() || isWifiWaiting() || isInetWaiting() -> RDesign.attr.secondaryBorderColor
            else -> RDesign.attr.activeBackgroundColor
        }
    }

    /**@SelfDocumented*/
    @AttrRes
    fun secondaryProgressTint(): Int {
        return if (isSent()) RDesign.attr.successBorderColor
        else RDesign.attr.contrastProgressColor
    }

    /**@SelfDocumented*/
    fun progressToShow(): Int {
        return if (isSent()) 100
        else progress
    }

    fun copyToClipboard(clipboardManager: ClipboardManager) {
        clipboardManager.copyToClipboard(incidentId)
    }
}