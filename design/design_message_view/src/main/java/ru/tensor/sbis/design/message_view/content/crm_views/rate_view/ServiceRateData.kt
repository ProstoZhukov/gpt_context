package ru.tensor.sbis.design.message_view.content.crm_views.rate_view

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Дата-класс модели сервисного сообщения запроса/результата оценки в чатах техпоодержки.
 * @property requestMessageText текст запроса оценки.
 * @property consultationRateType тип запроса/результата оценки(звезды, пальцы, смайлы).
 * @property requestIsActive активный ли запрос.
 * @property needChangeColor необходимо ли менять цвет звёзд(для чатов оператора отличаются в зависимости от оценки).
 * @property disableComment нужно ли отключить ввод комментария при оценке.
 *
 * @author da.zhukov
 */
data class ServiceRateData(
    val requestMessageText: String? = null,
    val consultationRateType: ConsultationRateType = StarType(),
    val requestIsActive: Boolean = false,
    val needChangeColor: Boolean = false,
    val disableComment: Boolean = false
)

/**
 * Тип запроса/результата оценки.
 *
 * @author da.zhukov
 */
@Parcelize
sealed interface ConsultationRateType : Parcelable

/**
 * Тип запроса/результата оценки в виде звёзд.
 */
@Parcelize
data class StarType(val starsCount: Int = 0) : ConsultationRateType

/**
 * Тип запроса/результата оценки в виде эмодзи.
 */
@Parcelize
data class EmojiType(val emoji: EmojiRate = EmojiRate.SMILE) : ConsultationRateType

/**
 * Тип запроса/результата оценки в виде пальцев.
 */
@Parcelize
data class ThumbType(val finger: FingerRating = FingerRating.LIKE) : ConsultationRateType

/**
 * Эмодзи для оценки.
 */
enum class EmojiRate(private val rating: String) {
    SMILE("5"),
    NEUTRAL("3"),
    ANNOYED("1");

    companion object {
        fun getFromRating(rating: String): EmojiRate {
            return when (rating) {
                SMILE.rating -> SMILE
                NEUTRAL.rating -> NEUTRAL
                else -> ANNOYED
            }
        }
    }
}

/**
 * Пальцы для оценки.
 */
enum class FingerRating(private val rating: String) {
    LIKE("5"),
    DISLIKE("1");

    companion object {
        fun getFromRating(rating: String): FingerRating {
            return if (rating == LIKE.rating) LIKE else DISLIKE
        }
    }
}