package ru.tensor.sbis.person_decl.employee.my_profile.card_configurations.blocks

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Конфигурация блока "О себе" */
interface AboutMeConfiguration : Parcelable {
    /** Конфигурация блока с датой рождения */
    val birthdayCnf: BirthdayConfiguration?

    /** Конфигурация блока с указанием пола */
    val genderCnf: GenderConfiguration?

    /** Конфигурация блока с дополнительной информацией о себе */
    val aboutDescCnf: AboutDescriptionConfiguration?

    /**
     * Метод, позволяющий понять по конфигурации, доступно ли редактирование дня рождения
     */
    fun isBirthdayEditAvailable() = birthdayCnf?.birthdayEditIsAvailable == true

    /**
     * Метод, позволяющий понять по конфигурации, если кнопки управления областью и форматом
     * отображения даты рождения должны быть доступными.
     */
    fun isBirthdayTypeFormatSwitcherAvailable() = birthdayCnf?.editBirthdayVisibilityTypeAndFormatIsAvailable == true

    /**
     * Метод, позволяющий понять по конфигурации, доступно ли редактирование пола
     */
    fun isEditGenderAvailable() = genderCnf?.editIsAvailable == true

    /**
     * Метод, позволяющий понять по конфигурации, доступна ли информация О себе
     */
    fun isAboutMeInfoAvailable() = aboutDescCnf != null

    /**
     * Метод, позволяющий понять по конфигурации, доступно ли редактирование информации О себе
     */
    fun isEditAboutMeInfoAvailable() = aboutDescCnf?.editIsAvailable == true
}

/**
 * Конфигурация блока с датой рождения
 *
 * @property birthdayEditIsAvailable - true, если функционал редактирования даты рождения
 * должен быть доступен.
 * @property editBirthdayVisibilityTypeAndFormatIsAvailable - true, если кнопки управления областью и форматом
 * отображения даты рождения должны быть доступными.
 **/
@Parcelize
data class BirthdayConfiguration(
    val birthdayEditIsAvailable: Boolean = false,
    val editBirthdayVisibilityTypeAndFormatIsAvailable: Boolean = false,
) : Parcelable

/**
 * Конфигурация блока с указанием пола
 *
 * @property editIsAvailable - true, если функционал редактирования пола
 * должен быть доступен
 **/
@Parcelize
data class GenderConfiguration(val editIsAvailable: Boolean = false) : Parcelable

/**
 * Конфигурация блока с дополнительной информацией о себе
 *
 * @property editIsAvailable - true, если функционал редактирования информации о себе
 * должен быть доступен
 **/
@Parcelize
data class AboutDescriptionConfiguration(val editIsAvailable: Boolean = false) : Parcelable