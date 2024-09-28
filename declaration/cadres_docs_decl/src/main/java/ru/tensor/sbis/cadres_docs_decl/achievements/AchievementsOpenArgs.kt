package ru.tensor.sbis.cadres_docs_decl.achievements

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

import java.util.*

/**
 * Общая модель аргументов открытия фрагмента ПиВ.
 *
 * @property docType - тип документа
 * @property openFrom - тип с указанием места откуда происходит открытие.
 * @property openScreenState - первоначальное состояние экрана при открытии.
 */
sealed class AchievementsOpenArgs : Parcelable {
    abstract val docType: AchievementsType
    abstract val openFrom: AchievementsOpenFrom
    abstract val openScreenState: AchievementsScreenState
    abstract val actionSettings: AchievementsActionSettings
}

/**
 * Стандартая модель для открытия ПиВ.
 */
@Parcelize
data class AchievementsDefaultArgs(
    val documentUUID: UUID,
    override val docType: AchievementsType,
    override val openFrom: AchievementsOpenFrom,
    override val openScreenState: AchievementsScreenState,
    override val actionSettings: AchievementsActionSettings = AchievementsActionSettings()
) : AchievementsOpenArgs()

/**
 * Модель открытия фрагмента ПиВ для создания нового документа.
 *
 * @param baseDocUUID - uuid документа-основания по которому создаётся ПиВ.
 */
@Parcelize
data class AchievementsCreateArgs(
    val pivType: Long,
    val recipientEmployee: UUID,
    override val docType: AchievementsType,
    val baseDocUUID: UUID?
) : AchievementsOpenArgs() {

    @IgnoredOnParcel
    override val openFrom: AchievementsOpenFrom = AchievementsOpenFrom.OTHER

    @IgnoredOnParcel
    override val openScreenState: AchievementsScreenState = AchievementsScreenState.EDITING

    @IgnoredOnParcel
    override val actionSettings: AchievementsActionSettings = AchievementsActionSettings()
}