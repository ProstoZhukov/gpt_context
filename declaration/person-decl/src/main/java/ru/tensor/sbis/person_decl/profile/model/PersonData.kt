package ru.tensor.sbis.person_decl.profile.model

import java.util.UUID

/**
 * Базовые данные персоны
 *
 * @property uuid             идентификатор персоны
 * @property faceId           идентификатор лица
 * @property name             ФИО
 * @property photoUrl         ссылка на фото профиля
 * @property gender           пол персоны
 * @property initialsStubData данные для отображения заглушки с инициалами
 * @property hasAccess        имеет ли логин в систему
 *
 * @author ra.temnikov
 */
interface PersonData {
    val uuid: UUID
    val faceId: Long?
    val name: PersonName
    val photoUrl: String?
    val gender: Gender
    val initialsStubData: InitialsStubData?
    val hasAccess: Boolean
}