package ru.tensor.sbis.design.rating.api

import ru.tensor.sbis.design.rating.model.SbisRatingIconType
import ru.tensor.sbis.design.rating.SbisRatingView

/**
 * Api для компонента [SbisRatingView].
 *
 * @author ps.smirnyh
 */
interface SbisRatingViewApi : SbisRatingIconTypeApi {

    /** Иконки, которые будут отображаться в рейтинге. */
    var iconType: SbisRatingIconType

    /** Тип взаимодействия с компонентов (только просмотр, возможность изменения). */
    var readOnly: Boolean

    /** Может ли пользователь отменить выставление рейтинга нажатием еще раз на выбранную иконку. */
    var allowUserToResetRating: Boolean

    /** Callback на изменение рейтинга пользователем. */
    var onRatingSelected: ((Double) -> Unit)?
}
