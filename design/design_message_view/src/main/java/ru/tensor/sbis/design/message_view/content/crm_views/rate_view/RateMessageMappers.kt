package ru.tensor.sbis.design.message_view.content.crm_views.rate_view

import org.json.JSONObject
import ru.tensor.sbis.communication_decl.communicator.media.getServiceObject
import ru.tensor.sbis.communicator.generated.Message

/**
 * Методы для получения [ServiceRateData] из модели [Message].
 *
 * @author da.zhukov
 */

/** Получить [ServiceRateData] из модели [Message]. */
internal fun Message.createServiceRateData(
    isRateRequest: Boolean,
    isCrmMessageForOperator: Boolean
): ServiceRateData? {
    val serviceRateData: ServiceRateData
    this.apply {
        val rateData = getRateData() ?: return null
        val requestMessageText: String? = rateData.optString("request_msg")
        val requestIsActive: Boolean = getServiceObject(this.serviceObject)?.optBoolean("isActive") ?: false
        val consultationRateType: ConsultationRateType = rateData.getRateType(isRateRequest)
        val disableComment: Boolean = rateData.optBoolean("disable_comment")
        serviceRateData = ServiceRateData(
            requestMessageText,
            consultationRateType,
            requestIsActive,
            isCrmMessageForOperator,
            disableComment
        )
    }
    return serviceRateData
}

/** Получить JSONObject, содержащий данные для [ServiceRateData]. */
internal fun Message.getRateData(): JSONObject? =
    getServiceObject(serviceObject)?.getJSONObject("RateData")

private fun JSONObject.getRateType(isRateRequest: Boolean): ConsultationRateType {
    val type = this.optString("type")
    val rating = this.optString("value")

    val consultationRateType: ConsultationRateType = when (type) {
        "thumbs" -> {
            if (isRateRequest) {
                ThumbType()
            } else {
                ThumbType(FingerRating.getFromRating(rating))
            }
        }

        "smile" -> {
            if (isRateRequest) {
                EmojiType()
            } else {
                EmojiType(EmojiRate.getFromRating(rating))
            }
        }

        "stars" -> {
            if (isRateRequest) {
                StarType()
            } else {
                StarType(rating.toInt())
            }
        }

        else -> StarType()
    }
    return consultationRateType
}