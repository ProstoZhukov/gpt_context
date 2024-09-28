package ru.tensor.sbis.logging.data

import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import ru.tensor.sbis.logging.R
import java.util.Date
import java.util.UUID

import ru.tensor.sbis.design.R as RDesign

/**
 * Пакет логов.
 *
 * @author av.krymov
 */
data class LogPackage(
    private val uuid: UUID,
    val startTime: Date,
    val endTime: Date?,
    private val name: String,
    val size: Double,
    val isLargeSize: Boolean,
    val deliveryStatus: LogPackageDeliveryStatus,
    val incidentId: Long?,
    val progress: Int,
    private val clientId: Long,
    private val userId: Long
) : ComparableItem<LogPackage> {

    /**@SelfDocumented*/
    fun isWaiting(): Boolean {
        return deliveryStatus == LogPackageDeliveryStatus.WAITING
    }

    /**@SelfDocumented*/
    fun isWifiWaiting(): Boolean {
        return deliveryStatus == LogPackageDeliveryStatus.WIFIWAITING
    }

    /**@SelfDocumented*/
    fun isInetWaiting(): Boolean {
        return deliveryStatus == LogPackageDeliveryStatus.INETWAITING
    }

    /**@SelfDocumented*/
    fun isProgress(): Boolean {
        return deliveryStatus == LogPackageDeliveryStatus.INPROCESS
    }

    override fun areTheSame(otherItem: LogPackage) = uuid == otherItem.uuid

    @AttrRes
    internal fun backgroundProgressTint(): Int {
        return when {
            isSent() -> RDesign.attr.successBorderColor
            isWaiting() || isWifiWaiting() || isInetWaiting() -> RDesign.attr.secondaryBorderColor
            else -> RDesign.attr.contrastProgressColor
        }
    }

    @AttrRes
    internal fun progressIconColor(): Int {
        return when {
            isWaiting() || isWifiWaiting() || isInetWaiting() || isProgress() -> RDesign.attr.secondaryBorderColor
            else -> RDesign.attr.successBorderColor
        }
    }

    @AttrRes
    internal fun progressTint(): Int {
        return when {
            isSent() -> RDesign.attr.successBorderColor
            else -> RDesign.attr.secondaryBorderColor
        }
    }

    @AttrRes
    internal fun secondaryProgressTint(): Int {
        return when {
            isSent() -> RDesign.attr.successBorderColor
            else -> RDesign.attr.contrastProgressColor
        }
    }

    internal fun progressToShow(): Int {
        return when {
            isSent() -> 100
            else -> progress
        }
    }

    /**@SelfDocumented*/
    private fun isSent(): Boolean {
        return deliveryStatus == LogPackageDeliveryStatus.SENT
    }
}