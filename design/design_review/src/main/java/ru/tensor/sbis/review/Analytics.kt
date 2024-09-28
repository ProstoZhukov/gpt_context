package ru.tensor.sbis.review

import android.os.Bundle
import ru.tensor.sbis.statistic.StatisticService
import ru.tensor.sbis.statistic.model.StatisticEvent

/**
 * @author ma.kolpakov
 */
internal class Analytics {

    /**
     * Публикует событие для отслеживания запроса на оценку приложения
     */
    fun reportReviewEvent(eventClass: String, eventName: String, isFirst: Boolean) {
        val parameters = Bundle().apply {
            this.putString(REVIEW_SOURCE_CLASS_KEY, eventClass)
            this.putString(REVIEW_SOURCE_NAME_KEY, eventName)
            this.putBoolean(IS_FIRST_REVIEW_KEY, isFirst)
        }
        StatisticService.report(parameters.toStatisticEvent(DESIGN_REVIEW_MODULE_NAME, ON_REVIEW_REQUESTED_KEY))
    }

    private companion object {
        const val REVIEW_SOURCE_CLASS_KEY = "review_source_class"
        const val REVIEW_SOURCE_NAME_KEY = "review_source_name"
        const val IS_FIRST_REVIEW_KEY = "is_first_review"
        const val ON_REVIEW_REQUESTED_KEY = "on_review_requested"

        const val DESIGN_REVIEW_MODULE_NAME = "design_review"

        fun Bundle.toStatisticEvent(
            functional: String,
            context: String
        ): StatisticEvent {
            val builder = StringBuilder()
            for (key in keySet()) {
                builder.appendLine("[$key] = ${getString(key)}")
            }
            return StatisticEvent(functional, context, builder.toString())
        }
    }
}