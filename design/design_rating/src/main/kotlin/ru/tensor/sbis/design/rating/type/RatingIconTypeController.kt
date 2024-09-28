package ru.tensor.sbis.design.rating.type

import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.rating.api.SbisRatingIconTypeApi

/**
 * Класс для управления логикой, в зависимости от типа иконок.
 *
 * @author ps.smirnyh
 */
internal interface RatingIconTypeController : SbisRatingIconTypeApi {

    /** Список отображаемых иконок. */
    val icons: MutableList<TextLayout>

    /** Провайдер нужных иконок, в зависимости от текущих параметров. */
    val iconProvider: RatingIconProvider
}