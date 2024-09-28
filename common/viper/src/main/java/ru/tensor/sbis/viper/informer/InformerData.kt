package ru.tensor.sbis.viper.informer

import androidx.annotation.StringRes

/**
 * Модель данных для показа snackbar
 */
data class InformerData constructor(
    val type: InformerType,
    val message: String = "",
    @StringRes val messageResId: Int = 0,
    @StringRes val iconResId: Int? = null
)
