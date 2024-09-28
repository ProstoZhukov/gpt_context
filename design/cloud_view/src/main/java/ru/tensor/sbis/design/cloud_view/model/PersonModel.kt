package ru.tensor.sbis.design.cloud_view.model

import ru.tensor.sbis.design.profile_decl.person.PhotoData

/**
 * Модель с информацией о фотографии профиля, ФИО пользователя
 *
 * @author ma.kolpakov
 */
interface PersonModel {

    /**
     * Информация для отображения фотографии пользователя и статуса активности
     */
    val personData: PhotoData

    /**
     * Имя пользователя
     */
    var name: String
}