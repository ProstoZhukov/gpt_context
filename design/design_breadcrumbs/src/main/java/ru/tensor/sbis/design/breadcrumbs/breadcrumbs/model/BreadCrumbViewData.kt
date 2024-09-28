package ru.tensor.sbis.design.breadcrumbs.breadcrumbs.model

import android.text.Spannable
import android.view.ViewGroup

/**
 * Представляет данные для отображения элемента хлебных крошек
 *
 * @author us.bessonov
 */
internal data class BreadCrumbViewData(
    val title: Spannable,
    val id: String,
    val hasArrow: Boolean,
    val width: Int = ViewGroup.LayoutParams.WRAP_CONTENT
)