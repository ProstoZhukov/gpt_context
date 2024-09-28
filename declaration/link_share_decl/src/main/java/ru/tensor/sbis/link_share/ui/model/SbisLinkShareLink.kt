package ru.tensor.sbis.link_share.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель ссылки, которой нужно поделиться
 * @param url ссылка
 * @param caption название таба (нужно, если ссылки две)
 */
@Parcelize
data class SbisLinkShareLink(val url: String, val caption: String = "") : Parcelable