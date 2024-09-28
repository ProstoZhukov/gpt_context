package ru.tensor.sbis.business_card_host_decl.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.person_decl.profile.model.ProfileContact
import java.util.Date
import java.util.UUID

/**
 * Визитка
 * @property id Идентификатор визитки
 * @property title Название визитки
 * @property personName Имя владельца визитки
 * @property personRole Должность владельца визитки
 * @property personPhoto Фото владельца визитки
 * @property links Ссылки на визитку
 * @property pinned Признак пина
 * @property backgroundColor Цвет фона
 * @property contacts Список контактов
 */
@Parcelize
data class BusinessCard(
    val id: UUID,
    val createdTs: Date,
    val title: String,
    val personName: String?,
    val personRole: String?,
    val personPhoto: String?,
    val snapshotUrl: String?,
    val links: ArrayList<BusinessCardLink>,
    val pinned: Boolean,
    var style: BusinessCardStyle?,
    val contacts: List<ProfileContact>
) : Parcelable

