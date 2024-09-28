package ru.tensor.sbis.onboarding.ui.host.adapter

import java.io.Serializable

/**
 * Параметры инициализации экрана конкретной фичи или заглушки
 *
 * @author as.chadov
 */
internal data class PageParams(val uuid: String,
                               val position: Int,
                               val count: Int) : Serializable