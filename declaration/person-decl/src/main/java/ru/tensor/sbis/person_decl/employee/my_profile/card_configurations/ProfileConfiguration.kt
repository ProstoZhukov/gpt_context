package ru.tensor.sbis.person_decl.employee.my_profile.card_configurations

import android.os.Parcelable
import ru.tensor.sbis.person_decl.employee.card_configuration_common.SocialNetworksConfiguration
import ru.tensor.sbis.person_decl.employee.card_configuration_common.WorkInfoConfiguration
import ru.tensor.sbis.person_decl.employee.my_profile.card_configurations.blocks.AboutMeConfiguration
import ru.tensor.sbis.person_decl.employee.my_profile.card_configurations.blocks.ContactsConfiguration
import ru.tensor.sbis.person_decl.employee.my_profile.card_configurations.blocks.CredentialsOptionsConfiguration
import ru.tensor.sbis.person_decl.employee.my_profile.card_configurations.blocks.MyProfileFabButtonsConfiguration

/**
 * Конфигурация экрана "Мой Профиль"
 * @property credentialsOptionsConfiguration - конфигурация профиля, раздела фото и ФИО
 * @property workInfoConfiguration - конфигурация места о работе
 * @property motivationIsAvailable - флаг доступности перехода в мотивацию.
 *
 * @author ra.temnikov
 */
interface ProfileConfiguration : Parcelable {
    val credentialsOptionsConfiguration: CredentialsOptionsConfiguration?
    val workInfoConfiguration: WorkInfoConfiguration?
    val fabButtonsConfiguration: MyProfileFabButtonsConfiguration?
    val socialNetworkConfiguration: SocialNetworksConfiguration?
    val contactsConfiguration: ContactsConfiguration?
    val aboutMeConfiguration: AboutMeConfiguration?
    val motivationIsAvailable: Boolean

    // credentials area start
    /**
     * Метод, позволяющий понять по конфигурации, может ли пользователь изменить ФИО
     * @param isPhysic - является ли пользователь физ лицом
     */
    fun canUserChangeCredentials(isPhysic: Boolean) =
        isPhysic && credentialsOptionsConfiguration?.canPhysicChangeCredentials == true ||
            isPhysic.not() && credentialsOptionsConfiguration?.canChangeCredentials == true

    /**
     * Метод, позволяющий понять по конфигурации, может ли пользователь удалить фото.
     * @param isPhysic - является ли пользователь физ лицом.
     * @param canEditPhoto - разрешено ли пользователю менять фото настройкой онлайна.
     */
    fun canUserDeletePhoto(isPhysic: Boolean, canEditPhoto: Boolean) =
        isPhysic && credentialsOptionsConfiguration?.canPhysicDeletePhoto == true ||
            isPhysic.not() && canEditPhoto && credentialsOptionsConfiguration?.canDeletePhoto == true

    /**
     * Метод, позволяющий понять по конфигурации, может ли пользователь изменить фото
     * @param isPhysic - является ли пользователь физ лицом
     * @param canUserEditPhoto - разрешено ли пользователю менять фото настройкой онлайна
     */
    fun canUserChangePhoto(isPhysic: Boolean, canUserEditPhoto: Boolean): Boolean {
        return isPhysic && credentialsOptionsConfiguration?.canPhysicEditPhoto == true ||
            isPhysic.not() && canUserEditPhoto && credentialsOptionsConfiguration?.canEditPhoto == true
    }

    /**
     * Метод, позволяющий понять по конфигурации, может ли пользователь копировать ссылку на профиль
     */
    fun isCanCopyLink() = credentialsOptionsConfiguration?.canCopyLink == true
    // credentials area end

    // workInfo area start
    /**
     * Метод, позволяющий понять по конфигурации, может ли пользователь видеть трудовую книжку
     */
    fun isRecordBookScreenAvailable() = workInfoConfiguration?.isRecordBookScreenAvailable == true
    // workInfo area end

    // fabButtons area start
    /** @SelfDocumented */
    fun getPopupMenuConfiguration() = fabButtonsConfiguration?.popupEtcMenuButtonConfiguration
    // fabButtons area end

    // contacts area start
    /**
     * Метод, позволяющий понять по конфигурации, может ли пользователь видеть не подтвержденные контакты
     */
    fun isCanShowNotVerifiedContacts() = contactsConfiguration?.showNotVerifiedContacts == true

    /**
     * Метод, позволяющий понять по конфигурации, может ли пользователь видеть переключатели управления видимостью контактов
     */
    fun isVisibilityStatusAvailable() = contactsConfiguration?.visibilityStatusIsAvailable == true

    /**
     * Метод, позволяющий понять по конфигурации, доступен ли видеозвонок
     */
    fun isVideoCallAvailable() = contactsConfiguration?.videoCallIsAvailable == true

    /**
     * Метод, позволяющий понять по конфигурации, должно ли отображаться местоположение
     */
    fun isCityAndCountryAvailable() = contactsConfiguration?.cityAndCountryIsAvailable == true

    /** Необходимо ли сразу верифицировать добавляемые контакты. */
    fun needVerifyAddedPersonalContacts() = contactsConfiguration?.needVerifyAddedPersonalContacts == true

    /**
     * Есть ли ограничение на добавление новых контактов,
     * при условии наличия верифицированных мобильного телефона и почты.
     */
    fun restrictionOnAddingNewContactsIfHasVerifiedContacts() = contactsConfiguration?.restrictionOnAddingNewContactsIfHasVerifiedContacts == true
    // contacts area end

    // about_me area start
    /**
     * Метод, позволяющий понять по конфигурации, доступно ли редактирование дня рождения
     */
    fun isBirthdayEditAvailable() = aboutMeConfiguration?.isBirthdayEditAvailable() == true

    /**
     * Метод, позволяющий понять по конфигурации, если кнопки управления областью и форматом
     * отображения даты рождения должны быть доступными.
     */
    fun isBirthdayTypeFormatSwitcherAvailable() = aboutMeConfiguration?.isBirthdayTypeFormatSwitcherAvailable() == true

    /**
     * Метод, позволяющий понять по конфигурации, доступно ли редактирование пола
     */
    fun isEditGenderAvailable() = aboutMeConfiguration?.isEditGenderAvailable() == true

    /**
     * Метод, позволяющий понять по конфигурации, доступна ли информация О себе
     */
    fun isAboutMeInfoAvailable() = aboutMeConfiguration?.isAboutMeInfoAvailable() == true

    /**
     * Метод, позволяющий понять по конфигурации, доступно ли редактирование информации О себе
     */
    fun isEditAboutMeInfoAvailable() = aboutMeConfiguration?.isEditAboutMeInfoAvailable() == true
    // about_me area end
}