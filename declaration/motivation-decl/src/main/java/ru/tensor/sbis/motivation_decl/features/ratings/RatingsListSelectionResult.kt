package ru.tensor.sbis.motivation_decl.features.ratings

import android.os.Parcelable

/** Контракт возвращаемого результата из списка рейтингов. */
interface RatingsListSelectionResult : Parcelable {

    /** Идентификатор рейтинга, по которому был сделан клик. */
    val ratingId: Long

    /** @SelfDocumented */
    interface Factory : Parcelable {
        fun getResult(ratingId: Long): RatingsListSelectionResult
    }
}