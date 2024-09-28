package ru.tensor.sbis.person_decl.employee.my_profile.card_configurations.blocks

import android.os.Parcelable

/**
 * Конфигурация раздела с Фотографией и ФИО + опции меню на экране "Мой Профиль"
 * @property canEditPhoto - может ли текущий пользователь изменить фото
 * @property canPhysicEditPhoto - те же права, только для физика
 * @property canChangeCredentials - может ли текущий пользователь изменить ФИО
 * @property canPhysicChangeCredentials - те же права, только для физика
 * @property canDeletePhoto - может ли текущий пользователь удалить фото
 * @property canPhysicDeletePhoto - те же права, только для физика
 *
 * @author ra.temnikov
 */
interface CredentialsOptionsConfiguration : Parcelable {
    val canEditPhoto: Boolean
    val canPhysicEditPhoto: Boolean
    val canChangeCredentials: Boolean
    val canPhysicChangeCredentials: Boolean
    val canDeletePhoto: Boolean
    val canPhysicDeletePhoto: Boolean
    val canCopyLink: Boolean
}