package ru.tensor.sbis.communicator.declaration.saby.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Модель параметров для открытия/создания саби-чата.
 *
 * @property title отображаемый текст в тулбаре.
 * @property showSubtitle true, чтобы отобразить подзаголовок.
 * @property showToolbar true, чтобы отобразить тулбар.
 * @property isSwipeBackEnabled - true, чтобы работал свайпбэк.
 * @author vv.chekurda
 */
sealed interface SabyChatParams : Parcelable {
    val title: String?
    val showSubtitle: Boolean
    val showToolbar: Boolean
    val isSwipeBackEnabled: Boolean
    val hasAccordion: Boolean
}

/**
 * Параметры для открытия существующего чата в приложении Saby Get:
 * @param chatUuid            идентификатор чата.
 * @param relevantMessageUuid идентификатор релевантного сообщения.
 */
@Suppress("SpellCheckingInspection", "MemberVisibilityCanBePrivate", "unused")
@Parcelize
class SabyChatOpenParams(
    val chatUuid: UUID,
    val relevantMessageUuid: UUID? = null,
    override val title: String? = null,
    override val showSubtitle: Boolean = title == null,
    override val showToolbar: Boolean = true,
    override val isSwipeBackEnabled: Boolean = true,
    override val hasAccordion: Boolean = false
) : SabyChatParams

/**
 * Параметры для создания чата в приложении Saby Get:
 * @param socialGroupUuid   идентификатор социальной группы компании.
 * @param name              название компании.
 * @param address           адрес компании.
 * @param photoUrl          url аватарки компании.
 * @param fromFavorites     true, если создание чата из реестра избранного.
 */
@Suppress("SpellCheckingInspection", "MemberVisibilityCanBePrivate", "unused")
@Parcelize
class SabyChatCreationParams(
    val socialGroupUuid: UUID,
    val name: String,
    val address: String,
    val photoUrl: String,
    val fromFavorites: Boolean = false,
    override val title: String? = null,
    override val showSubtitle: Boolean = title == null,
    override val showToolbar: Boolean = true,
    override val isSwipeBackEnabled: Boolean = true,
    override val hasAccordion: Boolean = false
) : SabyChatParams
