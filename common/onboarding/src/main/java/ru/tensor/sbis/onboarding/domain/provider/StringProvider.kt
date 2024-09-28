package ru.tensor.sbis.onboarding.domain.provider

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.onboarding.contract.providers.content.BasePage
import ru.tensor.sbis.onboarding.contract.providers.content.Page
import javax.inject.Inject

/**
 * Класс-обертка над ApplicationContext для предоставления строковых ресурсов по id
 *
 * @author as.chadov
 */
internal class StringProvider @Inject constructor(private val mContext: Context) {

    fun getString(@StringRes stringRes: Int) = mContext.getString(stringRes)

    fun findLongestString(pages: List<Page>): String =
        pages.filterIsInstance(BasePage::class.java)
            .map {
                val (textResId, text) = it.description.run { textResId to text }
                if (textResId != ID_NULL) {
                    getString(textResId)
                } else {
                    text
                }
            }.maxByOrNull { it.length }.orEmpty()
}