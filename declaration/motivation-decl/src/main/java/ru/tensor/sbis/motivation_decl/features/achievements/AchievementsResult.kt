package ru.tensor.sbis.motivation_decl.features.achievements

import android.os.Parcelable
import java.util.UUID

/** Контракт результата на экране достижений. */
interface AchievementsResult : Parcelable {

    /** @SelfDocumented */
    interface Factory : Parcelable {

        fun createResultForOpenIncentives(): AchievementsResult

        fun createResultForCreateNewAchievements(personUUID: UUID, personId: Long): AchievementsResult

        fun createResultForOpenAchievementDocument(documentUUID: UUID, docType: String): AchievementsResult
    }
}