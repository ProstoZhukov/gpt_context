package ru.tensor.sbis.wrhdoc_decl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Настройки главного экрана складских документов
 *
 * @property showLogoAtMainScreen показыватить логотип или заголовок "Документы"
 */
@Parcelize
data class WrhDocumentsConfig(
    val showLogoAtMainScreen: Boolean = false
) : Parcelable
