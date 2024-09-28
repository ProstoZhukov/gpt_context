package ru.tensor.sbis.cadres_docs_decl.achievements.achievements_section

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.cadres_docs_decl.achievements.AchievementsScreenState
import ru.tensor.sbis.cadres_docs_decl.achievements.AchievementsType
import java.util.*

/** Общий класс для моделей аргументов создания секции ПиВ */
sealed class AchievementsSectionOpenArgs : Parcelable

/**
 * Модель создания секции для просмотра
 *
 * @property documentUUID - идентификатор документа ПиВ. Опционален, так как есть возможность
 * отложенной инициализации документа.
 * Сделано про просьбе команды новостей: https://online.sbis.ru/opendoc.html?guid=6eebf5c4-19d8-4d5e-aa02-b756d51a9dc9
 *
 * @property screenState - состояние открытия документа (просмотр/редактирование).
 */
@Parcelize
data class AchievementsSectionShowArgs(
    val documentUUID: UUID? = null,
    val screenState: AchievementsScreenState
) : AchievementsSectionOpenArgs()

/**
 * Модель создания секции при создании нового документа ПиВ.
 *
 * @property recipientUUID - идентификатор получателя ПиВ.
 * @property achievementsId - идентификатор типа ПиВ, по которому необходимо создать документ.
 * @param baseDocUUID - uuid документа-основания по которому создаётся ПиВ.
 */
@Parcelize
data class AchievementsSectionCreateArgs(
    val recipientUUID: UUID,
    val achievementsId: Long,
    val docType: AchievementsType,
    val baseDocUUID: UUID?
) : AchievementsSectionOpenArgs()